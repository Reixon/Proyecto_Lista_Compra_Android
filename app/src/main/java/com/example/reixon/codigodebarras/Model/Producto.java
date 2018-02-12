package com.example.reixon.codigodebarras.Model;

import java.io.Serializable;

/**
 * Created by reixon on 26/09/2017.
 */

public class Producto implements Serializable {

    private int id, category =0, quantity, unity;
    private String name, imagePath, code;
    private double price;
    public Producto() {
        name ="";
        imagePath ="";
        code ="";
        price =0.0;
    }

    public Producto(String name){
        this.name = name;
        this.price =0.0;
        imagePath ="";
        code ="";
    }

    public Producto (String name, String code, String imagePath){
        this.name = name;
        this.code = code;
        this.imagePath = imagePath;
        this.price =0.0;
    }

    public Producto(int id, String name, double price, String imagePath, String code, int category, int unity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.code = code;
        this.category = category;
        this.unity = unity;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCategory() {return category;}

    public void setCategory(int category) {this.category = category;}

    public int getQuantity() {
        return quantity;
    }

    public int getUnity() {
        return unity;
    }

    public void setUnity(int unity) {
        this.unity = unity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", category=" + category +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
