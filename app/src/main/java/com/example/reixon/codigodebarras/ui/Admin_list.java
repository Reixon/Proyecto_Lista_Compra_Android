package com.example.reixon.codigodebarras.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.reixon.codigodebarras.Model.SuperMercado;
import com.example.reixon.codigodebarras.db.MySQL;
import com.example.reixon.codigodebarras.R;

import java.util.ArrayList;

public class Admin_list extends AppCompatActivity {

    private ListView listView;
    private Adapter_admin_list adapterAdminListListAdmin;
    private ArrayList<String> nameSuperM;
    private ArrayList<SuperMercado> arraySupers;
    private SQLiteDatabase db;
    private MySQL mysql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_admin_list_superm);

        listView = (ListView) findViewById(R.id.list_admin_superm);

        if(getIntent().getExtras()!=null)
        {
            arraySupers = (ArrayList<SuperMercado>) getIntent().getExtras().getSerializable("Lista Supers");
        }
        //Pasar solo los nombres de los superM
        adapterAdminListListAdmin = new Adapter_admin_list(this,R.layout.list_adapter_adm,arraySupers);
        listView.setAdapter(adapterAdminListListAdmin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_admin_list_superm);
        toolbar.setTitle("Listas");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button buttonCreate = (Button) findViewById(R.id.add_bt_category_to_list_categories);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Admin_list.this);
                builder.setTitle("Añadir lista de compra");

                final EditText edit = new EditText(Admin_list.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                edit.setLayoutParams(lp);
                builder.setView(edit);

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db = mysql.getWritableDatabase();
                        SuperMercado lS = new SuperMercado(edit.getText().toString(), 0, 0);
                        mysql.addSuper(db, lS);
                        arraySupers.add(lS);
                        adapterAdminListListAdmin.setArraySupers(arraySupers);
                        adapterAdminListListAdmin.notifyDataSetChanged();

                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

    }

}
