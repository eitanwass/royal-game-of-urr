package com.stadio.urr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.transports.Polling;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private Socket mSocket;
    {
        try {
            IO.Options opts = new IO.Options();
            opts.transports = new String[] {Polling.NAME};
            this.mSocket = IO.socket("http://10.0.2.2");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private ProgressBar loadingProgressBar;
    private TextView messageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        getReferences();

        LOGGER.log(Level.INFO, mSocket.toString());

        mSocket.connect();

        tryConnect();
    }

    private void tryConnect() {
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                displayMessage("--Connected to server--");

                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                GoToLoginPage();
            }
        }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                displayMessage("--Could not connect to server--\n--Online features unavailable--");

                LOGGER.log(Level.INFO, "Connection attempt timed out.");

                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                StartGameInOfflineMode();
            }
        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                displayMessage("--Could not connect to server--\n--Online features unavailable--");

                LOGGER.log(Level.INFO, "Connection error: " + args[0].toString());

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                StartGameInOfflineMode();
            }
        });
    }

    private void StartGameInOfflineMode() {
        LOGGER.log(Level.INFO, "Start game in offline mode.");
    }

    private void GoToLoginPage() {
        LOGGER.log(Level.INFO, "Go to login.");

        Intent loginIntent = new Intent(getApplicationContext(), LoginMenuActivity.class);
        startActivity(loginIntent);
    }

    private void displayMessage(final String displayMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingProgressBar.setVisibility(View.INVISIBLE);
                messageTextView.setText(displayMessage);
            }
        });
    }

    private void getReferences() {
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        messageTextView = findViewById(R.id.messageTextView);
    }
}