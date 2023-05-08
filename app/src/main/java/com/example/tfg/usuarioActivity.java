package com.example.tfg;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
public class usuarioActivity extends AppCompatActivity {
    TextView nombreUsuario, telefonoUsuario, emailUsuario, textViewContraseña;
    Button btnEliminarCuenta, botonCerrarSesion;
    ImageView inicio, ojo;
    String contraseña;
    StringBuilder contraseñaOculta;
    boolean contraseñaBoolean = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
        nombreUsuario = findViewById(R.id.nombreUsuario);
        telefonoUsuario = findViewById(R.id.telefonoUsuario);
        emailUsuario = findViewById(R.id.emailUsuario);
        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);
        botonCerrarSesion = findViewById(R.id.botonCerrarSesion);
        textViewContraseña = findViewById(R.id.textViewContraseña);
        ojo = findViewById(R.id.ojo);
        ojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contraseñaBoolean == false){

                    textViewContraseña.setText(contraseña);

                    ojo.setImageResource(R.drawable.ojo_cerrado_blanco);
                    contraseñaBoolean = true;
                }else{
                    textViewContraseña.setText(contraseñaOculta);
                    contraseñaBoolean = false;
                    ojo.setImageResource(R.drawable.ojo_abierto_blanco);
                }


            }
        });
        inicio = findViewById(R.id.inicio2);
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(usuarioActivity.this, PantallaCitas.class));
            }
        });
        setup();
    }

    private void setup() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userDocRef = db.collection("Usuarios").document(userId);

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String userName = document.getString("nombre");
                    String email = document.getString("email");
                    String telefono = document.getString("telefono");
                    contraseña = document.getString("contraseña");



                    int longitudTexto = contraseña.length();
                    contraseñaOculta = new StringBuilder();

                    for (int i = 0; i < longitudTexto; i++) {
                        contraseñaOculta.append("•");
                    }

                    String resultado = contraseñaOculta.toString();





                    nombreUsuario.setText(userName);
                    telefonoUsuario.setText(telefono);
                    emailUsuario.setText(email);
                    textViewContraseña.setText(resultado);
                } else {
                    Log.d(TAG, "User document not found");
                }
            } else {
                Log.e(TAG, "Error getting user document: ", task.getException());
            }
        });
        botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(usuarioActivity.this, MainActivity.class));
            }
        });


        btnEliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Construir el diálogo emergente
                AlertDialog.Builder builder = new AlertDialog.Builder(usuarioActivity.this);
                builder.setTitle("Eliminar cuenta");
                builder.setMessage("Escribe 'ELIMINAR' para eliminar tu cuenta");

                // Agregar el EditText para confirmar
                final EditText input = new EditText(usuarioActivity.this);
                builder.setView(input);
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String confirmacion = input.getText().toString();
                        if (confirmacion.equals("ELIMINAR")) {
                            // Eliminar la cuenta
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            db.collection("Usuarios").document(uid).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseAuth.getInstance().getCurrentUser().delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {
                                                                db.collection("Citas").document(uid).delete();
                                                                Toast.makeText(usuarioActivity.this, "El usuario se elimino exitosamente", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(usuarioActivity.this, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(usuarioActivity.this, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            // Redirigir al usuario a la pantalla de inicio de sesión
                            startActivity(new Intent(usuarioActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // Mostrar mensaje de error
                            Toast.makeText(usuarioActivity.this, "La palabra de confirmación es incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancelar", null);

                // Mostrar el diálogo emergente
                builder.show();
            }
        });

    }
}
