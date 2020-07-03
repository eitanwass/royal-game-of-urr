package com.stadio.urr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        usernameEditText = findViewById(R.id.loginUsernameEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
    }

    public void loginOnClick(View view) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        mSocket.connect();

        while (!mSocket.connected()){
            // Do stuff here.
        }

        JSONObject emissionJson = new JSONObject();
        try {
            emissionJson.put("username", username);
            emissionJson.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("login", emissionJson);
    }
}