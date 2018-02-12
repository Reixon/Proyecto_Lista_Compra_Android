package com.example.reixon.codigodebarras.ui;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by reixon on 11/02/2018.
 */

public class PreferenciasActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenciasFragment()).commit();
    }
}
