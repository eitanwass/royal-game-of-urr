package com.stadio.urr;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListenForEvents();

        getReferences();

        if(AccountDetails.avatar == null) {
            JSONObject emissionJson = new JSONObject();
            try {
                emissionJson.put("email", AccountDetails.email);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AccountDetails.socket.emit("get-avatar", emissionJson);
            Log.d("", "Requested avatar");
        } else {
            setProfile();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private void getReferences() {
        profileImageView = getView().findViewById(R.id.profileImageView);
    }

    public void ListenForEvents() {
        AccountDetails.socket.on("avatar-image", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String imageBase64 = args[0].toString().split(",")[1];
                byte[] imageBytes = Base64.decode(imageBase64, Base64.DEFAULT);

                AccountDetails.avatar = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                setProfile();
            }
        });
    }


    private void setProfile() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                profileImageView.setImageBitmap(AccountDetails.avatar);
            }
        });
    }
}