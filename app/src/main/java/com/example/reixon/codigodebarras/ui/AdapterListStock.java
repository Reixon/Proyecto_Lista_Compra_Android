package com.example.reixon.codigodebarras.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reixon.codigodebarras.Model.Producto;
import com.example.reixon.codigodebarras.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by reixon on 18/12/2017.
 */


 class AdapterListStock extends BaseAdapter {

    private ViewHolder holder = null;
    private List<Producto> proList;
    private Lista_productos lista_productos;
    private boolean [] itemChecks;
    private int numChecks;
    private boolean checkAll,checkState;

    public AdapterListStock(Context context, int textViewResourceId,
                            ArrayList<Producto> listaProductosDada) {
        proList = listaProductosDada;
        this.lista_productos = (Lista_productos) context;
        itemChecks = new boolean[listaProductosDada.size()];
        numChecks=0;
        checkState=false;
        checkAll=false;
    }

    public void setList(ArrayList<Producto> proTotal){
        proList = proTotal;
        itemChecks = new boolean[proTotal.size()];
        notifyDataSetChanged();
    }

    public boolean[] getItemCheck(){
        return itemChecks;
    }

    public void vaciarArrayCheck(){
        itemChecks=new boolean[proList.size()];
        numChecks=0;
        this.notifyDataSetChanged();
    }


    public void updateList(ArrayList<Producto> productos){
        proList = productos;
        itemChecks = new boolean[proList.size()];
        this.notifyDataSetChanged();
    }

    public boolean getCheckAll(){
        return checkAll;
    }

    public boolean getStateCheckMenu(){
        return checkState;
    }


    public void setCheckAll(){
        if(checkAll){
            checkAll=false;
            numChecks=0;
            lista_productos.setVisibleMenusActive(false);
        }
        else{
            checkAll=true;
            numChecks=proList.size();
            lista_productos.setVisibleMenusActive(true);

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
        CheckBox check;
        TextView cantidad;
    }



    @Override
    public int getCount() {
        return proList.size();
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
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) lista_productos.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = vi.inflate(R.layout.stock_product_adapter, null);
                holder = new ViewHolder();
                holder.imagen = (ImageView) convertView
                        .findViewById(R.id.imagenListProductos);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.precio = (TextView) convertView.findViewById(R.id.txt_precio_prod);
                holder.check = (CheckBox) convertView.findViewById(R.id.etcheckBox);
                holder.cantidad =(TextView)convertView.findViewById(R.id.txtCantidad);
                holder.cantidad.setVisibility(View.GONE);
                convertView.setTag(holder);

            } else {
                Log.d("MyApp", " View no null");
                holder = (ViewHolder) convertView.getTag();
            }
            Producto p = this.proList.get(position);
            holder.name.setText(p.getName() + " ");
            holder.precio.setText(p.getPrice() + " ");
            holder.check.setChecked(itemChecks[position]);
            holder.check.setTag(position);



            if(p.getImagePath().equals("")) {
                holder.imagen.setImageDrawable(lista_productos.getResources().getDrawable(R.drawable.photo_icon));
            }
            else{
                //creamos un hilo para que cargue las imagenes
                new loadImage(holder.imagen).execute(p.getImagePath());
            }
            holder.check.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    CheckBox cb = (CheckBox) v;
                    if(cb.isChecked()) {
                        itemChecks[Integer.valueOf(position)]=cb.isChecked();
                        numChecks++;
                        Toast.makeText(lista_productos, proList.get(position).getName(),Toast.LENGTH_SHORT);

                        if(numChecks>0) {
                            checkState=false;
                            lista_productos.setVisibleMenusActive(true);
                        }
                    }
                    else {
                        itemChecks[Integer.valueOf(position)]=cb.isChecked();
                        if(numChecks==proList.size()){
                            checkState=false;
                            checkAll=false;
                            lista_productos.invalidateOptionsMenu();
                        }
                        numChecks--;

                        if(numChecks==0){
                            checkAll=false;
                            lista_productos.setVisibleMenusActive(false);

                        }
                    }
                    cb.setChecked(itemChecks[position]);
                    if(numChecks==proList.size()){
                        setCheckAll();
                        checkState=true;
                       //ponemos el menu en no visible
                    }
                }
            });
        }
        catch (Exception e){ e.printStackTrace();  Log.d("MyApp", e.getLocalizedMessage());}
        return convertView;
    }

    private class loadImage extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageRef;
        private loadImage(ImageView imageRef) {
            this.imageRef = new WeakReference<ImageView>(imageRef);

        }
        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            InputStream ims = null;
            Bitmap image =null;
            try {
                ims = new FileInputStream(params[0]);
                image = BitmapFactory.decodeStream(ims);
                    /*    Bitmap imageResize = Bitmap.createScaledBitmap(image,
                                50, 50, true);*/
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
