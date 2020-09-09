package com.stadio.urr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private SwitchMaterial soundEffectToggle;
    private LinearLayout linearLayout;
    private CardView logOutButton;
    private CardView confirmButton;
    private CardView cancelButton;
    private LinearLayout yesNoLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    SharedPreferences sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        sharedPref = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        soundEffectToggle = getActivity().findViewById(R.id.sound_effect_toggle);
        linearLayout = getActivity().findViewById(R.id.scrollViewLinearLayout);
        logOutButton = getActivity().findViewById(R.id.logOutButton);
        yesNoLayout = getActivity().findViewById(R.id.yesNoLayout);
        confirmButton = getActivity().findViewById(R.id.confirmButton);
        cancelButton = getActivity().findViewById(R.id.cancelButton);
        linearLayout.removeView(yesNoLayout);
        setSwitches();
        soundEffectToggle.setOnCheckedChangeListener(this);
        logOutButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    private void setSwitches() {
        boolean sound = sharedPref.getBoolean(getString(R.string.sound_effects), true);
        soundEffectToggle.setChecked(sound);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.sound_effect_toggle:
                commitToSharedPreferences(getString(R.string.sound_effects), b);
                break;
        }
    }

    public void commitToSharedPreferences(String label, boolean bool) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(label, bool);
        editor.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logOutButton:
                logOut();
                break;
            case R.id.confirmButton:
                confirmLogOut();
                break;
            case R.id.cancelButton:
                cancelLogOut();
        }
    }

    private void cancelLogOut() {
        linearLayout.removeView(yesNoLayout);
        linearLayout.addView(logOutButton);
    }

    private void confirmLogOut() {
        Context context = getActivity();
        SharedPreferences sharedPreferencesCredentials = context.getSharedPreferences(
                getString(R.string.credentials), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesCredentials.edit();
        editor.remove(getString(R.string.email));
        editor.remove(getString(R.string.password));
        editor.apply();
        startActivity(new Intent(getActivity(), LoginMenuActivity.class));
        getActivity().finish();
    }

    public void logOut() {
        linearLayout.removeView(logOutButton);
        linearLayout.addView(yesNoLayout);
    }
}