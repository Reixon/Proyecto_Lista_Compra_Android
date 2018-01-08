package com.example.reixon.codigodebarras;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import java.util.ArrayList;

/**
 * Created by reixon on 18/12/2017.
 */

public class activity_list_products_expanable extends AppCompatActivity {


    protected MySQL mysql;
    protected SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_expanable);

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.expanable_listView);
        mysql = new MySQL(this);
        db = mysql.getWritableDatabase();
        ArrayList<Producto> allProd = mysql.loadFullProduct(db);
        db = mysql.getWritableDatabase();
        ArrayList<Category> cate = mysql.loadCategorias(db);
        ArrayList<Category> groups = prepareData(allProd, cate);
        final Adapter_categories_expanable adapter = new Adapter_categories_expanable(this, groups);
        listView.setAdapter(adapter);
    }

    public ArrayList<Category> prepareData(ArrayList<Producto> allProd,  ArrayList<Category> cate){
        for(int i=0; i<cate.size(); i++){

            for(int x = 0; x<allProd.size(); x++){
                if(allProd.get(x).getCategoria()==cate.get(i).getId()){
                    cate.get(i).addProductCategory(allProd.get(x));
                }
            }
        }
        return cate;
    }
}
