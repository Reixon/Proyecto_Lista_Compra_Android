package com.example.reixon.myshoppingcart.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.reixon.myshoppingcart.Model.Principal;
import com.example.reixon.myshoppingcart.R;

import java.util.ArrayList;

/**
 * Created by reixon on 10/02/2018.
 */

public class AdapterSpinnerShoppingList extends ArrayAdapter {

    private ViewHolder holder;
    private ArrayList<String> listasCompra;
    private Principal principal;
    public AdapterSpinnerShoppingList(@NonNull Context context, ArrayList<String> listasCompra, Principal p) {
        super(context,0,listasCompra);
        this.listasCompra=listasCompra;
        this.principal=p;

    }

    public void setDatos(Principal p){
        principal=p;
    }

    private class ViewHolder {
        TextView name;
        ImageView imagen;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent){
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_style_online,parent,false);
            holder.name = (TextView) convertView.findViewById(R.id.txt_name_spinner);
        //    holder.imagen = (ImageView) convertView.findViewById(R.id.icon_online_spinner);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(listasCompra.get(position));
    //    holder.imagen.setImageResource(R.drawable.ic_online);
     /*   if(principal.isListaOnline(position)){
            holder.imagen.setVisibility(View.VISIBLE);
        }
        else{
            holder.imagen.setVisibility(View.GONE);
        }*/
        return convertView;
    }
}
