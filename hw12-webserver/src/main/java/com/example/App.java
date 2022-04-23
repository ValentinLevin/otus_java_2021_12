package com.example;

import com.example.core.repository.DataTemplateHibernate;
import com.example.core.repository.HibernateUtils;
import com.example.core.sessionmanager.TransactionManagerHibernate;
import com.example.crm.service.DBServiceClientImpl;
import com.example.crm.service.DBServiceUserImpl;
import com.example.crm.dbmigrations.MigrationsExecutorFlyway;
import com.example.crm.model.Address;
import com.example.crm.model.Client;
import com.example.crm.model.Phone;
import com.example.crm.model.User;
import com.example.server.WebServer;
import com.example.server.WebServerImpl;
import com.example.services.ClientServiceImpl;
import com.example.services.TemplateProcessorFreemarker;
import com.example.services.UserService;
import com.example.services.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.cfg.Configuration;

public class App {
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";
    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) throws Exception {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUsername = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUsername, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class, User.class);
        var transactionManager = new TransactionManagerHibernate(sessionFactory);

        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        var dbServiceClient = new DBServiceClientImpl(transactionManager, clientTemplate);
        var clientService = new ClientServiceImpl(dbServiceClient);

        var userTemplate = new DataTemplateHibernate<>(User.class);
        var dbServiceUser = new DBServiceUserImpl(transactionManager, userTemplate);
        var userService = new UserServiceImpl(dbServiceUser);

        var objectMapper = new ObjectMapper();
        var templateProcessor = new TemplateProcessorFreemarker(TEMPLATES_DIR);

        WebServer webServer = new WebServerImpl(WEB_SERVER_PORT, clientService, userService, objectMapper, templateProcessor);
        webServer.start();
        webServer.join();
    }
}
