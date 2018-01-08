package com.example.reixon.codigodebarras;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by reixon on 29/10/2017.
 */

public class AdapterListBuyProd extends BaseAdapter {
    private ArrayList<Producto> searchList;
    private ViewHolder holder;
    private List<Producto> proList;
    private MySQL mysql;
    private lista_compra context;
    private SQLiteDatabase db;
    private SuperMerc sp;
    private boolean [] itemChecks;
    private int numChecks;
    private boolean checkAll;

    public AdapterListBuyProd(Context context,
            int textViewResourceId, ArrayList<Producto> listaProductosDada) {
        this.context = (lista_compra) context;
        proList = listaProductosDada;
        searchList = listaProductosDada;
        //ArrayCheck = new CheckProduct[proList.size()];
        //numSelectCheck=0;
        mysql = new MySQL(context);
        sp = this.context.getSuperMerc();
    }

    /*public void vaciarArrayCheck(){
        //ArrayCheck=new CheckProduct[proList.size()];
        //numSelectCheck=0;
        this.notifyDataSetChanged();
    }*/

    public void setList(ArrayList<Producto> listP){
        this.proList=listP;
        this.searchList=listP;
    }

    public void setCheckAll(){
        if(checkAll){
            checkAll=false;
            numChecks=0;
            context.setVisibleDelete(false);
        }
        else{
            checkAll=true;
            numChecks=proList.size();

        }
        for(int i=0; i<itemChecks.length;i++){
            itemChecks[i]=checkAll;
        }
        notifyDataSetChanged();
    }

    /*public void deleteProductCheck(){
        int posSig;
        String nombres="";
        for(int i=0; i<searchList.size(); i++){
            if(searchList.get(i).isCheck()) {
                db = mysql.getWritableDatabase();
                mysql.eliminar_Producto_D_SuperMerc_Producto(searchList.get(i).getId(), sp.getId(), db);
                nombres = nombres + "\n" + searchList.get(i).getNombre()+"";
                sp.eliminarProducto(i);
            }
        }
        proList = sp.getProductos();
        searchList = sp.getProductos();
        Toast.makeText(context, nombres + " eliminados", Toast.LENGTH_SHORT).show();
        context.getDelete().setVisible(false);

        notifyDataSetChanged();
    }*/

    private class ViewHolder {
        TextView name;
        ImageView imagen;
        TextView precio;
        TextView cantidad;
        CheckBox check;
        TextView et_category;
    }


    @Override
    public int getCount() {
        return proList.size();
    }

    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        searchList.clear();
        if(charText.length() == 0){
            proList.addAll(searchList);
        }else
        {
            for(Producto postDetail : searchList){
                if(charText.length()!=0 && postDetail.getNombre().toLowerCase(
                        Locale.getDefault()).contains(charText))
                {
                    proList.add(postDetail);
                    //encuentra=false;
                }
            }
            db = mysql.getWritableDatabase();
            Producto p =mysql.searchProductoWithName(charText,db);
            if(p!=null){

                //encuentra=true;
            }

        }
        //   Log.d("MyApp","notificacion");
        notifyDataSetChanged();
    }

   public void setSuper(SuperMerc sm){
       this.sp = sm;
       this.proList = sp.getProductos();
       this.searchList = sp.getProductos();
       this.notifyDataSetChanged();
   }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null)
            {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                holder = new ViewHolder();

                convertView = vi.inflate(R.layout.stock_product_adapter, null);
                holder.imagen = (ImageView) convertView
                        .findViewById(R.id.imagenListProductos);
                holder.name = (TextView) convertView.findViewById(R.id.nombre);
                holder.precio = (TextView) convertView.findViewById(R.id.txt_precio_prod);
                holder.check = (CheckBox) convertView.findViewById(R.id.etcheckBox);
                holder.cantidad = (TextView) convertView.findViewById(R.id.txtCantidad);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
        /*    if(proList.get(position).getCategoria()!=-1) {

            }
            else
            {*/

                Producto p = this.proList.get(position);
                holder.name.setText(p.getNombre() + " ");
                if(p.isCheck()){
                    holder.check.setChecked(true);
                }
                else{
                    holder.check.setChecked(false);

                }
                holder.check.setTag(position);
                double precioXCant = 0;
                if (p.getCantidad() > 0) {
                    precioXCant = p.getPrecio() * p.getCantidad();
                }
                holder.precio.setText(precioXCant + " ");
                if (p.getRutaImagen().equals("")) {
                    holder.imagen.setImageDrawable(context.getResources().getDrawable(R.drawable.photo_icon));
                }
                holder.cantidad.setText(" (" + proList.get(position).getCantidad() + ")");

                holder.check.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        CheckBox cb = (CheckBox) v;
                        int pos=(int) v.getTag();

                        int x=0;
                        if (cb.isChecked()) {
                            proList.get(pos).setCheck(true);
                            x=1;
                            holder.name.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                        } else {
                            holder.name.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                            proList.get(pos).setCheck(false);
                            x=0;
                        }
                        db = mysql.getWritableDatabase();
                        mysql.modificarSuperMerc_Productos(x+"","checked",proList.get(pos),sp,db);
                        notifyDataSetChanged();
                    }
                });
            //}
        }
        catch (Exception e){  e.printStackTrace();}
        return convertView;
    }

}

