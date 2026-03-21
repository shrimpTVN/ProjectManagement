package com.app.src.core.async;

import com.app.src.exceptions.GlobalExceptionHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncExecutor {
    private static AsyncExecutor instance;
    private final ExecutorService threadPool;

    public static synchronized AsyncExecutor getInstance() {
        if (instance == null) {
            instance = new AsyncExecutor();
        }
        return instance;
    }

    private AsyncExecutor() {
        // Sử dụng ThreadFactory tùy chỉnh để đặt tên và gắn Exception Handler
        ThreadFactory customThreadFactory = new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "AppWorkerThread-" + threadNumber.getAndIncrement());
                t.setDaemon(true); // Đảm bảo luồng chết khi tắt app
                // Gắn bộ xử lý lỗi tập trung
                t.setUncaughtExceptionHandler(new GlobalExceptionHandler());
                return t;
            }
        };

        // Sử dụng CachedThreadPool hoặc FixedThreadPool tùy cấu hình
        this.threadPool = Executors.newCachedThreadPool(customThreadFactory);
    }


    public void runAsync(Runnable task) {
        threadPool.submit(task);
    }

    public void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(2, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
