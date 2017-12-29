package com.example.reixon.codigodebarras;

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
import android.graphics.Rect;
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

    private static String APP_DIRECTORY ="Imagenes_MyShop/";
    private static String MEDIA_DIRECTORY= APP_DIRECTORY +"PictureApp";
    private final int SELECT_PICTURE =300;
    private final int MY_PERMISSIONS=100;
    private final int PHOTO_CODE=200;
    private final int PIC_CROP=2;
    private Producto producto;
    private Spinner spinner_categorias, spinnerUnidad;
    private ImageButton scanBtn, btPhot, btCategoria;
    private TextView txtScan;
    private EditText txtNameProduct, txtPrecioProducto;
    private ArrayList<String> arrayCategoriaN;
    private ArrayList<Category> arrayCategoria;
    private int unidadSelec;
    private String imagePath;
    private ConstraintLayout layout;
    private SQLiteDatabase db;
    private MySQL mysql;
    private boolean addList;
    private SuperMerc sp;
    private boolean photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        txtNameProduct = (EditText) findViewById(R.id.txtNombre);
        txtPrecioProducto = (EditText) findViewById(R.id.txtPrecio);
        scanBtn = (ImageButton) findViewById(R.id.btBarCode);
        btPhot = (ImageButton) findViewById(R.id.btPhoto);
        spinner_categorias = (Spinner) findViewById(R.id.spinner_categoria_add);
        btCategoria = (ImageButton) findViewById(R.id.btEditCategoria);
        txtScan = (TextView) findViewById(R.id.txtCodigo);
        layout = (ConstraintLayout)findViewById(R.id.layout_add_product);
        spinnerUnidad= (Spinner)findViewById(R.id.spinnerUnidad_AddP);
        imagePath="";
        addList=false;
        Log.d("MyApp", "ADD PRODUCT");
        String [] arrayUnidad ={"unidad","lata","botella","paquete","caja","bolsa","mg","gr","kg",
                "ml","cl","litro"};

        this.setTitle("Añadir Producto");

        /*Cargar base de datos de categorias*/
        mysql = new MySQL(this);
        db = mysql.getWritableDatabase();
        arrayCategoria = mysql.loadCategorias(db);
        arrayCategoriaN = new ArrayList<String>();
        if (arrayCategoria.size() == 0) {
            arrayCategoriaN.add(0, "Ninguna");
        } else {
            for (int i = 0; i < arrayCategoria.size(); i++) {
                arrayCategoriaN.add(arrayCategoria.get(i).getNombre());
            }
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
                this.txtNameProduct.setText(producto.getNombre());
                this.txtPrecioProducto.setText(producto.getPrecio());
                this.txtScan.setText(producto.getCodigo());
                txtScan.setVisibility(View.VISIBLE);
            }*/
           if(b.get("Add_lista")!=null){
               addList=true;
               sp = (SuperMerc) b.get("SuperMerc");
            }

            if (b.get("AddProducto") != null) {
                this.txtNameProduct.setText(b.getString("AddProducto"));
                txtScan.setVisibility(View.VISIBLE);
                b.remove("AddProducto");
                producto =  new Producto();
            } else if (b.get("CODIGO") != null) {
                this.txtScan.setText(b.getString("CODIGO"));
                txtScan.setVisibility(View.VISIBLE);
                b.remove("CODIGO");
                producto =  new Producto();
                producto.setCodigo(b.getString("CODIGO"));
            } else if (b.getSerializable("Producto_scanner_internet") != null) {
                Log.d("MyApp", "Existe un producto desde internet");

                producto = (Producto) b.getSerializable("Producto_scanner_internet");
                this.btPhot.setEnabled(false);
                this.txtNameProduct.setText(producto.getNombre());
                this.txtScan.setText(producto.getCodigo());
                txtScan.setVisibility(View.VISIBLE);
                Log.d("MyApp", "Ruta imagen '" + producto.getRutaImagen() + "'");
                if (producto.getRutaImagen().equals("")) {
                    Log.d("MyApp", "Imagen vacia");
                    btPhot.setImageDrawable(getResources().getDrawable(R.drawable.photo_icon));
                    //btPhot.setImageResource(R.drawable.photo_icon);
                }
                else {
                    new CargarImagenInternet().execute(producto.getRutaImagen());
                }
                Toast.makeText(this, "Producto encontrado en Internet.", Toast.LENGTH_LONG).show();

            }
            this.txtPrecioProducto.setText("0.0");
        }


        spinner_categorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int pos, long id) {
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
            if (v.getId() == btCategoria.getId()) {

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
                        db = mysql.getWritableDatabase();
                        mysql.addCategoria(input.getText().toString(), db);
                        db = mysql.getWritableDatabase();
                        arrayCategoria = mysql.loadCategorias(db);
                        Log.d("MyApp", "categoria cargada");
                        arrayCategoriaN.add(input.getText().toString());
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
                        IntentIntegrator integrator = new IntentIntegrator(Add_product.this);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                        integrator.setPrompt("Scan");
                        integrator.setCameraId(0);
                        integrator.setBeepEnabled(false);
                        integrator.setBarcodeImageEnabled(false);
                        integrator.initiateScan();
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

    }

    private void takePintureIntent(){
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

  /*  private void showCamera(){
        Intent intent;
        File file = new File(Environment.
                getExternalStorageDirectory(), MEDIA_DIRECTORY);

        if(!file.exists()){
            file.mkdirs();
            Log.d("MyApp", file.exists()+" mkdir ");
            Log.d("MyApp", file.getPath()+"  ");
        }
        if(file.exists()){

            Long timestamp= System.currentTimeMillis()/1000;
            String imageName = timestamp.toString()+".jpg";
            imagePath = Environment.getExternalStorageDirectory()
                    +File.separator+MEDIA_DIRECTORY+
                    File.separator+imageName;
            Log.d("MyApp", imagePath+"  ");
            File newFile = new File(imagePath);
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Log.d("MyApp", " 2 ");

            Uri photoURI = FileProvider.getUriForFile (Add_product.this,
                    this.getApplicationContext().getPackageName() +
                            ".provider", newFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));

            Log.d("MyApp", " 3 ");
            startActivityForResult(intent, PHOTO_CODE);
        }
    }*/



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

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED))
            return true;

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
        return false;
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_product, menu);
        MenuItem menu_aceptar = menu.findItem(R.id.menu_aceptar);
        MenuItem menu_cancelar = menu.findItem(R.id.menu_borrar);

        menu_aceptar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d("MyApp","Aceptar");
                int idCategoria;
                if(arrayCategoria.size()==0){
                    idCategoria=0;
                }
                else{
                    idCategoria = arrayCategoria.get(unidadSelec).getId();
                }
                //guardar la imagen en la memoria del movil y cambiar la direccion de la imagen de producto
                if(!txtNameProduct.getText().toString().equals("")) {
                    if(imagePath.equals("") && !producto.getRutaImagen().equals("")){
                        try {
                            imagePath = new guardarImagenInternet().execute(producto.getRutaImagen(),txtNameProduct.getText().toString() ).get();
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
                            loadImagePath();
                        }
                    }

                    //Si la activity es llamada desde lista de compra, añadiremos el producto a la lista
                    if(addList){
                        anyadirProductoListaCompra(idCategoria);
                    }
                    else {
                        db = mysql.getWritableDatabase();
                        mysql.addProducto(txtNameProduct.getText().toString(),
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

    private void loadImagePath(){
        try {
            Bitmap bitmap = ((BitmapDrawable) btPhot.getDrawable()).getBitmap();
            File dirImg = new ContextWrapper(getApplicationContext()).getDir("Imagenes", Context.MODE_PRIVATE);
            File ruta=  new File(dirImg, txtNameProduct.getText().toString()+".png");
            FileOutputStream out = new FileOutputStream(ruta);
            bitmap.compress(Bitmap.CompressFormat.PNG,10,out);
            out.flush();
            imagePath=ruta.getAbsolutePath();

        }catch (Exception e){}
    }

    public void anyadirProductoListaCompra(int idCategoria){

        db = mysql.getWritableDatabase();
        producto = new Producto(-1, txtNameProduct.getText().toString(),
                Double.parseDouble(txtPrecioProducto.getText().toString()),
                imagePath, txtScan.getText().toString(),
                idCategoria, spinnerUnidad.getSelectedItemPosition());

        mysql.add_Producto_And_Add_Producto_To_Lista_Supermercado(db,sp,producto);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
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
                   /* Uri photoURI = FileProvider.getUriForFile(Add_product.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                           null);*/
                   /* Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
                            "${applicationId}.provider", null);*/

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
                    txtScan.setText(result.getContents());
                    txtScan.setVisibility(View.VISIBLE);
                }
            } else {
                switch (requestCode) {
                    case PHOTO_CODE:
                        extras = data.getExtras();
                        if(extras!=null){
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
     /*   if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
            if(b.get("AddProducto")!=null) {
                Intent i = new Intent(this, activity_lista_productos.class);
                startActivity(i);
            }
        }
        else {
            Intent i = new Intent(this, Inicio.class);
            startActivity(i);
        }
        finish();*/
   /*     Intent i = new Intent(this, activity_lista_productos.class);
        startActivity(i);
        finish();*/

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(InputStream res, Rect rc) {

        int reqWidth = 250;
        int reqHeight = 250;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(res, rc, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(res, rc, options);
    }

   private class CargarImagenInternet extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap imagen=null;
            InputStream input = null;
            try{
                URL imageUrl = new URL(producto.getRutaImagen());
                HttpURLConnection conex= (HttpURLConnection)imageUrl.openConnection();
                conex.connect();
                input = conex.getInputStream();
                imagen = BitmapFactory.decodeStream(conex.getInputStream());

            }catch (Exception e){
                e.printStackTrace();
            }
            return imagen;
        }
        protected  void onPostExecute(Bitmap imagen){
           // imagen = Bitmap.createScaledBitmap(imagen, btPhot.getWidth(),btPhot.getHeight(), false);
            Add_product.this.btPhot.setImageBitmap(imagen);
        }
    }

    private class guardarImagenInternet extends  AsyncTask<String, Void, String>{

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

}
