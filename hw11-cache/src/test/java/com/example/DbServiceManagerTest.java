package com.example;

import com.example.cache.MyCache;
import com.example.core.repository.DataTemplateJdbc;
import com.example.core.repository.executor.DbExecutorImpl;
import com.example.core.repository.mapper.EntityClassMetaDataImpl;
import com.example.core.repository.mapper.EntitySQLMetaDataImpl;
import com.example.core.sessionmanager.TransactionRunnerJdbc;
import com.example.crm.model.Manager;
import com.example.crm.service.DbServiceManager;
import com.example.crm.service.DbServiceManagerImpl;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

@Testcontainers
@DisplayName("Проверка взаимодействия с БД по работе с классом Manager ")
class DbServiceManagerTest {
    private DbServiceManager dbServiceManagerWithCache;
    private DbServiceManager dbServiceManagerWithoutCache;

    @Container
    private final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("demoDB")
            .withUsername("usr")
            .withPassword("pwd")
            .withClasspathResourceMapping("00_createTables.sql", "/docker-entrypoint-initdb.d/00_createTables.sql", BindMode.READ_ONLY)
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(5430), new ExposedPort(5432)))
            ));

    @BeforeEach
    void setUp() {
        var dataSource = makeConnectionPool();
        var dbExecutor = new DbExecutorImpl();
        var entityClassMetaDataManager = new EntityClassMetaDataImpl<>(Manager.class);
        var entitySQLMetaDataManager = new EntitySQLMetaDataImpl<>(entityClassMetaDataManager);

        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dataTemplate = new DataTemplateJdbc<>(dbExecutor, entityClassMetaDataManager, entitySQLMetaDataManager); //реализация DataTemplate, универсальная
        this.dbServiceManagerWithCache = new DbServiceManagerImpl(transactionRunner, dataTemplate, new MyCache<>());
        this.dbServiceManagerWithoutCache = new DbServiceManagerImpl(transactionRunner, dataTemplate, null);
    }

    private DbServiceManager getDataTemplate(boolean withCache) {
        return withCache ? this.dbServiceManagerWithCache : this.dbServiceManagerWithoutCache;
    }

    @ParameterizedTest(name = "использование кэша: {0}")
    @ValueSource(booleans = {true, false})
    @DisplayName(", что производится корректная вставка данных")
    void insertManagerTest(boolean withCache) {
        Manager Manager = new Manager("Manager_name");
        Manager insertedManager = this.getDataTemplate(withCache).saveManager(Manager);
        Assertions.assertThat(insertedManager.getLabel()).isEqualTo(Manager.getLabel());
        Assertions.assertThat(insertedManager.getParam1()).isEqualTo(Manager.getParam1());
        Assertions.assertThat(insertedManager.getNo())
                .isNotNull()
                .isPositive();
    }

    @ParameterizedTest(name = "использование кэша: {0}")
    @ValueSource(booleans = {true, false})
    @DisplayName(", что производится корректное обновление данных")
    void updateManagerTest(boolean withCache) {
        Manager Manager = this.getDataTemplate(withCache).saveManager(new Manager("Manager_name"));
        Manager.setLabel(Manager.getLabel() + "_changed");
        Manager updatedManager = this.getDataTemplate(withCache).saveManager(Manager);

        Assertions.assertThat(Manager.getLabel()).isEqualTo(updatedManager.getLabel());
        Assertions.assertThat(Manager.getNo()).isEqualTo(updatedManager.getNo());
    }

    @ParameterizedTest(name = "использование кэша: {0}")
    @ValueSource(booleans = {true, false})
    @DisplayName(", что возвращает все сохраненные записи")
    void findAllTest(boolean withCache) {
        Manager Manager1 = this.getDataTemplate(withCache).saveManager(new Manager("first"));
        Manager Manager2 = this.getDataTemplate(withCache).saveManager(new Manager("second"));

        List<Manager> Managers = this.getDataTemplate(withCache).findAll();

        Assertions.assertThat(Managers)
                .hasSize(2)
                .contains(Manager1)
                .contains(Manager2);
    }

    @ParameterizedTest(name = "использование кэша: {0}")
    @ValueSource(booleans = {true, false})
    @DisplayName(" на корректность удаления записи")
    void deleteTest(boolean withCache) {
        Manager Manager = this.getDataTemplate(withCache).saveManager(new Manager("first"));
        this.getDataTemplate(withCache).deleteManager(Manager.getNo());
        List<Manager> Managers = this.getDataTemplate(withCache).findAll();
        Assertions.assertThat(Managers).isEmpty();
    }

    private Properties getConnectionProperties() {
        Properties props = new Properties();
        props.setProperty("user", postgreSQLContainer.getUsername());
        props.setProperty("password", postgreSQLContainer.getPassword());
        props.setProperty("ssl", "false");
        return props;
    }

    DataSource makeConnectionPool() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
        config.setConnectionTimeout(3000); //ms
        config.setIdleTimeout(60000); //ms
        config.setMaxLifetime(600000);//ms
        config.setAutoCommit(false);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(10);
        config.setPoolName("DemoHiPool");
        config.setRegisterMbeans(true);

        config.setDataSourceProperties(getConnectionProperties());

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }
}
