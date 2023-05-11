package com.example.tfg;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitaHoraServicio extends AppCompatActivity {
    Spinner spinnerHora, servicio;
    ImageView imagenPeluquero;
    TextView fecha;
    Button reservar;
    String horaSeleccionada;
    String servicioSeleccionado;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cita_hora_servicio);
        spinnerHora = findViewById(R.id.hora);
        servicio = findViewById(R.id.servicio);
        imagenPeluquero = findViewById(R.id.imagenPeluquero);
        fecha = findViewById(R.id.fecha);
        reservar = findViewById(R.id.reservar);
        mAuth = FirebaseAuth.getInstance();
        spinnerHora.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 horaSeleccionada = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se ha seleccionado nada
            }
        });
        servicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 servicioSeleccionado = parent.getItemAtPosition(position).toString();
                // Aquí se puede hacer algo con el servicio seleccionado
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se ha seleccionado nada
            }
        });
        setup();

    }


    private void setup() {
        String diaString = getIntent().getStringExtra("dia");
        String mesString = getIntent().getStringExtra("mes");
        String añoString = getIntent().getStringExtra("año");
        String peluquero = getIntent().getStringExtra("peluquero");
        int dia = Integer.parseInt(diaString);
        int mes = Integer.parseInt(mesString);
        int año = Integer.parseInt(añoString);

        switch (peluquero) {
            case "Josemi":
                imagenPeluquero.setImageResource(R.drawable.josemi);
                break;
            case "Morillas":
                imagenPeluquero.setImageResource(R.drawable.morillas);
                break;
            case "Diego":
                imagenPeluquero.setImageResource(R.drawable.diego);
                break;

        }

        fecha.setText(diaString + "/" + mesString + "/" + añoString);


        String peluqueroString = getIntent().getStringExtra("peluquero");

        Toast.makeText(this, dia+ "/" + mes + "/" + año+ "/" + peluqueroString, Toast.LENGTH_SHORT).show();

// Crear una lista de horas disponibles
        List<String> horasDisponibles = new ArrayList<>();
        for (int hora = 11; hora <= 20; hora++) { // de 9:00AM a 10:00PM
            for (int minuto = 0; minuto < 60; minuto += 30) { // con intervalo de 30 minutos
                String horaString = String.format("%02d", hora); // para formatear a dos dígitos
                String minutoString = String.format("%02d", minuto); // para formatear a dos dígitos
                horasDisponibles.add(horaString + ":" + minutoString);
            }
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference citasRef = db.collection("Citas");
        Query query = citasRef.whereEqualTo("dia", dia)
                .whereEqualTo("mes", mes)
                .whereEqualTo("año", año)
                .whereEqualTo("peluquero", peluqueroString);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> horasOcupadas = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String horaOcupada = document.getString("hora");
                    horasOcupadas.add(horaOcupada);
                }
                horasDisponibles.removeAll(horasOcupadas);

                // Llenar el selector box con las horas disponibles
                horasDisponibles.add(0, "HORA");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, horasDisponibles);
                spinnerHora.setAdapter(adapter);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });



        reservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (horaSeleccionada.equals("HORA") || servicioSeleccionado.equals("SERVICIO")){
                    Toast.makeText(CitaHoraServicio.this, "Debes seleccionar una opción válida", Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String uid = currentUser.getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("Usuarios").document(uid);
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                String campo = documentSnapshot.getString("nombre");
                                Map<String, Object> cita = new HashMap<>();
                                cita.put("dia", dia);
                                cita.put("mes", mes);
                                cita.put("año", año);
                                cita.put("hora", horaSeleccionada);
                                cita.put("peluquero", peluquero);
                                cita.put("Servicio", servicioSeleccionado);
                                cita.put("nombre" , campo);



                                db.collection("Citas").document(uid).set(cita)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(CitaHoraServicio.this, "Cita Reservada", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(CitaHoraServicio.this, PantallaCitas.class));



                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(CitaHoraServicio.this, "Error al registrar la cita", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Log.d(TAG, "No existe el documento");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error al obtener el documento: ", e);
                        }
                    });

                }


            }
        });
    }
}