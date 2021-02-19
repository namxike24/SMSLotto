package com.smsanalytic.lotto.common;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class InternetCheck extends AsyncTask<Void, Void, Boolean> {

    private Consumer mConsumer;

    public interface Consumer {
        void accept(Boolean internet);
    }

    public InternetCheck(Consumer consumer) {
        mConsumer = consumer;
        execute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress("8.8.8.8", 53), 1000);
            sock.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean internet) {
        mConsumer.accept(internet);
    }
}
