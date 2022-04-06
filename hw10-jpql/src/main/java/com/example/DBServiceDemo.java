package com.example;

import com.example.core.repository.DataTemplateHibernate;
import com.example.core.repository.HibernateUtils;
import com.example.core.sessionmanager.TransactionManagerHibernate;
import com.example.crm.dbmigrations.MigrationsExecutorFlyway;
import com.example.crm.model.Address;
import com.example.crm.model.Client;
import com.example.crm.model.Phone;
import com.example.crm.service.DBServiceClientImpl;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DBServiceDemo {
    private static final Logger logger = LoggerFactory.getLogger(DBServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUsername = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUsername, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);
        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);

        var dbServiceClient = new DBServiceClientImpl(transactionManager, clientTemplate);

        var client = new Client(null, "Vasya", new Address(null, "AnyStreet"), List.of(new Phone(null, "13-555-22"),
                new Phone(null, "14-666-333")));

        logger.info("First saved client: {}", dbServiceClient.saveClient(client));

        var clientSecond = dbServiceClient.saveClient(new Client("dbServiceSecond"));
        logger.info("Second saved client: {}", clientSecond);

        var clientSecondSelected = dbServiceClient.getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id: " + clientSecond.getId()));
        logger.info("Second client, after fetch from db: {}", clientSecondSelected);

        logger.info("Updated second client: {}",
                dbServiceClient.saveClient(new Client(clientSecondSelected.getId(), "dbServiceSecondUpdated"))
        );
        var clientUpdated = dbServiceClient.getClient(clientSecondSelected.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecondSelected.getId()));

        logger.info("Updated second client after fetching from DB: {}", clientUpdated);
        dbServiceClient.findAll().forEach(item -> logger.info("Client: {}", item));
    }
}
