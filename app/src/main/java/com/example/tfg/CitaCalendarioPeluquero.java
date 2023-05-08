package com.example.tfg;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CitaCalendarioPeluquero extends AppCompatActivity {
    FirebaseAuth mAuth;
    private Spinner peluqueroSpinner;
    private Button fechaButton, continuar;
    private FirebaseFirestore db;
    ImageView inicio4, peluqueroImagen;
    String campo;
    TextView fechaTexto;
    Calendar calendar = Calendar.getInstance();
    int currentYear = calendar.get(Calendar.YEAR);
    int currentMonth = calendar.get(Calendar.MONTH);
    int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cita_calendario_peluquero);
        mAuth = FirebaseAuth.getInstance();
        peluqueroSpinner = findViewById(R.id.spinner);
        peluqueroImagen = findViewById(R.id.peluqueroImagen);
        fechaTexto = findViewById(R.id.fecha);
        fechaButton = findViewById(R.id.btn_pick_date);
        continuar = findViewById(R.id.continuar);
        inicio4 = findViewById(R.id.inicio4);
        ArrayList<String> peluqueros_array = new ArrayList<>();
        peluqueros_array.add("SELECCIONA UN PELUQUERO");
        peluqueros_array.add("Josemi");
        peluqueros_array.add("Morillas");
        peluqueros_array.add("Diego");
        peluqueroSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        peluqueroImagen.setImageResource(R.drawable.josemi);
                        break;
                    case 2:
                        peluqueroImagen.setImageResource(R.drawable.morillas);
                        break;
                    case 3:
                        peluqueroImagen.setImageResource(R.drawable.diego);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        inicio4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CitaCalendarioPeluquero.this, PantallaCitas.class));
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, peluqueros_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        peluqueroSpinner.setAdapter(adapter);

        // Conectamos a Firebase Firestore
        db = FirebaseFirestore.getInstance();

        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = (DatePicker) fechaButton.getTag();
                String peluquero = peluqueroSpinner.getSelectedItem().toString();

                if(peluquero.equals("SELECCIONA UN PELUQUERO")){
                    Toast.makeText(CitaCalendarioPeluquero.this, "Es necesario seleccionar un peluquero", Toast.LENGTH_SHORT).show();
                }else{
                    if(datePicker != null){
                            if( (datePicker.getYear() < currentYear)
                                    || (datePicker.getYear() == currentYear && datePicker.getMonth() < currentMonth)
                                    || (datePicker.getYear() == currentYear && datePicker.getMonth() == currentMonth && datePicker.getDayOfMonth() <= currentDay)){
                                Toast.makeText(CitaCalendarioPeluquero.this, "Selecciona una fecha valida", Toast.LENGTH_SHORT).show();
                            }else{

                            int year = datePicker.getYear();
                            int month = datePicker.getMonth()+1;
                            int day = datePicker.getDayOfMonth();

                            // Creamos una consulta a la colección "Citas" donde el campo "Peluquero" contenga el nombre del peluquero seleccionado
                            db.collection("Citas")
                                    .whereEqualTo("peluquero", peluquero)
                                    .whereEqualTo("dia", day).whereEqualTo("mes",month).whereEqualTo("año", year)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot querySnapshot = task.getResult();

                                            if (querySnapshot != null && querySnapshot.size() >= 26) {
                                                // Si hay 26 o más citas, mostramos un Toast indicando que no hay citas disponibles
                                                Toast.makeText(CitaCalendarioPeluquero.this, "No hay citas disponibles para este día y peluquero", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Intent intent = new Intent(CitaCalendarioPeluquero.this, CitaHoraServicio.class);
                                                String diaString = String.valueOf(day);
                                                String mesString = String.valueOf(month);
                                                String añoString = String.valueOf(year);
                                                intent.putExtra("dia", diaString);
                                                intent.putExtra("mes", mesString);
                                                intent.putExtra("año", añoString);
                                                intent.putExtra("peluquero", peluquero);
                                                startActivity(intent);






                                            }
                                        }
                                    });
                        }
                        }else {
                            Toast.makeText(CitaCalendarioPeluquero.this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
                    }

                }

            }

        });


        fechaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String fecha = dayOfMonth + "/" + (month+1) + "/" + year;
                        String peluquero = peluqueroSpinner.getSelectedItem().toString();
                        fechaTexto.setText(fecha);
                        fechaButton.setTag(view); // Guardamos la instancia de DatePicker en el botón para poder obtenerla después
                    }
                }, year, month, day);

        datePickerDialog.show();
    }




}
