package com.example.reixon.codigodebarras.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.reixon.codigodebarras.db.MySQL;
import com.example.reixon.codigodebarras.Class.Producto;
import com.example.reixon.codigodebarras.Class.SuperMerc;
import com.example.reixon.codigodebarras.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
            int textViewResourceId, ArrayList<Producto> listaProductosDada,SuperMerc sp) {
        this.context = (lista_compra) context;
        proList = listaProductosDada;
        searchList = listaProductosDada;
        itemChecks = new boolean[listaProductosDada.size()];
        numChecks=0;
        mysql = new MySQL(context);
        this.sp = sp;
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
           // context.setVisibleDelete(false);
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

    private class ViewHolder {
        TextView name;
        ImageView imagen;
        TextView precio;
        TextView cantidad;
        CheckBox check;
    }


    @Override
    public int getCount() {
        return proList.size();
    }

   public void setSuper(SuperMerc sm){
       this.sp = sm;
       this.proList = new ArrayList<>(sp.getProductos());
       this.searchList = new ArrayList<>(sp.getProductos());
       itemChecks = new boolean[proList.size()];
       numChecks=0;
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
                Producto p = this.proList.get(position);
                holder.name.setText(p.getNombre() + " ");
                holder.check.setChecked(itemChecks[position]);
                if(p.getRutaImagen().equals("")) {
                    holder.imagen.setImageDrawable(context.getResources().getDrawable(R.drawable.photo_icon));
                }
                else{
                    //creamos un hilo para que cargue las imagenes
                    new loadImage(holder.imagen).execute(p.getRutaImagen());
                }
                holder.check.setTag(position);
                double precioXCant = 0;
                if (p.getCantidad() > 0) {
                    precioXCant = p.getPrecio() * p.getCantidad();
                }
                holder.precio.setText(precioXCant + " ");
                holder.cantidad.setText(" (" + proList.get(position).getCantidad() + ")");

                holder.check.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    if(cb.isChecked()) {
                        itemChecks[Integer.valueOf(position)]=cb.isChecked();
                        numChecks++;
                        if(numChecks>0) {
                           // context.setVisibleDelete(true);
                        }
                    }
                    else {
                        itemChecks[Integer.valueOf(position)]=cb.isChecked();
                        numChecks--;
                        if(numChecks==0){
                            checkAll=false;
                            //context.setVisibleDelete(false);
                        }
                    }
                    cb.setChecked(itemChecks[position]);
                    }
                });
            //}
        }
        catch (Exception e){  e.printStackTrace();}
        return convertView;
    }

    private class loadImage extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageRef;
        private loadImage(ImageView imageRef) {
            this.imageRef = new WeakReference<ImageView>(imageRef);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            InputStream ims = null;
            Bitmap image =null;
            try {
                ims = new FileInputStream(params[0]);
                image = BitmapFactory.decodeStream(ims);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return image;
        }

        protected void onPostExecute(Bitmap image){
            ImageView imageView = imageRef.get();
            imageView.setImageBitmap(image);
        }
    }

}

