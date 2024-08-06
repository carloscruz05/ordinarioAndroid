package com.example.puntodeventa;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "OrdinarioBD.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
        db.execSQL("CREATE TABLE products (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, price REAL, quantity INTEGER)");
        db.execSQL("CREATE TABLE sales (id INTEGER PRIMARY KEY AUTOINCREMENT, product_id INTEGER, quantity INTEGER, price REAL, importe REAL)");

        // Insertar productos predeterminados con nuevos nombres
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('Coca Cola', 10.0, 50)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('Pepsi', 12.0, 30)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('Fanta', 8.0, 20)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('Pizza Margarita', 100.0, 10)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('Pizza Pepperoni', 120.0, 15)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('Pizza Hawaiana', 90.0, 5)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('Harina', 20.0, 25)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('Azúcar', 15.0, 40)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('Arroz', 25.0, 35)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS sales");
        onCreate(db);
    }
}

