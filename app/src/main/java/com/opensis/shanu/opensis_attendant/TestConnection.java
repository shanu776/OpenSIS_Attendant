package com.opensis.shanu.opensis_attendant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

/**
 * Created by Shanu on 2/24/2017.
 */

public class TestConnection {
    @SuppressLint("NewApi")
    public static boolean pingHost() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("52.38.90.246", 8080), 3000);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    }

