package com.example.reixon.codigodebarras.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.reixon.codigodebarras.Model.Producto;
import com.example.reixon.codigodebarras.Model.SuperMercado;
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
    private ViewHolder holder;
    private List<Producto> proList;
    private Lista_compra lista_compra;
    private SuperMercado sp;
    private boolean [] itemChecks;
    private int numChecks;
    private boolean checkAll;
    private String money;

    public AdapterListBuyProd(Context context,
            int textViewResourceId, ArrayList<Producto> listaProductosDada,SuperMercado sp, String money) {
        this.lista_compra = (Lista_compra) context;
        proList = listaProductosDada;
        itemChecks = new boolean[listaProductosDada.size()];
        numChecks=0;
        this.sp = sp;
        this.money=money;
    }

    /*public void vaciarArrayCheck(){
        //ArrayCheck=new CheckProduct[proList.size()];
        //numSelectCheck=0;
        this.notifyDataSetChanged();
    }*/

    public void setList(ArrayList<Producto> listP){
        this.proList=listP;
    }
/*
    public void setCheckAll(){
        if(checkAll){
            checkAll=false;
            numChecks=0;
           // lista_compra.setVisibleDelete(false);
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
*/
    private class ViewHolder {
        TextView name;
        ImageView imagen;
        TextView precio;
        TextView cantidad;
        TextView etPrecio;
        CheckBox check;
    }

    @Override
    public int getCount() {
        return proList.size();
    }

   public void setSuper(SuperMercado sm){
       this.sp = sm;
       this.proList = new ArrayList<>(sp.getProductos());
       itemChecks = new boolean[proList.size()];
       numChecks=0;
       this.notifyDataSetChanged();
   }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        //if(getItem(position) instanceof Producto)
        return 0;
    }

    public void setMoney(String money){
       this.money=money;
       notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null)
            {
                LayoutInflater vi = (LayoutInflater) lista_compra.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                holder = new ViewHolder();

                convertView = vi.inflate(R.layout.stock_product_adapter, null);
                holder.imagen = (ImageView) convertView
                        .findViewById(R.id.imagenListProductos);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.precio = (TextView) convertView.findViewById(R.id.txt_precio_prod);
                holder.check = (CheckBox) convertView.findViewById(R.id.etcheckBox);
                holder.cantidad = (TextView) convertView.findViewById(R.id.txtCantidad);
                holder.etPrecio = (TextView)convertView.findViewById(R.id.etMoneda);

                if(convertView!=null) {
                    convertView.setTag(holder);
                }
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

                Producto p = this.proList.get(position);
                holder.name.setText(p.getName());
                holder.check.setChecked(itemChecks[position]);
                if(p.getImagePath().equals("")) {
                    holder.imagen.setImageDrawable(lista_compra.getResources().getDrawable(R.drawable.photo_icon));
                }
                else{
                    //creamos un hilo para que cargue las imagenes
                    new loadImage(holder.imagen).execute(p.getImagePath());
                }
                holder.check.setTag(position);
                double precioXCant = 0;
                if (p.getQuantity() > 0) {
                    precioXCant = p.getPrice() * p.getQuantity();
                }
                holder.precio.setText(precioXCant + " ");
                holder.cantidad.setText(" (" + proList.get(position).getQuantity() + ")");

                holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        View row = (View) buttonView.getParent();

                        if(isChecked){
                            row.setBackgroundColor(0x60606000);
                        }
                        else{
                            row.setBackgroundColor(0xFFFFFF);
                        }
                    }
                });
                 holder.etPrecio.setText(money);
                /*
                holder.check.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    if(cb.isChecked()) {
                        itemChecks[Integer.valueOf(position)]=cb.isChecked();
                        numChecks++;
                        if(numChecks>0) {
                           // lista_compra.setVisibleDelete(true);
                        }
                    }
                    else {

                        itemChecks[Integer.valueOf(position)]=cb.isChecked();
                        numChecks--;
                        if(numChecks==0){
                            checkAll=false;
                            //lista_compra.setVisibleDelete(false);
                        }
                    }
                    cb.setChecked(itemChecks[position]);
                    }
                });*/
            if(holder.check.isChecked()) {
                holder.name.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else{
                holder.name.setPaintFlags(holder.name.getPaintFlags());
            }

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

