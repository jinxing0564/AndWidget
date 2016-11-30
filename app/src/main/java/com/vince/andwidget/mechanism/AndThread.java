package com.vince.andwidget.mechanism;

/**
 * Created by tianweixin on 2016-11-29.
 */

public abstract class AndThread extends Thread {
    private int posInPool = -1;
    private AndThreadPool threadPool;

    public void setPosInPool(int pos) {
        posInPool = pos;
    }

    public int getPosInPool() {
        return posInPool;
    }

    public void setThreadPool(AndThreadPool pool) {
        threadPool = pool;
    }

    @Override
    public final void run() {
        deal();
        if (threadPool != null) {
            threadPool.removeThread(posInPool);
        }
    }

    public abstract void deal();
}
