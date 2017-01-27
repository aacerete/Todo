package com.example.a46990527d.todo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> todos;
    FirebaseListAdapter adapter;
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //todos los registros de todos los usuarios
        final DatabaseReference todosRef = database.getReference("message");

        //menu de autenticacion de usuario
        setupAuth();

        //Registros del usuario logueado
        final DatabaseReference userRef = todosRef.child(mAuth.getCurrentUser().getUid());

        //Listview de referencias
        ListView listView = (ListView) findViewById(R.id.lvTodos);

        //adapter
        todos = new ArrayList<>();
        adapter = new FirebaseListAdapter<String>(this, String.class, android.R.layout.simple_list_item_1, userRef) {
            @Override
            protected void populateView(View v, String model, int position) {
                Log.d(null, model);
                ((TextView) v.findViewById(android.R.id.text1)).setText(model);
            }
        };

        listView.setAdapter(adapter);

        Button btnSend = (Button) findViewById(R.id.btnSend);
        final EditText txt = (EditText) findViewById(R.id.etTodo);

        //al donar al btton pujem la referencia de text
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference nou = userRef.push();
                nou.setValue(txt.getText().toString());
                txt.setText("");
            }
        });

    }

    //Autenticacio
    private void setupAuth(){
        mAuth = FirebaseAuth.getInstance();
        //si ja estem loguejats
        if (mAuth.getCurrentUser() != null) {
            Log.d("Current user", String.valueOf(mAuth.getCurrentUser()));

            //si no obrim activity d'autenticació
        } else {
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
                            ).build(),
                    RC_SIGN_IN);}
    }

}
