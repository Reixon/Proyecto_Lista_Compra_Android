package com.example.reixon.codigodebarras.Model;

import java.io.Serializable;

/**
 * Created by reixon on 26/09/2017.
 */

public class Producto implements Serializable {

    private int id, categoria=0, cantidad, unidad;
    private String nombre,  rutaImagen, codigo;
    private double precio;

    public Producto() {
        nombre="";
        rutaImagen="";
        codigo="";
        precio=0.0;
    }

    public Producto(String nombre){
        this.nombre = nombre;
        this.precio=0.0;
        rutaImagen="";
        codigo="";
    }
    public Producto (String nombre, String codigo, String rutaImagen){
        this.nombre = nombre;
        this.codigo = codigo;
        this.rutaImagen = rutaImagen;
        this.precio=0.0;

    }

    public Producto(int id, String nombre, double precio, String rutaImagen, String codigo, int categoria, int unidad) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.rutaImagen = rutaImagen;
        this.codigo = codigo;
        this.categoria= categoria;
        this.unidad = unidad;

    }

    public Producto(int id, String nombre, double precio, String rutaImagen, String codigo, int categoria, int cantidad, int unidad) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.rutaImagen = rutaImagen;
        this.codigo = codigo;
        this.categoria= categoria;
        this.cantidad = cantidad;
        this.unidad =unidad;
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCategoria() {return categoria;}

    public void setCategoria(int categoria) {this.categoria = categoria;}

    public int getCantidad() {
        return cantidad;
    }

    public int getUnidad() {
        return unidad;
    }

    public void setUnidad(int unidad) {
        this.unidad = unidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", categoria=" + categoria +
                ", nombre='" + nombre + '\'' +
                ", precio='" + precio + '\'' +
                ", rutaImagen='" + rutaImagen + '\'' +
                ", codigo='" + codigo + '\'' +
                '}';
    }
}
