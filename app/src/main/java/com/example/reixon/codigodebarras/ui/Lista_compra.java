package com.example.reixon.codigodebarras.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import android.view.WindowManager;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Lista_compra extends AppCompatActivity {

    private AdapterListBuyProd adaptador_Lista_Compra;
    private ArrayList<Producto> arrayProductosStock;;
    private ArrayList<Category> arrayCategories;
    private ArrayList<SuperMercado>arraySupers;
    private ArrayList<UserAccount>userAccounts;
    private ArrayList<Producto> arrayStock;
    private boolean loadData;;
    private static final int LOAD_DATA_MYSQL=100;
    private Account mAccount;
    private SharedPreferences pref;
    private AccountManager mAccountManager;

    private Spinner spinner_super;
    private ImageButton bt_speak,btCode,btOk,btCross;
    private ListView listView;
    private EditText txt_edit;
    private NavigationView navigationView;
    private TextView nameUser, email,txtPrecioT,numElement;


    private AccountManagerCallback<Bundle> mGetAuthTokenCallback =
            new AccountManagerCallback<Bundle>() {
                @Override
                public void run(final AccountManagerFuture<Bundle> arg0) {
                    try {
                        String token = (String) arg0.getResult().get(AccountManager.KEY_AUTHTOKEN);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getContentResolver();
        mAccountManager = (AccountManager) getSystemService(
                ACCOUNT_SERVICE);

        assert mAccountManager != null;
        final Account[] accounts = mAccountManager.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);
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
        txtPrecioT = (TextView)findViewById(R.id.txtPrecioTotal);
        numElement = (TextView)findViewById(R.id.txtNumProd);
        spinner_super = (Spinner)findViewById(R.id.spinner_super);

        spinner_super = (Spinner)findViewById(R.id.spinner_super);
        bt_speak =(ImageButton)findViewById(R.id.bt_speak);
        txt_edit =(EditText)findViewById(R.id.txt_lista_productos);
        btCode = (ImageButton)findViewById(R.id.bt_scanner_search);
        btOk = (ImageButton)findViewById(R.id.bt_ok_menu_search);
        btCross =(ImageButton)findViewById(R.id.bt_cross_menu_search);
        listView = (ListView) findViewById(R.id.lista_productos_comprar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nameUser = (TextView) headerView.findViewById(R.id.et_name_user);
        email = (TextView) headerView.findViewById(R.id.et_email_user);

        txt_edit.clearFocus();
        txt_edit.setSingleLine();
        txt_edit.setHorizontallyScrolling(true);
        loadData=false;

        Toolbar toolbar = findViewById(R.id.toolbar_lista_compra);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /*********************
         * LOAD DATA SQLite
         * *******************/
        MySQL mysql = MySQL.getInstance(this);
        SQLiteDatabase db = mysql.getReadableDatabase();
        arraySupers = mysql.cargarSuperMercadosBD(db);
        arrayCategories = mysql.cargarCategorias(db);
        arrayStock = mysql.cargarProductos(db);
        userAccounts = mysql.cargarUsuarios(db);
        db.close();




        /***************Usuarios***************/
        if(mAccount!=null) {
            nameUser.setText(userAccounts.get(0).getName());
            email.setText(userAccounts.get(0).getEmail());
        }

        ArrayList<String> arrayNombreSupers = new ArrayList<>();
        for(int i=0; i<arraySupers.size(); i++){
            arrayNombreSupers.add(arraySupers.get(i).getNombre());
        }
        spinner_super.setSelection(0);

        ArrayList<String> arrayNameCategories = new ArrayList<>();
        for(int i=0; i<arrayNameCategories.size(); i++){
            arrayNameCategories.add(arrayCategories.get(i).getNombre());
            for(int x =0; x<arraySupers.get(spinner_super.getSelectedItemPosition()).getProductos().size();x++){
                if(arrayCategories.get(i).getId()==arraySupers.get(spinner_super.getSelectedItemPosition()).getProductos().get(x).getCategory())
                    arrayNameCategories.add(arraySupers.get(spinner_super.getSelectedItemPosition()).getProductos().get(x).getName());
            }
        }

        pref= PreferenceManager.getDefaultSharedPreferences(Lista_compra.this);

        this.arrayProductosStock = arraySupers.get(spinner_super.getSelectedItemPosition()).getProductos();
        adaptador_Lista_Compra = new AdapterListBuyProd(this,R.layout.stock_product_adapter,
                arrayProductosStock,arraySupers.get(spinner_super.getSelectedItemPosition()),
                pref.getString("money","€"));
        listView.setAdapter(adaptador_Lista_Compra);


        arrayNombreSupers = new ArrayList<String>();
        for(int i = 0; i< arraySupers.size(); i++) {
            arrayNombreSupers.add(this.arraySupers.get(i).getNombre());
        }
        spinner_super.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrayNombreSupers));

        /*********************************************************
         *******************LISTENERS*********************************
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
                        b.putSerializable("Full Products", arrayStock);
                        b.putSerializable("Array Categories", arrayCategories);
                        i = new Intent(Lista_compra.this, Lista_productos.class);
                        i.putExtras(b);
                        startActivityForResult(i,LOAD_DATA_MYSQL);
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
                        if(mAccount==null) {
                            i = new Intent(Lista_compra.this, Admin_account.class);
                            b.putSerializable("Array Users", userAccounts);
                            i.putExtras(b);
                            startActivityForResult(i, LOAD_DATA_MYSQL);
                        }

                        break;
                    case R.id.nav_settings:
                        i = new Intent(Lista_compra.this, PreferenciasActivity.class);
                        startActivity(i);
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
                Producto p = arrayProductosStock.get(pos);
                Bundle b = new Bundle();
                b.putSerializable("Producto", p);
                b.putSerializable("SuperMercado", spinner_super.getSelectedItemPosition());
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
                    insertProduct();
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

        btCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.bt_cross_menu_search){
                    txt_edit.setText("");
                }
            }
        });

        txt_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!s.toString().equals("")){
                    btOk.setVisibility(View.VISIBLE);
                    btCross.setVisibility(View.VISIBLE);
                    bt_speak.setVisibility(View.GONE);
                    btCode.setVisibility(View.GONE);
                }
                else {
                    btOk.setVisibility(View.GONE);
                    btCross.setVisibility(View.GONE);
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
                    insertProduct();
                }
                return false;
            }
        });

        spinner_super.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                    arrayProductosStock = arraySupers.get(spinner_super.getSelectedItemPosition()).getProductos();
                    adaptador_Lista_Compra.setSuper(arraySupers.get(spinner_super.getSelectedItemPosition()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    /**
     * Si el la versión del movil es 23+ podrá utilizar el escaner de código de barras
     */
    private void scannerBarCode() {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            IntentIntegrator integrator = new IntentIntegrator(Lista_compra.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            integrator.setPrompt("Scan");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
   /*     } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Lista_compra.this);
            builder.setTitle("Escaner no disponible");
            builder.setMessage("El escaner no esta disponible para su versión de móvil");
            btCode.setEnabled(false);
        }*/
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

    /***********************
     * RESUME**************
     ***********************/
    public void onResume() {
        super.onResume();

        if(loadData) {

            MySQL mysql = MySQL.getInstance(this);
            SQLiteDatabase db = mysql.getReadableDatabase();
            arraySupers = mysql.cargarSuperMercadosBD(db);
            arrayCategories = mysql.cargarCategorias(db);
            arrayStock = mysql.cargarProductos(db);
            userAccounts = mysql.cargarUsuarios(db);
            db.close();

            arrayProductosStock = arraySupers.get(spinner_super.getSelectedItemPosition()).getProductos();
            ArrayList<String> arrayNombreSupers = new ArrayList<>();
            for(int i=0; i<arraySupers.size(); i++){
                arrayNombreSupers.add(arraySupers.get(i).getNombre());
            }
            spinner_super.setAdapter(new ArrayAdapter<>(Lista_compra.this,
                    android.R.layout.simple_list_item_1, arrayNombreSupers));
            ArrayList<String> arrayNameCategories = new ArrayList<>();
            for(int i=0; i<arrayNameCategories.size(); i++){
                arrayNameCategories.add(arrayCategories.get(i).getNombre());
            }
            adaptador_Lista_Compra.setSuper(arraySupers.get(spinner_super.getSelectedItemPosition()));
            if (mAccount!=null && userAccounts.get(0)!=null){
                //UserAccount user = userAccounts.get(0);
                nameUser.setText(userAccounts.get(0).getName());
                email.setText(userAccounts.get(0).getEmail());
            }
            else{
                Account[] accounts = mAccountManager.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);
                if(accounts.length>0 && userAccounts.get(0)!=null) {
                    mAccount = accounts[0];
                    nameUser.setText(userAccounts.get(0).getName());
                    email.setText(userAccounts.get(0).getEmail());
                }
            }
            loadData=false;
        }
        adaptador_Lista_Compra.setMoney(pref.getString("money","€"));
        if(pref.getBoolean("wake_lock",false)){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        //SI la lista esta vacia
        listView.setEmptyView(findViewById(R.id.text_list_empty));
        updatePrice();


    }

    public void updatePrice(){
        double total=0;

        for(int i = 0; i< arrayProductosStock.size(); i++){
            total += arrayProductosStock.get(i).getPrice()*
                    arrayProductosStock.get(i).getQuantity();
        }
        int numP = arraySupers.get(spinner_super.getSelectedItemPosition()).getNumProductosParaComprar();
        numElement.setText(Integer.toString(numP));
        txtPrecioT.setText(total+" € ");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public void insertProduct(){
        MySQL mysql = MySQL.getInstance(this);
        SQLiteDatabase db = mysql.getReadableDatabase();
        EditText txt_edit = (EditText)findViewById(R.id.txt_lista_productos);
        Producto p = new Producto(txt_edit.getText().toString());
        Spinner spinner_super = (Spinner)findViewById(R.id.spinner_super);
        mysql.anyadir_Producto_Y_Anyadir_A_Lista_SuperMercado(db,arraySupers.get(spinner_super.getSelectedItemPosition()),p);
        txt_edit.clearFocus();
        loadData=true;
        Toast.makeText(this, p.getName() +" creado", Toast.LENGTH_SHORT).show();
        onResume();
        txt_edit.setText("");
        adaptador_Lista_Compra.notifyDataSetChanged();


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
                    MySQL mysql = MySQL.getInstance(this);
                    SQLiteDatabase db = mysql.getReadableDatabase();
                    Producto p = mysql.buscarProductoPorCodigo(result.getContents(), db);
                    if (p != null) {
                        if (p.getName().equals("")) {
                            Toast.makeText(this, "VACIO", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                Intent i = new Intent(this, ViewProduct.class);
                                i.putExtra("Producto_scanner", p);
                                i.putExtra("SuperMercado", arraySupers.get(spinner_super.getSelectedItemPosition()));
                                i.putExtra("Lista Supers", arraySupers);
                                startActivityForResult(i,LOAD_DATA_MYSQL);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {/*PRODUCTO NO EXISTE EN LA BD*/
                        Intent i = new Intent(Lista_compra.this, Add_product.class);
                        i.putExtra("Add_lista","");
                        i.putExtra("SuperMercado",arraySupers.get(spinner_super.getSelectedItemPosition()));
                        try {
                            p=new ProcessJSON(this).execute(result.getContents().toString()).get();
                            if(p!=null){
                                Toast.makeText(Lista_compra.this,"Producto encontrado en Internet",Toast.LENGTH_SHORT).show();
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

    /*public void eliminarProductCheck(){
        int posSig;
        String nombres="";
        for(int i=0; i<arrayProductosStock.size(); i++){
            if(arrayProductosStock.get(i).isCheck()) {
                db = mysql.getWritableDatabase();
                mysql.eliminar_Producto_D_SuperMerc_Producto(arrayProductosStock.get(i).getId(), sp.getId(), db);
                nombres = nombres + "\n" + arrayProductosStock.get(i).getName()+"";
                sp.removeProduct(i);
            }
        }
        adaptador_Lista_Compra.setList(sp.getProductos());
        Toast.makeText(this, nombres + " eliminados", Toast.LENGTH_SHORT).show();
        adaptador_Lista_Compra.notifyDataSetChanged();
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main_menu_buy_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        getMenuInflater().inflate(R.menu.main_menu_buy_list, menu);

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
/*
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

    }*/
/*
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
            for(int i = 0; i< arrayProductosStock.size(); i++){
                product.put("name", arrayProductosStock.get(i).getName());
                //int id, String nombre, double precio, String rutaImagen, String codigo, int categoria, int cantidad, int unidad
                product.put("price", arrayProductosStock.get(i).getPrice());
                product.put("image", arrayProductosStock.get(i).getImagePath());
                product.put("codigo", arrayProductosStock.get(i).getCode());
                for(int x =0; x<arrayCategories.size(); x++){
                    if(arrayCategories.get(x).getId()== arrayProductosStock.get(i).getCategory()){
                        category = new JSONObject();
                        category.put("name", arrayCategories.get(x).getNombre());
                        product.put("category", category);
                    }
                }
                product.put("quantity", arrayProductosStock.get(i).getQuantity());
                product.put("unity", arrayProductosStock.get(i).getUnity());
                arrayProducts.put(product);
            }
            superMJSON.put("productos", arrayProducts);


        }catch (org.json.JSONException e){

        }
        return superMJSON;
    }*/


}
