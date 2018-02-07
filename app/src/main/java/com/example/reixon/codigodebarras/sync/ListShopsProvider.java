package com.example.reixon.codigodebarras.sync;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.reixon.codigodebarras.db.MySQL;

/**
 * Created by reixon on 28/01/2018.
 */

public class ListShopsProvider extends ContentProvider {

    public static final int LISTSHOPS_LIST = 1;
    public static final int LISTSHOP_ID = 2;
    public static final int PRODUCTS_LIST = 3;
    public static final int PRODUCTS_ID = 4;
    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        /*
        * URI para todas las listas
        * */
        sUriMatcher.addURI(ListShopContract.AUTHORITY,"listshops",LISTSHOPS_LIST);

        /*
        * URI para una lista
        * */
        sUriMatcher.addURI(ListShopContract.AUTHORITY,"listshops/#",LISTSHOP_ID);

        sUriMatcher.addURI(ListShopContract.AUTHORITY,"products",PRODUCTS_LIST);

        sUriMatcher.addURI(ListShopContract.AUTHORITY,"products/#",PRODUCTS_ID);

    }

    private MySQL mySQL;

    public ListShopsProvider(){ }

    @Override
    public boolean onCreate() {
        mySQL = MySQL.getInstance(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null;
    }


    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)){
            case LISTSHOPS_LIST:
                return ListShopContract.URI_TYPE_LISTSHOP_DIR;
            case LISTSHOP_ID:
                return ListShopContract.URI_TYPE_LISTSHOP_ITEM;
            case PRODUCTS_LIST:
                return ListShopContract.URI_TYPE_PRODUCTS_DIR;
            case PRODUCTS_ID:
                return ListShopContract.URI_TYPE_PRODUCTS_ITEM;
            default:
                return null;
        }
    }

    @Override
    public Uri insert( Uri uri, ContentValues values) {
        SQLiteDatabase db = mySQL.getWritableDatabase();

       // db.insert(Datab)
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update( Uri uri, ContentValues values,
                       String selection, String[] selectionArgs) {
        return 0;
    }
}
