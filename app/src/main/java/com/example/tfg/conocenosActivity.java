package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class conocenosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conocenos);
        ImageView localizacionImagen = findViewById(R.id.localizacionImagen);
        ImageView inicio5 = findViewById(R.id.inicio5);
        TextView telefono1 = findViewById(R.id.telefono1);
        TextView telefono2 = findViewById(R.id.telefono2);
        localizacionImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la aplicación de Google Maps en la ubicación especificada

                String url = "https://goo.gl/maps/msESLhzo4iUJtvRa8";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        inicio5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(conocenosActivity.this, PantallaCitas.class));
            }
        });

        telefono1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "910859527"; // Aquí puedes especificar el número que deseas llamar

                Uri uri = Uri.parse("tel:" + phoneNumber);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });
        telefono2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "622590569"; // Aquí puedes especificar el número que deseas llamar

                Uri uri = Uri.parse("tel:" + phoneNumber);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });
    }
}