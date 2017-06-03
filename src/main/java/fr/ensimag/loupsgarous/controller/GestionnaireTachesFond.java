/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.loupsgarous.controller;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import org.apache.taglibs.standard.tag.common.sql.DataSourceWrapper;

/**
 *
 * @author baplar
 */
@WebListener
public class GestionnaireTachesFond implements ServletContextListener {
    private ScheduledExecutorService scheduler;
    
    private DataSource ds;
    
    public GestionnaireTachesFond() {
        DataSourceWrapper ds = new DataSourceWrapper();
        try {
            ds.setDriverClassName("oracle.jdbc.OracleDriver");
            ds.setJdbcURL("jdbc:oracle:thin:@ensioracle1.imag.fr:1521:ensioracle1");
            ds.setUserName("lartigab");
            ds.setPassword("lartigab");
            ds.setLoginTimeout(30);
        } catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException ex) {
            // System.err.println("Could not establish connexion (Context probably not initialized yet)");
        }
        this.ds = ds;
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new BackgroundUpdater(ds), (59 - LocalDateTime.now().getSecond()) % 59, 60, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        scheduler.shutdownNow();
    }

}
