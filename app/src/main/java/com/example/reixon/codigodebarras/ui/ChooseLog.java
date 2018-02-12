package com.example.reixon.codigodebarras.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.reixon.codigodebarras.R;

public class ChooseLog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_new_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_account);
        toolbar.setTitle("Nueva Cuenta");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btNewAccount = (Button)findViewById(R.id.button_new);
        Button btExist = (Button)findViewById(R.id.button_exist);

        btNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseLog.this, CreateUser.class);
                startActivity(i);
            }
        });

        btExist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseLog.this, Login.class);
                startActivity(i);
            }
        });
    }
}
