package com.example.reixon.codigodebarras.ui;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.reixon.codigodebarras.Class.Category;
import com.example.reixon.codigodebarras.db.MySQL;
import com.example.reixon.codigodebarras.Class.Producto;
import com.example.reixon.codigodebarras.Class.SuperMerc;
import com.example.reixon.codigodebarras.R;

import java.util.ArrayList;

public class Inicio extends AppCompatActivity {
    private SQLiteDatabase db;
    private MySQL mysql;
    private ArrayList<SuperMerc>arraySupers;
    private ArrayList<Producto>productoTotal;
    private ArrayList<Category>arrayCategories;
    private boolean pause;
    private static final int LOAD_DATA_MYSQL=100;
    private boolean loadData;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_lista_compra);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });
        setupNavigationDrawerContent(navigationView);
    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_listBuy:
                            menuItem.setChecked(true);
                            setFragment(0);
                            drawerLayout.closeDrawer(GravityCompat.START);
                            return true;
                        case R.id.nav_listProd:
                            menuItem.setChecked(true);
                            setFragment(1);
                            drawerLayout.closeDrawer(GravityCompat.START);
                            return true;
                    }
                    return true;
                }
            });
    }

    public void setFragment(int position) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        switch (position) {
            case 0:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                //InboxFragment inboxFragment = new InboxFragment();
               // fragmentTransaction.replace(R.id.fragment, inboxFragment);
                fragmentTransaction.commit();
                break;
            case 1:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
              /*  StarredFragment starredFragment = new StarredFragment();
                fragmentTransaction.replace(R.id.fragment, starredFragment);*/
                fragmentTransaction.commit();
                break;
        }
    }

      /*  btStock = (ImageButton)findViewById(R.id.btStock);
        btStock2 = (ImageButton)findViewById(R.id.btStockPrueba);
        btListaCompra =(ImageButton)findViewById(R.id.btListaCompra);
        btScanner = (ImageButton)findViewById(R.id.btScanner);
        btConnection =(ImageButton)findViewById(R.id.btConnection);*/

     //   mysql = new MySQL(this);
     /*   db = mysql.getWritableDatabase();
        arraySupers=mysql.cargarSuperMercadosBD(db);
        productoTotal = mysql.loadFullProduct(db);
        arrayCategories = mysql.loadCategories(db);*/

            //arraySupers=mysql.cargarSuperMercadosBD(db);

     /*   btStock2.setOnClickListener(new View.OnClickListener() {
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
        });*/
  //  }

   /* @Override
    protected void onResume() {
        super.onResume();
    //    if(loadData) {
            db = mysql.getWritableDatabase();
            arraySupers=mysql.cargarSuperMercadosBD(db);
            productoTotal=mysql.loadFullProduct(db);
            arrayCategories = mysql.loadCategories(db);
    //    }

    }*/
/*
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

    }*/

}
