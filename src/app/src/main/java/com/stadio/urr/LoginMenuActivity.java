package com.stadio.urr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginMenuActivity extends AppCompatActivity {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private EditText emailEditText;
    private EditText passwordEditText;

    private ProgressBar progressBar;
    private TextView errorTextView;

    private String enteredEmail = "";
    private String enteredPassword = "";

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.credentials), Context.MODE_PRIVATE);;

        getReferences();

        ListenForEvents();

        checkLoggedIn();
    }

    private void checkLoggedIn() {
        enteredEmail = sharedPref.getString(getString(R.string.email), "");
        enteredPassword = sharedPref.getString(getString(R.string.password), "");

        assert enteredPassword != null;
        if (!enteredEmail.equals("") && !enteredPassword.equals("")) {
            sendLoginData(AccountDetails.socket);
        }

    }

    private void getReferences() {
        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);

        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);
    }

    public void ListenForEvents() {
        AccountDetails.socket.on("login-success", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                displayMessage(args[0].toString());

                AccountDetails.email = enteredEmail;

                saveCredentials();

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

    private void saveCredentials() {
        CheckBox remember = findViewById(R.id.remember_me);
        if (remember.isChecked()) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.email), enteredEmail);
            editor.putString(getString(R.string.password), enteredPassword);
            editor.commit();
        }
    }

    public void loginOnClick(View view) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            getLoginCredentials();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        sendLoginData(AccountDetails.socket);
    }

    private void getLoginCredentials() throws NoSuchAlgorithmException {
        enteredEmail = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(password.getBytes());
        enteredPassword = Utils.bytesToHex(digest.digest());
    }

    private void sendLoginData(Socket socket) {
        JSONObject emissionJson = new JSONObject();
        try {
            emissionJson.put("email", enteredEmail);
            emissionJson.put("password", enteredPassword);
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
        finish();
    }

    public void switchToRegisterMenu(View view) {
        Intent registerIntent = new Intent(getApplicationContext(), RegisterMenuActivity.class);
        startActivity(registerIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AccountDetails.disconnect(this);
    }
}