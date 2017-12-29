package com.example.reixon.codigodebarras;

/**
 * Created by reixon on 08/10/2017.
 */

public class CheckProduct {

    private boolean check;
    private int position, idProducto;

    public CheckProduct(int idProducto, int position, boolean check) {
        this.check = check;
        this.position = position;
        this.idProducto = idProducto;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
}
