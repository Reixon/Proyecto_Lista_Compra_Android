package com.example.reixon.codigodebarras.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.reixon.codigodebarras.Model.Producto;

import org.json.JSONObject;

/**
 * Created by reixon on 16/12/2017.
 */

public class ProcessJSON  extends AsyncTask<String, Void, Producto> {

    private String codigo;
    ProgressDialog progressDialog;

    public ProcessJSON (Activity activity){
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        this.progressDialog.setMessage("Buscando producto...");
        this.progressDialog.show();
    }

    @Override
    protected Producto doInBackground(String... params) {
        String stream = null;

        codigo = params[0];
        String url = "https://world.openfoodfacts.org/api/v0/product/" + params[0] + ".json";
        Producto p=null;
        HTTPDataHandler hh = new HTTPDataHandler();
        stream = hh.GetHTTPData(url);

        if(stream !=null){
            try{
                JSONObject reader = new JSONObject(stream);
                JSONObject prod = reader.getJSONObject("product");
                String name = prod.getString("product_name_en");
                if(!prod.getString("quantity").equals("")) {
                    String quantity = prod.getString("quantity");
                }
                String image = prod.getString("image_url");
                String code =  reader.getString("code");
                p = new Producto(name, code, image);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return p;
    }
    protected void onPostExecute(Producto p){
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }


}
