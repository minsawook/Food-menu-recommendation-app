package com.example.a2017261037;

import static android.content.Intent.ACTION_PICK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class MenuAdd extends AppCompatActivity {
    final  int REQ_IMAGE_PICK = 1000;
    Uri selectImage;
    ImageView imageIv;
    EditText menuEt;
    Button submitBtn;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_add);

        imageIv  = findViewById(R.id.image_iv);
        menuEt = findViewById(R.id.menu_et);
        submitBtn = findViewById(R.id.submit_btn);

        imageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQ_IMAGE_PICK);
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fileName = UUID.randomUUID().toString();
                storage.getReference().child(auth.getCurrentUser().getEmail()).child(fileName).putFile(selectImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                uploadPost(imageUrl,fileName);
                                Log.d("MainAC", uri.toString());
                            }
                        });
                    }
                });
            }
        });
    }
    public void  uploadPost(String imageUrl, String fileName){
        String menun = menuEt.getText().toString();
        String FileName = fileName;
        FirebaseUser user = auth.getCurrentUser();
        String writeId;
        if (user == null){
            writeId="dnr111222@naver.com";
        }
        else{
            writeId = user.getEmail();
        }
        Menu menu = new Menu();
        menu.setMenu(menun);
        menu.setMenuUrl(imageUrl);
        menu.setFileName(FileName);
        firestore.collection(writeId).document().set(menu).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                menuEt.setText("");
                Intent intent = new Intent(MenuAdd.this,SecondActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQ_IMAGE_PICK && resultCode == RESULT_OK){
            imageIv.setImageURI(data.getData());
            selectImage = data.getData();
        }
    }

}

