package com.example;

import com.example.cache.MyCache;
import com.example.core.repository.DataTemplateJdbc;
import com.example.core.repository.executor.DbExecutorImpl;
import com.example.core.repository.mapper.EntityClassMetaDataImpl;
import com.example.core.repository.mapper.EntitySQLMetaDataImpl;
import com.example.core.sessionmanager.TransactionRunnerJdbc;
import com.example.crm.model.Client;
import com.example.crm.service.DbServiceClient;
import com.example.crm.service.DbServiceClientImpl;
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
@DisplayName("Проверка взаимодействия с БД по работе с классом Client ")
class DbServiceClientTest {
    private DbServiceClient dbServiceClientWithCache;
    private DbServiceClient dbServiceClientWithoutCache;

    @Container
    private final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:12")
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
        var entityClassMetaDataClient = new EntityClassMetaDataImpl<>(Client.class);
        var entitySQLMetaDataClient = new EntitySQLMetaDataImpl<>(entityClassMetaDataClient);

        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dataTemplate = new DataTemplateJdbc<>(dbExecutor, entityClassMetaDataClient, entitySQLMetaDataClient); //реализация DataTemplate, универсальная
        this.dbServiceClientWithCache = new DbServiceClientImpl(transactionRunner, dataTemplate, new MyCache<>());
        this.dbServiceClientWithoutCache = new DbServiceClientImpl(transactionRunner, dataTemplate, null);
    }

    private DbServiceClient getDataTemplate(boolean withCache) {
        return withCache ? this.dbServiceClientWithCache : this.dbServiceClientWithoutCache;
    }

    @DisplayName(", что производится корректная вставка данных")
    @ParameterizedTest(name = "использование кэша: {0}")
    @ValueSource(booleans = {true, false})
    void insertClientTest(boolean withCache) {
        Client client = new Client("client_name");
        Client insertedClient = this.getDataTemplate(withCache).saveClient(client);
        Assertions.assertThat(insertedClient.getName())
                .isEqualTo(client.getName());
        Assertions.assertThat(insertedClient.getId())
                .isNotNull()
                .isPositive();
    }

    @DisplayName(", что производится корректное обновление данных")
    @ParameterizedTest(name = "использование кэша: {0}")
    @ValueSource(booleans = {true, false})
    void updateClientTest(boolean withCache) {
        Client client = this.getDataTemplate(withCache).saveClient(new Client("client_name"));
        client.setName(client.getName() + "_changed");
        Client updatedClient = this.getDataTemplate(withCache).saveClient(client);

        Assertions.assertThat(client.getName())
                .isEqualTo(updatedClient.getName());
        Assertions.assertThat(client.getId())
                .isEqualTo(updatedClient.getId());
    }

    @DisplayName(", что возвращает все сохраненные записи")
    @ParameterizedTest(name = "использование кэша: {0}")
    @ValueSource(booleans = {true, false})
    void findAllTest(boolean withCache) {
        Client client1 = this.getDataTemplate(withCache).saveClient(new Client("first"));
        Client client2 = this.getDataTemplate(withCache).saveClient(new Client("second"));

        List<Client> clients = this.getDataTemplate(withCache).findAll();

        Assertions.assertThat(clients)
                .hasSize(2)
                .contains(client1)
                .contains(client2);
    }

    @DisplayName(" на корректность удаления записи")
    @ParameterizedTest(name = "использование кэша: {0}")
    @ValueSource(booleans = {true, false})
    void deleteTest(boolean withCache) {
        Client client = this.getDataTemplate(withCache).saveClient(new Client("first"));
        this.getDataTemplate(withCache).deleteClient(client.getId());
        List<Client> clients = this.getDataTemplate(withCache).findAll();
        Assertions.assertThat(clients).isEmpty();
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
