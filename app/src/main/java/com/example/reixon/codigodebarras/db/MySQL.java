package com.example.reixon.codigodebarras.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.reixon.codigodebarras.Model.Category;
import com.example.reixon.codigodebarras.Model.Producto;
import com.example.reixon.codigodebarras.Model.SuperMercado;
import com.example.reixon.codigodebarras.Model.UserAccount;

import java.util.ArrayList;

/**
 * Created by reixon on 25/09/2017.
 */

public class MySQL extends SQLiteOpenHelper{


    private static final String DATABASE_NAME = "MyShop.db";
    private static final int DATABASE_VERSION = 1;
    private static MySQL instancia;

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("MyApp", "CREAR BASE DE DATOS");

        db.execSQL("CREATE TABLE Product("+
                " id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                " nombre TEXT, " +
                " precio TEXT, " +
                " rutaImagen TEXT, " +
                " codigo TEXT, " +
                " categoria INTEGER, " +
                " unidad INTEGER, " +
                " FOREIGN KEY(categoria) REFERENCES Categoria(id) );");

        Log.d("MyApp", "Crear categoria");

        db.execSQL("CREATE TABLE Category("+
                " id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                " nombre TEXT );");

        Log.d("MyApp", "Crear supermercado");

        db.execSQL("CREATE TABLE SuperMercado("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " nombre TEXT, "
                + " numProductosParaComprar TEXT, "
                + " numProductosComprados TEXT);");

        Log.d("MyApp", "Crear supermerc_producto");


        db.execSQL("CREATE TABLE SuperMerc_Producto("
                +" id INTEGER PRIMARY KEY AUTOINCREMENT,"
                +" idSuperMerc INTEGER, "
                +" idProducto INTEGER, "
                +" cantidad INTEGER, "
                +"FOREIGN KEY(idSuperMerc) REFERENCES SuperMercado(id), "
                +"FOREIGN KEY(idProducto) REFERENCES Product(id)); ");

        db.execSQL("CREATE TABLE User(" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " name TEXT UNIQUE, " +
                " email TEXT UNIQUE," +
                " token TEXT); ");

        this.anyadirCategoria("Sin Categoria", db);
        this.addSuper(db,new SuperMercado(0,"lista",0,0));

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists "+DATABASE_NAME);
            onCreate(db);
    }

    public static synchronized MySQL getInstance(Context context){
        if(instancia == null){
            instancia = new MySQL(context.getApplicationContext());
        }
        return instancia;
    }

    public MySQL(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void borrarListaCompra(int id, SQLiteDatabase db) {
        ContentValues valor = new ContentValues();
        db.delete("SuperMercado", "id='" + id + "'", null);
    }

    public void eliminar_Producto_D_SuperMerc_Producto(int idProd, int idSuper, SQLiteDatabase db){
        ArrayList listIds = new ArrayList<>();
        Cursor myCur = db.rawQuery("SELECT sp.id  FROM SuperMerc_Producto sp " +
                        "WHERE ?=sp.idProducto AND ?=sp.idSuperMerc ",

                new String[]{String.valueOf(idProd),String.valueOf(idSuper)});
        int cont = 0;
        while (myCur.moveToNext()) {

            boolean seleccionado;
            boolean checked;
            listIds.add(Integer.parseInt(myCur.getString(0)));
            System.out.println("*****" + cont + "******");
        }

        while(cont<listIds.size()) {
            db.delete("SuperMerc_Producto", "id='" + listIds.get(cont) + "'", null);
            cont++;
        }
    }

    public void editListShop(String nuevoValor, String campo, int id,
                             SQLiteDatabase db) {
        ContentValues v = new ContentValues();

        v.put("" + campo + "", nuevoValor);

        db.update("SuperMercado", v, "id='" + id + "'", null);
    }

    public void editCategory(String newValue, int id, SQLiteDatabase db){
        ContentValues v = new ContentValues();

        v.put("nombre", newValue);

        db.update("Category", v, "id='" + id + "'", null);
    }

    public void addSuper(SQLiteDatabase db, SuperMercado superM) {
        System.out.println("****INSERTAR SUPERMERCADO******");

        db.execSQL("INSERT INTO SuperMercado(nombre, numProductosParaComprar, numProductosComprados) "
                + "values("
                + "'"
                + superM.getNombre()
                + "', "
                + "'" + 0 + "', "
                + "'" + 0 + "');");

        System.out.println(" DATOS INSERTADOS CORRECTAMENTE");
        System.out.println("****DESPUES******");
        //consulta(db);
    }

    public ArrayList cargarSuperMercadosBD(SQLiteDatabase db) {

        ArrayList<SuperMercado>superMercados = new ArrayList();

        Log.d("MyApp", "CargarDatos SuperMercado");

        String[] columns = {"id", "nombre", "numProductosParaComprar","numProductosComprados"};

        Cursor myCur = db.query("SuperMercado", columns, null, null, null, null, "id");

        int idCol = myCur.getColumnIndex("id");
        int nomCol = myCur.getColumnIndex("nombre");
        int numProdCompCol = myCur.getColumnIndex("numProductosParaComprar");
        int numProdParCompCol = myCur.getColumnIndex("numProductosComprados");

        int numPosicion = 0;
        while (myCur.moveToNext()) {
            //while (myCur.moveToNext() && numPosicion < superMercados.size()) {
            columns[0] = Integer.toString(myCur.getInt(idCol));
            columns[1] = myCur.getString(nomCol);
            columns[2] = Integer.toString(myCur.getInt(numProdParCompCol));
            columns[3] = Integer.toString(myCur.getInt(numProdCompCol));

            ArrayList<Producto> listaProductos = this.cargarProductosListaCompra(Integer.parseInt(columns[0]),db);
            Log.d("MyApp","lista Productos " + listaProductos.size());

            SuperMercado sup = new SuperMercado(Integer.parseInt(columns[0]),columns[1],
                    Integer.parseInt(columns[2]), Integer.parseInt(columns[3]));
            sup.setProductos(listaProductos);

            superMercados.add(sup);

            numPosicion++;

        }
        Log.d("MyApp", "Datos Cargardos SuperMercado");
        //this.numSuper=numPosicion;

        return superMercados;
    }

    private ArrayList<Producto> cargarProductosListaCompra(int idSuper, SQLiteDatabase db) {

        ArrayList<Producto> productos = new ArrayList<Producto>();
        System.out.println("TAMA�O LISTA PRODUCTOS DE CARGAR *******PRODUCTOS " + productos.size());
        System.out.println("CargarDatos SuperMerc_Productos   DB "+db);

        Cursor myCur = db.rawQuery("SELECT p.id, p.nombre, p.precio, p.rutaImagen, p.codigo, " +
                        "p.categoria, sp.cantidad, p.unidad " +
                        " FROM Product p, SuperMerc_Producto sp " +
                        "WHERE p.id=sp.idProducto AND ?=sp.idSuperMerc ",
                new String[]{String.valueOf(idSuper)});
        int cont = 0;
        while (myCur.moveToNext()) {

            Producto pro = new Producto(Integer.parseInt(myCur.getString(0)), myCur.getString(1),
                    myCur.getDouble(2), myCur.getString(3), myCur.getString(4),
                    Integer.parseInt(myCur.getString(5)),
                    Integer.parseInt(myCur.getString(7)));
            pro.setQuantity(Integer.parseInt(myCur.getString(6)));

            Log.d("MyApp","Producto "+pro.toString());

            productos.add(pro);
            cont++;
            System.out.println("*****" + cont + "******");
        }
        System.out.println("Se Cargaron los datos de Super_ProSuper_Productos");
        myCur.close();

        return productos;
    }

    //anyade un producto a la BD, carga el id del producto y lo almacena en la lista de super
    public void anyadir_Producto_Y_Anyadir_A_Lista_SuperMercado(SQLiteDatabase db, SuperMercado superM, Producto p){

        this.insertProducto(p.getName(),Double.toString(p.getPrice()),p.getImagePath(),p.getCode(),p.getCategory(),p.getUnity(),db);
        //SELECT MAX(id) FROM tabla
        Cursor mCursor = db.rawQuery("SELECT MAX(id) FROM Product", null);
        int id=-1;
        if (mCursor.moveToFirst()) {
            do {
                id = mCursor.getInt(0);
            } while(mCursor.moveToNext());
        }

        p.setId(id);
        this.anyadir_Producto_A_Lista_SuperMercado(db,superM,p);

    }


    public void anyadir_Producto_A_Lista_SuperMercado(SQLiteDatabase db, SuperMercado superM, Producto p) {
        //INSERTAMOS EN LA TABLA SUPER_PRO EL ID DEL SUPER Y LA ID DEL PRODUCTO


        Log.d("MyApp", "INSERTAMOS SuperMercado" + superM.toString() + ", Producto " + p.toString());

        db.execSQL("INSERT INTO SuperMerc_Producto(idSuperMerc, idProducto, cantidad) "
                + "values("
                + "'"+ superM.getId()+ "', "
                + "'"+ p.getId()+"', "
                + "'" + 1 + "');");

        editListShop(Integer.toString(superM.getNumProductosParaComprar()+1), "numProductosParaComprar",superM.getId(),db);

        Log.d("MyApp", " DATOS INSERTADOS CORRECTAMENTE");
        Log.d("MyApp", "****DESPUES******");
        //consulta(db);
    }

    public void modificarSuperMerc_Productos(String nuevoNombre, String campo, Producto p, SuperMercado superMerc,
                                     SQLiteDatabase db) {
        ContentValues v = new ContentValues();

        v.put("" + campo + "", nuevoNombre);

        db.update("SuperMerc_Producto", v, "idProducto='"+p.getId()+"' AND idSuperMerc='"+superMerc.getId()+"'", null);
    }
    /*Cargar categorias*/
    public ArrayList<Category> cargarCategorias(SQLiteDatabase db){
        ArrayList<Category> lC = new ArrayList<>();

        Cursor fila = db.rawQuery("SELECT id, nombre FROM Category", null);

        if (fila.moveToFirst()) {
            do {
                lC.add(new Category(fila.getInt(0), fila.getString(1)));
            } while(fila.moveToNext());
        } else
            Log.d("MyApp", "No existe ningúna categoria");

        return lC;
    }

    public void anyadirCategoria(String nombre, SQLiteDatabase db){
        try {
            Log.d("MyApp", "INSERTAR CATEGORIA");
            db.execSQL("INSERT INTO Category(nombre) "
                    + "values('" + nombre + "');");
            Log.d("MyApp", "Insertada");

        }catch(SQLiteException e){e.printStackTrace();}

    }

    /*Buscar producto por Codigo de barras*/
    public Producto buscarProductoPorCodigo(String codigo, SQLiteDatabase db){

        Producto p=null;
        Log.d("MyApp", "BUSCAR PRODUCT");
        String[] args = new String[]{codigo};
        Cursor fila = db.rawQuery("SELECT id, nombre, precio, rutaImagen, codigo, categoria, unidad" +
                " FROM Product WHERE codigo=? ", args);
        Log.d("MyApp", "consulta realizada");
        if (fila.moveToFirst()) {

            do {

                p = new Producto(Integer.parseInt(fila.getString(0)), fila.getString(1), fila.getDouble(2),
                        fila.getString(3),  fila.getString(4), Integer.parseInt(fila.getString(5)), Integer.parseInt(fila.getString(6)));

                //Log.d("MyApp", p.getName());

            } while(fila.moveToNext());
           // Log.d("MyApp", "Encontrado");
        } else
            Log.d("MyApp", "No existe ningún producto con ese codigo");
        return p;
    }
    /*Buscar producto por Codigo de barras*/
    public boolean buscar_Email_Cuenta_Usuario(String userEmail, SQLiteDatabase db){
        boolean bool=false;
        String[] args = new String[]{userEmail};
        Cursor mCursor = db.rawQuery("SELECT count(email) FROM User WHERE email=? ", args);
        int count=-1;
        if (mCursor.moveToFirst()) {
            do {
                count = mCursor.getInt(0);
            } while(mCursor.moveToNext());
        }
        if(count>=1){
            bool=true;
        }

        return bool;
    }

    /*Añadir un producto nuevo*/
    public void insertProducto(String nombre, String precio, String rutaImagen, String codigo,
                               int categoria, int unidad, SQLiteDatabase db){
        try {
            Log.d("MyApp", "INSERTAR Producto");

            db.execSQL("INSERT INTO Product(nombre, precio, rutaImagen, codigo, categoria, unidad) "
                    + "values('" + nombre + "', '" + precio + "', '" + rutaImagen + "', '" + codigo + "', '" + categoria + "', '" + unidad + "' );");
            Log.d("MyApp", "Hecho");

        }catch(SQLiteException e){e.printStackTrace(); Log.d("MyApp", e.toString()); }

    }

    public void modificarProducto(Producto p, SQLiteDatabase db){
        Log.d("MyApp", "BD MODIFICAR PRODUCTO");
        ContentValues cv = new ContentValues();

        cv.put("nombre", p.getName());
        cv.put("precio", p.getPrice());
        cv.put("rutaImagen", p.getImagePath());
        cv.put("codigo", p.getCode());
        cv.put("categoria", p.getCategory());
        cv.put("unidad", p.getUnity());
        db.update("Product", cv, "id='"+p.getId()+"'", null);

        Log.d("MyApp","Producto Modificado");
    }

    public ArrayList<UserAccount> cargarUsuarios(SQLiteDatabase db) {
        Cursor fila = db.rawQuery("SELECT id, name, email, token FROM User", null);
        ArrayList<UserAccount>usersAccounts= new ArrayList<>();
        UserAccount user=null;
        Log.d("MyApp", "consulta");
        if (fila.moveToFirst()) {
            do {
                user = new UserAccount(Integer.parseInt(fila.getString(0)),
                        fila.getString(1),fila.getString(2),fila.getString(3));
                usersAccounts.add(user);
            } while(fila.moveToNext());
        } else
            Log.d("MyApp", "No existe ningún usuario");
        return usersAccounts;
    }

    /*Cargar todos los productos de despensa*/
    public ArrayList<Producto> cargarProductos(SQLiteDatabase db) {
        ArrayList<Producto> lP = new ArrayList<Producto>();
        Producto p;
        Log.d("MyApp", "consulta Producto");
        Cursor fila = db.rawQuery("SELECT id, nombre, precio, rutaImagen, codigo, " +
                "categoria, unidad FROM Product", null);
        Log.d("MyApp", "consulta");
        if (fila.moveToFirst()) {
            do {
                p = new Producto(Integer.parseInt(fila.getString(0)), fila.getString(1),
                        fila.getDouble(2), fila.getString(3), fila.getString(4), fila.getInt(5), fila.getInt(6));
                lP.add(p);
            } while(fila.moveToNext());
        } else
            Log.d("MyApp", "No existe ningún producto");
        return lP;
    }

    public void eliminarProducto(int idProd,  SQLiteDatabase db){
        try {
            db.delete("Product", "id='"+idProd+"'", null);
        }catch(SQLiteException e){e.printStackTrace();}
    }

    public void eliminarCategoria(int idCategory, SQLiteDatabase db){
        try {
            db.delete("Category","id='"+idCategory+"'",null);
        }catch(SQLiteException e){e.printStackTrace();}
    }
    public void eliminarUsuario(int idUser, SQLiteDatabase db){
        try {
            db.delete("User","id='"+idUser+"'",null);
        }catch(SQLiteException e){e.printStackTrace();}
    }

    /****
     * Insertamos un nuevo usuario en la bd local
     * *****/
    public void insertarUsuario(String name, String email, String token, SQLiteDatabase db){
        try {
            db.execSQL("INSERT INTO User(name, email, token) "
                    + "values('" + name + "', '"+email+"', '"+token+"' );");

        }catch(SQLiteException e){
            e.printStackTrace();
        }
    }

}


