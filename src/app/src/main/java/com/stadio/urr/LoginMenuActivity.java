package com.stadio.urr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginMenuActivity extends AppCompatActivity {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private Socket mSocket;
    {
        try {
            this.mSocket = IO.socket("http://10.0.2.2");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private EditText emailEditText;
    private EditText passwordEditText;

    private ProgressBar progressBar;
    private TextView errorTextView;

    private String enteredEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        getReferences();

        ListenForEvents();
    }

    private void getReferences() {
        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);

        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);
    }

    public void ListenForEvents() {
        mSocket.on("login-success", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                displayMessage(args[0].toString());

                StartGame();
            }
        });

        mSocket.on("login-failed", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                displayMessage(args[0].toString());
            }
        });
    }

    public void loginOnClick(View view) {
        progressBar.setVisibility(View.VISIBLE);

        sendLoginData(mSocket);
    }

    private void sendLoginData(Socket socket) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        enteredEmail = email;
        // TODO: hash password.

        JSONObject emissionJson = new JSONObject();
        try {
            emissionJson.put("email", email);
            emissionJson.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("login", emissionJson);
    }

    private void displayMessage(final String displayMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                errorTextView.setText(displayMessage);
            }
        });
    }

    private void StartGame() {
        LOGGER.log(Level.INFO, "Start game in online mode.");
        Bundle bundle = new Bundle();
        bundle.putString("UserEmail", enteredEmail);

//        Intent gameStartActivity = new Intent(getApplicationContext(), GameActivity.class);
//        startActivity(gameStartActivity);
        Intent mainMenuActivity = new Intent(getApplicationContext(), MainMenuStub.class);
        mainMenuActivity.putExtras(bundle);
        startActivity(mainMenuActivity);
    }

    public void switchToRegisterMenu(View view) {
        Intent registerIntent = new Intent(getApplicationContext(), RegisterMenuActivity.class);
        startActivity(registerIntent);
    }
}