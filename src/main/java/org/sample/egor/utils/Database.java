package org.sample.egor.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.NoArgsConstructor;
import lombok.Singular;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@NoArgsConstructor
public class Database {
    private final static Logger logger = LoggerFactory.getLogger(Database.class);
    @Singular
    private final static DataSource hikariPool;
    private static Map<String, ReentrantLock> accountLocks = new ConcurrentHashMap<>();

    static {
        String url = "jdbc:hsqldb:mem:mymemdb";
        Flyway flyway = Flyway.configure().dataSource(url, null, null).load();
        flyway.migrate();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setAutoCommit(true);
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("");
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(30));
        hikariConfig.setValidationTimeout(TimeUnit.MINUTES.toMillis(1));
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(0);
        hikariConfig.setMaxLifetime(TimeUnit.MINUTES.toMillis(2));
        hikariConfig.setIdleTimeout(TimeUnit.MINUTES.toMillis(1));
        hikariConfig.setConnectionTimeout(TimeUnit.MINUTES.toMillis(5));
//        hikariConfig.setConnectionTestQuery("select 1");
        hikariPool = new HikariDataSource(hikariConfig);

    }

    ExecutorService pool = new ForkJoinPool();

    public static void lock(String[] keys, Runnable run) throws InterruptedException {
        //will lock accounts in a sorted order to prevent deadlock
        Arrays.sort(keys);
        for (String key : keys) {
            accountLocks.putIfAbsent(key, new ReentrantLock());
            Lock lock = accountLocks.get(key);
            lock.lockInterruptibly();
            logger.debug("key {} locked, ", key);
        }
        try {
            run.run();
        } finally {
            //unlock will be made in reverse order
            Arrays.sort(keys, Collections.reverseOrder());
            for (String key : keys) {
                Lock lock = accountLocks.get(key);
                lock.unlock();
                logger.debug("key {} unlocked, ", key);
            }
        }

    }

    public static DataSource getDataSource() {
        return hikariPool;
    }
}
