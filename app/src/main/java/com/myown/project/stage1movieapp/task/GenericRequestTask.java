package com.myown.project.stage1movieapp.task;

import android.os.AsyncTask;
import android.util.Log;

import com.myown.project.stage1movieapp.util.NetworkUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * This class executes the http request in the background and notifies it's listener about changes and the final result.
 */
public class GenericRequestTask extends AsyncTask<String, Void, String> {
    private static final String TAG = GenericRequestTask.class.getSimpleName();

    private GenericRequestListener mListener;

    public GenericRequestTask(GenericRequestListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        mListener.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            String jsonBody = params.length > 1 ? params[1] : new JSONObject().toString();

            return NetworkUtils.post(url, jsonBody);
        } catch (IOException e) {
            Log.w(TAG, "Http request failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        mListener.onPostExecute(result);
    }

    /**
     * The interface for listening to events within the GenericRequestTask class.
     */
    public interface GenericRequestListener {
        /**
         * Invoked when the task is about to be executed.
         */
        void onPreExecute();

        /**
         * Invoked after the task is finished.
         *
         * @param json the json result of the request.
         */
        void onPostExecute(String json);
    }
}
