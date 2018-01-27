package com.example.reixon.codigodebarras.Class;

import java.io.Serializable;

/**
 * Created by reixon on 06/10/2017.
 */

public class Category implements Serializable {

    private int id;
    private String nombre;

    public Category(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Category(String nombre){
        this.nombre = nombre;
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

}
