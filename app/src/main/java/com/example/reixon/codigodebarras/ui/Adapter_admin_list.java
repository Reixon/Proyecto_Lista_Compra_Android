package com.example.reixon.codigodebarras.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reixon.codigodebarras.Model.SuperMercado;
import com.example.reixon.codigodebarras.R;
import com.example.reixon.codigodebarras.db.MySQL;

import java.util.ArrayList;

/**
 * Created by reixon on 10/02/2018.
 */

public class Adapter_admin_list extends BaseAdapter {

    private ViewHolder holder;
    private ArrayList<SuperMercado> arraySupers;
    private MySQL mysql;
    private Admin_list admin_list;

    public Adapter_admin_list(Context context, int textViewResourceId, ArrayList<SuperMercado>arraySupers) {
        admin_list= (Admin_list) context;
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
                LayoutInflater vi = (LayoutInflater) admin_list.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                holder = new Adapter_admin_list.ViewHolder();

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(admin_list);
                    builder.setTitle("Editar Lista ");

                    final EditText edit = new EditText(admin_list);
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
                            SQLiteDatabase db = mysql.getWritableDatabase();
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
                                SQLiteDatabase db = mysql.getWritableDatabase();
                                mysql.borrarListaCompra(arraySupers.get(position).getId(), db);
                                arraySupers.remove(position);
                                notifyDataSetChanged();
                            }
                            else{
                                Toast.makeText(admin_list,"Debe haber al menos una lista de compra",Toast.LENGTH_SHORT).show();
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
