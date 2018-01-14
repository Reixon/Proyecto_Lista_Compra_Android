package com.example.reixon.codigodebarras;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class lista_compra extends AppCompatActivity{

    private AdapterListBuyProd adapterListCom = null;
    private ArrayList<Producto> listaProductosCompra;;
    private ArrayList<String> arrayNombreSupers;
    private ArrayList<Category> arrayCategories;
    private ArrayList<SuperMerc>arraySupers;
    private ArrayList<Producto>productoTotal;
    private ListView listView = null;
    private MenuItem delete, searchItem, compartir;
    private Spinner spinner_super;
    private ImageButton edit_listaSuper;
    private boolean encuentra;
    private int unidadSelec;
    private ImageButton bt_speak, btCode, btOk;
    private SuperMerc sp;
    private TextView numElement,txtPrecioT;
    protected PowerManager.WakeLock wakelock;
    private EditText txt_edit;
    private SQLiteDatabase db;
    private MySQL mysql;
    private boolean loadData, menu_active;;
    private static final int LOAD_DATA_MYSQL=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_lista_compra);
        final PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        txt_edit =(EditText)findViewById(R.id.txt_lista_productos);
        spinner_super = (Spinner)findViewById(R.id.spinner_super);
        edit_listaSuper =(ImageButton)findViewById(R.id.bt_edit_listaSuper);
        txtPrecioT = (TextView)findViewById(R.id.txtPrecioTotal);
        numElement = (TextView)findViewById(R.id.txtNumProd);
        bt_speak =(ImageButton)findViewById(R.id.bt_speak);
        txt_edit =(EditText)findViewById(R.id.txt_lista_productos);
        btCode = (ImageButton)findViewById(R.id.bt_scanner_search);
        btOk = (ImageButton)findViewById(R.id.bt_ok_menu_search);
        listView = (ListView) findViewById(R.id.lista_productos_comprar);

        txt_edit.clearFocus();
        txt_edit.setSingleLine();
        txt_edit.setHorizontallyScrolling(true);
        loadData=false;
        menu_active=false;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_lista_compra);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        /*cargar bd*/
        mysql = new MySQL(this);
        if(getIntent().getExtras()!=null){
            Bundle b = getIntent().getExtras();
            if(b.getSerializable("Lista Supers")!=null){
                arraySupers = (ArrayList<SuperMerc>) b.getSerializable("Lista Supers");
            }
            if(b.getSerializable("Array Categories")!=null){
                arrayCategories =(ArrayList<Category>) b.getSerializable("Array Categories");
            }
        }
       // db = mysql.getWritableDatabase();
     //   this.arraySupers = mysql.cargarSuperMercadosBD(db);
        spinner_super.setSelection(0);
        sp = (SuperMerc)arraySupers.get(spinner_super.getSelectedItemPosition());

        this.listaProductosCompra = sp.getProductos();
        adapterListCom = new AdapterListBuyProd(this,R.layout.stock_product_adapter,
                listaProductosCompra);
        listView.setAdapter(adapterListCom);

        arrayNombreSupers = new ArrayList<String>();
        for(int i = 0; i< arraySupers.size(); i++) {
            this.arrayNombreSupers.add(this.arraySupers.get(i).getNombre());
        }
        spinner_super.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrayNombreSupers));

        //LISTENERS
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos,
                                    long id) {
                Producto p = listaProductosCompra.get(pos);
                SuperMerc superM =  arraySupers.get(spinner_super.getSelectedItemPosition());
            //    delete.setVisible(false);
                Bundle b = new Bundle();
                b.putSerializable("Producto", p);
                //b.putSerializable("Lista Supers", arraySupers);
                b.putSerializable("SuperMerc",arraySupers.get(spinner_super.getSelectedItemPosition()));
                b.putBoolean("LISTA_COMPRA",true);
                Intent intentViewProduct= new Intent(lista_compra.this, ViewProduct.class);
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
                    IntentIntegrator integrator = new IntentIntegrator(lista_compra.this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                    integrator.setPrompt("Escaneo");
                    integrator.setCameraId(0);
                    integrator.setBeepEnabled(false);
                    integrator.setBarcodeImageEnabled(true);
                    integrator.initiateScan();
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

        edit_listaSuper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(lista_compra.this);
                builder.setTitle("Añadir lista de compra");

                final EditText edit = new EditText(lista_compra.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                edit.setLayoutParams(lp);
                builder.setView(edit);

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db = mysql.getWritableDatabase();
                        SuperMerc lS = new SuperMerc(edit.getText().toString(), 0, 0);
                        mysql.addSuper(db, lS);
                        arraySupers.add(lS);
                        arrayNombreSupers.add(lS.getNombre());
                        spinner_super.setAdapter(new ArrayAdapter<String>(lista_compra.this,
                                android.R.layout.simple_list_item_1, arrayNombreSupers));
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }

        });

        spinner_super.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){

                sp = (SuperMerc)arraySupers.get(spinner_super.getSelectedItemPosition());
                listaProductosCompra = sp.getProductos();
                adapterListCom.setSuper(sp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        //Mantener la pantalla encendida
        this.wakelock=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        Toast.makeText(this, "La pantalla se mantendrá encendida hasta pasar de pantalla.", Toast.LENGTH_SHORT).show();
        wakelock.acquire();

    }

    public void onResume() {
        super.onResume();
        if(loadData) {
            db = mysql.getWritableDatabase();
            arraySupers = mysql.cargarSuperMercadosBD(db);
            sp = arraySupers.get(spinner_super.getSelectedItemPosition());
            listaProductosCompra = sp.getProductos();
            arrayNombreSupers = new ArrayList<String>();
            for(int i=0; i<arraySupers.size(); i++){
                arrayNombreSupers.add(arraySupers.get(i).getNombre());
            }
            spinner_super.setAdapter(new ArrayAdapter<String>(lista_compra.this,
                    android.R.layout.simple_list_item_1, arrayNombreSupers));
            productoTotal = mysql.loadFullProduct(db);
            adapterListCom.setSuper(sp);
            loadData=false;
        }
        actualizarPrecioYelementos();


    }


    public void insertarProducto(){
        db = mysql.getWritableDatabase();
        Producto p = new Producto(this.txt_edit.getText().toString());
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
                    db = mysql.getWritableDatabase();
                    Producto p = mysql.searchProductoWithCode(result.getContents(), db);
                    if (p != null) {
                        if (p.getNombre().equals("")) {
                            Toast.makeText(this, "VACIO", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                Intent i = new Intent(this, ViewProduct.class);
                                i.putExtra("Producto_scanner", p);
                                i.putExtra("SuperMerc", sp);
                                i.putExtra("Lista Supers", arraySupers);
                                startActivityForResult(i,LOAD_DATA_MYSQL);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Intent i = new Intent(lista_compra.this, Add_product.class);
                        i.putExtra("Add_lista","");
                        i.putExtra("SuperMerc",sp);
                        try {
                            p=new ProcessJSON(this).execute(result.getContents().toString()).get();
                            if(p!=null){
                                i.putExtra("Producto_scanner_internet_Anyadir", p);
                                startActivityForResult(i,LOAD_DATA_MYSQL);
                            }
                            else{
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //cuando acabe la actividad entonces se podrá apagar la pantalla
        wakelock.release();
    }

    public SuperMerc getSuperMerc(){
        return sp;
    }



    public void deleteProductCheck(){
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
        this.delete.setVisible(false);

        adapterListCom.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.main_menu_buy_list, menu);
        return true;
    }

    public void setVisibleDelete(Boolean b){
        menu_active=b;
        invalidateOptionsMenu();
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
            case R.id.checkAll:
                adapterListCom.setCheckAll();
                break;
            case R.id.EtBorrar_producto:
                deleteProductCheck();
                //adapterListCom.deleteProductCheck();
                listView.invalidate();
                actualizarPrecioYelementos();
                return true;
            case R.id.Adm_list:
                Bundle bundle = new Bundle();
                bundle.putSerializable("Lista Supers",arraySupers);
              //  bundle.putStringArrayList("Name_Supers",arrayNombreSupers);
                Intent intent = new Intent(lista_compra.this, Admin_list.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,LOAD_DATA_MYSQL);
                break;
            case R.id.action_settings:
                Toast.makeText(lista_compra.this,"opciones",Toast.LENGTH_SHORT);
                return true;
            case R.id.share_list:
                createFileJSON();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createFileJSON(){
        JSONObject superJSON = convertArrayTOJSON();
        Writer output = null;
        try {
        /*    File dirImg = new ContextWrapper(getApplicationContext()).getDir("Archivos", Context.MODE_APPEND);
            File file = new File(dirImg, "listShare.json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(superJSON.toString());
            output.close();*/
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TITLE,"lista compra JSON");
            sendIntent.putExtra(Intent.EXTRA_TEXT, superJSON.toString());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            Toast.makeText(lista_compra.this,"Datos compartidos",Toast.LENGTH_SHORT);
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
        for(int i=0; i<listaProductosCompra.size(); i++){
            total +=listaProductosCompra.get(i).getPrecio()*
                    listaProductosCompra.get(i).getCantidad();
        }

        int numP = sp.getNumProductosParaComprar();
        numElement.setText(Integer.toString(numP));
        txtPrecioT.setText(total+" € ");
    }

 /*   @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }*/
}
