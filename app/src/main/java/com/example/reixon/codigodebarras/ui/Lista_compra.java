package com.example.reixon.codigodebarras.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reixon.codigodebarras.Model.Category;
import com.example.reixon.codigodebarras.Model.Producto;
import com.example.reixon.codigodebarras.Model.SuperMercado;
import com.example.reixon.codigodebarras.Model.UserAccount;
import com.example.reixon.codigodebarras.R;
import com.example.reixon.codigodebarras.db.MySQL;
import com.example.reixon.codigodebarras.http.ProcessJSON;
import com.example.reixon.codigodebarras.sync.AccountAuthenticator;
import com.example.reixon.codigodebarras.sync.ListShopContract;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Lista_compra extends AppCompatActivity {

    private AdapterListBuyProd adapterListCom = null;
    private ArrayList<Producto> listaProductosCompra;;
    private ArrayList<String> arrayNombreSupers;
    private ArrayList<Category> arrayCategories;
    private ArrayList<String> arrayNameCategories;
    private ArrayList<SuperMercado>arraySupers;
    private ArrayList<UserAccount>userAccounts;
    private ArrayList<Producto>productoTotal;
    private SuperMercado sp;
    private SQLiteDatabase db;
    private MySQL mysql;
    private boolean loadData, menu_active;;
    private static final int LOAD_DATA_MYSQL=100;
    private PowerManager pm;
    private PowerManager.WakeLock wakelock;


    private AccountManagerCallback<Bundle> mGetAuthTokenCallback =
            new AccountManagerCallback<Bundle>() {
                @Override
                public void run(final AccountManagerFuture<Bundle> arg0) {
                    try {
                        String token = (String) arg0.getResult().get(AccountManager.KEY_AUTHTOKEN);
                    } catch (Exception e) {
                        // handle error
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getContentResolver();
        AccountManager mAccountManager = (AccountManager) getSystemService(
                ACCOUNT_SERVICE);
        Account[] accounts = mAccountManager.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);
        if (accounts.length != 0){
            mAccount = accounts[0];
            mAccountManager.getAuthToken(mAccount, AccountAuthenticator.ACCOUNT_TYPE, null, this,
                    mGetAuthTokenCallback, null);
            resolver.setIsSyncable(mAccount, ListShopContract.AUTHORITY, 1);
            resolver.setSyncAutomatically(mAccount, ListShopContract.AUTHORITY, true);
        }

        TableObserver observer = new TableObserver(null);
        resolver.registerContentObserver(ListShopContract.LISTSHOPS_URI, true, observer);

        setContentView(R.layout.activity_navigation_drawer);
        Spinner spinner_super = (Spinner)findViewById(R.id.spinner_super);
        ImageButton bt_speak =(ImageButton)findViewById(R.id.bt_speak);
        EditText txt_edit =(EditText)findViewById(R.id.txt_lista_productos);
        ImageButton btCode = (ImageButton)findViewById(R.id.bt_scanner_search);
        ImageButton btOk = (ImageButton)findViewById(R.id.bt_ok_menu_search);
        ListView listView = (ListView) findViewById(R.id.lista_productos_comprar);
        pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        txt_edit.clearFocus();
        txt_edit.setSingleLine();
        txt_edit.setHorizontallyScrolling(true);
        loadData=false;
        menu_active=false;

        Toolbar toolbar = findViewById(R.id.toolbar_lista_compra);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        wakelock=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /*********************
         * LOAD DATA SQLite
         * *******************/
        mysql = new MySQL(this);
        db = mysql.getReadableDatabase();
        arraySupers = mysql.cargarSuperMercadosBD(db);
        arrayCategories = mysql.loadCategories(db);
        productoTotal = mysql.loadFullProduct(db);
        userAccounts = mysql.loadUser(db);
        db.close();

        /**************+*Usuarios***************/
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(userAccounts.size()>0) {
            View headerView = navigationView.getHeaderView(0);
            TextView nameUser = (TextView) headerView.findViewById(R.id.et_name_user);
            nameUser.setText(userAccounts.get(0).getName());
            TextView email = (TextView) headerView.findViewById(R.id.et_email_user);
            email.setText(userAccounts.get(0).getEmail());
        }

        arrayNombreSupers = new ArrayList<>();
        for(int i=0; i<arraySupers.size(); i++){
            arrayNombreSupers.add(arraySupers.get(i).getNombre());
        }
        spinner_super.setSelection(0);
        sp = (SuperMercado)arraySupers.get(spinner_super.getSelectedItemPosition());

        arrayNameCategories = new ArrayList<>();
        for(int i=0; i<arrayNameCategories.size(); i++){
            arrayNameCategories.add(arrayCategories.get(i).getNombre());
            for(int x =0; x<sp.getProductos().size();x++){
                if(arrayCategories.get(i).getId()==sp.getProductos().get(x).getCategoria())
                    arrayNameCategories.add(sp.getProductos().get(x).getNombre());
            }
        }

        this.listaProductosCompra = sp.getProductos();
        adapterListCom = new AdapterListBuyProd(this,R.layout.stock_product_adapter,
                listaProductosCompra,sp,arrayNameCategories);
        listView.setAdapter(adapterListCom);


        arrayNombreSupers = new ArrayList<String>();
        for(int i = 0; i< arraySupers.size(); i++) {
            this.arrayNombreSupers.add(this.arraySupers.get(i).getNombre());
        }
        spinner_super.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrayNombreSupers));

        /*******************LISTENERS*****************************************
        *****************************************************************/
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                Bundle b = new Bundle();
                Intent i=null;
                switch(item.getItemId()){
                    case R.id.nav_listBuy:
                        b.putSerializable("Lista Supers",arraySupers);
                        //  bundle.putStringArrayList("Name_Supers",arrayNombreSupers);
                        i = new Intent(Lista_compra.this, Admin_list.class);
                        i.putExtras(b);
                        startActivityForResult(i,LOAD_DATA_MYSQL);
                        break;
                    case R.id.nav_listProd:
                        b.putSerializable("Lista Supers", arraySupers);
                        b.putSerializable("Full Products", productoTotal);
                        b.putSerializable("Array Categories", arrayCategories);
                        i = new Intent(Lista_compra.this, Lista_productos.class);
                        i.putExtras(b);
                        startActivity(i);
                        break;
                    case R.id.nav_categories:
                        b.putSerializable("Array Categories",arrayCategories);
                        i = new Intent(Lista_compra.this, Admin_Category.class);
                        i.putExtras(b);
                        startActivityForResult(i,LOAD_DATA_MYSQL);
                        break;
                    case R.id.nav_history:
                        break;
                    case R.id.nav_account:
                        i = new Intent(Lista_compra.this,Admin_account.class);
                        b.putSerializable("Array Users", userAccounts);
                        i.putExtras(b);
                        startActivityForResult(i,LOAD_DATA_MYSQL);
                        break;
                    case R.id.nav_settings:
                        break;
                    case R.id.nav_about:

                        break;

                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos,
                                    long id) {
                Spinner spinner = (Spinner)findViewById(R.id.spinner_super);
                Producto p = listaProductosCompra.get(pos);
                Bundle b = new Bundle();
                b.putSerializable("Producto", p);
                b.putSerializable("SuperMercado",arraySupers.get(spinner.getSelectedItemPosition()));
                b.putBoolean("LISTA_COMPRA",true);
                Intent intentViewProduct= new Intent(Lista_compra.this, ViewProduct.class);
                intentViewProduct.putExtras(b);
                startActivityForResult(intentViewProduct,LOAD_DATA_MYSQL);
            }
        });

        bt_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"es-MX");
                startActivityForResult(intent,1);
            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.bt_ok_menu_search){
                    insertarProducto();
                }
            }
        });
        btCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.bt_scanner_search) {
                    scannerBarCode();
                }
            }
        });

        txt_edit.addTextChangedListener(new TextWatcher() {
            ImageButton btOk = (ImageButton)findViewById(R.id.bt_ok_menu_search);
            ImageButton bt_speak = (ImageButton)findViewById(R.id.bt_scanner_search);
            ImageButton btCode = (ImageButton)findViewById(R.id.bt_scanner_search);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")){

                    btOk.setVisibility(View.VISIBLE);
                    bt_speak.setVisibility(View.GONE);
                    btCode.setVisibility(View.GONE);
                }
                else {
                    btOk.setVisibility(View.GONE);
                    bt_speak.setVisibility(View.VISIBLE);
                    btCode.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txt_edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if((keyCode == KeyEvent.KEYCODE_ENTER)&& event.getAction()== KeyEvent.ACTION_DOWN){
                    insertarProducto();
                }
                return false;
            }
        });

        spinner_super.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                    Spinner spiner = (Spinner)findViewById(R.id.spinner_super);
                    sp = (SuperMercado) arraySupers.get(spiner.getSelectedItemPosition());
                    listaProductosCompra = sp.getProductos();
                    adapterListCom.setSuper(sp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        //Mantener la pantalla encendida
        PowerManager.WakeLock wakelock=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        Toast.makeText(this, "La pantalla se mantendrá encendida hasta pasar de pantalla.", Toast.LENGTH_SHORT).show();
        wakelock.acquire();

    }


    /**
     * Si el la versión del movil es 23+ podrá utilizar el escaner de código de barras
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void scannerBarCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            IntentIntegrator integrator = new IntentIntegrator(Lista_compra.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            integrator.setPrompt("Scan");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Lista_compra.this);
            builder.setTitle("Escaner no disponible");
            builder.setMessage("El escaner no esta disponible para su versión de móvil");
            ImageButton btCode = (ImageButton)findViewById(R.id.bt_scanner_search);
            btCode.setEnabled(false);
        }
    }
    /**
     * Escucha los cambios que hayan en
     * {@link com.example.reixon.codigodebarras.sync.ListShopsProvider}.
     */
    public class TableObserver extends ContentObserver {

        public TableObserver(Handler handler) {
            super(handler);
        }

        /**
         * Define el método que es llamado cuando los datos en el content provider cambian.
         * Este método es solo para que haya compatibilidad con plataformas más viejas.
         */
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }
        /**
         * Define el método que es llamado cuando los datos en el content provider cambian.
         */
        @Override
        public void onChange(boolean selfChange, Uri changeUri) {

            if (mAccount != null) {
                // Corre la sincronizacion
                ContentResolver.requestSync(mAccount, ListShopContract.AUTHORITY, null);
            }
        }
    }

    public void onResume() {
        super.onResume();

        if(loadData) {
            db = mysql.getReadableDatabase();
            arraySupers = mysql.cargarSuperMercadosBD(db);
            arrayCategories = mysql.loadCategories(db);
            productoTotal = mysql.loadFullProduct(db);
            userAccounts = mysql.loadUser(db);
            db.close();

            Spinner spinner_super = (Spinner)findViewById(R.id.spinner_super);
            sp = arraySupers.get(spinner_super.getSelectedItemPosition());
            listaProductosCompra = sp.getProductos();
            arrayNombreSupers = new ArrayList<>();
            for(int i=0; i<arraySupers.size(); i++){
                arrayNombreSupers.add(arraySupers.get(i).getNombre());
            }
            spinner_super.setAdapter(new ArrayAdapter<>(Lista_compra.this,
                    android.R.layout.simple_list_item_1, arrayNombreSupers));
            arrayNameCategories = new ArrayList<>();
            for(int i=0; i<arrayNameCategories.size(); i++){
                arrayNameCategories.add(arrayCategories.get(i).getNombre());
            }
            adapterListCom.setSuper(sp);
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

            if (userAccounts.size()!=0){
                UserAccount user = userAccounts.get(0);
                View headerView = navigationView.getHeaderView(0);
                TextView nameUser = (TextView) headerView.findViewById(R.id.et_name_user);
                nameUser.setText(user.getName());
                TextView email = (TextView) headerView.findViewById(R.id.et_email_user);
                email.setText(user.getEmail());
            }

            loadData=false;
        }
        ListView listView = (ListView) findViewById(R.id.lista_productos_comprar);
        listView.setEmptyView(findViewById(R.id.text_list_empty));
        actualizarPrecioYelementos();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(wakelock!=null){
                try {
                    wakelock.release();
                }catch (Throwable th){}
            }

            super.onBackPressed();
        }
    }


    public void insertarProducto(){
        db = mysql.getWritableDatabase();
        EditText txt_edit = (EditText)findViewById(R.id.txt_lista_productos);
        Producto p = new Producto(txt_edit.getText().toString());
        mysql.add_Producto_And_Add_Producto_To_Lista_Supermercado(db,sp,p);
        txt_edit.clearFocus();
        loadData=true;
        Toast.makeText(this, p.getNombre() +" creado", Toast.LENGTH_SHORT).show();
        onResume();
        txt_edit.setText("");
        adapterListCom.notifyDataSetChanged();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case LOAD_DATA_MYSQL:
                loadData=true;
                break;
            case 1:
                if(resultCode == RESULT_OK && null!=data){
                    EditText txt_edit = (EditText)findViewById(R.id.txt_lista_productos);
                    ArrayList<String> txtSpeak = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String txt = txtSpeak.get(0).substring(0,1).toUpperCase()+txtSpeak.get(0).substring(1);
                    txt_edit.setText(txt);
                }
                break;
            case 49374:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (result.getContents() == null) {
                    Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show();
                } else {
                    db = mysql.getReadableDatabase();
                    Producto p = mysql.searchProductoWithCode(result.getContents(), db);
                    if (p != null) {
                        if (p.getNombre().equals("")) {
                            Toast.makeText(this, "VACIO", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                Intent i = new Intent(this, ViewProduct.class);
                                i.putExtra("Producto_scanner", p);
                                i.putExtra("SuperMercado", sp);
                                i.putExtra("Lista Supers", arraySupers);
                                startActivityForResult(i,LOAD_DATA_MYSQL);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {/*PRODUCTO NO EXISTE EN LA BD*/
                        Intent i = new Intent(Lista_compra.this, Add_product.class);
                        i.putExtra("Add_lista","");
                        i.putExtra("SuperMercado",sp);
                        try {
                            p=new ProcessJSON(this).execute(result.getContents().toString()).get();
                            if(p!=null){
                                i.putExtra("Producto_scanner_internet_Anyadir", p);
                                startActivityForResult(i,LOAD_DATA_MYSQL);
                            }
                            else{
                                i.putExtra("CODIGO", result.getContents());
                                Toast.makeText(Lista_compra.this,"Producto no encontrado en Internet",Toast.LENGTH_SHORT).show();
                                startActivityForResult(i,LOAD_DATA_MYSQL);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                }
                break;
        }
    }

    /*public void deleteProductCheck(){
        int posSig;
        String nombres="";
        for(int i=0; i<listaProductosCompra.size(); i++){
            if(listaProductosCompra.get(i).isCheck()) {
                db = mysql.getWritableDatabase();
                mysql.eliminar_Producto_D_SuperMerc_Producto(listaProductosCompra.get(i).getId(), sp.getId(), db);
                nombres = nombres + "\n" + listaProductosCompra.get(i).getNombre()+"";
                sp.eliminarProducto(i);
            }
        }
        adapterListCom.setList(sp.getProductos());
        Toast.makeText(this, nombres + " eliminados", Toast.LENGTH_SHORT).show();
        adapterListCom.notifyDataSetChanged();
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.main_menu_buy_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(menu_active){
            menu.clear();
            getMenuInflater().inflate(R.menu.main_menu_active, menu);
        }
        else{
            menu.clear();
            getMenuInflater().inflate(R.menu.main_menu_buy_list, menu);

        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_save_history:

                break;
            case R.id.menu_share_list:
                chooseShared();
                //Toast.makeText(Lista_productos.this,"COMPARTIR",Toast.LENGTH_SHORT);
                return true;
            case R.id.menu_masOpciones:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void chooseShared(){
        final CharSequence [] option ={"Enviar", "Enviar como texto"};
        final android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(Lista_compra.this);
        builder.setTitle("Selecciona una opcion");
        builder.setItems(option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                switch(which){
                    case 0:
                        sendList();
                       // if(usersAccounts==null) {
                       /*     Intent i = new Intent(Lista_compra.this, Admin_account.class);
                            startActivity(i);*/
                      /*  }else{

                        }*/
                        break;
                    case 1:
                        /*Compartir lista por texto*/
                       // Intent i = new Intent()
                        break;
                    case 2:

                        break;
                }
            }
        });
        builder.show();
    }

    public void sendList(){

        final android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(Lista_compra.this);
        builder.setTitle("Selecciona una opcion");
        final EditText edit = new EditText(Lista_compra.this);
        builder.setView(edit);
        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!edit.getText().toString().equals("")){

                    //Comprobar que ese correo existe en la bd remota
                    //si no existe enviar solicitud por correo electronico al contacto
                    //si exite compartir lista
                }
                else{
                    Toast.makeText(Lista_compra.this,"Introduce el correo electronico",Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }

    private void createFileJSON(){
        JSONObject superJSON = convertArrayTOJSON();
        Writer output = null;
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TITLE,"lista compra JSON");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "text/plain");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            Toast.makeText(Lista_compra.this,"Datos compartidos",Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private JSONObject convertArrayTOJSON(){
        JSONObject superMJSON = new JSONObject();
        JSONArray arrayProducts = new JSONArray();
        JSONObject product;
        JSONObject category;

        try {
           // nombre, int numProductosComprados, int numProductosParaComprar
            superMJSON.put("name", this.sp.getNombre());
            superMJSON.put("numProductsBought", 0);
            superMJSON.put("numProductsForBuy", sp.getNumProductosParaComprar());
            product = new JSONObject();
            for(int i=0; i<listaProductosCompra.size(); i++){
                product.put("name", listaProductosCompra.get(i).getNombre());
                //int id, String nombre, double precio, String rutaImagen, String codigo, int categoria, int cantidad, int unidad
                product.put("price", listaProductosCompra.get(i).getPrecio());
                product.put("image", listaProductosCompra.get(i).getRutaImagen());
                product.put("codigo", listaProductosCompra.get(i).getCodigo());
                for(int x =0; x<arrayCategories.size(); x++){
                    if(arrayCategories.get(x).getId()== listaProductosCompra.get(i).getCategoria()){
                        category = new JSONObject();
                        category.put("name", arrayCategories.get(x).getNombre());
                        product.put("category", category);
                    }
                }
                product.put("quantity", listaProductosCompra.get(i).getCantidad());
                product.put("unity",listaProductosCompra.get(i).getUnidad());
                arrayProducts.put(product);
            }
            superMJSON.put("productos", arrayProducts);


        }catch (org.json.JSONException e){

        }
        return superMJSON;
    }

    public void actualizarPrecioYelementos(){
        double total=0;
        TextView txtPrecioT = (TextView)findViewById(R.id.txtPrecioTotal);
        TextView numElement = (TextView)findViewById(R.id.txtNumProd);
        for(int i=0; i<listaProductosCompra.size(); i++){
            total +=listaProductosCompra.get(i).getPrecio()*
                    listaProductosCompra.get(i).getCantidad();
        }
        int numP = sp.getNumProductosParaComprar();
        numElement.setText(Integer.toString(numP));
        txtPrecioT.setText(total+" € ");
    }


    /****************************************************************************************/


    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.example.android.datasync.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.example.reixon.codigodebarras";
    // The account name
    public static final String ACCOUNT = "";
    // Instance fields
    Account mAccount;

    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

}
