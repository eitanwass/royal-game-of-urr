package com.stadio.urr;

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
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static String email;
    public static String username;

    public static Bitmap avatar;
}
