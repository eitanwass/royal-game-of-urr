package com.stadio.urr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainMenuStub extends AppCompatActivity {

    private Button queueMatchButton;
    private TextView messageTextView;

    private Socket mSocket;
    {
        try {
            this.mSocket = IO.socket("http://10.0.2.2");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private String thisEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_stub);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            thisEmail = bundle.getString("UserEmail");
        }

        getReferences();

        ListenForEvents();
    }

    public void ListenForEvents() {
        mSocket.on("joined-queue", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                displayMessage("Looking For Match...");
            }
        });

        mSocket.on("found-match", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String otherUserUsername = args[0].toString();
                displayMessage("Match Found With: " + otherUserUsername);
                joinMatch(otherUserUsername);
            }
        });
    }

    private void getReferences() {
        queueMatchButton = findViewById(R.id.queueMatchButton);
        messageTextView = findViewById(R.id.messageTextView);
    }

    private void displayMessage(final String displayMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageTextView.setText(displayMessage);
            }
        });
    }

    public void queueMatchOnClick(View view) {
        JSONObject emissionJson = new JSONObject();
        try {
            emissionJson.put("username", thisEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("quick_match", emissionJson);
    }

    private void joinMatch(String otherUserUsername) {
        Bundle bundle = new Bundle();
        bundle.putString("otherUsername", otherUserUsername);

        Intent gameStartActivity = new Intent(getApplicationContext(), GameActivity.class);
        gameStartActivity.putExtras(bundle);
        startActivity(gameStartActivity);
    }
}