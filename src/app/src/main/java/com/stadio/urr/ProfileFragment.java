package com.stadio.urr;

import android.accounts.Account;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private ImageView profileImageView;
    private TextView usernameLabel;

    private TextView winsAmountLabel;
    private TextView lossesAmountLabel;

    private ProgressBar winsLossesRatioBar;


    private Activity currentActivity = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentActivity = getActivity();

        ListenForEvents();

        getReferences();

        if(AccountDetails.avatar == null) {
            AccountDetails.socket.emit("get-avatar");
        } else {
            updateProfileAvatar();
        }

        AccountDetails.socket.emit("get-wins-losses");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private void getReferences() {
        profileImageView = getView().findViewById(R.id.profileImageView);
        usernameLabel = getView().findViewById(R.id.usernameLabel);

        winsAmountLabel = getView().findViewById(R.id.winsAmountLabel);
        lossesAmountLabel = getView().findViewById(R.id.lossesAmountLabel);

        winsLossesRatioBar = getView().findViewById(R.id.winsLossesRatioBar);
    }

    public void ListenForEvents() {
        AccountDetails.socket.on("avatar-image", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String imageBase64 = args[0].toString().split(",")[1];

                AccountDetails.avatar = Utils.parseBitmapFromBase64(imageBase64);

                updateProfileAvatar();
            }
        });

        AccountDetails.socket.on("update-wins-losses", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = null;
                int wins = 0;
                int losses = 0;

                try {
                    obj = new JSONObject(args[0].toString());
                    wins = obj.getInt("wins");
                    losses = obj.getInt("losses");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                AccountDetails.wins = wins;
                AccountDetails.losses = losses;

                updateProfileWinsLosses();
            }
        });
    }

    private int calcWinPercentage() {
        return (int) ((float) AccountDetails.wins / (AccountDetails.wins + AccountDetails.losses) * 100);
    }


    private void updateProfileAvatar() {
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                profileImageView.setImageBitmap(AccountDetails.avatar);
                usernameLabel.setText(AccountDetails.username);
            }
        });
    }

    private void updateProfileWinsLosses() {
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                winsAmountLabel.setText(Integer.toString(AccountDetails.wins));
                lossesAmountLabel.setText(Integer.toString(AccountDetails.losses));

                winsLossesRatioBar.setProgress(calcWinPercentage());
            }
        });
    }
}