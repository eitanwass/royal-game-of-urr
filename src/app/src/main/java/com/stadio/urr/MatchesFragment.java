package com.stadio.urr;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MatchesFragment extends Fragment {
    AnimationDrawable queueAnimation;


    boolean in_queue = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListenForEvents();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.quickMatchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queueMatchOnClick(view);
            }
        });

        queueAnimation = (AnimationDrawable) ((ImageView) getActivity().findViewById(R.id.quick_join_icon)).getDrawable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_matches, container, false);
    }

    public void ListenForEvents() {
        AccountDetails.socket.on("joined-queue", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("", "Looking For Match...");
            }
        });

        AccountDetails.socket.on("found-match", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("", "Match!");
                queueAnimation.stop();
                joinMatch(args[0].toString());
            }
        });
    }

    private void joinMatch(String opponentInfo) {
        Bundle bundle = new Bundle();
        bundle.putString("opponentInfo", opponentInfo);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                queueAnimation.stop();
                ((TextView) getActivity().findViewById(R.id.quick_match_text_view)).setText(getString(R.string.quick_match));
                in_queue = false;
            }
        });

        Intent gameStartActivity = new Intent(getActivity(), GameActivity.class);
        gameStartActivity.putExtras(bundle);
        gameStartActivity.putExtra(getString(R.string.sound_effects), ((SwitchMaterial) getActivity().findViewById(R.id.sound_effect_toggle)).isChecked());
        startActivity(gameStartActivity);
    }

    private void cancelQueue() {
        AccountDetails.socket.emit("cancel-match");
        queueAnimation.stop();
        ((TextView) getActivity().findViewById(R.id.quick_match_text_view)).setText(getString(R.string.quick_match));
        in_queue = false;
    }


    public void queueMatchOnClick(View view) {
        if (!in_queue) {
            queueAnimation.start();
            ((TextView) getActivity().findViewById(R.id.quick_match_text_view)).setText(getString(R.string.cancel_queue));
            AccountDetails.socket.emit("quick-match");
            in_queue = true;
        } else {
            cancelQueue();
        }
    }
}
