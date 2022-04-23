package com.example;

import com.example.cache.MyCache;
import com.example.core.repository.DataTemplateJdbc;
import com.example.core.repository.executor.DbExecutorImpl;
import com.example.core.repository.mapper.EntityClassMetaData;
import com.example.core.repository.mapper.EntityClassMetaDataImpl;
import com.example.core.repository.mapper.EntitySQLMetaData;
import com.example.core.repository.mapper.EntitySQLMetaDataImpl;
import com.example.crm.datasource.DriverManagerDataSource;
import com.example.crm.model.Client;
import com.example.crm.model.Manager;
import com.example.crm.service.DbServiceClientImpl;
import com.example.core.sessionmanager.TransactionRunnerJdbc;
import com.example.crm.service.DbServiceManagerImpl;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class HomeWork {
    private static final Logger log = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
// Общая часть
        var dataSource = new DriverManagerDataSource("hikaricp.properties");
        flywayMigrations(dataSource);
        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dbExecutor = new DbExecutorImpl();

// Работа с клиентом
        EntityClassMetaData<Client> entityClassMetaDataClient = new EntityClassMetaDataImpl<>(Client.class);
        EntitySQLMetaData<Client> entitySQLMetaDataClient = new EntitySQLMetaDataImpl<>(entityClassMetaDataClient);
        var dataTemplateClient = new DataTemplateJdbc<>(dbExecutor, entityClassMetaDataClient, entitySQLMetaDataClient); //реализация DataTemplate, универсальная

        MyCache<String, Client> clientCache = new MyCache<>();
        clientCache.addListener(HomeWork::clientCacheListener);
// Код дальше должен остаться
        var dbServiceClient = new DbServiceClientImpl(transactionRunner, dataTemplateClient, clientCache);
        var clientFirst = dbServiceClient.saveClient(new Client("dbServiceFirst"));
        dbServiceClient.deleteClient(clientFirst.getId());

        var clientSecond = dbServiceClient.saveClient(new Client("dbServiceSecond"));
        var clientSecondSelected = dbServiceClient.getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
        log.info("clientSecondSelected:{}", clientSecondSelected);
        clientSecond.setName(clientSecond.getName() + "_changed");
        dbServiceClient.saveClient(clientSecond);
        var changedClientSecond = dbServiceClient.getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
        log.info("clientSecondSelected after changing:{}", changedClientSecond);

// Сделайте тоже самое с классом Manager (для него надо сделать свою таблицу)

        EntityClassMetaData<Manager> entityClassMetaDataManager = new EntityClassMetaDataImpl<>(Manager.class);
        EntitySQLMetaData<Manager> entitySQLMetaDataManager = new EntitySQLMetaDataImpl<>(entityClassMetaDataManager);
        var dataTemplateManager = new DataTemplateJdbc<>(dbExecutor, entityClassMetaDataManager, entitySQLMetaDataManager);
        MyCache<String, Manager> managerCache = new MyCache<>();
        managerCache.addListener(HomeWork::managerCacheListener);

        var dbServiceManager = new DbServiceManagerImpl(transactionRunner, dataTemplateManager, managerCache);
        Manager managerFirst = dbServiceManager.saveManager(new Manager("ManagerFirst"));
        managerFirst.setParam1("param1_changed");
        dbServiceManager.saveManager(managerFirst);

        var managerSecond = dbServiceManager.saveManager(new Manager("ManagerSecond"));
        var managerSecondSelected = dbServiceManager.getManager(managerSecond.getNo())
                .orElseThrow(() -> new RuntimeException("Manager not found, id:" + managerSecond.getNo()));
        log.info("managerSecondSelected:{}", managerSecondSelected);
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }

    private static void clientCacheListener(String key, Client value, String action) {
        log.info("Client action in cache: action: {}, key: {}, entity: {}", action, key, value);
    }

    private static void managerCacheListener(String key, Manager value, String action) {
        log.info("Manager action in cache: action: {}, key: {}, entity: {}", action, key, value);
    }
}
