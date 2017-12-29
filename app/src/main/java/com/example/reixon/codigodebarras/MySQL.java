package com.example.reixon.codigodebarras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by reixon on 25/09/2017.
 */

public class MySQL extends SQLiteOpenHelper{


    private static final String DATABASE_NAME = "MyShop.db";
    private static final int DATABASE_VERSION = 1;

    public MySQL(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

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

        db.execSQL("CREATE TABLE Categoria("+
                " id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                " nombre TEXT );");

        this.addCategoria("Sin Categoria", db);

        Log.d("MyApp", "Crear supermercado");

        db.execSQL("CREATE TABLE SuperMercado("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " nombre TEXT, "
                + " numProductosParaComprar TEXT, "
                + " numProductosComprados TEXT);");

        Log.d("MyApp", "Crear supermerc_producto");

        this.addSuper(db,new SuperMerc(0,"lista",0,0));

        db.execSQL("CREATE TABLE SuperMerc_Producto("
                +" id INTEGER PRIMARY KEY AUTOINCREMENT,"
                +" idSuperMerc INTEGER, "
                +" idProducto INTEGER, "
                +" checked INTEGER NOT NULL, "
                +" cantidad INTEGER, "
                +" btSeleccionado INTEGER NOT NULL, "
                +"FOREIGN KEY(idSuperMerc) REFERENCES SuperMercado(id), "
                +"FOREIGN KEY(idProducto) REFERENCES Product(id)); ");

        //crear la 1º lista de compra
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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

    public void modificarListaCompra(String nuevoValor, String campo, int id,
                                       SQLiteDatabase db) {
        ContentValues v = new ContentValues();

        v.put("" + campo + "", nuevoValor);

        db.update("SuperMercado", v, "id='" + id + "'", null);
    }

    public void addSuper(SQLiteDatabase db, SuperMerc superM) {
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

        ArrayList<SuperMerc>superMercados = new ArrayList();

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

            SuperMerc sup = new SuperMerc(Integer.parseInt(columns[0]),columns[1],
                    Integer.parseInt(columns[2]), Integer.parseInt(columns[3]));
            sup.setProductos(listaProductos);

            superMercados.add(sup);

            numPosicion++;

        }
        Log.d("MyApp", "Datos Cargardos SuperMercado");
        //this.numSuper=numPosicion;
        myCur.close();

        return superMercados;
    }

    private ArrayList<Producto> cargarProductosListaCompra(int idSuper, SQLiteDatabase db) {

        ArrayList<Producto> productos = new ArrayList<Producto>();
        System.out.println("TAMA�O LISTA PRODUCTOS DE CARGAR *******PRODUCTOS " + productos.size());
        System.out.println("CargarDatos SuperMerc_Productos   DB "+db);

        Cursor myCur = db.rawQuery("SELECT p.id, p.nombre, p.precio, p.rutaImagen, p.codigo, " +
                        "p.categoria, sp.cantidad, p.unidad, sp.checked " +
                        " FROM Product p, SuperMerc_Producto sp " +
                        "WHERE p.id=sp.idProducto AND ?=sp.idSuperMerc ",

                new String[]{String.valueOf(idSuper)});
        int cont = 0;
        while (myCur.moveToNext()) {

            boolean seleccionado;
            boolean checked;
            Producto pro = new Producto(Integer.parseInt(myCur.getString(0)), myCur.getString(1),
                    myCur.getDouble(2), myCur.getString(3), myCur.getString(4), Integer.parseInt(myCur.getString(5)), Integer.parseInt(myCur.getString(6)),
                    Integer.parseInt(myCur.getString(7)));
            if(myCur.getInt(8)==1) {
                pro.setCheck(true);
            }
            else{
                pro.setCheck(false);
            }
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
    public void add_Producto_And_Add_Producto_To_Lista_Supermercado(SQLiteDatabase db, SuperMerc superM, Producto p){

        this.addProducto(p.getNombre(),Double.toString(p.getPrecio()),p.getRutaImagen(),p.getCodigo(),p.getCategoria(),p.getUnidad(),db);
        //SELECT MAX(id) FROM tabla
        Cursor mCursor = db.rawQuery("SELECT MAX(id) FROM Product", null);
        int id=-1;
        if (mCursor.moveToFirst()) {
            do {
                id = mCursor.getInt(0);
            } while(mCursor.moveToNext());
        }

        p.setId(id);
        this.add_Producto_A_Lista_SuperMercado(db,superM,p);

    }


    public void add_Producto_A_Lista_SuperMercado(SQLiteDatabase db, SuperMerc superM, Producto p) {
        //INSERTAMOS EN LA TABLA SUPER_PRO EL ID DEL SUPER Y LA ID DEL PRODUCTO


        Log.d("MyApp", "INSERTAMOS SuperMerc" + superM.toString() + ", Producto " + p.toString());

        db.execSQL("INSERT INTO SuperMerc_Producto(idSuperMerc, idProducto, checked, cantidad, btSeleccionado) "
                + "values("
                + "'"+ superM.getId()+ "', "
                + "'"+ p.getId()+"', "
                + "'" + 0 + "', "
                + "'" + 1 + "', "
                + "'" + 0 + "');");

        modificarListaCompra(Integer.toString(superM.getNumProductosParaComprar()+1), "numProductosParaComprar",superM.getId(),db);

        Log.d("MyApp", " DATOS INSERTADOS CORRECTAMENTE");
        Log.d("MyApp", "****DESPUES******");
        //consulta(db);
    }

    public void modificarSuperMerc_Productos(String nuevoNombre, String campo, Producto p, SuperMerc superMerc,
                                     SQLiteDatabase db) {
        ContentValues v = new ContentValues();

        v.put("" + campo + "", nuevoNombre);

        db.update("SuperMerc_Producto", v, "idProducto='"+p.getId()+"' AND idSuperMerc='"+superMerc.getId()+"'", null);
    }

   /* private void modSuperMerc_ProductosIDS(Producto p, SuperMerc superM, SQLiteDatabase db){
        Log.d("MyApp", "BD MODIFICAR SUPER_PROD");
        ContentValues cv = new ContentValues();

        cv.put("cantidad", p.getCantidad());

        db.update("SuperMerc_Producto", cv, "idProducto='"+p.getId()+"' AND idSuperMerc='"+superM.getId()+"'", null);

        Log.d("MyApp","SuperMerc Modificado");
    }*/

    /*Cargar categorias*/
    public ArrayList<Category> loadCategorias(SQLiteDatabase db){
        ArrayList<Category> lC = new ArrayList<>();

        Cursor fila = db.rawQuery("SELECT id, nombre FROM Categoria", null);

        if (fila.moveToFirst()) {
            do {
                lC.add(new Category(fila.getInt(0), fila.getString(1)));
            } while(fila.moveToNext());
        } else
            Log.d("MyApp", "No existe ningúna categoria");
        db.close();
        return lC;
    }

    public void addCategoria(String categoria, SQLiteDatabase db){
        try {
            Log.d("MyApp", "INSERTAR CATEGORIA");
            db.execSQL("INSERT INTO Categoria(nombre) "
                    + "values('" + categoria + "');");
            Log.d("MyApp", "Insertada");

        }catch(SQLiteException e){e.printStackTrace();}

    }

    /*Buscar producto por Codigo de barras*/
    public Producto searchProductoWithCode(String codigo, SQLiteDatabase db){

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

                //Log.d("MyApp", p.getNombre());

            } while(fila.moveToNext());
           // Log.d("MyApp", "Encontrado");
        } else
            Log.d("MyApp", "No existe ningún producto con ese codigo");

        db.close();


        return p;
    }
    public Producto searchProductoWithName(String name, SQLiteDatabase db){

        Producto p=null;
        Log.d("MyApp", "BUSCAR PRODUCT");
        String[] args = new String[]{name};
        Cursor fila = db.rawQuery("SELECT id, nombre, precio, rutaImagen, codigo, categoria, unidad" +
                " FROM Product WHERE nombre=? ", args);
        Log.d("MyApp", "consulta realizada");
        if (fila.moveToFirst()) {

            do {

                p = new Producto(Integer.parseInt(fila.getString(0)), fila.getString(1), fila.getDouble(2),
                        fila.getString(3),  fila.getString(4), Integer.parseInt(fila.getString(5)), Integer.parseInt(fila.getString(6)));

                Log.d("MyApp", p.getNombre());

            } while(fila.moveToNext());
            Log.d("MyApp", "Encontrado");
        } else
            Log.d("MyApp", "No existe ningún producto con ese Nombre");

        db.close();


        return p;
    }

    /*Añadir un producto nuevo*/
    public void addProducto(String nombre, String precio, String rutaImagen, String codigo,
                            int categoria, int unidad, SQLiteDatabase db){
        try {
            Log.d("MyApp", "INSERTAR Producto");

            db.execSQL("INSERT INTO Product(nombre, precio, rutaImagen, codigo, categoria, unidad) "
                    + "values('" + nombre + "', '" + precio + "', '" + rutaImagen + "', '" + codigo + "', '" + categoria + "', '" + unidad + "' );");
            Log.d("MyApp", "Hecho");

        }catch(SQLiteException e){e.printStackTrace(); Log.d("MyApp", e.toString()); }

    }

    public void modProduct(Producto p, SQLiteDatabase db){
        Log.d("MyApp", "BD MODIFICAR PRODUCTO");
        ContentValues cv = new ContentValues();

        cv.put("nombre", p.getNombre());
        cv.put("precio", p.getPrecio());
        cv.put("rutaImagen", p.getRutaImagen());
        cv.put("codigo", p.getCodigo());
        cv.put("categoria", p.getCategoria());
        cv.put("unidad", p.getUnidad());
        db.update("Product", cv, "id='"+p.getId()+"'", null);

        Log.d("MyApp","Producto Modificado");
    }

    /*Cargar todos los productos de despensa*/
    public ArrayList<Producto> loadFullProduct(SQLiteDatabase db) {
        ArrayList<Producto> lP = new ArrayList<>();
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
            Log.d("MyApp", "No existe ningún producto con ese codigo");
        return lP;
    }

    public void eliminarProducto(int idProd,  SQLiteDatabase db){

        db.delete("Product", "id='"+idProd+"'", null);
    }

  /*  public ArrayList<Category> loadCategories(SQLiteDatabase db){
        ArrayList<Category> cP = new ArrayList<>();


        return cP;
    }

    public ArrayList<Category> loadFullProductByCategories(SQLiteDatabase db, String category){
        ArrayList<Category> lP = new ArrayList<>();
        Producto p;
        String[] args = new String[]{category};
        Log.d("MyApp", "consulta Productos por categorias");
        Cursor fila = db.rawQuery("SELECT id, nombre, precio, rutaImagen, codigo, " +
                "categoria, unidad FROM Product Where categoria=?", args);
        Log.d("MyApp", "consulta");
        if (fila.moveToFirst()) {
            do {
                p = new Producto(Integer.parseInt(fila.getString(0)), fila.getString(1),
                        fila.getDouble(2), fila.getString(3), fila.getString(4), fila.getInt(5), fila.getInt(6));
                lP.add(p);
            } while(fila.moveToNext());
        } else
            Log.d("MyApp", "No existe ningún producto con ese codigo");
        db.close();
        return lP;
    }*/

}


