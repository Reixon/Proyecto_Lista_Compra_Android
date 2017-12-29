package com.example.reixon.codigodebarras;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class lista_productos extends AppCompatActivity {

    private AdapterListDespensa adapterListPro = null;
    private ArrayList<String> listaProductosNombre, listaSuperNombre;
    private ArrayList<SuperMerc>arraySupers;
    private ArrayList<Producto>productoTotal;
    private ListView listView = null;
    private boolean encuentra,filterOn;
    private MenuItem delete, compartir; //searchItem
    private Button anyadirListBuy;
    private ImageButton bt_speak, btCode, btOk;
    private Spinner spinnerSupers;
    private SearchView sv;
    protected MySQL mysql;
    protected SQLiteDatabase db;
    private boolean loadData;
    private static final int LOAD_DATA_MYSQL=100;

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



    }

    public void setProductosTotales(ArrayList<Producto> p){
        productoTotal = p;
    }
    public ArrayList<Producto> getProductosTotales(){
        return productoTotal;
    }

    public void setVisibleDelete(Boolean b){

        delete.setVisible(b);
    }

    public void setVisibleSpinnersSupers(int n){
        spinnerSupers.setVisibility(n);
    }

    public void setVisibleButtomAnyadir(int n){
        anyadirListBuy.setVisibility(n);
    }

    public SuperMerc getSuperWithSpinnersPosition(int n){
        return arraySupers.get(n);
    }

    public int getVisibilitySpinnerSupers(){
        return this.spinnerSupers.getVisibility();
    }

    public int getVisibilityButtonAnyadir(){
        return this.anyadirListBuy.getVisibility();
    }

    public int getSpinnersSupersSelectedPosition(){
        return spinnerSupers.getSelectedItemPosition();
    }

    public void addProductListSuper(Producto prod){
        arraySupers.get(spinnerSupers.getSelectedItemPosition()).addProduct(prod);
    }

    public void insertarProducto(EditText txt){
        String nombre = txt.getText().toString();
        adapterListPro.anyadirProducto(nombre);
        loadData=true;
        txt.clearFocus();
        txt.setText("");
    }




    public void onResume() {
        super.onResume();
        Log.d("MyApp", "*** ON RESUME LISTA PRODUCTOS *** ");
        if(loadData){
            db = mysql.getWritableDatabase();
            productoTotal = mysql.loadFullProduct(db);
            loadData=false;
        }
        txt_edit.clearFocus();
        adapterView();

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

    private void adapterView() {

    //    Log.d("MyApp", " Adapter");
        adapterListPro = new AdapterListDespensa(this,
                R.layout.adapter_producto_stock, productoTotal);

        listView = (ListView) findViewById(R.id.listaProductos);
        listView.setAdapter(adapterListPro);
        listView.setTextFilterEnabled(true);

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

                //startActivity(intentViewProduct);
              //  finish();
            }
        });
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

        getMenuInflater().inflate(R.menu.main_menu_activity, menu);
        //searchItem = main_menu.findItem(R.id.search_list_products);
       // compartir = main_menu.findItem(R.id.compartir);
        //sv = (SearchView) MenuItemCompat.getActionView(searchItem);
        //sv.setQueryHint("Buscar o Añadir Producto");

     //   delete = menu.findItem(R.id.EtBorrar_producto);
     //   delete.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.EtBorrar_producto:
                DialogInterface.OnClickListener dialogClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                adapterListPro.deleteProductCheck();
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
            case R.id.share:
                Toast.makeText(lista_productos.this,"COMPARTIR",Toast.LENGTH_SHORT);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
