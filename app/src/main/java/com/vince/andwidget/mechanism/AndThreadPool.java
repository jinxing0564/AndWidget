package com.vince.andwidget.mechanism;

import android.util.Log;

/**
 * Created by tianweixin on 2016-11-29.
 * 如果当前任务数ThreadPool已满，丢弃当前操作
 */

public class AndThreadPool {
    private AndThread[] threads;
    private int poolSize;

    public AndThreadPool(int size) {
        poolSize = size;
        init();
    }

    private void init() {
        threads = new AndThread[poolSize];
    }

    public int getPoolSize() {
        return poolSize;
    }

    public AndThread[] getThreads() {
        return threads;
    }

    public synchronized int addThread(AndThread thread) {
        int pos = getFarMostPos(thread);
        Log.d("jinxing", "pos = " + pos);
        if (threads[pos] != null) {
            threads[pos].interrupt();
        }
        threads[pos] = thread;
        thread.setPosInPool(pos);
        thread.setThreadPool(this);
        thread.start();
        return pos;
    }

    public synchronized void cleanPool() {
        for (int i = 0; i < poolSize; i++) {
            if (threads[i] != null) {
                threads[i].interrupt();
                threads[i] = null;
            }
        }
    }

    public synchronized void removeThread(int pos) {
        if (pos != -1) {
            threads[pos] = null;
        }
    }

//    private synchronized int getFreePos() {
//        for (int i = 0; i < poolSize; i++) {
//            if (threads[i] == null) {
//                return i;
//            }
//        }
//        return -1;
//    }

    public synchronized int getFarMostPos(AndThread nowThread) {
        return 0;
    }


}
