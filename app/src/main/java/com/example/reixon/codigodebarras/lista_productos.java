package com.example.reixon.codigodebarras;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class lista_productos extends AppCompatActivity {

    private AdapterListStock adapterListPro;
    private ArrayList<String> listaSuperNombre;
    private ArrayList<SuperMerc>arraySupers;
    private ArrayList<Producto>productoTotal;
    private ArrayList<Category>arrayCategories;
    private ListView listView = null;
    private boolean encuentra,filterOn;
    private MenuItem delete, checkAll; //searchItem
    private Button anyadirListBuy;
    private ImageButton bt_speak, btCode, btOk;
    private Spinner spinnerSupers;
    private SearchView sv;
    protected MySQL mysql;
    protected SQLiteDatabase db;
    private boolean loadData, menu_active;
    private static final int LOAD_DATA_MYSQL=100;
    protected PowerManager.WakeLock wakelock;

    private EditText txt_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_productos_stock);
       // encuentra=false;
        anyadirListBuy =(Button)findViewById(R.id.bt_anyadir_listaCompra);
        spinnerSupers =(Spinner)findViewById(R.id.spinner_listSuper);
        bt_speak =(ImageButton)findViewById(R.id.bt_speak);
        txt_edit =(EditText)findViewById(R.id.txt_lista_productos);
        btCode = (ImageButton)findViewById(R.id.bt_scanner_search);
        btOk = (ImageButton)findViewById(R.id.bt_ok_menu_search);


        txt_edit.clearFocus();
        txt_edit.setSingleLine();
        txt_edit.setHorizontallyScrolling(true);
        filterOn=false;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Despensa");
        setSupportActionBar(toolbar);
        mysql = new MySQL(this);
        anyadirListBuy.setVisibility(View.GONE);
        spinnerSupers.setVisibility(View.GONE);
        loadData = false;
        menu_active=false;

        final PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        this.wakelock=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        Toast.makeText(this, "La pantalla se mantendrá encendida hasta pasar de pantalla.", Toast.LENGTH_SHORT).show();
        wakelock.acquire();

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
                    insertarProducto(txt_edit);
                }
            }
        });


        Bundle b = getIntent().getExtras();
        if(b!=null) {
            arraySupers = (ArrayList<SuperMerc>) b.getSerializable("Lista Supers");
            productoTotal = (ArrayList<Producto>)b.getSerializable("Full Products");
            arrayCategories =(ArrayList<Category>) b.getSerializable("Array Categories");
        }

        listaSuperNombre = new ArrayList<String>();
        for (int i = 0; i < arraySupers.size(); i++) {
            listaSuperNombre.add(arraySupers.get(i).getNombre());
        }

        spinnerSupers.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listaSuperNombre));

        btCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getId()==R.id.bt_scanner_search) {
                    IntentIntegrator integrator = new IntentIntegrator(lista_productos.this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                    integrator.setPrompt("Escaneo");
                    integrator.setCameraId(0);
                    integrator.setBeepEnabled(false);
                    integrator.setBarcodeImageEnabled(true);
                    integrator.initiateScan();
                }
            }
        });

        txt_edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if((keyCode == KeyEvent.KEYCODE_ENTER)&& event.getAction()== KeyEvent.ACTION_DOWN){
                        EditText txt = (EditText)v;
                //    if (encuentra==false || productoTotal.size() == 0) {
                        /*Intent intentAddProducto = new Intent(activity_lista_productos.this, Add_product.class);
                        Bundle b = new Bundle();
                        b.putString("AddProducto", txt.getText().toString());
                        intentAddProducto.putExtras(b);
                        txt.clearFocus();
                        txt.setText("");
                        startActivity(intentAddProducto);*/
                    String nombre = txt.getText().toString();
                    adapterListPro.anyadirProducto(nombre);
                    txt.clearFocus();
                    txt.setText("");
                       // encuentra=false;
                /*    }
                    else if(encuentra==true){
                        db = mysql.getWritableDatabase();
                        Producto p =mysql.searchProductoWithName(txt.getText().toString(),db);
                        Bundle b = new Bundle();
                        b.putSerializable("Producto", p);
                        Intent intentViewProduct= new Intent(activity_lista_productos.this, ViewProduct.class);
                        intentViewProduct.putExtras(b);
                        txt.clearFocus();
                        txt.setText("");
                        encuentra=false;
                        startActivity(intentViewProduct);


                    }*/
                }
                return false;
            }
        });

        anyadirListBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean [] checks = adapterListPro.getItemCheck();
                ArrayList<Producto> productos =  arraySupers.get(spinnerSupers.getSelectedItemPosition()).getProductos();
                int numP= productos.size();
                String nombre="";
                boolean b= false;
                for(int i = 0; i< checks.length; i++) {
                    if(checks[i]) {
                        if(numP>0){
                            for(int x=0;x<numP; x++) {
                                if (productos.get(x).getId() != productoTotal.get(i).getId()) {

                                } else {
                                    nombre += productoTotal.get(i).getNombre() + ", ";
                                    b = true;
                                    break;
                                }
                            }
                            if(b==false){
                                db = mysql.getWritableDatabase();
                                mysql.add_Producto_A_Lista_SuperMercado(db,
                                        arraySupers.get(spinnerSupers.getSelectedItemPosition()),
                                        productoTotal.get(i));
                                arraySupers.get(spinnerSupers.getSelectedItemPosition()).
                                        addProduct(productoTotal.get(i));
                            }
                        }else{
                            db = mysql.getWritableDatabase();
                            mysql.add_Producto_A_Lista_SuperMercado(db,
                                    arraySupers.get(spinnerSupers.getSelectedItemPosition()),
                                    productoTotal.get(i));
                            arraySupers.get(spinnerSupers.getSelectedItemPosition()).
                                    addProduct(productoTotal.get(i));
                        }
                    }
                }
                setVisibleMenusActive(false);
                adapterListPro.vaciarArrayCheck();

                if(b){
                    Toast.makeText(lista_productos.this, nombre+" ya esta en la lista", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(lista_productos.this, "Añadido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txt_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               // adapterListPro.getFilter().filter(s.toString().trim());
                if(spinnerSupers.getVisibility()==View.VISIBLE && anyadirListBuy.getVisibility()==View.VISIBLE) {
                    spinnerSupers.setVisibility(View.GONE);
                    anyadirListBuy.setVisibility(View.GONE);
                    adapterListPro.vaciarArrayCheck();
                    setVisibleMenusActive(false);
                }
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

        adapterListPro = new AdapterListStock(this,
                R.layout.stock_product_adapter, productoTotal);

        listView = (ListView) findViewById(R.id.listaProductos);
        listView.setAdapter(adapterListPro);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos,
                                    long id) {
                Producto p = adapterListPro.getSearchList().get(pos);
                Bundle b = new Bundle();

                b.putSerializable("Producto", p);
                b.putSerializable("Lista Supers",arraySupers);
                Intent intentViewProduct= new Intent(lista_productos.this, ViewProduct.class);
                intentViewProduct.putExtras(b);
                startActivityForResult(intentViewProduct,LOAD_DATA_MYSQL);

            }
        });

    }

    public void setProductosTotales(ArrayList<Producto> p){
        productoTotal = p;
    }

    public ArrayList<Producto> getProductosTotales(){
        return productoTotal;
    }

    public void insertarProducto(EditText txt){
        String nombre = txt.getText().toString();
        adapterListPro.anyadirProducto(nombre);
        loadData=true;
        txt.clearFocus();
        txt.setText("");
    }

    public void deleteProductCheck(){
        String nombres="";
        boolean[] itemChecks = adapterListPro.getItemCheck();
        for(int i=0; i<productoTotal.size(); i++){
            if(itemChecks[i]==true) {
                db = mysql.getWritableDatabase();
                mysql.eliminarProducto(productoTotal.get(i).getId(), db);
                nombres = nombres + "\n" + productoTotal.get(i).getNombre();
            }
        }
        db = mysql.getReadableDatabase();
        productoTotal = mysql.loadFullProduct(db);

        Toast.makeText(this, nombres+" eliminado", Toast.LENGTH_SHORT).show();
        this.setVisibleMenusActive(false);
        adapterListPro.setList(productoTotal);
    }

    public void onResume() {
        super.onResume();
        if(loadData){
            db = mysql.getWritableDatabase();
            productoTotal = mysql.loadFullProduct(db);
            arraySupers = mysql.cargarSuperMercadosBD(db);
            loadData=false;
            adapterListPro.setList(productoTotal);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOAD_DATA_MYSQL:
                    loadData=true;
                    break;
                case 1:
                    if (null != data) {
                        ArrayList<String> txtSpeak = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        String txt = txtSpeak.get(0).substring(0, 1).toUpperCase() + txtSpeak.get(0).substring(1);

                        txt_edit.setText(txt);
                    }
                    break;
                case 49374:
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (result.getContents() == null) {
                        Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show();
                    } else {
                        db = mysql.getWritableDatabase();
                        Producto p = mysql.searchProductoWithCode(result.getContents(), db);
                        if (p != null) {
                            if (p.getNombre().equals("")) {
                                Toast.makeText(this, "VACIO", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    Intent i = new Intent(this, ViewProduct.class);
                                    i.putExtra("Producto_scanner", p);
                                    i.putExtra("Lista Supers",arraySupers);
                                    startActivityForResult(i,LOAD_DATA_MYSQL);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                p = new ProcessJSON(this).execute(result.getContents().toString()).get();
                                if (p != null) {
                                    Intent i = new Intent(lista_productos.this, Add_product.class);
                                    i.putExtra("Producto_scanner_internet", p);
                                    startActivityForResult(i,LOAD_DATA_MYSQL);
                                } else {
                                    Intent i = new Intent(lista_productos.this, Add_product.class);
                                    i.putExtra("CODIGO", result.getContents());
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
    }


    @Override
    protected void onStop() {
        super.onStop();
        txt_edit.setText("");
        txt_edit.clearFocus();
    }

    /*Actividad con main_menu de busqueda*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu_products, menu);

        return true;
    }



    public void setVisibleMenusActive(Boolean b){
        if(b){
            anyadirListBuy.setVisibility(View.VISIBLE);
            spinnerSupers.setVisibility(View.VISIBLE);
        }
        else{
            anyadirListBuy.setVisibility(View.GONE);
            spinnerSupers.setVisibility(View.GONE);
        }

        menu_active=b;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.checkAll:
                adapterListPro.setCheckAll();
                break;
            case R.id.EtBorrar_producto:
                DialogInterface.OnClickListener dialogClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            deleteProductCheck();
                            listView.invalidate();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.cancel();
                            break;
                    }
                    }
                };
                AlertDialog.Builder dialog = new AlertDialog.Builder(lista_productos.this);
                dialog.setMessage("¿Quieres eliminarlo?").setPositiveButton("Si",
                        dialogClick).setNegativeButton("No",dialogClick).show();
            return true;
            case R.id.action_settings:
                Toast.makeText(lista_productos.this,"opciones",Toast.LENGTH_SHORT);
                return true;
            case R.id.share_products:
                createFileJSON();
                Toast.makeText(lista_productos.this,"COMPARTIR",Toast.LENGTH_SHORT);
                return true;
            case R.id.download_products:

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(menu_active){
            menu.clear();
            getMenuInflater().inflate(R.menu.main_menu_active, menu);
            if(adapterListPro.getCheckAll() || adapterListPro.getStateCheckMenu()){
                menu.getItem(1).setIcon(R.drawable.icon_check_off);
            }
            else{
                menu.getItem(1).setIcon(R.drawable.icon_check_on);
            }

        }
        else{
            menu.clear();
            getMenuInflater().inflate(R.menu.main_menu_products, menu);

        }

        return super.onPrepareOptionsMenu(menu);
    }

    /*COMPARTIR*/
    private void createFileJSON(){
        JSONArray arrayProdJSON = convertArrayTOJSON();
        Writer output = null;
        try {
        /*    File dirImg = new ContextWrapper(getApplicationContext()).getDir("Archivos", Context.MODE_APPEND);
            File file = new File(dirImg, "listShare.json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(superJSON.toString());
            output.close();*/
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TITLE,"lista  productos JSON");
            sendIntent.putExtra(Intent.EXTRA_TEXT, arrayProdJSON.toString());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private JSONArray convertArrayTOJSON(){
        JSONArray arrayProducts = new JSONArray();
        JSONObject product;
        JSONObject category;

        try {
            product = new JSONObject();
            for(int i=0; i<this.productoTotal.size(); i++){
                product = new JSONObject();
                product.put("name", productoTotal.get(i).getNombre());
                product.put("price", productoTotal.get(i).getPrecio());
                //product.put("image", productoTotal.get(i).getRutaImagen());
                //imagen pasarla a bytes o no pasarla
                product.put("codigo", productoTotal.get(i).getCodigo());
                for(int x =0; x<arrayCategories.size(); x++){
                    if(arrayCategories.get(x).getId()== productoTotal.get(i).getCategoria()){
                        category = new JSONObject();
                        category.put("name", arrayCategories.get(x).getNombre());
                        product.put("category", category);
                    }
                }
                product.put("quantity", productoTotal.get(i).getCantidad());
                product.put("unity",productoTotal.get(i).getUnidad());
                arrayProducts.put(product);
            }


        }catch (org.json.JSONException e){

        }
        return arrayProducts;
    }

}
