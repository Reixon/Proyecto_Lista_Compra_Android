package com.example.reixon.myshoppingcart.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by reixon on 15/06/2018.
 */

public class Almacen implements Serializable {
    private ArrayList<Producto> productosTotales;
    private ArrayList<String> categorias;
    private boolean[][]itemsChecks;
    private int tamChecks;

    public Almacen(){
        productosTotales = new ArrayList<Producto>();
        categorias = new ArrayList<String>();
        itemsChecks=new boolean[categorias.size()][productosTotales.size()];
    }

    public void addProducto(Producto p){
        productosTotales.add(p);
    }

    public ArrayList<Producto> getProductos(){
        return productosTotales;
    }

   /* public SortedMap<String, List> getProductosCategoria(){
        SortedMap<String, List> productosTotalesAlmacen= new TreeMap<>();
        categorias = new ArrayList<>();
        List productosT = new ArrayList<>();
        String[] datosP;
        String datos="";
        String categoriaAnterior="";
        String nombreC="";
        Collections.sort(productosTotales);
        int maxProductosCategoria=0;

        for(int i=0; i<this.productosTotales.size();i++){
            datos = productosTotales.get(i).toString();
            datosP = datos.split(",;");
            nombreC = datosP[4];
           // productosTotales.get(i).setPosicionListaCompra(i);
            if(categorias.contains(nombreC)){
                productosT.add(productosTotales.get(i));
            }else{
                if(i>0){
                    if(maxProductosCategoria<productosT.size()){
                        maxProductosCategoria=productosT.size();
                    }
                    Collections.sort(productosT);
                    productosTotalesAlmacen.put(categoriaAnterior, productosT);
                    productosT = new ArrayList<>();
                }
                categorias.add(nombreC);
                productosT.add(productosTotales.get(i));
            }
            categoriaAnterior=nombreC;
        }
        if(productosT.size()>0){
            Collections.sort(productosT);
            productosTotalesAlmacen.put(categoriaAnterior, productosT);
            if(maxProductosCategoria<productosT.size()){
                maxProductosCategoria=productosT.size();
            }
        }

        itemsChecks = new boolean[categorias.size()][maxProductosCategoria];
        return productosTotalesAlmacen;
    }*/
   public SortedMap<String, List> getProductosCategoria(){
       SortedMap<String, List> productosAlmacen= new TreeMap<>();
       categorias = new ArrayList<>();
       List productosT = new ArrayList<>();
       String[] datosP;
       String datos="";
       String categoriaAnterior="";
       String nombreC="";
       Collections.sort(productosTotales);
       int maxProductosCategoria=0;

       for(int i=0; i<this.productosTotales.size();i++){
           datos = productosTotales.get(i).toString();
           datosP = datos.split(",;");
           nombreC = datosP[4];
           //    productosDespensa.get(i).setPosProductoListaCompra(i);
           if(categorias.contains(nombreC)){
               productosT.add(productosTotales.get(i));
           }else{
               if(i>0){
                   if(maxProductosCategoria<productosT.size()){
                       maxProductosCategoria=productosT.size();
                   }
                   Collections.sort(productosT);
                   productosAlmacen.put(categoriaAnterior, productosT);
                   productosT = new ArrayList<>();
               }
               categorias.add(nombreC);
               productosT.add(productosTotales.get(i));
           }
           categoriaAnterior=nombreC;
       }
       if(productosT.size()>0){
           Collections.sort(productosT);
           productosAlmacen.put(categoriaAnterior, productosT);
           if(maxProductosCategoria<productosT.size()){
               maxProductosCategoria=productosT.size();
           }
       }

       itemsChecks = new boolean[categorias.size()][maxProductosCategoria];
       return productosAlmacen;
   }

    public boolean getProductoCheck(int posCat, int pos){
        return  itemsChecks[posCat][pos];
    }

    public int getTamChecks(){
        return tamChecks;
    }

    public void setProductosChecks(boolean[][] items){
        itemsChecks= items;
    }

    public int getSizeProducts(){
        return productosTotales.size();
    }

    public void setProductoCheck(int posCat, int pos){
        itemsChecks[posCat][pos]=true;
        tamChecks++;
    }

    public void setProductoCheckOut(int posCat, int pos){
        itemsChecks[posCat][pos]=false;
        if(tamChecks>0)
            tamChecks--;
    }

    public boolean isAllProductsSelected(){
        if(productosTotales.size()==this.tamChecks){
            return true;
        }
        return false;
    }

    public boolean[][] getProductosChecks(){
        return itemsChecks;
    }

    public ArrayList<String> getCategoriasListaCompra(){
        return categorias;
    }

    public int getTamProductos(){
        return productosTotales.size();
    }
}


