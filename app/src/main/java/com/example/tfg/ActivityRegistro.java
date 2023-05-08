package com.example.tfg;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActivityRegistro extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText editTextName, editTextPhone, editTextEmail, editTextContraseña;
    private Button buttonRegister;

    private static final String TAG = "MainActivity";
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextName = findViewById(R.id.nombre);
        editTextPhone = findViewById(R.id.telefono);
        buttonRegister = findViewById(R.id.botonRegistrar);
        editTextContraseña = findViewById(R.id.contraseña);
        editTextEmail =findViewById(R.id.email);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String email =  editTextEmail.getText().toString();
        final String name = editTextName.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String contraseña = editTextContraseña.getText().toString();
        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Se requiere un nombre");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError("Se requiere un telefono");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Se requiere un Email");
            return;
        }

        if (TextUtils.isEmpty(contraseña)) {
            editTextContraseña.setError("Se requiere una contraseña");
            return;
        }




        // Registrar el usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, contraseña)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Obtener el ID del usuario registrado
                            String uid = task.getResult().getUser().getUid();

                            // Crear un nuevo documento para el usuario en Firestore
                            Map<String, Object> user = new HashMap<>();
                            user.put("nombre", name);
                            user.put("telefono", phone);
                            user.put("email", email);
                            user.put("contraseña", contraseña);
                            db.collection("Usuarios").document(uid).set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ActivityRegistro.this, "Usuario Registrado con exito", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ActivityRegistro.this, MainActivity.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ActivityRegistro.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(ActivityRegistro.this, "El correo electrónico introducido ya está registrado", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ActivityRegistro.this, "Error registrando el usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });


    }
}