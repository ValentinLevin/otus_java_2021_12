package com.example;

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
import org.junit.jupiter.api.Test;
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
    private DbServiceClient dbServiceClient;

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
        var entityClassMetaDataClient = new EntityClassMetaDataImpl<>(Client.class);
        var entitySQLMetaDataClient = new EntitySQLMetaDataImpl<>(entityClassMetaDataClient);

        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dataTemplate = new DataTemplateJdbc<>(dbExecutor, entityClassMetaDataClient, entitySQLMetaDataClient); //реализация DataTemplate, универсальная
        this.dbServiceClient = new DbServiceClientImpl(transactionRunner, dataTemplate);
    }

    @Test
    @DisplayName(", что производится корректная вставка данных")
    void insertClientTest() {
        Client client = new Client("client_name");
        Client insertedClient = this.dbServiceClient.saveClient(client);
        Assertions.assertThat(insertedClient.getName())
                .isEqualTo(client.getName());
        Assertions.assertThat(insertedClient.getId())
                .isNotNull()
                .isPositive();
    }

    @Test
    @DisplayName(", что производится корректное обновление данных")
    void updateClientTest() {
        Client client = this.dbServiceClient.saveClient(new Client("client_name"));
        client.setName(client.getName() + "_changed");
        Client updatedClient = this.dbServiceClient.saveClient(client);

        Assertions.assertThat(client.getName())
                .isEqualTo(updatedClient.getName());
        Assertions.assertThat(client.getId())
                .isEqualTo(updatedClient.getId());
    }

    @Test
    @DisplayName(", что возвращает все сохраненные записи")
    void findAllTest() {
        Client client1 = this.dbServiceClient.saveClient(new Client("first"));
        Client client2 = this.dbServiceClient.saveClient(new Client("second"));

        List<Client> clients = this.dbServiceClient.findAll();

        Assertions.assertThat(clients)
                .hasSize(2)
                .contains(client1)
                .contains(client2);
    }

    @Test
    @DisplayName(" на корректность удаления записи")
    void deleteTest() {
        Client client = this.dbServiceClient.saveClient(new Client("first"));
        this.dbServiceClient.deleteClient(client.getId());
        List<Client> clients = this.dbServiceClient.findAll();
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
