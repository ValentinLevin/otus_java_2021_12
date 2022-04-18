package com.example.server;

import com.example.helpers.FileSystemHelper;
import com.example.services.ClientService;
import com.example.services.TemplateProcessor;
import com.example.services.UserService;
import com.example.servlet.AuthorizationFilter;
import com.example.servlet.ClientApiServlet;
import com.example.servlet.ClientServlet;
import com.example.servlet.LoginServlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.Arrays;

public class WebServerImpl implements WebServer {
    private static final String START_PAGE_NAME = "index.html";
    private static final String COMMON_RESOURCES_DIR = "static";

    private final ClientService clientService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final TemplateProcessor templateProcessor;
    private final Server server;

    public WebServerImpl(
            int port,
            ClientService clientService,
            UserService userService,
            ObjectMapper objectMapper,
            TemplateProcessor templateProcessor
    ) {
        this.clientService = clientService;
        this.objectMapper = objectMapper;
        this.templateProcessor = templateProcessor;
        this.userService = userService;
        this.server = new Server(port);
    }

    @Override
    public void start() throws Exception {
        if (this.server.getHandlers().length == 0) {
            initContext();
        }
        server.start();
    }

    @Override
    public void join() throws Exception {
        this.server.join();
    }

    @Override
    public void stop() throws Exception {
        this.server.stop();
    }

    private void initContext() {
        ResourceHandler resourceHandler = createResourceHandler();
        ServletContextHandler servletContextHandler = createServletContextHandler();

        HandlerList handlers = new HandlerList();
        handlers.addHandler(resourceHandler);
        handlers.addHandler(applySecurity(servletContextHandler, "/clients", "/api/clients"));

        this.server.setHandler(handlers);
    }

    private ResourceHandler createResourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setWelcomeFiles(new String[]{START_PAGE_NAME});
        resourceHandler.setResourceBase(FileSystemHelper.localFileNameOrResourceNameToFullPath(COMMON_RESOURCES_DIR));
        return resourceHandler;
    }

    private ServletContextHandler createServletContextHandler() {
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(new ServletHolder(new LoginServlet(templateProcessor, userService)), "/login");
        servletContextHandler.addServlet(new ServletHolder(new ClientApiServlet(clientService, objectMapper)), "/api/clients/*");
        servletContextHandler.addServlet(new ServletHolder(new ClientServlet(clientService, templateProcessor)), "/clients");
        return servletContextHandler;
    }

    private Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        AuthorizationFilter authorizationFilter = new AuthorizationFilter();
        Arrays.stream(paths).forEachOrdered(path -> servletContextHandler.addFilter(new FilterHolder(authorizationFilter), path, null));
        return servletContextHandler;
    }
}
