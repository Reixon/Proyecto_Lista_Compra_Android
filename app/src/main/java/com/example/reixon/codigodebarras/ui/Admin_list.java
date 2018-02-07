package com.example.reixon.codigodebarras.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reixon.codigodebarras.Model.SuperMercado;
import com.example.reixon.codigodebarras.db.MySQL;
import com.example.reixon.codigodebarras.R;

import java.util.ArrayList;

public class Admin_list extends AppCompatActivity {

    private ListView listView;
    private Adapter adapterListAdmin;
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
        adapterListAdmin = new Adapter(this,R.layout.list_adapter_adm,arraySupers);
        listView.setAdapter(adapterListAdmin);

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
                        nameSuperM.add(lS.getNombre());
                        adapterListAdmin.setArraySupers(arraySupers);
                        adapterListAdmin.notifyDataSetChanged();

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



    public class Adapter extends BaseAdapter {

        private ViewHolder holder;
        private ArrayList<SuperMercado>arraySupers;

        public Adapter(Context context,int textViewResourceId, ArrayList<SuperMercado>arraySupers) {

            mysql = new MySQL(context);
            this.arraySupers = arraySupers;
            holder = null;
        }

        public void setArraySupers(ArrayList supers){
            this.arraySupers=supers;
        }

        @Override
        public int getCount() {
            return this.arraySupers.size();
        }

        @Override
        public Object getItem(int position) {
            return this.arraySupers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return this.arraySupers.get(position).getId();
        }

        private class ViewHolder {
            TextView name;
            ImageButton bt_edit;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null)
                {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    holder = new Adapter.ViewHolder();

                    convertView = vi.inflate(R.layout.list_adapter_adm, null);
                    holder.name = (TextView)convertView.findViewById(R.id.name_list_admin);
                    holder.bt_edit = (ImageButton)convertView.findViewById(R.id.bt_edit_list_admin);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.name.setText(arraySupers.get(position).getNombre());
                holder.bt_edit.setTag(position);

                holder.bt_edit.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                       // final int pos = (Integer) v.getTag();
                        AlertDialog.Builder builder = new AlertDialog.Builder(Admin_list.this);
                        builder.setTitle("Editar Lista ");

                        final EditText edit = new EditText(Admin_list.this);
                        edit.setText(arraySupers.get(position).getNombre());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        edit.setLayoutParams(lp);
                        builder.setView(edit);

                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db = mysql.getWritableDatabase();
                                mysql.editListShop(edit.getText().toString(),"nombre",arraySupers.get(position).getId(),db);
                                arraySupers.get(position).setNombre(edit.getText().toString());
                                notifyDataSetChanged();
                            }
                        });

                        builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(arraySupers.size()>1) {
                                    db = mysql.getWritableDatabase();
                                    mysql.borrarListaCompra(arraySupers.get(position).getId(), db);
                                    arraySupers.remove(position);
                                    notifyDataSetChanged();
                                }
                                else{
                                    Toast.makeText(Admin_list.this,"Debe haber al menos una lista de compra",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        builder.show();
                    }
                });

            }
            catch (Exception e){
                e.printStackTrace();
            }
            return convertView;
        }
    }
}
