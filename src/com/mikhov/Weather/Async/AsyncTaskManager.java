package com.mikhov.Weather.Async;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

public final class AsyncTaskManager implements IProgressTracker, OnCancelListener {
    
    private final OnTaskCompleteListener mTaskCompleteListener;
    private final ProgressDialog mProgressDialog;
    private Task mAsyncTask;

    public AsyncTaskManager(Context context, OnTaskCompleteListener taskCompleteListener) {
        mTaskCompleteListener = taskCompleteListener;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(this);
    }

    public void setupTask(Task asyncTask) {
        mAsyncTask = asyncTask;
        mAsyncTask.setProgressTracker(this);
        mAsyncTask.execute();
    }

    @Override
    public void onProgress(String message) {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        mProgressDialog.setMessage(message);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mAsyncTask.cancel(true);
        mTaskCompleteListener.onTaskComplete(mAsyncTask);
        mAsyncTask = null;
    }
    
    @Override
    public void onComplete() {
        mProgressDialog.dismiss();
        mTaskCompleteListener.onTaskComplete(mAsyncTask);
        mAsyncTask = null;
    }

    public Object retainTask() {
        if (mAsyncTask != null) {
            mAsyncTask.setProgressTracker(null);
        }
        return mAsyncTask;
    }

    public void handleRetainedTask(Object instance) {
        if (instance instanceof Task) {
            mAsyncTask = (Task) instance;
            mAsyncTask.setProgressTracker(this);
        }
    }

    public boolean isWorking() {
	    return mAsyncTask != null;
    }
}