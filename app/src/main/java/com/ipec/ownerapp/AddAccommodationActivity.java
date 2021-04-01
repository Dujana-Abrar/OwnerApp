package com.ipec.ownerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class AddAccommodationActivity extends AppCompatActivity {


    private static final int PICK_IMAGE = 1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "AddAccommodations";
    List<String> sizes;
    Button btn_add;
    String selectedSpinnerItem;
    Spinner spinner;
    ImageView imageView;
    Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    private String path;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addaccommodation);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        sizes = new ArrayList<>();
        getListItems();
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v)
            {
                addDataToDatabase();
            }});

        imageView = findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            filePath = data.getData();
            imageView.setImageURI(filePath);
        }
    }


    private void uploadImage()
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Image");
            progressDialog.show();

            // Defining the child of storageReference


            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    path=uri.toString();
                    Log.d("path", path);
                }
            });
            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddAccommodationActivity.this, "Image Uploaded!!", Toast.LENGTH_LONG).show();
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(AddAccommodationActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
                                }
                            });
        }
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
        EditText TVname = findViewById(R.id.editText_name);
        EditText TVcity = findViewById(R.id.editText_city);
        EditText TVnotice = findViewById(R.id.editText_notice);
        EditText TVrent = findViewById(R.id.editText_rent);
        EditText TVaddress = findViewById(R.id.editText_address);

        String name = TVname.getText().toString();
        String city = TVcity.getText().toString();
        String notice = TVnotice.getText().toString();
        String rent = TVrent.getText().toString();
        String address = TVaddress.getText().toString();

        if(name.isEmpty() || city.isEmpty() || notice.isEmpty() || rent.isEmpty() || address.isEmpty() || selectedSpinnerItem.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "All Fields are mandatory", Toast.LENGTH_LONG).show();
        }

        else {
            uploadImage();
            Map<String, Object> accMap = new HashMap<>();
            accMap.put("acc_name", name);
            accMap.put("address", address);
            accMap.put("image", path);
            accMap.put("location", city);
            accMap.put("notice_period", notice);
            accMap.put("rent", rent);
            accMap.put("room", selectedSpinnerItem);

            db.collection("accommodations").document(generateRandom())
                    .set(accMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            finish();
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