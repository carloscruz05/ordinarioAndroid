package com.example.puntodeventa;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class InventarioActivity extends AppCompatActivity {

    private ImageView imgRefrescos, imgPizzas, imgAbarrotes;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        imgRefrescos = findViewById(R.id.imgRefrescos);
        imgPizzas = findViewById(R.id.imgPizzas);
        imgAbarrotes = findViewById(R.id.imgAbarrotes);
        dbHelper = new DatabaseHelper(this);

        imgRefrescos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInsertDialog();
            }
        });

        imgPizzas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInsertDialog();
            }
        });

        imgAbarrotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInsertDialog();
            }
        });
    }

    private void showInsertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insert Product");

        View viewInflated = getLayoutInflater().inflate(R.layout.dialog_insert, null);
        builder.setView(viewInflated);

        final EditText inputName = viewInflated.findViewById(R.id.inputName);
        final EditText inputPrice = viewInflated.findViewById(R.id.inputPrice);
        final EditText inputQuantity = viewInflated.findViewById(R.id.inputQuantity);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = inputName.getText().toString();
                String price = inputPrice.getText().toString();
                String quantity = inputQuantity.getText().toString();

                if (name.isEmpty() || price.isEmpty() || quantity.isEmpty()) {
                    Toast.makeText(InventarioActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    saveProduct(name, price, quantity);
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

    private void saveProduct(String name, String price, String quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("price", price);
        values.put("quantity", quantity);

        long newRowId = db.insert("products", null, values);
        if (newRowId != -1) {
            Toast.makeText(this, "Product saved with ID: " + newRowId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error saving product", Toast.LENGTH_SHORT).show();
        }
    }
}
