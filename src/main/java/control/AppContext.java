package control;

import model.ConnectionPool;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

@WebListener
public class AppContext implements ServletContextListener {
    private static final int POOL_SIZE = 10;
    private static final Logger logger = Logger.getLogger(AppContext.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ConnectionPool.init(POOL_SIZE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ConnectionPool.releaseResources();
        ServletContextListener.super.contextDestroyed(sce);
    }
}
