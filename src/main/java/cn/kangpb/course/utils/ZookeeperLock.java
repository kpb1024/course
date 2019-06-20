package cn.kangpb.course.utils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

public class ZookeeperLock {
    private static String address = "129.204.15.237:2181";
    public static CuratorFramework client;

    static{
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        client = CuratorFrameworkFactory.newClient(address, retryPolicy);
        client.start();

    }

    private ZookeeperLock(){}

    private static class SingletonHolder{
        private static InterProcessMutex mutex = new InterProcessMutex(client, "/curator/lock");
    }
    public static InterProcessMutex getMutex() { return SingletonHolder.mutex;}

    public static boolean acquire(long time, TimeUnit unit) {
        try {
            return getMutex().acquire(time, unit);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void release() {
        try {
            getMutex().release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
