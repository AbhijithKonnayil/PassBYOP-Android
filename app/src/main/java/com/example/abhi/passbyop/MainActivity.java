package com.example.abhi.passbyop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.sql.Types.NULL;

public class MainActivity extends AppCompatActivity {
    private TextView title;
    private Button btnChoose, btnRegister, btnLogin, btnReset;
    private ImageView imageView;
    private EditText etUsername;
    private int X0, Y0, X1, Y1, X2, Y2, X3, Y3, touchCount = 0,attempts=0;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference dbref;
    Retrofit retrofit;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),"fonts/KaushanScript-Regular.ttf");
        title = findViewById(R.id.title);
        title.setTypeface(custom_font);
        btnReset= (Button) findViewById(R.id.btnReset);
        btnChoose = (Button) findViewById(R.id.btnChoose);
        btnRegister = (Button) findViewById(R.id.btnUpload);
        btnLogin = (Button) findViewById(R.id.login);
        imageView = (ImageView) findViewById(R.id.imgView);
        etUsername = findViewById(R.id.etUsername);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etUsername.setText("");
                touchCount=0;
                imageView.setImageResource(0);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
        dbref = FirebaseDatabase.getInstance().getReference("user");

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (imageView.getDrawable()==null){
                    Toast.makeText(getApplicationContext(),"Please choose an Image",Toast.LENGTH_LONG).show();
                }else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float screenX = event.getX();
                    float screenY = event.getY();

                    switch (touchCount) {
                        case 0:
                            X0 = Math.round(screenX / 100) * 100;
                            Y0 = Math.round(screenY / 100) * 100;
                            Log.e(String.valueOf(X0), String.valueOf(Y0 + " " + touchCount));
                            Toast.makeText(MainActivity.this, "Need 3 Points more", Toast.LENGTH_SHORT).show();
                            break;

                        case 1:
                            X1 = Math.round(screenX / 100) * 100;
                            Y1 = Math.round(screenY / 100) * 100;
                            Log.e(String.valueOf(X1), String.valueOf(Y1 + " " + touchCount));
                            Toast.makeText(MainActivity.this, "Need 2 Points more", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            X2 = Math.round(screenX / 100) * 100;
                            Y2 = Math.round(screenY / 100) * 100;
                            Log.e(String.valueOf(X2), String.valueOf(Y2 + " " + touchCount));
                            Toast.makeText(MainActivity.this, "Need 1 Points more", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            X3 = Math.round(screenX / 100) * 100;
                            Y3 = Math.round(screenY / 100) * 100;
                            Log.e(String.valueOf(X3), String.valueOf(Y3 + " " + touchCount));
                            Toast.makeText(MainActivity.this, "Added 4 Points", Toast.LENGTH_SHORT).show();
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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etUsername.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"Username Cannot be empty",Toast.LENGTH_LONG).show();
                }
                else if (touchCount < 4) {
                    Log.e("dkj",etUsername.getText().toString());
                    Toast.makeText(MainActivity.this, "Need " + String.valueOf(4 - touchCount) + " Points more", Toast.LENGTH_SHORT).show();
                }
                else if(attempts>3){
                    Toast.makeText(MainActivity.this,"Too Many Attempts !!",Toast.LENGTH_LONG).show();
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final StorageReference Imageref = storageReference.child("temp/" + UUID.randomUUID());
            Log.e("Stogare ref", Imageref.toString());
            Imageref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadURL = uri.toString();
                                    Log.e("download temp url",downloadURL.toString());
                                    String username = etUsername.getText().toString();
                                    RetroUserModel retroUserModel = new RetroUserModel(username, X0, Y0, X1, Y1, X2, Y2, X3, Y3, downloadURL);
                                    Api api = retrofit.create(Api.class);
                                    Call<RetroUserModel> call = api.getUser(retroUserModel);
                                    call.enqueue(new Callback<RetroUserModel>() {
                                        @Override
                                        public void onResponse(Call<RetroUserModel> call, Response<RetroUserModel> response) {
                                            if (!response.isSuccessful()) {
                                                Log.e("Response ", " " + response.body());
                                                if(response.code()==401){
                                                    Toast.makeText(getApplicationContext(),"Password Error",Toast.LENGTH_LONG).show();
                                                    touchCount=0;
                                                    X0=X1=X2=X3=Y0=Y1=Y2=Y3=NULL;
                                                    imageView.setImageResource(0);
                                                    attempts++;
                                                }
                                                else if(response.code()==404){
                                                    Toast.makeText(getApplicationContext(),"Invalid Username",Toast.LENGTH_LONG).show();
                                                    touchCount=0;
                                                    etUsername.setText("");
                                                    X0=X1=X2=X3=Y0=Y1=Y2=Y3=NULL;
                                                    imageView.setImageResource(0);
                                                }
                                                Log.e("Errror on register ", " " + response.code());
                                                return;
                                            }
                                            RetroUserModel res = response.body();
                                            Intent intent = new Intent(MainActivity.this, SampleActivity.class);
                                            intent.putExtra("User", res.getUsername());
                                            startActivity(intent);
                                            finish();
                                            Log.e("Sucess on register ", "" + response.code());
                                            Log.e("Username  ", "" + res.getUsername() + res.getImage_url());
                                        }

                                        @Override
                                        public void onFailure(Call<RetroUserModel> call, Throwable t) {
                                            Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });

                            Toast.makeText(MainActivity.this, "Wait", Toast.LENGTH_SHORT).show();


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }


}
