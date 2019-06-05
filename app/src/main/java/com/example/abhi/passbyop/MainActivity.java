package com.example.abhi.passbyop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Button btnChoose, btnUpload,btnLogin;
    private ImageView imageView;
    private EditText etUsername;
    private int X0,Y0,X1,Y1,X2,Y2,X3,Y3,touchCount=0;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference dbref;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnChoose = (Button) findViewById(R.id.btnChoose);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnLogin = (Button) findViewById(R.id.login);
        imageView = (ImageView) findViewById(R.id.imgView);
        etUsername = findViewById(R.id.etUsername);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        final Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Api.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Api api = retrofit.create(Api.class);
                String username = etUsername.getText().toString();
                Call<RetroUserModel> call = api.getUser(username);
                call.enqueue(new Callback<RetroUserModel>() {
                    @Override
                    public void onResponse(Call<RetroUserModel> call, Response<RetroUserModel> response) {
                        RetroUserModel user = response.body();
                        Log.e("User",user.getUsername());
                        Toast.makeText(getApplicationContext(),"Sucess",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<RetroUserModel> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                        Log.e("Error",t.getMessage());
                    }
                });

            }
        }); 

        dbref = FirebaseDatabase.getInstance().getReference("user");

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    float screenX = event.getX();
                    float screenY = event.getY();

                    switch (touchCount){
                        case 0:
                                X0 = Math.round(screenX/100)*100;
                                Y0= Math.round(screenY/100)*100;
                                Log.e(String.valueOf(X0),String.valueOf(Y0 +" " + touchCount));
                                break;

                        case 1:
                                X1 = Math.round(screenX/100)*100;
                                Y1 = Math.round(screenY/100)*100;
                            Log.e(String.valueOf(X1),String.valueOf(Y1 +" " + touchCount));
                                break;
                        case 2:
                                X2 = Math.round(screenX/100)*100;
                                Y2 = Math.round(screenY/100)*100;
                            Log.e(String.valueOf(X2),String.valueOf(Y2 +" " + touchCount));
                                break;
                        case 3:
                                X3 = Math.round(screenX/100)*100;
                                Y3 = Math.round(screenY/100)*100;
                            Log.e(String.valueOf(X3),String.valueOf(Y3 +" " + touchCount));
                                break;
                    }
                    touchCount++;
                    return true;
                }
                return false;
            }
        });
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(touchCount<4)
                {
                    Toast.makeText(MainActivity.this,"Need "+String.valueOf(4-touchCount+1) +" Points more",Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadImage();
                }
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user");
        Log.e("db", String.valueOf(ref));
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            final String storageRef = ref.toString();
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            String username = etUsername.getText().toString();
                            dbref.child(username).child("image").setValue(storageRef);
                            dbref.child(username).child("coor").child("X0").setValue(X0);
                            dbref.child(username).child("coor").child("Y0").setValue(Y0);
                            dbref.child(username).child("coor").child("X1").setValue(X1);
                            dbref.child(username).child("coor").child("Y1").setValue(Y1);
                            dbref.child(username).child("coor").child("X2").setValue(X2);
                            dbref.child(username).child("coor").child("Y2").setValue(Y2);
                            dbref.child(username).child("coor").child("X3").setValue(X3);
                            dbref.child(username).child("coor").child("Y3").setValue(Y3);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }



}
