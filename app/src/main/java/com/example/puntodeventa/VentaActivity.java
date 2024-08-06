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
    private Button btnFinalizarVenta, btnLimpiar, btnMenu;
    private DatabaseHelper dbHelper;
    private ArrayList<String> productList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);

        listViewProducts = findViewById(R.id.listViewProducts);
        btnFinalizarVenta = findViewById(R.id.btnFinalizarVenta);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnMenu = findViewById(R.id.btnMenu);

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

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSales();
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VentaActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadProducts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM products", null);

        productList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String product = cursor.getString(1); // Obtener solo el nombre del producto
            productList.add(product);
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        listViewProducts.setAdapter(adapter);
    }

    private void showSaleDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Venta de Producto");

        View viewInflated = getLayoutInflater().inflate(R.layout.dialog_sale, null);
        builder.setView(viewInflated);

        final EditText inputQuantity = viewInflated.findViewById(R.id.inputQuantity);

        builder.setPositiveButton("Vender", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String quantityStr = inputQuantity.getText().toString();
                if (quantityStr.isEmpty()) {
                    Toast.makeText(VentaActivity.this, "Por favor, ingrese la cantidad", Toast.LENGTH_SHORT).show();
                } else {
                    int quantity = Integer.parseInt(quantityStr);
                    processSale(position, quantity);
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void processSale(int position, int quantity) {
        String productName = productList.get(position);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM products WHERE name=?", new String[]{productName});
        if (cursor.moveToFirst()) {
            int productId = cursor.getInt(0);
            double price = cursor.getDouble(2);

            double importe = price * quantity;

            ContentValues values = new ContentValues();
            values.put("product_id", productId);
            values.put("quantity", quantity);
            values.put("price", price);
            values.put("importe", importe);

            long newRowId = db.insert("sales", null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "Venta registrada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al registrar la venta", Toast.LENGTH_SHORT).show();
            }
        }
        cursor.close();
    }

    private void showSalesSummary() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM sales", null);

        ArrayList<String> salesList = new ArrayList<>();
        double total = 0;
        while (cursor.moveToNext()) {
            String sale = "ID del Producto: " + cursor.getInt(1) + ", Cantidad: " + cursor.getInt(2) + ", Precio: " + cursor.getDouble(3) + ", Importe: " + cursor.getDouble(4);
            salesList.add(sale);
            total += cursor.getDouble(4);
        }
        cursor.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resumen de Ventas");

        View viewInflated = getLayoutInflater().inflate(R.layout.dialog_sales_summary, null);
        builder.setView(viewInflated);

        ListView listViewSales = viewInflated.findViewById(R.id.listViewSales);
        TextView textViewTotal = viewInflated.findViewById(R.id.textViewTotal);

        // Continuación del método showSalesSummary
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, salesList);
        listViewSales.setAdapter(adapter);

        textViewTotal.setText("Total: $" + total);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void clearSales() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete("sales", null, null);
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Ventas borradas", Toast.LENGTH_SHORT).show();
            loadProducts(); // Opcional: recargar la lista de productos si es necesario
        } else {
            Toast.makeText(this, "No se pudieron borrar las ventas", Toast.LENGTH_SHORT).show();
        }
    }
}


