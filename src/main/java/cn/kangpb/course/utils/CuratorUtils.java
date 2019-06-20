package cn.kangpb.course.utils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CuratorUtils {
    private static String address = "129.204.15.237:2181";

    public static void main(String[] args) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(address, retryPolicy);
        client.start();
        final InterProcessMutex mutex = new InterProcessMutex(client,"/curator/lock");
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            fixedThreadPool.submit(() -> {
                boolean flag = false;
                try {
                    flag = mutex.acquire(5, TimeUnit.SECONDS);
                    Thread currentThread = Thread.currentThread();
                    if (flag) {
                        System.out.println("*********************线程"+currentThread.getId()+"获取锁成功");
                    } else {
                        System.out.println("*********************线程"+currentThread.getId()+"获取锁失败");
                    }
                    Thread.sleep(4000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (flag) {
                        try {
                            mutex.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}
