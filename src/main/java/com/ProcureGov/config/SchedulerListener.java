package com.ProcureGov.config;

import com.ProcureGov.backgroundtasks.EmailBroker;
import com.ProcureGov.backgroundtasks.TenderStatusManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@WebListener
public class SchedulerListener implements ServletContextListener {

    private ScheduledExecutorService tenderScheduler;
    private ScheduledExecutorService emailScheduler;

    // Flags to prevent overlapping executions within each task type
    private final AtomicBoolean isTenderTaskRunning = new AtomicBoolean(false);
    private final AtomicBoolean isEmailTaskRunning = new AtomicBoolean(false);

    // Thread pool for parallel execution of independent tasks
    private ExecutorService taskExecutor;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/procure_gov");
            TenderStatusManager.init(ds);

            tenderScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "tender-closing-thread");
                t.setDaemon(true);
                return t;
            });

            emailScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "email-sending-thread");
                t.setDaemon(true);
                return t;
            });

            // Schedule tender closing task
            tenderScheduler.scheduleWithFixedDelay(() -> {
                if (!isTenderTaskRunning.compareAndSet(false, true)) {
                    System.out.println("Previous tender closing task still running, skipping");
                    return;
                }

                try {
                    System.out.println("Running tender closing task on thread: " +
                            Thread.currentThread().getName());
                    long startTime = System.currentTimeMillis();

                    TenderStatusManager.closeExpiredTenders();

                    long duration = System.currentTimeMillis() - startTime;
                    System.out.println("Tender closing completed in " + duration + "ms");

                } catch (Exception e) {
                    System.err.println("Error in tender closing task: " + e.getMessage());
                } finally {
                    isTenderTaskRunning.set(false);
                }
            }, 1, 1, TimeUnit.MINUTES);

            // Schedule email sending task independently
            emailScheduler.scheduleWithFixedDelay(() -> {
                if (!isEmailTaskRunning.compareAndSet(false, true)) {
                    System.out.println("Previous email task still running, skipping");
                    return;
                }

                try {
                    System.out.println("Running email sending task on thread: " +
                            Thread.currentThread().getName());
                    long startTime = System.currentTimeMillis();

                    // Each task gets its own connection from the pool
                    try (Connection conn = ds.getConnection()) {
                        conn.setAutoCommit(false);
                        try {
                            EmailBroker.EmailBrokerExecutor(conn);
                            conn.commit();
                        } catch (Exception e) {
                            conn.rollback();
                            throw e;
                        }
                    }

                    long duration = System.currentTimeMillis() - startTime;
                    System.out.println("Email sending completed in " + duration + "ms");

                } catch (Exception e) {
                    System.err.println("Error in email sending task: " + e.getMessage());
                } finally {
                    isEmailTaskRunning.set(false);
                }
            }, 1, 1, TimeUnit.MINUTES);

        } catch (NamingException e) {
            System.err.println("Failed to initialize schedulers: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        shutdownScheduler(tenderScheduler, "Tender scheduler");
        shutdownScheduler(emailScheduler, "Email scheduler");
    }

    private void shutdownScheduler(ExecutorService scheduler, String name) {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                System.out.println("Shutting down " + name + "...");
                if (!scheduler.awaitTermination(120, TimeUnit.SECONDS)) {
                    System.out.println(name + " did not terminate gracefully, forcing shutdown");
                    scheduler.shutdownNow();
                    // Wait a bit more for forced shutdown
                    if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                        System.err.println(name + " did not terminate even after forced shutdown");
                    }
                }
                System.out.println(name + " shutdown complete");
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}