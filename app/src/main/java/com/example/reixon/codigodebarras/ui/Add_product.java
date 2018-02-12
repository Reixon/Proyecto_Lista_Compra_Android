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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class Add_product extends AppCompatActivity {

    private final int SELECT_PICTURE =300;
    private final int MY_PERMISSIONS=100;
    private final int PHOTO_CODE=200;

    private Producto producto;
    private ArrayList<Category> arrayCategoria;
    private int unidadSelec;
    private String imagePath;
    private boolean addList;
    private SuperMercado superMerc;
    private boolean photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        EditText txtNameProduct = (EditText) findViewById(R.id.txtNombre);
        EditText txtPrecioProducto = (EditText) findViewById(R.id.txtPrecio);
        ImageButton scanBtn = (ImageButton) findViewById(R.id.btBarCode);
        ImageButton btPhot = (ImageButton) findViewById(R.id.btPhoto);
        Spinner spinner_categorias = (Spinner) findViewById(R.id.spinner_categoria_add);
        ImageButton btCategoria = (ImageButton) findViewById(R.id.btEditCategoria);
        TextView txtScan = (TextView) findViewById(R.id.txtCodigo);
        Spinner spinnerUnidad= (Spinner)findViewById(R.id.spinnerUnidad_AddP);
        imagePath="";
        addList=false;
        Log.d("MyApp", "ADD PRODUCT");
        String [] arrayUnidad ={"unidad","lata","botella","paquete","caja","bolsa","mg","gr","kg",
                "ml","cl","litro"};

        this.setTitle("Añadir Producto");

        /*Cargar base de datos de categorias*/

        MySQL mysql = MySQL.getInstance(this);
        SQLiteDatabase db = mysql.getWritableDatabase();
        mysql = new MySQL(this);
        arrayCategoria = mysql.cargarCategorias(db);
        ArrayList<String> arrayCategoriaN = new ArrayList<String>();
        for (int i = 0; i < arrayCategoria.size(); i++) {
            arrayCategoriaN.add(arrayCategoria.get(i).getNombre());
        }
        unidadSelec = 0;

        if(myRequestStoragePermission()){
            btPhot.setEnabled(true);
        }
        else{
            btPhot.setEnabled(false);
        }

        spinnerUnidad.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrayUnidad));

         spinner_categorias.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrayCategoriaN));

        if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
           /* if (b.getSerializable("Producto") != null) {
                producto = (Producto) b.getSerializable("Producto");
                this.txtNameProduct.setText(producto.getName());
                this.txtPrecioProducto.setText(producto.getPrice());
                this.txtScan.setText(producto.getCode());
                txtScan.setVisibility(View.VISIBLE);
            }*/
           if(b.get("Add_lista")!=null){
               addList=true;
               superMerc = (SuperMercado) b.get("SuperMercado");
            }

            if (b.get("AddProducto") != null) {
                txtNameProduct.setText(b.getString("AddProducto"));
                txtScan.setVisibility(View.VISIBLE);
                b.remove("AddProducto");
                producto =  new Producto();
            } else if (b.get("CODIGO") != null) {
                txtScan.setText(b.getString("CODIGO"));
                txtScan.setVisibility(View.VISIBLE);
                b.remove("CODIGO");
                producto =  new Producto();
                producto.setCode(b.getString("CODIGO"));
            } else if (b.getSerializable("Producto_scanner_internet_Anyadir") != null) {
                Log.d("MyApp", "Existe un producto desde internet");
                producto = (Producto) b.getSerializable("Producto_scanner_internet_Anyadir");
                btPhot = (ImageButton) findViewById(R.id.btPhoto);
                btPhot.setEnabled(false);
                txtNameProduct.setText(producto.getName());
                txtScan.setText(producto.getCode());
                txtScan.setVisibility(View.VISIBLE);
                Log.d("MyApp", "Ruta imagen '" + producto.getImagePath() + "'");
                if (producto.getImagePath().equals("")) {
                    Log.d("MyApp", "Imagen vacia");
                    btPhot.setImageDrawable(getResources().getDrawable(R.drawable.photo_icon));
                    //btPhot.setImageResource(R.drawable.photo_icon);
                }
                else {
                    new CargarImagenInternet().execute(producto.getImagePath());
                }
                Toast.makeText(this, "Producto encontrado en Internet.", Toast.LENGTH_LONG).show();

            }
            txtPrecioProducto.setText("0.0");
        }


        spinner_categorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int pos, long id) {
                Spinner spinner_categorias = (Spinner) findViewById(R.id.spinner_categoria_add);
                if(v.getId()==spinner_categorias.getId()) {
                    Log.d("MyApp", "pos categoria " + pos);
                    unidadSelec = pos;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                unidadSelec = 0;
                Log.d("MyApp", "No selecionado ninguna");
            }

        });

        btCategoria.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btEditCategoria) {

                /******Ventana de introducir arrayCategoria*******/

                AlertDialog.Builder alert = new AlertDialog.Builder(Add_product.this);
                alert.setTitle("Añadir arrayCategoria");

                final EditText input = new EditText(Add_product.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alert.setView(input);

                /******Aceptar**********/
                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("MyApp", "Aceptar categoria");
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
                        Spinner spinner_categorias = (Spinner) findViewById(R.id.spinner_categoria_add);
                        spinner_categorias.setAdapter(new ArrayAdapter<String>(Add_product.this, android.R.layout.simple_list_item_1, arrayCategoriaN));
                        Log.d("MyApp", "spinner");
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

        btPhot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.btPhoto){
                        final CharSequence [] option ={"Tomar foto", "Galeria","Cancelar"};
                        final AlertDialog.Builder builder =
                                new AlertDialog.Builder(Add_product.this);
                        builder.setTitle("Elige una opcion");
                        builder.setItems(option, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("MyApp", "caso "+which);
                                Intent intent;
                                switch(which){
                                    case 0:
                                        takePictureIntent();
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

    }

    private void scannerBarCode() {

     //   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.) {
            IntentIntegrator integrator = new IntentIntegrator(Add_product.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            integrator.setPrompt("Scan");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
      //  } else {
    /*        AlertDialog.Builder builder = new AlertDialog.Builder(Add_product.this);
            builder.setTitle("Escaner no disponible");
            builder.setMessage("El escaner no esta disponible para su versión de móvil");
            ImageButton scanBtn = (ImageButton) findViewById(R.id.btBarCode);
            scanBtn.setEnabled(false);
        }*/
    }

    private void takePictureIntent(){
        try {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
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
            Toast.makeText(Add_product.this, error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", imagePath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imagePath = savedInstanceState.getString("file_path");
    }


    private boolean myRequestStoragePermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    && (checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED))
                return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) ||
                    (shouldShowRequestPermissionRationale(CAMERA))){
                ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.layout_add_product);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_product, menu);
        MenuItem menu_aceptar = menu.findItem(R.id.menu_aceptar);
        MenuItem menu_cancelar = menu.findItem(R.id.menu_borrar);

        menu_aceptar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d("MyApp","Aceptar");
                int idCategoria;
                EditText txtNameProduct = (EditText) findViewById(R.id.txtNombre);
                idCategoria = arrayCategoria.get(unidadSelec).getId();

                //guardar la imagen en la memoria del movil y cambiar la direccion de la imagen de producto
                if(!txtNameProduct.getText().toString().equals("")) {
                    if(imagePath.equals("") && !producto.getImagePath().equals("")){
                        try {
                            imagePath = new ObtenerRutaImagenDeInternet().execute(producto.getImagePath(),txtNameProduct.getText().toString() ).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        //si se hizo una foto
                        if(photo)
                        {
                            imagePath= loadImagePath();
                        }
                    }
                    //Si la activity es llamada desde lista de compra, añadiremos el producto a la lista
                    if(addList){
                        anyadirProductoListaCompra(idCategoria);
                    }
                    else {
                        MySQL mysql = MySQL.getInstance(getApplicationContext());
                        SQLiteDatabase db = mysql.getWritableDatabase();
                        Spinner spinnerUnidad= (Spinner)findViewById(R.id.spinnerUnidad_AddP);
                        TextView txtScan = (TextView) findViewById(R.id.txtCodigo);
                        EditText txtPrecioProducto = (EditText) findViewById(R.id.txtPrecio);

                        mysql.insertProducto(txtNameProduct.getText().toString(),
                                txtPrecioProducto.getText().toString(),
                                imagePath, txtScan.getText().toString(),
                                idCategoria, spinnerUnidad.getSelectedItemPosition(), db);
                    }
                    Toast.makeText(Add_product.this, txtNameProduct.getText().toString() +
                            " creado", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    Add_product.this.finish();
                }
                else{
                    Toast.makeText(Add_product.this, "Introduzca el nombre del producto",
                            Toast.LENGTH_SHORT).show();
                }
                return true;

            }
        });

        menu_cancelar.setIcon(null);
        menu_cancelar.setTitle("Cancelar");
        menu_cancelar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Add_product.this.finish();
                return false;
            }
        });
        return true;
    }

    /****************************
    *****Carga la ruta de la foto
    * ***********************/
    private String loadImagePath(){
        String path="";
        ImageButton btPhot = (ImageButton) findViewById(R.id.btPhoto);
        EditText txtNameProduct = (EditText) findViewById(R.id.txtNombre);
        try {
            Bitmap bitmap = ((BitmapDrawable) btPhot.getDrawable()).getBitmap();
            File dirImg = new ContextWrapper(getApplicationContext()).getDir("Imagenes", Context.MODE_PRIVATE);
            File ruta=  new File(dirImg, txtNameProduct.getText().toString()+".png");
            FileOutputStream out = new FileOutputStream(ruta);
            bitmap.compress(Bitmap.CompressFormat.PNG,10,out);
            out.flush();
            path=ruta.getAbsolutePath();

        }catch (Exception e){e.printStackTrace();}
        return path;
    }

    public void anyadirProductoListaCompra(int idCategoria){

        Spinner spinnerUnidad= (Spinner)findViewById(R.id.spinnerUnidad_AddP);
        TextView txtScan = (TextView) findViewById(R.id.txtCodigo);
        EditText txtNameProduct = (EditText) findViewById(R.id.txtNombre);
        EditText txtPrecioProducto = (EditText) findViewById(R.id.txtPrecio);

        MySQL mysql = MySQL.getInstance(this);
        SQLiteDatabase db = mysql.getWritableDatabase();
        producto = new Producto(-1, txtNameProduct.getText().toString(),
                Double.parseDouble(txtPrecioProducto.getText().toString()),
                imagePath, txtScan.getText().toString(),
                idCategoria, spinnerUnidad.getSelectedItemPosition());

        mysql.anyadir_Producto_Y_Anyadir_A_Lista_SuperMercado(db, superMerc,producto);

    }

    /*Si queremos guardar el producto debemos almacenar la imagen, que es una URL y pasarla a un
    * mapabit */
    private class ObtenerRutaImagenDeInternet extends  AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            Bitmap imagen =null;
            URL imageUrl = null;
            File dirImg = new ContextWrapper(getApplicationContext()).getDir("Imagenes", Context.MODE_PRIVATE);
            File ruta=  new File(dirImg, params[1]+".png");
            try {
                imageUrl = new URL(params[0]);
                HttpURLConnection conex = (HttpURLConnection) imageUrl.openConnection();
                conex.connect();
                imagen = BitmapFactory.decodeStream(conex.getInputStream());
                FileOutputStream fos = new FileOutputStream(ruta);
                imagen.compress(Bitmap.CompressFormat.PNG,10,fos);
                fos.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return ruta.getAbsolutePath();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                ImageButton btPhot = (ImageButton) findViewById(R.id.btPhoto);
                btPhot.setEnabled(true);
            }
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Add_product.this);
            builder.setTitle("Permisos denegados");
            builder.setMessage("Para usar las funciones de la App necesitas aceptar los permisos");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
                    //abrira la configuracion de esta aplicacion
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Bundle extras;
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("MyApp", " Result");
        if(resultCode == RESULT_OK){
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                    resultCode, data);
            if (result != null) {
                /*ESCANER*/
                Log.d("MyApp", " Scanner result");
                if (result.getContents() == null) {
                    Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("MyApp", "SCANNER");
                    TextView txtScan = (TextView) findViewById(R.id.txtCodigo);
                    txtScan.setText(result.getContents());
                    txtScan.setVisibility(View.VISIBLE);
                }
            } else {
                switch (requestCode) {
                    case PHOTO_CODE:
                        extras = data.getExtras();
                        if(extras!=null){
                            Bitmap phot = extras.getParcelable("data");
                            ImageButton btPhot = (ImageButton) findViewById(R.id.btPhoto);
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

    /*En el caso que el producto se haya cargado mediante el escaner de codigo de barras,
    * se cargará la imagen del producto que es una URL*/
   private class CargarImagenInternet extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap imagen=null;
            InputStream input = null;
            try{
                URL imageUrl = new URL(producto.getImagePath());
                HttpURLConnection conex= (HttpURLConnection)imageUrl.openConnection();
                conex.connect();
                imagen = BitmapFactory.decodeStream(conex.getInputStream());

            }catch (Exception e){
                e.printStackTrace();
            }
            return imagen;
        }
        protected  void onPostExecute(Bitmap imagen){
            ImageButton btPhot = (ImageButton) findViewById(R.id.btPhoto);
            btPhot.setImageBitmap(imagen);
        }
    }



}
