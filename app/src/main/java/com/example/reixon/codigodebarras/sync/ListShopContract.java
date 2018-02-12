package com.example.reixon.codigodebarras.sync;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by reixon on 28/01/2018.
 */

public class ListShopContract {


    public static final String AUTHORITY = "com.example.reixon.codigodebarras.provider";
    public static final Uri BASE_URI = Uri.parse("content://"+AUTHORITY);
    public static final Uri LISTSHOPS_URI = Uri.withAppendedPath(ListShopContract.BASE_URI,"/listas");

    /*
    * MIME Types
    * */
    public static final String URI_TYPE_LISTSHOP_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE+
            "/vnd.com.example.reixon.provider.listshops";
    public static final String URI_TYPE_LISTSHOP_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE+
            "/vnd.com.example.reixon.provider.listshops";

    public static final class ListShopsColumns implements BaseColumns{

        private ListShopsColumns(){}
        public static final String NAME ="name";
        public static final String ID_CLOUD = "idcloud";

        public static final String DEFAULT_SORT_ORDER = ID_CLOUD + " ASC";
    }
}
