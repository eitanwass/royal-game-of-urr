package com.stadio.urr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginMenuActivity extends AppCompatActivity {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private EditText emailEditText;
    private EditText passwordEditText;

    private ProgressBar progressBar;
    private TextView errorTextView;

    private String enteredEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getReferences();

        ListenForEvents();
    }

    private void getReferences() {
        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);

        progressBar = findViewById(R.id.winsLossesRatioBar);
        errorTextView = findViewById(R.id.errorTextView);
    }

    public void ListenForEvents() {
        AccountDetails.socket.on("login-success", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                displayMessage(args[0].toString());

                AccountDetails.email = enteredEmail;

                StartGame();
            }
        });

        AccountDetails.socket.on("login-failed", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                displayMessage(args[0].toString());
            }
        });
    }

    public void loginOnClick(View view) {
        progressBar.setVisibility(View.VISIBLE);

        sendLoginData(AccountDetails.socket);
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
                errorTextView.setVisibility(View.VISIBLE);
                errorTextView.setText(displayMessage);
            }
        });
    }

    private void StartGame() {
        LOGGER.log(Level.INFO, "Start game in online mode.");

        Intent mainMenuActivity = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(mainMenuActivity);
    }

    public void switchToRegisterMenu(View view) {
        Intent registerIntent = new Intent(getApplicationContext(), RegisterMenuActivity.class);
        startActivity(registerIntent);
    }
}