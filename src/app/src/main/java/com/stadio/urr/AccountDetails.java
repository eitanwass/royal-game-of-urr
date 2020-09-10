package com.stadio.urr;

import android.app.Activity;
import android.graphics.Bitmap;

import com.github.nkzawa.engineio.client.transports.Polling;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class AccountDetails {
    public static Socket socket; static {
        try {
            IO.Options opts = new IO.Options();
            opts.transports = new String[] {Polling.NAME};
            socket = IO.socket("https://urr-server.herokuapp.com/");
//            socket = IO.socket("http://10.0.2.2");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static String email;
    public static String username;

    public static int wins = 0;
    public static int losses = 0;

    public static Bitmap avatar;

    public static void disconnect(Activity activity) {
        socket.off();
        socket.disconnect();
        socket.close();
        activity.finish();
    }
}
