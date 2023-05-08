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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MiCitaActivity extends AppCompatActivity {
    ImageView peluqueroImagen, inicio;
    TextView peluqueroTexto, servicioEditable, fechaTexto, horaTexto;
    FirebaseAuth mAuth;
    Button cancelarCita;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_cita);
        mAuth = FirebaseAuth.getInstance();
        peluqueroImagen = findViewById(R.id.imagenPeluquero);
        peluqueroTexto = findViewById(R.id.peluqueroTextoEditable);
        servicioEditable = findViewById(R.id.servicioEditable);
        horaTexto = findViewById(R.id.horaEditable);
        fechaTexto = findViewById(R.id.fechaEditable);
        inicio = findViewById(R.id.inicio3);
        cancelarCita = findViewById(R.id.cancelarCita);
        Button modificarCita = findViewById(R.id.modificarCita);
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MiCitaActivity.this, PantallaCitas.class));
            }
        });

        modificarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MiCitaActivity.this);
                builder.setTitle("Modificar Cita");
                builder.setMessage("¿Desea modificar la cita?");

                // Agregar botón para cancelar la operación
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // No hacer nada
                    }
                });

                // Agregar botón para confirmar la operación
                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(MiCitaActivity.this, CitaCalendarioPeluquero.class));
                    }
                });

                // Mostrar el diálogo
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        cancelarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MiCitaActivity.this);
                builder.setTitle("Cancelar cita");
                builder.setMessage("¿Está seguro de que desea cancelar la cita?");

                // Agregar botón para cancelar la operación
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // No hacer nada
                    }
                });

                // Agregar botón para confirmar la operación
                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String currentUser = mAuth.getCurrentUser().getUid();
                        // Obtener la referencia del documento "citaId" de la colección "Citas"
                        DocumentReference docRef = db.collection("Citas").document(currentUser);

                        docRef.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Documento borrado correctamente!");
                                        Toast.makeText(MiCitaActivity.this, "Cita cancelada correctamente", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MiCitaActivity.this, PantallaCitas.class));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error al borrar documento", e);
                                    }
                                });
                    }
                });

                // Mostrar el diálogo
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    setup();

    }

    private void setup() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUser = mAuth.getCurrentUser().getUid();
        // Obtener la referencia del documento "citaId" de la colección "Citas"
        DocumentReference docRef = db.collection("Citas").document(currentUser);

// Obtener los datos del documento "citaId" como un objeto DocumentSnapshot
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Obtener los valores de los campos del documento
                String peluquero = documentSnapshot.getString("peluquero");
                int dia = documentSnapshot.getLong("dia").intValue();
                int mes = documentSnapshot.getLong("mes").intValue();
                int año = documentSnapshot.getLong("año").intValue();
                String servicio = documentSnapshot.getString("Servicio");
                String hora = documentSnapshot.getString("hora");
                peluqueroTexto.setText(peluquero);

                horaTexto.setText(hora);

                fechaTexto.setText(dia + "/" + mes + "/" + año);



                switch (peluquero) {
                    case "Diego":
                        peluqueroImagen.setImageResource(R.drawable.diego);
                        break;
                    case "Morillas":
                        peluqueroImagen.setImageResource(R.drawable.morillas);
                        break;
                    case "Josemi":
                        peluqueroImagen.setImageResource(R.drawable.josemi);
                        break;

                }
                servicioEditable.setText(servicio);

                // Hacer lo que necesites con los valores de los campos
                // ...
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error al obtener los datos del documento", e);
            }
        });

    }
}