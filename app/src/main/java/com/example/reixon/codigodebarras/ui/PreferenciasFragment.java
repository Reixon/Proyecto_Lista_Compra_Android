package com.example.reixon.codigodebarras.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.reixon.codigodebarras.R;

/**
 * Created by reixon on 11/02/2018.
 */


public class PreferenciasFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}
