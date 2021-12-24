package com.example.a2017261037;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MenuChangeActivity extends AppCompatActivity implements MenuAdapter.OnMessageClickListener{
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String writeId = user.getEmail();
    RecyclerView menuRv;
    List<Menu> menuList = new ArrayList<>();
    MenuAdapter menuAdapter;
    FirebaseStorage storage = FirebaseStorage.getInstance();



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button addMenuBtn;
        addMenuBtn = findViewById(R.id.addMenu_btn);
       addMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuChangeActivity.this,MenuAdd.class);
                startActivity(intent);
                finish();
            }
        });

        menuRv  = findViewById(R.id.menuChange_rv);
        menuAdapter = new MenuAdapter(this,menuList);
        menuRv.setAdapter(menuAdapter);
        menuAdapter.setOnmessageListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        menuRv.setLayoutManager(layoutManager);
        getMenu();
    }

    public void getMenu() {


        firestore.collection(writeId)
                .orderBy("menu", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentChange> documentChangeList= value.getDocumentChanges();
                        for (DocumentChange dc : documentChangeList){
                            if (dc.getType() == DocumentChange.Type.ADDED){
                                Menu menu = dc.getDocument().toObject(Menu.class); //메세지 클래스 형태로 변환
                                String id =   dc.getDocument().getId();
                                menu.setId(id);
                                menuList.add(menu);
                            }
                            if(dc.getType() == DocumentChange.Type.REMOVED){
                                String id = dc.getDocument().getId();
                                for (Menu menu: menuList){
                                    if(id.equals(menu.getId())){
                                        menuList.remove(menu);
                                        break;
                                    }
                                }
                            }
                        }
                        menuAdapter.notifyDataSetChanged();
                    }
                });
    }


    @Override
    public void onMessageLongClick(Menu menu) {
        firestore.collection(writeId).document(menu.getId()).delete();
        storage.getReference().child(writeId + "/" + menu.getFileName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

}
