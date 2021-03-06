package com.stadio.urr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterMenuActivity extends AppCompatActivity {

    private TextView repeatPasswordErrorTextView;

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;

    private ProgressBar progressBar;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_menu);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getReferences();

        repeatPasswordEditText.addTextChangedListener( new RepeatPasswordTextWatched());

        ListenForEvents();
    }

    private void getReferences() {
        repeatPasswordErrorTextView = findViewById(R.id.repeatPasswordErrorTextView);

        usernameEditText = findViewById(R.id.loginEmailEditText);
        emailEditText = findViewById(R.id.registerEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
        repeatPasswordEditText = findViewById(R.id.registerRepeatPasswordEditText);

        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);
    }

    public void ListenForEvents() {
        AccountDetails.socket.on("register-success", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                displayMessage(args[0].toString());

                Intent loginIntent = new Intent(getApplicationContext(), LoginMenuActivity.class);
                startActivity(loginIntent);
            }
        });

        AccountDetails.socket.on("register-failed", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                displayMessage(args[0].toString());
            }
        });
    }

    public void registerOnClick(View view) {
        try {
            sendRegisterData();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
    }

    private void sendRegisterData() throws NoSuchAlgorithmException {
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(password.getBytes());
        password = Utils.bytesToHex(digest.digest());

        JSONObject emissionJson = new JSONObject();
        try {
            emissionJson.put("username", username);
            emissionJson.put("email", email);
            emissionJson.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AccountDetails.socket.emit("register", emissionJson);
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

    /* User Registration. */
    private class RepeatPasswordTextWatched implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String password = passwordEditText.getText().toString();
            String repeatPassword = repeatPasswordEditText.getText().toString();

            final int visibility = password.equals(repeatPassword) ? View.INVISIBLE : View.VISIBLE;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    repeatPasswordErrorTextView.setVisibility(visibility);
                }
            });
        }
    }
}