package com.stadio.urr;

import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private Bitmap avatar;

    private ImageView profileImageView;

    private Socket mSocket;
    {
        try {
            this.mSocket = IO.socket("https://urr-server.herokuapp.com/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListenForEvents();

        getReferences();

        loadAvatarBitmap();

        if(avatar == null) {
            mSocket.emit("get-avatar");
        } else {
            profileImageView.setImageBitmap(avatar);
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
        mSocket.on("avatar-image", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String imageBase64 = args[0].toString().split(",")[1];
                byte[] imageBytes = Base64.decode(imageBase64, Base64.DEFAULT);

                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                avatar = bitmap;
                profileImageView.setImageBitmap(avatar);

                ContextWrapper wrapper = new ContextWrapper(getActivity());
                File file = wrapper.getDir("Images",MODE_PRIVATE);
                file = new File(file, "UserAvatar.png");

                try {
                    OutputStream stream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                    stream.flush();
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("", "Got and saved avatar");
            }
        });
    }

    private void loadAvatarBitmap() {
        ContextWrapper wrapper = new ContextWrapper(getActivity().getApplicationContext());
        File file = wrapper.getDir("Images",MODE_PRIVATE);
        file = new File(file, "UserAvatar.png");
        avatar = BitmapFactory.decodeFile(file.getAbsolutePath());

        Log.d("", "Got and loaded avatar");
    }
}