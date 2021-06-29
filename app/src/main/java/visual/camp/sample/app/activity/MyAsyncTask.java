package visual.camp.sample.app.activity;

import android.os.Handler;
import android.os.Looper;


abstract class MyAsyncTask<T1,T2,T3> {
    private boolean cancelled = false;

    final public void excute(final T1 args){
        onPreExecute();
        new Thread(){
            @Override
            public void run() {
                T3 result = doInBackground(args);
                if(cancelled) cancelRun(result); else postRun(result);
            }
        }.start();
    }
    final public void excute(){
        onPreExecute();
        new Thread(){
            @Override
            public void run() {
                postRun(doInBackground(null));
            }
        }.start();
    }

    final public void cancel(){
        cancelled = true;
    }

    final private void publishProgress(final T2 progress) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                onProgressUpdate(progress);
            }
        });
    }

    final private void postRun(final T3 result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                onPostExecute(result);
            }
        });
    }

    final private void cancelRun(final T3 result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                onCancelled(result);
            }
        });
    }

    protected abstract void onPreExecute();
    protected abstract T3 doInBackground(T1 arg);
    protected abstract void onProgressUpdate(T2 progress);
    protected abstract void onPostExecute(T3 result);
    protected abstract void onCancelled(T3 result);






}
