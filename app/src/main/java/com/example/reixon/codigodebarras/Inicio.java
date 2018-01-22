package com.example.reixon.codigodebarras;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Inicio extends AppCompatActivity {
    private SQLiteDatabase db;
    private MySQL mysql;
    private ImageButton btStock, btStock2, btListaCompra, btScanner,btConnection;
    private ArrayList<SuperMerc>arraySupers;
    private ArrayList<Producto>productoTotal;
    private ArrayList<Category>arrayCategories;
    private boolean pause;
    private static final int LOAD_DATA_MYSQL=100;
    private boolean loadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My List Shop");
        setSupportActionBar(toolbar);
        btStock = (ImageButton)findViewById(R.id.btStock);
        btStock2 = (ImageButton)findViewById(R.id.btStockPrueba);
        btListaCompra =(ImageButton)findViewById(R.id.btListaCompra);
        btScanner = (ImageButton)findViewById(R.id.btScanner);
        btConnection =(ImageButton)findViewById(R.id.btConnection);

        mysql = new MySQL(this);
     /*   db = mysql.getWritableDatabase();
        arraySupers=mysql.cargarSuperMercadosBD(db);
        productoTotal = mysql.loadFullProduct(db);
        arrayCategories = mysql.loadCategorias(db);*/

            //arraySupers=mysql.cargarSuperMercadosBD(db);

        btStock2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putSerializable("Lista Supers", arraySupers);
                b.putSerializable("Full Products", productoTotal);
                b.putSerializable("Array Categories", arrayCategories);
                Intent i = new Intent(Inicio.this, activity_list_products_expanable.class);
                i.putExtras(b);
                startActivity(i);
            }
        });

        btStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putSerializable("Lista Supers", arraySupers);
                b.putSerializable("Full Products", productoTotal);
                b.putSerializable("Array Categories", arrayCategories);
                Intent i = new Intent(Inicio.this, lista_productos.class);
                i.putExtras(b);
                startActivity(i);
            }
        });

        btScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId())
                {
                    case R.id.btScanner:
                        IntentIntegrator integrator = new IntentIntegrator(Inicio.this);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                        integrator.setPrompt("Escaneo");
                        integrator.setCameraId(0);
                        integrator.setBeepEnabled(false);
                        integrator.setBarcodeImageEnabled(true);
                        integrator.initiateScan();
                        break;
                }
            }
        });

        btListaCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putSerializable("Lista Supers", arraySupers);
                b.putSerializable("Array Categories", arrayCategories);
                Intent i = new Intent(Inicio.this, lista_compra.class);
                i.putExtras(b);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    //    if(loadData) {
            db = mysql.getWritableDatabase();
            arraySupers=mysql.cargarSuperMercadosBD(db);
            productoTotal=mysql.loadFullProduct(db);
            arrayCategories = mysql.loadCategorias(db);
    //    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      /*  if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOAD_DATA_MYSQL:
                    loadData=true;
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
                                    Intent i = new Intent(Inicio.this, Add_product.class);
                                    i.putExtra("Producto_scanner_internet", p);
                                    startActivityForResult(i,LOAD_DATA_MYSQL);
                                } else {
                                    Intent i = new Intent(Inicio.this, Add_product.class);
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

}
