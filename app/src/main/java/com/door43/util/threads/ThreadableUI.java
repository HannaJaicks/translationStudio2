package com.door43.util.threads;

import android.content.Context;
import android.os.Handler;

/**
 * This class allows you to execute operations in a seperate thread and then finish up by running some code on the ui thread
 * @deprecated you should probably almost always use the new task manager
 */
public abstract class ThreadableUI {
    private final Thread mThread;
    private final int mId;
    private static int mThreadCount;
    private boolean mInterrupted;
    private boolean mHasStarted = false;

    public ThreadableUI(Context context) {
        mThreadCount++;
        mId = mThreadCount;

        // create a new thread and handler
        final Handler handler = new Handler(context.getMainLooper());
        mThread = new Thread() {
            @Override
            public void run() {
                // execute the tasks
                ThreadableUI.this.run();
                // execute cleanup on the ui thread
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute();
                    }
                });
            }
        };
    }

    /**
     * Starts the thread
     */
    public void start() {
        if(!isInterrupted() && !mHasStarted) {
            mHasStarted = true;
            mThread.start();
        }
    }

    /**
     * Check if the thread has been interrupted
     * @return
     */
    public boolean isInterrupted() {
        return mThread.isInterrupted() || mInterrupted;
    }

    /**
     * kills the thread
     * WARNING: this may not be the safest way to terminate the thread
     */
    public void stop() {
        // TRICKY: we set a variable so we know if the thread has been interrupted before it has started.
        mInterrupted = true;
        if(mHasStarted && !mThread.isInterrupted()) {
            onStop();
            mThread.interrupt();
        }
    }

    /**
     * Return the id of thread
     * @return
     */
    public int getId() {
        return mId;
    }

    /**
     * Allow the thread to perform some cleanup actions before shutting down
     */
    abstract public void onStop();

    /**
     * Code to be ran when the thread starts up
     */
    abstract public void run();

    /**
     * Code to be ran on the UI thread after the run() is complete
     */
    abstract public void onPostExecute();
}
