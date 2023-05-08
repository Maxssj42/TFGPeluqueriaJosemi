package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class RestablecerContrasenia extends AppCompatActivity {
    Button enviar;
    EditText email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restablecer_contrasenia);
        email = findViewById(R.id.email);
        enviar = findViewById(R.id.enviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();


                auth.sendPasswordResetEmail(email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // El correo electrónico de restablecimiento de contraseña se ha enviado correctamente
                                    Toast.makeText(RestablecerContrasenia.this, "Se ha enviado un correo electrónico de restablecimiento de contraseña", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Ha ocurrido un error al enviar el correo electrónico de restablecimiento de contraseña
                                    // Verificar si la excepción es de tipo FirebaseAuthUserCollisionException
                                    Exception exception = task.getException();
                                    if (exception instanceof FirebaseAuthInvalidUserException) {
                                        String errorCode = ((FirebaseAuthInvalidUserException) exception).getErrorCode();
                                        if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                                            // El correo electrónico no está asociado a ninguna cuenta de Firebase
                                            Toast.makeText(RestablecerContrasenia.this, "No se ha encontrado ninguna cuenta asociada a este correo electrónico", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Otro tipo de excepción
                                        Toast.makeText(RestablecerContrasenia.this, "Ha ocurrido un error al enviar el correo electrónico de restablecimiento de contraseña", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
    }

}