package com.making.apps.organizador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BD extends SQLiteOpenHelper {

    //General
    private static final String DB_NAME = "database.db";
    private static final int DB_VERSION = 1;
    ///DATOS DE LAS TABLAS
    private static final String DB_TABLA_USUARIOS = "usuarios";
    private static final String DB_COLUMNA_USUARIOS_ID = "id";
    private static final String DB_COLUMNA_USUARIOS_NOMBRE = "nombre";
    private static final String DB_COLUMNA_USUARIOS_CLAVE = "clave";

    private static final String DB_TABLA_TAREAS = "tareas";
    private static final String DB_COLUMNA_TAREAS_ID = "id";
    private static final String DB_COLUMNA_TAREAS_NOMBRE = "nombre";
    private static final String DB_COLUMNA_TAREAS_DESCRIPCION = "descripion";
    private static final String DB_COLUMNA_TAREAS_ESTADO = "estado";

    private Context context;

    BD(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //tabla taras
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + DB_TABLA_TAREAS + " (" +
                DB_COLUMNA_TAREAS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DB_COLUMNA_TAREAS_NOMBRE + " TEXT, " +
                DB_COLUMNA_TAREAS_DESCRIPCION + " TEXT, " +
                DB_COLUMNA_TAREAS_ESTADO + " TEXT);");

        sqLiteDatabase.execSQL("CREATE INDEX `index_tareas` ON `tareas` (`id`,`estado`);");

        //tabla usuario
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + DB_TABLA_USUARIOS + " (" +
                DB_COLUMNA_USUARIOS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DB_COLUMNA_USUARIOS_NOMBRE + " TEXT, " +
                DB_COLUMNA_USUARIOS_CLAVE + " INTEGER);");

        sqLiteDatabase.execSQL("CREATE INDEX `index_usuarios` ON `usuarios` (`nombre`,`clave`);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLA_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLA_TAREAS);
        Log.e("en", "Tablas borradas");
        onCreate(db);
    }

    /**
     * Metodo que verifica si existe el nombre de usuario en la base de datos
     *
     * @param usuario nombre del usuario
     */
    public boolean existeEnBd(String usuario) {
        Log.e("en", "entre a verificar " + usuario);
        int contador = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        if (db.isOpen()) {
            Cursor c = db.rawQuery("select count(*) from usuarios where nombre='" + usuario + "'", null);
            if (c.moveToFirst()) {
                contador = c.getInt(0);
                c.close();
                db.close();
                return contador > 0;
            }
        }
///si no existe retorna falso
        return false;
    }

    /**
     * Metodo que permite iniciar sesion local
     *
     * @param usuario nombre de usuario
     * @param clave   clave del usuario
     */
    public int loginBD(String usuario, String clave) {

        String sclavebd = null;
        try {
            sclavebd = seguridad.get_SHA_512_SecurePassword(clave, usuario);
        } catch (Exception e) {
            Log.e("error", e.toString());
        }

        int id = -1;
        String clavebd;
        SQLiteDatabase db = this.getReadableDatabase();
        if (db.isOpen()) {
            Cursor c = db.rawQuery("select * from usuarios where nombre='" + usuario + "' and clave='" + sclavebd + "'", null);
            if (c.moveToFirst()) {
                id = c.getInt(0);
                clavebd = c.getString(2);
                c.close();
                db.close();
                if (clave.equals(clavebd)) {
                    //si existe y la clave es entonces permite login y retorna id de usuario
                    return id;
                }
            }
        }
        return id;
    }

    /**
     * Metodo que inserta los usuarios
     *
     * @param usuario nomrbe del usuario
     * @param clave   clave del usuario
     */
    public void insertarUsuario(String usuario, String clave) {
        String clavs = null;
        try {
            clavs = seguridad.get_SHA_512_SecurePassword(clave, usuario);
        } catch (Exception e) {
            Log.e("en", e.toString());
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues datos = new ContentValues();
        datos.put(DB_COLUMNA_USUARIOS_NOMBRE, usuario);
        datos.put(DB_COLUMNA_USUARIOS_CLAVE, clavs);
        db.insert(DB_TABLA_USUARIOS, null, datos);
    }

    /**
     * Metodo que insertar las tareas
     *
     * @param nombre      nombre de la tarea
     * @param descripcion descripcion de la tarea
     * @param estado      estadi de la tarea 1 vigente 0 terminada
     */
    public void insertarTareas(String nombre, String descripcion, int estado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues datos = new ContentValues();
        datos.put(DB_COLUMNA_TAREAS_NOMBRE, nombre);
        datos.put(DB_COLUMNA_TAREAS_DESCRIPCION, descripcion);
        datos.put(DB_COLUMNA_TAREAS_ESTADO, estado);
        db.insert(DB_TABLA_TAREAS, null, datos);
    }

}
