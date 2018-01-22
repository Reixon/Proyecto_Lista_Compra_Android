package com.example.reixon.codigodebarras;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Shared_option_list extends AppCompatActivity {

    private ListView list;
    private Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_option_list);

        list = (ListView)findViewById(R.id.list_shared_option);
        String [] listOptions = {"Añadir cuenta","Gestionar cuentas","Seleccionar cuenta","Cambiar contraseña","Sincronizar"};
        int [] images = {R.drawable.user_add, R.drawable.setting};

        adapter = new Adapter(this, R.layout.adapter_shared_option_list,listOptions, images);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent i = new Intent(Shared_option_list.this, NewAcount.class);
                        startActivity(i);
                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                }
            }
        });
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    holder = new ViewHolder();

                    convertView = vi.inflate(R.layout.adapter_shared_option_list, null);
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
