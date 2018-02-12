package com.example.reixon.codigodebarras.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reixon.codigodebarras.Model.UserAccount;
import com.example.reixon.codigodebarras.R;
import com.example.reixon.codigodebarras.db.MySQL;
import com.example.reixon.codigodebarras.sync.AccountAuthenticator;

import java.util.ArrayList;


public class Account_manage extends AppCompatActivity {

    private SQLiteDatabase db;
    private MySQL mysql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_account_manage);

        ListView listView = (ListView) findViewById(R.id.list_account_manage);
        AccountManager mAccountManager = AccountManager.get(this);

        /*GET INTENT*/
        ArrayList<UserAccount>userAccounts=null;
        if(getIntent().getExtras()!=null)
        {
            userAccounts = (ArrayList<UserAccount>) getIntent().getExtras().getSerializable("Array Users");
        }
        //Pasar solo los nombres de los superM
        Adapter adapterAccountManage = new Adapter(this,R.layout.list_adapter_adm,userAccounts);
        listView.setAdapter(adapterAccountManage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_account_manage);
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

    }

    public class Adapter extends BaseAdapter {

        private ViewHolder holder;
        private ArrayList<UserAccount> mArrayUserAccounts;

        public Adapter(Context context, int textViewResourceId, ArrayList<UserAccount>arrayUserAccount) {

            mysql = new MySQL(context);
            this.mArrayUserAccounts = arrayUserAccount;
            holder = null;
        }

        public void setmArrayUserAccounts(ArrayList arrayUserAccount){
            this.mArrayUserAccounts =arrayUserAccount;
        }

        @Override
        public int getCount() {
            return this.mArrayUserAccounts.size();
        }

        @Override
        public Object getItem(int position) {
            return this.mArrayUserAccounts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return this.mArrayUserAccounts.get(position).getId_user();
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
                    holder = new ViewHolder();

                    convertView = vi.inflate(R.layout.list_adapter_adm, null);
                    holder.name = (TextView)convertView.findViewById(R.id.name_list_admin);
                    holder.bt_edit = (ImageButton)convertView.findViewById(R.id.bt_edit_list_admin);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.name.setText(mArrayUserAccounts.get(position).getName());
                holder.bt_edit.setTag(position);

                holder.bt_edit.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // final int pos = (Integer) v.getTag();
                        AlertDialog.Builder builder = new AlertDialog.Builder(Account_manage.this);
                        builder.setTitle("Eliminar Cuenta ");

                        final EditText edit = new EditText(Account_manage.this);
                        edit.setText(mArrayUserAccounts.get(position).getName());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        edit.setLayoutParams(lp);
                        builder.setView(edit);


                        builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(mArrayUserAccounts.size()>1) {
                                    db = mysql.getWritableDatabase();
                                    mysql.eliminarUsuario(mArrayUserAccounts.get(position).getId_user(),db);
                                    mArrayUserAccounts.remove(position);
                                    AccountManager mAccountManager = AccountManager.get(Account_manage.this);
                                    Account[] account = mAccountManager.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);
                                    mAccountManager.removeAccount(account[0],Account_manage.this,null,null);
                                    notifyDataSetChanged();
                                }
                                else{
                                    Toast.makeText(Account_manage.this,"Debe haber al menos una cuenta",Toast.LENGTH_SHORT).show();
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
