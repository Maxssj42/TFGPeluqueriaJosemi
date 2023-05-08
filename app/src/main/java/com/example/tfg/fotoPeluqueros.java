package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class fotoPeluqueros extends AppCompatActivity {
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_peluqueros);
        mAuth = FirebaseAuth.getInstance();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // El usuario ya está conectado, omitir la pantalla de inicio de sesión y redirigir a la pantalla principal

                    currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (currentUser != null) {

                        String uid = currentUser.getUid();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference userRef = db.collection("Usuarios").document(uid);

                        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String nombre = documentSnapshot.getString("nombre");
                                    Toast.makeText(fotoPeluqueros.this, "Bienvenido " +nombre, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(fotoPeluqueros.this, PantallaCitas.class));
                                    finish();
                                }
                            }
                        });
                    }
                }else{
                    // Iniciar la siguiente actividad después de 2 segundos (2000 milisegundos)
                    Intent intent = new Intent(fotoPeluqueros.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 2000);
    }
}