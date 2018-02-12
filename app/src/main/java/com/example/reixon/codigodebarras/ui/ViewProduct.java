package com.example.reixon.codigodebarras.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reixon.codigodebarras.Model.Category;
import com.example.reixon.codigodebarras.Model.Producto;
import com.example.reixon.codigodebarras.Model.SuperMercado;
import com.example.reixon.codigodebarras.R;
import com.example.reixon.codigodebarras.db.MySQL;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.reixon.codigodebarras.R.id.btEditCategoria;

public class ViewProduct extends AppCompatActivity {

    private final int SELECT_PICTURE =300;
    private final int MY_PERMISSIONS=100;
    private final int PHOTO_CODE=101;

    private ArrayList<Category> arrayCategoria;
    private ArrayList<SuperMercado> arraySuper;
    private int unidadSelec;
    private Producto producto;
    private String imagePath;
    private boolean photo, listBuy;

    private ImageButton scanBtn, btPhot, btCategoria;
    private Spinner spinner_categorias,spinnerListaCompra,spinnerUnidad;
    private TextView txtPrecioTotal,etPrecioTotal;
    private Button anyadirListaCompra;
    private EditText txtCant, txtPrecioProducto, txtNameProduct;
    private LinearLayout layout_cant;
    private RelativeLayout layout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        txtNameProduct = (EditText) findViewById(R.id.txtNombre);
        txtPrecioProducto = (EditText) findViewById(R.id.txtPrecio);
        scanBtn = (ImageButton) findViewById(R.id.btBarCode);
        btPhot = (ImageButton) findViewById(R.id.imageProd);
        spinner_categorias = (Spinner) findViewById(R.id.spinner_categoria_view);
        btCategoria = (ImageButton) findViewById(btEditCategoria);
        TextView txtScan = (TextView) findViewById(R.id.txtCodigo);
        anyadirListaCompra = (Button) findViewById(R.id.bt_anyadir_listaCompra_proView);
        spinnerListaCompra =(Spinner)findViewById(R.id.spinner_listSuper_proView);
        spinnerUnidad = (Spinner)findViewById(R.id.spinner_unidad);
        txtCant =(EditText)findViewById(R.id.txtCantidad_View);
        txtPrecioTotal = (TextView)findViewById(R.id.txtPrecioTotalView);
        etPrecioTotal = (TextView)findViewById(R.id.etPrecioTotalView);
        layout_cant =(LinearLayout)findViewById(R.id.layout_cantidad);
        layout = (RelativeLayout)findViewById(R.id.layout_view_product);

        listBuy=false;

        txtPrecioTotal.setVisibility(View.GONE);
        etPrecioTotal.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_viewProduct);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*Permisos para realizar la captura de fotos*/
        if(myRequestStoragePermission()){
            btPhot.setEnabled(true);
        }
        else{
            btPhot.setEnabled(false);
        }

        String [] arrayUnidad = {"unidad","lata","botella","paquete","caja","bolsa","mg","gr","kg",
        "ml","cl","litro"};


        MySQL mysql = MySQL.getInstance(getApplicationContext());
        SQLiteDatabase db = mysql.getWritableDatabase();
        arrayCategoria = mysql.cargarCategorias(db);

        ArrayList<String> arrayCategoriaN = new ArrayList<String>();

        for (int i = 0; i < arrayCategoria.size(); i++) {
            arrayCategoriaN.add(arrayCategoria.get(i).getNombre());
        }
        spinnerUnidad.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrayUnidad));

        /*Cargar datos de activity anterior*/
        if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
            if(b.getSerializable("Lista Supers")!=null){
                arraySuper = (ArrayList<SuperMercado>) b.getSerializable("Lista Supers");
                ArrayList<String>  arrayNombreSupers = new ArrayList<String>();
                for (int i = 0; i < arraySuper.size(); i++) {
                    arrayNombreSupers.add(arraySuper.get(i).getNombre());
                }

                spinnerListaCompra.setAdapter(new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, arrayNombreSupers));
            }
            if (b.getSerializable("Producto") != null) {
                Log.d("MyApp", "Existe un producto desde lista");
                producto = (Producto) b.getSerializable("Producto");
                txtNameProduct.setText(producto.getName());
                txtPrecioProducto.setText(producto.getPrice()+"");
                txtScan.setText(producto.getCode());
                spinnerUnidad.setSelection(producto.getUnity());
                txtScan.setVisibility(View.VISIBLE);
                b.remove("Producto");
                if(b.getBoolean("LISTA_COMPRA")){
                    layout_cant.setVisibility(View.VISIBLE);
                    double precioT = producto.getQuantity()* producto.getPrice();
                    txtCant.setText(producto.getQuantity()+"");
                    if(producto.getPrice()>0){
                        txtPrecioTotal.setVisibility(View.VISIBLE);
                        etPrecioTotal.setVisibility(View.VISIBLE);
                        txtPrecioTotal.setText(Double.toString(precioT));
                    }
                    spinnerListaCompra.setVisibility(View.GONE);
                    anyadirListaCompra.setVisibility(View.GONE);
                    listBuy=true;
                }
                else{
                    layout_cant.setVisibility(View.GONE);
                }

                if(producto.getImagePath().equals("")){
                    btPhot.setImageDrawable(getResources().getDrawable(R.drawable.photo_icon));
                }
                else {
                    try {
                        Uri imageUri = Uri.parse(producto.getImagePath());
                        File file = new File(imageUri.getPath());
                        InputStream ims = new FileInputStream(file);

                        Bitmap image = BitmapFactory.decodeStream(ims);
                        btPhot.setImageBitmap(image);
                    }catch (Exception e){

                    }
                }
                for (int i = 0; i < arrayCategoria.size(); i++) {
                    if (arrayCategoria.get(i).getId() == producto.getCategory()) {
                       unidadSelec=i;
                    }
                }


            } else if (b.getSerializable("Producto_scanner") != null) {
                Log.d("MyApp", "Existe un producto desde scanner");

                producto = (Producto) b.getSerializable("Producto_scanner");

                txtNameProduct.setText(producto.getName());
                txtPrecioProducto.setText(producto.getPrice()+"");
                txtScan.setText(producto.getCode());
                Log.d("MyApp", "Ruta imagen '"+ producto.getImagePath()+"'");
                if(producto.getImagePath().equals("")){
                    Log.d("MyApp", "Imagen vacia");

                    btPhot.setImageDrawable(getResources().getDrawable(R.drawable.photo_icon));
                    //btPhot.setImageResource(R.drawable.photo_icon);
                }
                else {
                    try {
                        Uri imageUri = Uri.parse(producto.getImagePath());
                        File file = new File(imageUri.getPath());
                        InputStream ims = new FileInputStream(file);

                        Bitmap image = BitmapFactory.decodeStream(ims);
                        btPhot.setImageBitmap(image);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < arrayCategoria.size(); i++) {
                    if (arrayCategoria.get(i).getId() == producto.getCategory()) {
                        unidadSelec=i;
                    }
                }
                txtScan.setVisibility(View.VISIBLE);
                b.remove("Producto_scanner");
            }
            spinner_categorias.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayCategoriaN));
            spinner_categorias.setSelection(unidadSelec);
            this.setTitle(producto.getName());
        }

        anyadirListaCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num=  arraySuper.get(spinnerListaCompra.getSelectedItemPosition()).getProductos().size();
                ArrayList<Producto> productos = (ArrayList<Producto>)arraySuper.get(spinnerListaCompra.getSelectedItemPosition()).getProductos();
                boolean b=false;
                if(num>0) {
                    for (int i = 0; i < num; i++) {
                        if (productos.get(i).getId() == producto.getId()) {
                            b=true;
                        }
                    }
                }
                if(!b){
                    MySQL mysql = MySQL.getInstance(getApplicationContext());
                    SQLiteDatabase db = mysql.getWritableDatabase();
                    mysql.anyadir_Producto_A_Lista_SuperMercado(db,
                            arraySuper.get(spinnerListaCompra.getSelectedItemPosition()), producto);
                    arraySuper = mysql.cargarSuperMercadosBD(db);
                    Toast.makeText(ViewProduct.this, "Añadido", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                }
                else{
                    Toast.makeText(ViewProduct.this, "Ya existe en esa lista", Toast.LENGTH_SHORT).show();
                }

            }
        });

   /*     spinnerListaCompra.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                Spinner spinnerListaCompra =(Spinner)findViewById(R.id.spinner_listSuper_proView);
                superMerc = (SuperMercado)arraySuper.get(spinnerListaCompra.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });*/

        btPhot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            if(v.getId() == R.id.imageProd){
                final CharSequence [] option ={"Tomar foto", "Galeria","Cancelar"};
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(ViewProduct.this);
                builder.setTitle("Elige una opcion");
                builder.setItems(option, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    Log.d("MyApp", "caso "+which);
                    Intent intent;
                    switch(which){
                        case 0:
                            takePintureIntent();
                            break;
                        case 1:
                            intent = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.
                                            EXTERNAL_CONTENT_URI);
                            //indico que tipo de imagens quiero que se puedan ver jpg, png, etc
                            intent.setType("image/*");
                            //mostrara una lista de aplicaciones que se puedan elegir imagenes.
                            startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"),SELECT_PICTURE);
                            break;
                        case 2:
                            dialog.dismiss();
                            break;
                    }
                    }
                });
                builder.show();
            }
            }
        });

        spinner_categorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v,int pos, long id)
            {unidadSelec = pos;}

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                unidadSelec = 0;
            }

        });
        Log.d("MyApp", "desp");
        btCategoria.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == btCategoria.getId()) {

                    /******Ventana de introducir arrayCategoria*******/
                    AlertDialog.Builder alert = new AlertDialog.Builder(ViewProduct.this);
                    alert.setTitle("Añadir arrayCategoria");

                    final EditText input = new EditText(ViewProduct.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    alert.setView(input);

                    /******Aceptar**********/
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MySQL mysql = MySQL.getInstance(getApplicationContext());
                            SQLiteDatabase db = mysql.getWritableDatabase();
                            mysql.anyadirCategoria(input.getText().toString(), db);
                            db = mysql.getWritableDatabase();
                            arrayCategoria = mysql.cargarCategorias(db);
                            ArrayList<String> arrayCategoriaN = new ArrayList<String>();
                            for (int i = 0; i < arrayCategoria.size(); i++) {
                                arrayCategoriaN.add(arrayCategoria.get(i).getNombre());
                            }
                            arrayCategoriaN.add(input.getText().toString());
                            spinner_categorias.setAdapter(new ArrayAdapter<String>(ViewProduct.this, android.R.layout.simple_list_item_1, arrayCategoriaN));
                            unidadSelec = arrayCategoriaN.size() - 1;
                            spinner_categorias.setSelection(unidadSelec);
                        }
                    });
                    /******Cancelar ****/
                    alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
            }

        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btBarCode:
                        scannerBarCode();
                        break;
                }
            }
        });


        txtCant.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double total;

                if(!s.toString().equals("") && !txtPrecioProducto.getText().toString().equals(""))
                {
                    int cant = Integer.parseInt(s.toString());
                    double pr = Double.parseDouble(txtPrecioProducto.getText().toString());
                    total = pr * cant;
                    if (total > 0) {
                        txtPrecioTotal.setVisibility(View.VISIBLE);
                        etPrecioTotal.setVisibility(View.VISIBLE);
                        txtPrecioTotal.setText(Double.toString(total));
                    }
                }
                else{
                    txtCant.setHint("cantidad");
                    txtPrecioTotal.setVisibility(View.GONE);
                    etPrecioTotal.setVisibility(View.GONE);
                }
            }
        });

        txtPrecioProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double total;

                if(!txtCant.getText().toString().equals("")) {
                    int cantidad = Integer.parseInt(txtCant.getText().toString());
                    if (!s.toString().equals("") && cantidad > 0) {
                        txtPrecioTotal.setVisibility(View.VISIBLE);
                        etPrecioTotal.setVisibility(View.VISIBLE);
                        double pr = Double.parseDouble(s.toString());
                        if (pr >= 0) {
                            pr = cantidad*pr;
                            txtPrecioTotal.setText(Double.toString(pr));
                        }
                    }
                }
                else{
                    txtPrecioTotal.setVisibility(View.GONE);
                    txtPrecioTotal.setHint("Precio");
                    txtPrecioTotal.setText("0");
                }
            }
        });


    }

    private void scannerBarCode() {

    //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            IntentIntegrator integrator = new IntentIntegrator(ViewProduct.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            integrator.setPrompt("Scan");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
    /*    } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewProduct.this);
            builder.setTitle("Escaner no disponible");
            builder.setMessage("El escaner no esta disponible para su versión de móvil");
            scanBtn.setEnabled(false);
        }*/
    }


    private boolean myRequestStoragePermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    && (checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED))
                btPhot.setEnabled(true);
                return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) ||
                    (shouldShowRequestPermissionRationale(CAMERA))){
                Snackbar.make(layout,"Los permisos son necesarios para poder usar la aplicacion",Snackbar.
                        LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener(){

                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},MY_PERMISSIONS);
                    }
                });
            }else{
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},MY_PERMISSIONS);
            }
        }
        return false;
    }

    private void takePintureIntent(){
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra("crop",true);
            intent.putExtra("aspectX",3);
            intent.putExtra("aspectY",4);
            intent.putExtra("outputX",256);
            intent.putExtra("outputY",256);
            intent.putExtra("return-intent",true);
            startActivityForResult(intent,PHOTO_CODE);
        }
        catch (Exception e){
            String error = "Ocurrio un error en la imagen";
            Toast.makeText(ViewProduct.this, error, Toast.LENGTH_SHORT).show();
        }


    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        MenuItem menu_aceptar = menu.findItem(R.id.menu_aceptar);
        MenuItem menu_eliminar = menu.findItem(R.id.menu_borrar);

        menu_aceptar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            int idCategoria, cant=1;
            Spinner spinner_categorias = (Spinner) findViewById(R.id.spinner_categoria_view);
            EditText txtCant =(EditText)findViewById(R.id.txtCantidad_View);
                EditText txtNameProduct = (EditText) findViewById(R.id.txtNombre);
                EditText txtPrecioProducto = (EditText) findViewById(R.id.txtPrecio);
                TextView txtScan = (TextView) findViewById(R.id.txtCodigo);
                Spinner spinnerUnidad = (Spinner)findViewById(R.id.spinner_unidad);
                idCategoria = arrayCategoria.get(spinner_categorias.getSelectedItemPosition()).getId();

            if(!txtCant.getText().toString().equals("")){
                cant=Integer.parseInt(txtCant.getText().toString());
            }
            if(!txtNameProduct.getText().toString().equals("")&& !txtPrecioProducto.getText().toString().equals("")) {
                //comprobamos si se ha realizado una foto y borramos la imagen anterior
                if(photo ){
                    //habia una foto anteriormente, eliminamos la anterior
                    if(!producto.getImagePath().equals("")){
                        deleteFileImage();
                    }
                    imagePath= loadImagePath();
                }
                else{
                    imagePath= producto.getImagePath();
                }
                Producto px = new Producto(producto.getId(), txtNameProduct.getText().toString(),
                        Double.parseDouble(txtPrecioProducto.getText().toString()), imagePath,
                        txtScan.getText().toString(),
                        idCategoria, spinnerUnidad.getSelectedItemPosition());
                px.setQuantity(cant);
                MySQL mysql = MySQL.getInstance(getApplicationContext());
                SQLiteDatabase db = mysql.getWritableDatabase();
                mysql.modificarProducto(px, db);
                Bundle b = getIntent().getExtras();
                if (b.getSerializable("SuperMercado") != null) {
                    db = mysql.getWritableDatabase();
                    mysql.modificarSuperMerc_Productos(Integer.toString(px.getQuantity()), "cantidad", px, arraySuper.get(spinnerListaCompra.getSelectedItemPosition()), db);
                }
                Toast.makeText(ViewProduct.this, px.getName()+" modificado", Toast.LENGTH_SHORT).show();

                setResult(RESULT_OK);
                finish();
            } else{
                Toast.makeText(ViewProduct.this, "Introduzca el nombre del producto",
                        Toast.LENGTH_SHORT).show();
            }
            return true;
            }
        });

        menu_eliminar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewProduct.this);
                builder.setTitle("Eliminar Producto");
                builder.setMessage("¿Quieres eliminar el producto?");

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    boolean b=false;
                    if(!producto.getImagePath().equals("")) {
                        b=deleteFileImage();
                    }
                    else{
                        b=true;
                    }
                    if(b){
                        if(listBuy){
                            //eliminar de la lista
                            MySQL mysql = MySQL.getInstance(getApplicationContext());
                            SQLiteDatabase db = mysql.getWritableDatabase();
                            mysql.eliminar_Producto_D_SuperMerc_Producto(producto.getId(),arraySuper.get(spinnerListaCompra.getSelectedItemPosition()).getId(),db);
                            setResult(RESULT_OK);
                        }else {
                            EditText txtNameProduct = (EditText) findViewById(R.id.txtNombre);
                            MySQL mysql = MySQL.getInstance(getApplicationContext());
                            SQLiteDatabase db = mysql.getWritableDatabase();
                            mysql.eliminarProducto(producto.getId(), db);
                            Toast.makeText(ViewProduct.this, txtNameProduct.getText().toString() + " eliminado",
                                    Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                        }
                        ViewProduct.this.finish();
                    }
                    else{
                        Toast.makeText(ViewProduct.this, " Ocurrio un problema al eliminar el producto",
                                Toast.LENGTH_SHORT).show();
                        setResult(RESULT_CANCELED);
                        ViewProduct.this.finish();
                    }
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                builder.show();




                return true;
            }
        });
        return true;
    }

    private boolean deleteFileImage(){
        Boolean b=false;
        File file = new File(producto.getImagePath());
        if(file.exists()){
            if(file.delete()){
                b=true;
            }
        }
        return b;
    }

    private String loadImagePath(){
        File ruta=null;
        String path="";
        try {
            EditText txtNameProduct = (EditText) findViewById(R.id.txtNombre);
            ImageButton btPhot = (ImageButton) findViewById(R.id.imageProd);
            Bitmap bitmap = ((BitmapDrawable) btPhot.getDrawable()).getBitmap();
            File dirImg = new ContextWrapper(getApplicationContext()).getDir("Imagenes", Context.MODE_PRIVATE);
            ruta=  new File(dirImg, txtNameProduct.getText().toString()+".png");
            FileOutputStream out = new FileOutputStream(ruta);
            bitmap.compress(Bitmap.CompressFormat.PNG,10,out);
            out.flush();
            path= ruta.getAbsolutePath();


        }catch (Exception e){
            e.printStackTrace();
        }
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Bundle extras;
        super.onActivityResult(requestCode, resultCode, data);
        TextView txtScan = (TextView) findViewById(R.id.txtCodigo);
        if(resultCode == RESULT_OK) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                    resultCode, data);
            if (result != null) {
                Log.d("MyApp", " Scanner result");
                if (result.getContents() == null) {
                    Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("MyApp", "SCANNER");
                    txtScan.setText(result.getContents());
                    txtScan.setVisibility(View.VISIBLE);
                }
            } else {

                switch (requestCode) {

                    case PHOTO_CODE:
                        extras = data.getExtras();
                        if(extras!=null){
                            ImageButton btPhot = (ImageButton) findViewById(R.id.imageProd);
                            Bitmap phot = extras.getParcelable("data");
                            btPhot.setImageBitmap(phot);
                            photo=true;
                        }
                        break;
                    case SELECT_PICTURE:

                        break;

                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}


