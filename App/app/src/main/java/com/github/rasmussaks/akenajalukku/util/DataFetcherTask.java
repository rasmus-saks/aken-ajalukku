package com.github.rasmussaks.akenajalukku.util;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class DataFetcherTask extends AsyncTask<DataFetchListener, Void, String> {

    private DataFetchListener[] listeners;

    @Override
    protected String doInBackground(DataFetchListener... listeners) {
        this.listeners = listeners;
        try {
            URLConnection connection = new URL(Constants.DATA_URL).openConnection();
            return convertStreamToString(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        for (DataFetchListener listener : listeners) {
            listener.onDataFetched(s);
        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString().trim();
    }
}
