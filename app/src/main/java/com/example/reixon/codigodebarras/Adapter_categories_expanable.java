package com.example.reixon.codigodebarras;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Adapter_categories_expanable extends BaseExpandableListAdapter {
 /*   private Context mcontext;
    private ArrayList<Category> mCategories;
    private LayoutInflater mInflater;*/
    private Context mcontext;
    private LayoutInflater mInflater;
    private ArrayList<Category> mCategories;
    private ArrayList<Producto> searchList;
    private ViewHolder holder = null;
    private List<Producto> proList;
    private MySQL mysql;
    private SQLiteDatabase db;
    private boolean [] itemChecks;
    private int numChecks;
    private boolean checkAll,checkState;

    public Adapter_categories_expanable(Context context, ArrayList<Category> groups) {
        this.mcontext = context;
        mCategories = groups;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //proList = listaProductosDada;
        //searchList = listaProductosDada;
        mysql = new MySQL(context);
        //itemChecks = new boolean[listaProductosDada.size()];
        numChecks=0;
        checkState=false;
        checkAll=false;
    }


    private class ViewHolder {
        TextView name;
        ImageView imagen;
        TextView precio;
        CheckBox check;
        TextView cantidad;
    }

    @Override
    public int getGroupCount() {
        return mCategories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mCategories.get(groupPosition).getSizeListProductCategory();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCategories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mCategories.get(groupPosition).getListProductCategory().get(childPosition);
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
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView = mInflater.inflate(R.layout.stock_product_adapter,null);
        }
        Producto p = (Producto) getChild(groupPosition, childPosition);


        ImageView imagen = (ImageView) convertView
                .findViewById(R.id.imagenListProductos);
        TextView name  = (TextView) convertView.findViewById(R.id.nombre);
        TextView precio = (TextView) convertView.findViewById(R.id.txt_precio_prod);
        CheckBox check = (CheckBox) convertView.findViewById(R.id.etcheckBox);
        TextView cantidad =(TextView)convertView.findViewById(R.id.txtCantidad);
        cantidad.setVisibility(View.GONE);

        name.setText(p.getNombre() + " ");
        precio.setText(p.getPrecio() + " ");

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
