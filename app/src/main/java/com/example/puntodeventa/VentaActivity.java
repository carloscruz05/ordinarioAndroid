package com.example.puntodeventa;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class VentaActivity extends AppCompatActivity {

    private ListView listViewProducts;
    private Button btnFinalizarVenta, btnMenu, btnLimpiar;
    private DatabaseHelper dbHelper;
    private ArrayList<String> productList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);

        listViewProducts = findViewById(R.id.listViewProducts);
        btnFinalizarVenta = findViewById(R.id.btnFinalizarVenta);
        btnMenu = findViewById(R.id.btnMenu);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        dbHelper = new DatabaseHelper(this);

        loadProducts();

        listViewProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showSaleDialog(position);
            }
        });

        btnFinalizarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSalesSummary();
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VentaActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarDatos();
            }
        });
    }

    private void loadProducts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM products", null);

        productList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String product = "ID: " + cursor.getInt(0) + ", Name: " + cursor.getString(1) + ", Price: " + cursor.getString(2) + ", Quantity: " + cursor.getString(3);
            productList.add(product);
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        listViewProducts.setAdapter(adapter);
    }

    private void showSaleDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sale Product");

        View viewInflated = getLayoutInflater().inflate(R.layout.dialog_sale, null);
        builder.setView(viewInflated);

        final EditText inputQuantity = viewInflated.findViewById(R.id.inputQuantity);

        builder.setPositiveButton("Sell", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String quantityStr = inputQuantity.getText().toString();
                if (quantityStr.isEmpty()) {
                    Toast.makeText(VentaActivity.this, "Please enter quantity", Toast.LENGTH_SHORT).show();
                } else {
                    int quantity = Integer.parseInt(quantityStr);
                    processSale(position, quantity);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void processSale(int position, int quantity) {
        String productInfo = productList.get(position);
        String[] productDetails = productInfo.split(",");
        int productId = Integer.parseInt(productDetails[0].split(": ")[1]);
        double price = Double.parseDouble(productDetails[2].split(": ")[1]);

        double importe = price * quantity;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_id", productId);
        values.put("quantity", quantity);
        values.put("price", price);
        values.put("importe", importe);

        long newRowId = db.insert("sales", null, values);
        if (newRowId != -1) {
            Toast.makeText(this, "Sale recorded", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error recording sale", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSalesSummary() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM sales", null);

        ArrayList<String> salesList = new ArrayList<>();
        double total = 0;
        while (cursor.moveToNext()) {
            String sale = "Product ID: " + cursor.getInt(1) + ", Quantity: " + cursor.getInt(2) + ", Price: " + cursor.getDouble(3) + ", Importe: " + cursor.getDouble(4);
            salesList.add(sale);
            total += cursor.getDouble(4);
        }
        cursor.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sales Summary");

        View viewInflated = getLayoutInflater().inflate(R.layout.dialog_sales_summary, null);
        builder.setView(viewInflated);

        ListView listViewSales = viewInflated.findViewById(R.id.listViewSales);
        TextView textViewTotal = viewInflated.findViewById(R.id.textViewTotal);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, salesList);
        listViewSales.setAdapter(adapter);
        textViewTotal.setText("Total: $" + total);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void limpiarDatos() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM sales");
        db.execSQL("DELETE FROM products");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('coca-cola', 10.0, 50)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('mirinda', 12.0, 30)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('manzanita', 8.0, 20)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('Pizza 1', 100.0, 10)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('Pizza 2', 120.0, 15)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('Pizza 3', 90.0, 5)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('Abarrote 1', 20.0, 25)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('Abarrote 2', 15.0, 40)");
        db.execSQL("INSERT INTO products (name, price, quantity) VALUES ('Abarrote 3', 25.0, 35)");

        productList.clear();
        adapter.notifyDataSetChanged();
        loadProducts();

        Toast.makeText(this, "Datos limpiados", Toast.LENGTH_SHORT).show();
    }
}

