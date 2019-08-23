package org.sample.egor;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.sample.egor.controller.AccountController;
import org.sample.egor.controller.RuntimeExceptionMapper;
import org.sample.egor.dao.impl.AccountDAOImpl;
import org.sample.egor.provider.MyObjectMapperProvider;
import org.sample.egor.service.impl.AccountServiceImpl;

class JettyServer {

    private Server server;

    public JettyServer() {
    }

    public void start() {
        server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("");
        // adds Jersey Servlet with a customized ResourceConfig
        handler.addServlet(new ServletHolder(new ServletContainer(resourceConfig())), "/*");
        server.setHandler(handler);
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException("Could not start the server", e);
        }
    }

    private ResourceConfig resourceConfig() {
        // manually injecting dependencies (clock) to Jersey resource classes
        return new ResourceConfig()
                .packages("org.glassfish.jersey.examples.jackson")
                .packages("jersey.jetty.embedded")
                .register(MyObjectMapperProvider.class)  // No need to register this provider if no special configuration is required.
                .register(JacksonFeature.class)
                .register(RuntimeExceptionMapper.class)
                .register(new AccountController(new AccountServiceImpl(new AccountDAOImpl())));
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException("Could not stop the server", e);
        }
    }

    public void join() {
        try {
            server.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Could not join the thread", e);
        }
    }
}
