package com.example.a2017261037;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SecondActivity extends AppCompatActivity {
    List<Food> menuList = new ArrayList<>();
    Random random = new Random();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore  = FirebaseFirestore.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String writeId = user.getEmail();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    int i = -1;
    int num;
    int num2=9999;
    MenuAdapter menuAdapter;


   @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_second);

        TextView menuTv;
        Button anotherBtn;
        ImageView imageView;
        Toolbar myTollBar;


        imageView = findViewById(R.id.imageView);
        menuTv =findViewById(R.id.menu_tv);
        anotherBtn = findViewById(R.id.another_btn);
        myTollBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myTollBar);
       getdata();

        anotherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i ==-1){
                    Toast.makeText(getApplicationContext(), "저장된 음식이 없음", Toast.LENGTH_SHORT).show();
                }
                else if (i ==1) {
                    num = random.nextInt(i);
                    Glide.with(imageView).load(menuList.get(num).getMenuUrl()).into(imageView);
                    menuTv.setText(menuList.get(num).getMenu());
                }
                else if(i>=2) {
                    num = random.nextInt(i);
                    while (num == num2) {
                        num = random.nextInt(i);
                    }
                    num2 = num;
                    Log.d("아이", String.valueOf(num));
                    Glide.with(imageView).load(menuList.get(num).getMenuUrl()).into(imageView);
                    menuTv.setText(menuList.get(num).getMenu());
                }
            }
        }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.main,menu);
        MenuItem menuChange = menu.findItem(R.id.menuChange);
        MenuItem logout = menu.findItem(R.id.logout);
        MenuItem delete = menu.findItem(R.id.delete);
        menuChange.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(SecondActivity.this,MenuChangeActivity.class);
                startActivity(intent);
                return false;
            }
        });
        logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                signOut();
                Intent intent = new Intent(SecondActivity.this,MainActivity.class);
                startActivity(intent);
                return false;
            }
        });
        delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                revokeAccess();
                finishAffinity();
                return false;
            }
        });

        return true;
    }
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }
    private void revokeAccess() {
        mAuth.getCurrentUser().delete();
    }
    public void getdata(){

        firestore.collection(writeId)
                .orderBy("menu", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentChange> documentChangeList= value.getDocumentChanges();
                        for (DocumentChange dc : documentChangeList){
                            i  =  documentChangeList.size();
                            if (dc.getType() == DocumentChange.Type.ADDED){
                                i  =  documentChangeList.size();
                                Food food = dc.getDocument().toObject(Food.class);
                                menuList.add(food);
                            }
                            if(dc.getType() == DocumentChange.Type.REMOVED){
                                if (documentChangeList.size() == 0) {
                                    i = -1;
                                }
                                else {
                                    i  =  documentChangeList.size();
                                }
                                for (Food food: menuList){
                                        menuList.remove(food);
                                        break;
                                    }
                                }
                            }
                        }
                });
    }
}
