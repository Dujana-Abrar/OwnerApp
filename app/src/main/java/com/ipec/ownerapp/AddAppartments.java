package com.ipec.ownerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AddAppartments extends AppCompatActivity {


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "AddAppartments";
    List<String> sizes;
    Button add;
    String selectedSpinnerItem;
    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appartments);

        sizes = new ArrayList<>();
        getListItems();
        add = findViewById(R.id.Add_Data);
        add.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v)
            {
                addDataToDatabase();
            }});
    }


    public void getListItems()
    {
        db.collection("rooms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String size = document.getString("quantity");
                                System.out.println(size);
                                sizes.add(size);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            spinner = findViewById(R.id.spinner);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, sizes);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        selectedSpinnerItem = parent.getItemAtPosition(position).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    public void addDataToDatabase()
    {
        EditText TVname = findViewById(R.id.name);
        EditText TVcity = findViewById(R.id.city);
        EditText TVdescription = findViewById(R.id.description);
        EditText TVrent = findViewById(R.id.rent);
        EditText TVaddress = findViewById(R.id.address);

        String name = TVname.getText().toString();
        String city = TVcity.getText().toString();
        String description = TVdescription.getText().toString();
        String rent = TVrent.getText().toString();
        String address = TVaddress.getText().toString();

        if(name.isEmpty() || city.isEmpty() || description.isEmpty() || rent.isEmpty() || address.isEmpty() || selectedSpinnerItem.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "All Fields are mandatory", Toast.LENGTH_LONG).show();
        }

        else {
            Map<String, Object> apartment = new HashMap<>();
            apartment.put("acc_name", name);
            apartment.put("address", address);
            apartment.put("image", "qisi");
            apartment.put("location", "location");
            apartment.put("min_duration", "12");
            apartment.put("rent", rent);
            apartment.put("room", "room");

            db.collection("accommodations").document(generateRandom())
                    .set(apartment)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);
                }
            });
        }
    }


    public String generateRandom()
    {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        // create random string builder
        StringBuilder sb = new StringBuilder();

        // create an object of Random class
        Random random = new Random();

        // specify length of random string
        int length = 7;

        for(int i = 0; i < length; i++) {

            // generate random index number
            int index = random.nextInt(alphabet.length());

            // get character specified by index
            // from the string
            char randomChar = alphabet.charAt(index);

            // append the character to string builder
            sb.append(randomChar);
        }
        return sb.toString();
    }


}