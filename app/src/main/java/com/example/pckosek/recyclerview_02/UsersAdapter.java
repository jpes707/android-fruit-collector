package com.example.pckosek.recyclerview_02;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {
    private static final String PREFERENCE_KEY = "fruitKey";
    private static final String TAG = "logTag";

    private Activity context;
    private SharedPreferences.Editor mEditor;

    static List<MyUser> usersList;
    static List<MyViewHolder> holders;

    @SuppressLint("CommitPrefEdits")
    UsersAdapter(List<MyUser> ul, Context applicationContext) {
        this.usersList = ul;
        context = (Activity) applicationContext;
        holders = new ArrayList<MyViewHolder>();
        mEditor = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE).edit();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Gson gson = new GsonBuilder().create();
        final MyUser user = usersList.get(position);
        holders.add(holder);

        holder.textViewScore.setText( String.valueOf(user.score) );

        byte[] decodedString = Base64.decode(user.image, Base64.DEFAULT);
        holder.mImageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));

        holder.mButtonLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.score++;
                holder.textViewScore.setText( String.valueOf(user.score) );
                mEditor.putString(String.valueOf(position), gson.toJson(user));
                mEditor.apply();
            }
        });
        holder.mButtonImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    context.requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    context.startActivityForResult(cameraIntent, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewScore;
        Button mButtonLikes, mButtonImage;
        ImageView mImageView;

        MyViewHolder(View view) {
            super(view);
            textViewScore = view.findViewById(R.id.score);
            mButtonLikes = view.findViewById(R.id.likeButton);
            mButtonImage = view.findViewById(R.id.imageButton);
            mImageView = view.findViewById(R.id.imageView);
        }

    }
}