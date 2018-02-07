package com.example.reixon.codigodebarras.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.reixon.codigodebarras.Model.UserAccount;
import com.example.reixon.codigodebarras.R;
import com.example.reixon.codigodebarras.sync.AccountAuthenticator;

import java.util.ArrayList;

public class Admin_account extends AppCompatActivity {


    private Adapter adapter;
    private ListView list;
    private ArrayList<UserAccount> userAccounts;
    private AccountManager mAccountManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_admin_account);

        list = findViewById(R.id.list_admin_account);
        String [] listOptions = {"Añadir cuenta","Gestionar cuentas","Seleccionar cuenta","Cambiar contraseña"};
        int [] images = {R.drawable.user_add, R.drawable.setting, R.drawable.ic_account_circle_48px, R.drawable.ic_key_48px};

        adapter = new Adapter(this, R.layout.adapter_admin_account,listOptions, images);
        list.setAdapter(adapter);
        AccountManager mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_shared_option_list);
        toolbar.setTitle("Cuentas");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(getIntent().getExtras()!=null)
        {
            userAccounts = (ArrayList<UserAccount>) getIntent().getExtras().getSerializable("Array Users");
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i;
                Bundle b = new Bundle();
                switch (position){
                    case 0:
                        //i = new Intent(Admin_account.this, NewAcount.class);
                        //startActivity(i);
                        break;
                    case 1:
                        i = new Intent(Admin_account.this, Account_manage.class);
                        b.putSerializable("Array Users",userAccounts);
                        i.putExtras(b);
                        startActivity(i);
                        break;
                    case 2:
                        //Seleccionar cuenta
                        ArrayList<String> userAccountsString = new ArrayList<String>();
                        for(int x =0; x<userAccounts.size();x++){
                            userAccountsString.add(userAccounts.get(x).getEmail());
                        }
                        selecctAccount(userAccountsString);
                        break;
                    case 3:

                        break;
                }
            }
        });
    }

    public void selecctAccount(ArrayList<String>userAccountsString){
        AlertDialog.Builder builder = new AlertDialog.Builder(Admin_account.this);
        builder.setTitle("Seleccionar Cuenta ");

       /* builder.setSingleChoiceItems(userAccountsString, ,new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {



            }
        });
        builder.create();
        builder.show();




        builder.show();*/

    }

    public class Adapter extends BaseAdapter {

        private ViewHolder holder;
        private String [] listOptions;
        private int [] images;

        public Adapter(Context context, int textViewResourceId, String[] listOptions, int [] images) {

          //  mysql = new MySQL(context);
            this.listOptions = listOptions;
            this.images = images;
            holder = null;
        }


        @Override
        public int getCount() {
            return listOptions.length;
        }

        @Override
        public Object getItem(int position) {
            return listOptions[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        private class ViewHolder {
            ImageView imageView;
            TextView name;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            if(userAccounts.size()==0){
                return position == 0;
            }
            else{
                return true;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    holder = new ViewHolder();

                    convertView = vi.inflate(R.layout.adapter_admin_account, null);
                    holder.imageView = (ImageView) convertView.findViewById(R.id.image_shared_option_list);
                    holder.name = (TextView) convertView.findViewById(R.id.textView_shared_option);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.name.setText(listOptions[position]);
                holder.imageView.setImageResource(images[position]);
                holder.imageView.setTag(position);

            }
            catch (Exception e){
                e.printStackTrace();
            }
            return convertView;
        }
    }
}
