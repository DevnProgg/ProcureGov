package com.ProcureGov.config;

import com.ProcureGov.backgroundtasks.TenderStatusManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.ProcureGov.backgroundtasks.TenderStatusManager.closeExpiredTenders;

@WebListener
public class SchedulerListener implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/procure_gov");
            TenderStatusManager.init(ds);

            scheduler = Executors.newScheduledThreadPool(1);

            //configuration with longer initial delay
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    System.out.println("Running background task: close expired tenders");
                    long startTime = System.currentTimeMillis();

                    closeExpiredTenders();

                    long duration = System.currentTimeMillis() - startTime;
                    System.out.println("Background task completed in " + duration + "ms");

                } catch (Exception e) {
                    System.err.println("Error in scheduled task: " + e.getMessage());
                    e.printStackTrace();
                }
            }, 1, 1, TimeUnit.MINUTES);

        } catch (NamingException e) {
            System.err.println("Failed to initialize scheduler: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                // Wait for existing tasks to complete
                if (!scheduler.awaitTermination(120, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}