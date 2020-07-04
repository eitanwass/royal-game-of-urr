package com.stadio.urr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class LoginMenuActivity extends AppCompatActivity {

    private Socket mSocket;
    {
        try {
            this.mSocket = IO.socket("http://10.0.2.2:4242");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private EditText usernameEditText;
    private EditText passwordEditText;

    private ProgressBar progressBar;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        usernameEditText = findViewById(R.id.loginUsernameEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);

        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);
    }

    public void loginOnClick(View view) {

        ProgressBar progress = findViewById(R.id.progressBar);

        mSocket.connect();

        progress.setVisibility(View.VISIBLE);

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        SendLoginData();
                    }
                });
            }
        }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        DisplayError("Connection to server has timed out");
                    }
                });
            }
        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        DisplayError("A server error has occurred");
                    }
                });
            }
        });
    }

    private void SendLoginData(){
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        JSONObject emissionJson = new JSONObject();
        try {
            emissionJson.put("username", username);
            emissionJson.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("login", emissionJson);
    }

    private void DisplayError(String problem) {
        errorTextView.setText(problem);
    }
}