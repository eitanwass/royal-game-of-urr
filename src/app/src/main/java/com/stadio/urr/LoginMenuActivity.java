package com.stadio.urr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginMenuActivity extends AppCompatActivity {

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
        Button loginButton = (Button) view;

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
    }
}