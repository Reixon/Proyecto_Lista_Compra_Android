package com.example.reixon.codigodebarras;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Admin_list extends AppCompatActivity {

    private ListView listView;
    private Adapter adapterListAdmin;
    private ArrayList<String> nameSuperM;
    private ArrayList<SuperMerc> arraySupers;
    private SQLiteDatabase db;
    private MySQL mysql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        listView = (ListView) findViewById(R.id.list_superM_admin);

        if(getIntent().getExtras()!=null)
        {
           // nameSuperM= (ArrayList<String>)getIntent().getExtras().get("Name_Supers");
            arraySupers = (ArrayList<SuperMerc>) getIntent().getExtras().getSerializable("Lista Supers");
        }
        //Pasar solo los nombres de los superM
        adapterListAdmin = new Adapter(this,R.layout.list_adapter_adm,arraySupers);
        listView.setAdapter(adapterListAdmin);
    }

    public class Adapter extends BaseAdapter {

        private ViewHolder holder;
        private ArrayList<SuperMerc>arraySupers;

        public Adapter(Context context,int textViewResourceId, ArrayList<SuperMerc>arraySupers) {

            mysql = new MySQL(context);
            this.arraySupers = arraySupers;
            holder = null;
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
                                mysql.modificarListaCompra(edit.getText().toString(),"nombre",arraySupers.get(position).getId(),db);
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
                                db = mysql.getWritableDatabase();
                                mysql.borrarListaCompra(arraySupers.get(position).getId(),db);
                                arraySupers.remove(position);
                                notifyDataSetChanged();
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
