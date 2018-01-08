package com.example.reixon.codigodebarras;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by reixon on 18/12/2017.
 */


 class AdapterListDespensa extends BaseAdapter {

    private ArrayList<Producto> searchList;
    private ViewHolder holder = null;
    private List<Producto> proList;
    private lista_productos context;
    private MySQL mysql;
    private SQLiteDatabase db;
    private boolean [] itemChecks;
    private int numChecks;
    private boolean checkAll;

    public AdapterListDespensa(Context context, int textViewResourceId,
                               ArrayList<Producto> listaProductosDada) {
        proList = listaProductosDada;
        searchList = listaProductosDada;
        this.context = (lista_productos) context;
        mysql = new MySQL(context);
        itemChecks = new boolean[listaProductosDada.size()];
        numChecks=0;
        checkAll=false;
    }

    public void setList(ArrayList<Producto> proTotal){
        proList = proTotal;
        searchList = (ArrayList)proTotal;
        itemChecks = new boolean[proTotal.size()];
        notifyDataSetChanged();
    }
    public ArrayList<Producto>getSearchList(){
        return searchList;
    }

    public boolean[] getItemCheck(){
        return itemChecks;
    }

    public void vaciarArrayCheck(){
        itemChecks=new boolean[proList.size()];
        this.notifyDataSetChanged();
    }

    public void anyadirProducto(String nombre){
        db = mysql.getWritableDatabase();
        mysql.addProducto(nombre, "0","","",-1,0,db);
        db = mysql.getWritableDatabase();
        context.setProductosTotales(mysql.loadFullProduct(db));
        proList = context.getProductosTotales();
        searchList = context.getProductosTotales();
        itemChecks = new boolean[proList.size()];
        //Producto p = context.getProductosTotales().get(context.getProductosTotales().size()-1);

        Toast.makeText(context, nombre +" creado", Toast.LENGTH_SHORT).show();
        this.notifyDataSetChanged();
    }

    public boolean getCheckAll(){
        return checkAll;
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
            context.setVisibleDelete(true);

        }
        for(int i=0; i<itemChecks.length;i++){
            itemChecks[i]=checkAll;
        }
        notifyDataSetChanged();
    }

    /*    @Override
        public Filter getFilter() {
            Filter filter = new Filter(){

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    final List<Producto> tempFliteredDataList = new ArrayList<Producto>();
                    // Logica del filtro
                    if (constraint == null || constraint.toString().trim().length() == 0) {
                        results.values=proList;
                    }
                    else{
                        String constrainString = constraint.toString().toLowerCase();
                        for(Producto postDetail : proList){

                            boolean b = postDetail.getNombre().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase());

                            if(postDetail.getNombre().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase()))
                            {
                                tempFliteredDataList.add(postDetail);
                            }
                        }
                        results.values = tempFliteredDataList;
                        db = mysql.getWritableDatabase();
                        Producto p =mysql.searchProductoWithName(constraint.toString(),db);
                        if(p!=null){
                            encuentra=true;
                        }else {
                            encuentra = false;
                        }
                    }

                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if(results!=null){
                        searchList = (ArrayList<Producto>) results.values; // returns the filtered list based on the search
                        notifyDataSetChanged();
                    }
                }
            };

            return filter;
        }*/

       /* public void filter(String charText){
            charText = charText.toLowerCase(Locale.getDefault());
           // Log.d("MyApp"," ****filter******");
            proList.clear();

            if(charText.length() == 0){
               // Log.d("MyApp","vacio");
                proList.addAll(searchList);
            }else
            {
                for(Producto postDetail : searchList){
                //    Log.d("MyApp","SearchList "+searchList.size());
                    if(charText.length()!=0 && postDetail.getNombre().toLowerCase(
                            Locale.getDefault()).contains(charText))
                    {
                   //     Log.d("MyApp","Encuentra  Nombre "+postDetail.getNombre());
                        proList.add(postDetail);
                        encuentra=false;
                    }
                }
                db = mysql.getWritableDatabase();
                Producto p =mysql.searchProductoWithName(charText,db);
                if(p!=null){
                 //   Log.d("MyApp", "buscamos db "+p.getNombre());
                    encuentra=true;
                }

            }
            notifyDataSetChanged();
        }*/

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
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = vi.inflate(R.layout.stock_product_adapter, null);
                holder = new ViewHolder();
                holder.imagen = (ImageView) convertView
                        .findViewById(R.id.imagenListProductos);
                holder.name = (TextView) convertView.findViewById(R.id.nombre);
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
            holder.name.setText(p.getNombre() + " ");
            holder.precio.setText(p.getPrecio() + " ");
            holder.check.setChecked(itemChecks[position]);
            holder.check.setTag(position);

            if(p.getRutaImagen().equals("")) {
                holder.imagen.setImageDrawable(context.getResources().getDrawable(R.drawable.photo_icon));
            }
            else{
                //creamos un hilo para que cargue las imagenes
                new loadImage(holder.imagen).execute(p.getRutaImagen());
            }
            holder.check.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    CheckBox cb = (CheckBox) v;
                    if(cb.isChecked()) {
                        itemChecks[Integer.valueOf(position)]=cb.isChecked();
                        numChecks++;
                        Toast.makeText(context, proList.get(position).getNombre(),Toast.LENGTH_SHORT);

                        if(numChecks>0) {
                            context.setVisibleDelete(true);
                        }
                    }
                    else {
                        itemChecks[Integer.valueOf(position)]=cb.isChecked();
                        numChecks--;
                        if(numChecks==0){
                            checkAll=false;
                            context.setVisibleDelete(false);
                        }
                    }
                    cb.setChecked(itemChecks[position]);
                }
            });
        }
        catch (Exception e){ e.printStackTrace();  Log.d("MyApp", e.getLocalizedMessage());}
        return convertView;
    }

    private class loadImage extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageRef;
        //private ProgressDialog dialog;
        private loadImage(ImageView imageRef) {
            this.imageRef = new WeakReference<ImageView>(imageRef);
    //        dialog = new ProgressDialog(activity);
        }
    /*    @Override
        protected void onPreExecute(){
            dialog.setMessage("Cargando. Espere por favor.");
            dialog.show();
        }*/

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
         /*   if (dialog.isShowing()) {
                dialog.dismiss();
            }*/
            ImageView imageView = imageRef.get();
            imageView.setImageBitmap(image);
        }
    }

}
