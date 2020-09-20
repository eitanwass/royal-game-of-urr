package com.stadio.urr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class EndGameActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView userCrown;
    ImageView opponentCrown;

    ImageView userAvatar;
    ImageView opponentAvatar;
    TextView userName;
    TextView opponentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getReferences();
        setCrown();

        userAvatar.setImageBitmap(AccountDetails.avatar);
        userName.setText(AccountDetails.username);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            JSONObject obj = null;

            try {
                obj = new JSONObject(bundle.getString("opponentInfo"));
                opponentName.setText(obj.getString("username"));
                String imageBase64 = obj.getString("avatar");
                opponentAvatar.setImageBitmap(Utils.parseBitmapFromBase64(imageBase64));
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void setCrown() {
        if  (getIntent().getExtras().getBoolean("did_win")) {
            userCrown.setVisibility(View.VISIBLE);
        } else {
            opponentCrown.setVisibility(View.VISIBLE);
        }
    }

    private void getReferences() {
        userCrown = findViewById(R.id.crownUser);
        opponentCrown = findViewById(R.id.crownOpponent);

        userAvatar = findViewById(R.id.userAvatarEnd);
        opponentAvatar = findViewById(R.id.opponentAvatarEnd);
        userName = findViewById(R.id.userNameEnd);
        opponentName = findViewById(R.id.opponentNameEnd);

        findViewById(R.id.home).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home:
                finish();
        }
    }
}