package com.example.tfg;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PantallaCitas extends AppCompatActivity {

    ImageView conocenos, pedirCita, precios, misCitas, usuario;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_citas);
        conocenos = findViewById(R.id.conocenos);
        pedirCita = findViewById(R.id.pedirCita);
        precios = findViewById(R.id.precios);
        misCitas = findViewById(R.id.misCitas);
        usuario = findViewById(R.id.usuario);
        mAuth = FirebaseAuth.getInstance();

        setup();

    }

    private void setup() {


        conocenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PantallaCitas.this, conocenosActivity.class));
            }
        });
        usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PantallaCitas.this, usuarioActivity.class));
            }
        });

        precios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PantallaCitas.this, ListaDePrecios.class));
            }

        });
        pedirCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUser = mAuth.getCurrentUser().getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference citasRef = db.collection("Citas");

                citasRef.document(currentUser).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(PantallaCitas.this);
                                        builder.setTitle("Modificar Cita");
                                        builder.setMessage("Ya tiene una cita ¿Desea Modificarla?");

                                        // Agregar botón para cancelar la operación
                                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                // No hacer nada
                                            }
                                        });

                                        // Agregar botón para confirmar la operación
                                        builder.setPositiveButton("Modificar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                startActivity(new Intent(PantallaCitas.this, CitaCalendarioPeluquero.class));
                                            }
                                        });

                                        // Mostrar el diálogo
                                        AlertDialog dialog = builder.create();
                                        dialog.show();

                                    } else {
                                        startActivity(new Intent(PantallaCitas.this, CitaCalendarioPeluquero.class));
                                    }
                                } else {
                                    Log.d(TAG, "Error al obtener el documento: ", task.getException());
                                }
                            }
                        });

            }
        });
        misCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUser = mAuth.getCurrentUser().getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference citasRef = db.collection("Citas");

                citasRef.document(currentUser).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        // Aquí puedes obtener el nombre del documento:
                                        startActivity(new Intent(PantallaCitas.this, MiCitaActivity.class));

                                    } else {
                                        Toast.makeText(PantallaCitas.this, "No tienes ninguna cita", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d(TAG, "Error al obtener el documento: ", task.getException());
                                }
                            }
                        });

            }
        });
    }
}