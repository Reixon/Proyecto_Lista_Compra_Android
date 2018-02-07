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

import com.example.reixon.codigodebarras.Model.Category;
import com.example.reixon.codigodebarras.R;
import com.example.reixon.codigodebarras.db.MySQL;

import java.util.ArrayList;

public class Admin_Category extends AppCompatActivity {

    private ListView listView;
    private Adapter adapterListCategories;
    private ArrayList<String> nameCategories;
    private ArrayList<Category> arrayCategories;
    private SQLiteDatabase db;
    private MySQL mysql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_list_categories);

        listView = (ListView) findViewById(R.id.admin_list_categories);

        if (getIntent().getExtras() != null) {
            arrayCategories = (ArrayList<Category>) getIntent().getExtras().getSerializable("Array Categories");
            arrayCategories.remove(0);
        }

        //Pasar solo los nombres de los superM
        adapterListCategories = new Adapter(this, R.layout.list_adapter_adm, arrayCategories);
        listView.setAdapter(adapterListCategories);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_list_categories);
        toolbar.setTitle("Categorias");
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Admin_Category.this);
                builder.setTitle("Añadir categoria");

                final EditText edit = new EditText(Admin_Category.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                edit.setLayoutParams(lp);
                builder.setView(edit);

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db = mysql.getWritableDatabase();
                        Category category = new Category(edit.getText().toString());
                        mysql.addCategoria(edit.getText().toString(),db);
                        arrayCategories.add(category);
                        adapterListCategories.setmArrayCategories(arrayCategories);
                        adapterListCategories.notifyDataSetChanged();
                        setResult(RESULT_OK);

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
        private ArrayList<Category> mArrayCategories;

        public Adapter(Context context, int textViewResourceId, ArrayList<Category> arrayCategories) {

            mysql = new MySQL(context);
            this.mArrayCategories = arrayCategories;
            holder = null;
        }

        public void setmArrayCategories(ArrayList supers) {
            this.mArrayCategories = supers;
        }

        @Override
        public int getCount() {
            return this.mArrayCategories.size();
        }

        @Override
        public Object getItem(int position) {
            return this.mArrayCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return this.mArrayCategories.get(position).getId();
        }

        private class ViewHolder {
            TextView name;
            ImageButton bt_edit;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    holder = new Adapter.ViewHolder();

                    convertView = vi.inflate(R.layout.list_adapter_adm, null);
                    holder.name = (TextView) convertView.findViewById(R.id.name_list_admin);
                    holder.bt_edit = (ImageButton) convertView.findViewById(R.id.bt_edit_list_admin);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                    holder.name.setText(mArrayCategories.get(position).getNombre());
                    holder.bt_edit.setTag(position);

                holder.bt_edit.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // final int pos = (Integer) v.getTag();
                        AlertDialog.Builder builder = new AlertDialog.Builder(Admin_Category.this);
                        builder.setTitle("Editar Categoria ");

                        final EditText edit = new EditText(Admin_Category.this);
                        edit.setText(mArrayCategories.get(position).getNombre());
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
                                mysql.editCategory(edit.getText().toString(),mArrayCategories.get(position).getId(),db);
                                //mysql.editListShop(edit.getText().toString(), "nombre", mArrayCategories.get(position).getId(), db);
                                mArrayCategories.get(position).setNombre(edit.getText().toString());
                                notifyDataSetChanged();
                                setResult(RESULT_OK);
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
                                mysql.deleteCategory(mArrayCategories.get(position).getId(), db);
                                mArrayCategories.remove(position);
                                notifyDataSetChanged();
                                setResult(RESULT_OK);
                            }
                        });

                        builder.show();
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

    }
}
