package com.example.reixon.codigodebarras.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reixon.codigodebarras.Class.Category;
import com.example.reixon.codigodebarras.db.MySQL;
import com.example.reixon.codigodebarras.Class.Producto;
import com.example.reixon.codigodebarras.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class Adapter_categories_expanable extends BaseExpandableListAdapter {

    private activity_list_products_expanable mcontext;
    private LayoutInflater mInflater;
    private ArrayList<Category> mCategories;
    private ViewHolder holder;
    private List<Producto> proList;
    private MySQL mysql;
    private SQLiteDatabase db;
    private boolean[][] itemChecks;
    private int numChecks;
    private boolean checkAll,checkState;

    public Adapter_categories_expanable(Context context,int textResourceId, ArrayList<Category> groups,
                                        ArrayList<Producto> proT) {
        this.mcontext = (activity_list_products_expanable)context;
        mCategories = groups;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        holder = null;
        mysql = new MySQL(context);
        numChecks=0;
        checkState=false;
        checkAll=false;
        proList = proT;
        itemChecks = new boolean[mCategories.size()][proList.size()];
    }

    private class ViewHolder {
        TextView name;
        ImageView imagen;
        TextView precio;
        CheckBox check;
        TextView cantidad;
    }

    public void vaciarArrayCheck(){
        itemChecks=new boolean[mCategories.size()][proList.size()];
        numChecks=0;
        this.notifyDataSetChanged();
    }

    public void anyadirProducto(String nombre){
        db = mysql.getWritableDatabase();
        mysql.addProducto(nombre, "0","","",1,0,db);
        db = mysql.getWritableDatabase();
        mcontext.setProductosTotales(mysql.loadFullProduct(db));
        proList = mcontext.getProductosTotales();
        itemChecks = new boolean[mCategories.size()][proList.size()];
        Toast.makeText(mcontext, nombre +" creado", Toast.LENGTH_SHORT).show();
        this.notifyDataSetChanged();
        Toast.makeText(mcontext, " esta funcion no esta disponible", Toast.LENGTH_SHORT);
    }

    public void setList(ArrayList<Producto> proTotal, ArrayList<Category> categories){
        proList = proTotal;
        this.mCategories = categories;
        itemChecks = new boolean[mCategories.size()][proList.size()];
        notifyDataSetChanged();
    }

    public void setCheckAll(){
        if(checkAll){
            checkAll=false;
            numChecks=0;
            mcontext.setVisibleMenusActive(false);
        }
        else{
            checkAll=true;
            numChecks=proList.size();
            mcontext.setVisibleMenusActive(true);

        }
        for(int i=0; i<mCategories.size();i++){
            for(int x=0; x<proList.size();x++){
                itemChecks[i][x]=checkAll;
            }
        }
        notifyDataSetChanged();
    }

    public void setProductosTotales(ArrayList<Category> p) {
        mCategories = p;
    }

    public boolean getStateCheckMenu(){
        return checkState;
    }

    public boolean getCheckAll(){
        return checkAll;
    }

    public boolean[][] getItemCheck(){
        return itemChecks;
    }

    @Override
    public int getGroupCount() {
        return mCategories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;//mCategories.get(groupPosition).getSizeListProductCategory();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCategories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return 0;//mCategories.get(groupPosition).getListProductCategory().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView = mInflater.inflate(R.layout.category_adapter_header,null);
        }
        Category category = (Category)getGroup(groupPosition);
        TextView textView = (TextView) convertView.findViewById(R.id.et_category);

        textView.setText(category.getNombre());

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        try {
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
            Producto p =null;//= this.mCategories.get(groupPosition).getListProductCategory().get(childPosition);
            holder.name.setText(p.getNombre() + " ");
            holder.precio.setText(p.getPrecio() + " ");
            holder.check.setChecked(itemChecks[groupPosition][childPosition]);
            holder.check.setTag(childPosition);

            if(p.getRutaImagen().equals("")) {
                holder.imagen.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.photo_icon));
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
                        itemChecks[groupPosition][Integer.valueOf(childPosition)]=cb.isChecked();
                        numChecks++;
                        /*Toast.makeText(mcontext, mCategories.get(groupPosition).getListProductCategory()
                                .get(childPosition).getNombre(),Toast.LENGTH_SHORT).show();*/
                        if(numChecks>0) {
                            checkState=false;
                            mcontext.setVisibleMenusActive(true);
                        }
                    }
                    else {
                        itemChecks[groupPosition][Integer.valueOf(childPosition)]=cb.isChecked();
                        if(numChecks==proList.size()){
                            checkState=false;
                            checkAll=false;
                            mcontext.invalidateOptionsMenu();
                        }
                        numChecks--;

                        if(numChecks==0){
                            checkAll=false;
                            mcontext.setVisibleMenusActive(false);

                        }
                    }
                    cb.setChecked(itemChecks[groupPosition][childPosition]);
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


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
