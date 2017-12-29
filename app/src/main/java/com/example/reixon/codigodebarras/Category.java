package com.example.reixon.codigodebarras;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by reixon on 06/10/2017.
 */

public class Category implements Serializable {

    private int id;
    private String nombre;
    private ArrayList<Producto> listProductCategory;

    public Category(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        listProductCategory = new ArrayList<Producto>();
    }

    public Category(){
        listProductCategory = new ArrayList<Producto>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void addProductCategory(Producto p){
        listProductCategory.add(p);
    }

    public int getSizeListProductCategory(){
        return listProductCategory.size();
    }

    public ArrayList<Producto> getListProductCategory(){
        return listProductCategory;
    }
}
