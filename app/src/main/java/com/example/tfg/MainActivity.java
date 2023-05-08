package com.example.tfg;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Button botonLogin;
    EditText email, contraseña;
    FirebaseAuth mAuth;
    TextView registrar, contraseñaNueva;
    Drawable ojo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        botonLogin = findViewById(R.id.botonLogin);
        email = findViewById(R.id.email);
        contraseña = findViewById(R.id.contraseña);
        registrar = findViewById(R.id.registro);
        contraseñaNueva = findViewById(R.id.contraseñaNueva);

        Bundle bundle = new Bundle();
        bundle.putString("message", "Integracion de Firebase Completa");
        mFirebaseAnalytics.logEvent("InitScreen", bundle);











        // Setup
        setup();
    }

    private void setup() {









        //METODO QUE BORRA LAS CITAS PASADAS

// Obtén la fecha actual
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);



// Crea una referencia a la colección "Citas"
        CollectionReference citasRef = FirebaseFirestore.getInstance().collection("Citas");


// Crea la consulta para obtener todas las citas con un día anterior al actual
        Query queryDay = citasRef.whereEqualTo("año", currentYear)
                .whereEqualTo("mes", currentMonth)
                .whereLessThan("dia", currentDay);
        queryDay.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {

                    citasRef.document(document.getId()).delete();

                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());

            }
        });


        contraseñaNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RestablecerContrasenia.class));
            }
        });
        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                if (TextUtils.isEmpty(email.getText().toString().trim())|| TextUtils.isEmpty(contraseña.getText().toString().trim())){
                    Toast.makeText(MainActivity.this, "Datos no introducidos", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), contraseña.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {


                                        FirebaseUser currentUser = mAuth.getCurrentUser();

                                        String uid = currentUser.getUid();

                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        DocumentReference userRef = db.collection("Usuarios").document(uid);

                                        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    String nombre = documentSnapshot.getString("nombre");
                                                    Toast.makeText(MainActivity.this, "Bienvenido " +nombre, Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(MainActivity.this, PantallaCitas.class));

                                                }
                                            }
                                        });

                                    } else {
                                        // Inicio de sesión fallido
                                        Toast.makeText(MainActivity.this, "INICIO DE SESION FALLIDO", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this,ActivityRegistro.class));

            }
        });















    }
}
