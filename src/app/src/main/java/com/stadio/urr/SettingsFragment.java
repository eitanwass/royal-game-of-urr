package com.stadio.urr;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private SwitchMaterial soundEffectToggle;

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
        setSwitches();
        soundEffectToggle.setOnCheckedChangeListener(this);
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
}