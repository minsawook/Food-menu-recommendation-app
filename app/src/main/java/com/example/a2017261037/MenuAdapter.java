package com.example.a2017261037;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder>{
    List<Menu> mMenuList;
    Context mcontext;
    OnMessageClickListener mListener;
    public  MenuAdapter(Context context, List<Menu> menuList){
        this.mMenuList = menuList;
        this.mcontext = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.item_menu,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Menu menu = mMenuList.get(position);
        holder.bind(menu);
    }

    @Override
    public int getItemCount() {
       return mMenuList.size();
    }


    public  class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView menuNameTv;
        Button deleteBtn;
        Menu mMenu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            menuNameTv = itemView.findViewById(R.id.menuName_tv);
            deleteBtn = itemView.findViewById(R.id.delete_btn);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onMessageLongClick(mMenu);
                    Toast.makeText(mcontext, "삭제완료", Toast.LENGTH_SHORT).show();
                }
            });
        }
      public void bind(Menu menu) {
          mMenu = menu;
            menuNameTv.setText(menu.getMenu());
            Glide.with(imageView).load(menu.getMenuUrl()).into(imageView);
        }
    }
    interface OnMessageClickListener{
        void onMessageLongClick(Menu menu);
    }
    public void setOnmessageListener(MenuChangeActivity listener){
        this.mListener =listener;
    }
}
