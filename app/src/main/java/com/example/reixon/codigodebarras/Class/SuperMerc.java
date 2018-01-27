package com.example.reixon.codigodebarras.Class;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by reixon on 08/10/2017.
 */

public class SuperMerc implements Serializable {

    private int id, numProductosComprados, numProductosParaComprar;
    private String nombre;
    private ArrayList<Producto> productos;

    public SuperMerc(int id, String nombre, int numProductosComprados, int numProductosParaComprar) {
        this.id = id;
        this.nombre = nombre;
        this.numProductosComprados = numProductosComprados;
        this.numProductosParaComprar = numProductosParaComprar;
        this.productos = new ArrayList<>();
    }

    public SuperMerc(String nombre, int numProductosComprados, int numProductosParaComprar) {
        this.nombre = nombre;
        this.numProductosComprados = numProductosComprados;
        this.numProductosParaComprar = numProductosParaComprar;
        this.productos = new ArrayList<>();
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

    public int getNumProductosComprados() {
        return numProductosComprados;
    }

    public void setNumProductosComprados(int numProductosComprados) {
        this.numProductosComprados = numProductosComprados;
    }

    public void anyadirProducto(Producto p)
    {
        productos.add(p);
    }

    public void eliminarProducto(int pos){
        productos.remove(pos);
    }

    public int getNumProductosParaComprar() {
        return numProductosParaComprar=productos.size();
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public void addProduct(Producto p ){
        productos.add(p);

    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "SuperMerc{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", numProductosComprados=" + numProductosComprados +
                ", numProductosParaComprar=" + numProductosParaComprar +
                ", productos=" + productos +
                '}';
    }
}