package com.example.reixon.codigodebarras;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by reixon on 13/12/2017.
 */

public class HTTPDataHandler {
    static String stream = null;

    public HTTPDataHandler(){
    }

    public String GetHTTPData(String urlString){
        try{
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

            if(urlConnection.getResponseCode() == 200)
            {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = r.readLine())!=null){
                    sb.append(line);
                }
                stream = sb.toString();
                urlConnection.disconnect();
            }
            else{

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
        return stream;
    }
}
