package com.example.reixon.codigodebarras.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by reixon on 26/01/2018.
 */

public class listShopSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TABLE ="SuperMercado";
    //public static final Uri AUTHORITY_URI = Uri.parse("content://")
    //public static final Uri CONTENT_URI = Ur
    private static final String URL_SERVER = "http://webserviceslistshop.ddns.net/Android/Web_Service_List_Shop/";
    private final ContentResolver mContentResolver;

    public listShopSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        try{
            final URL location = new URL(URL_SERVER);
            InputStream stream = null;
            try{
                //String data =  getData();

            }catch (Exception e){

            }
        }catch (Exception e){

        }

    }
}
