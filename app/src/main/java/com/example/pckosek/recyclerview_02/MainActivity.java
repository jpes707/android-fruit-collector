package com.example.pckosek.recyclerview_02;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String PREFERENCE_KEY = "fruitKey";
    private static final String LENGTH_KEY = "count";
    private SharedPreferences.Editor mEditor;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new GsonBuilder().create();
        SharedPreferences mShared = getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        mEditor = mShared.edit();

        MyUser[] myUsers = new MyUser[mShared.getInt(LENGTH_KEY, 0)];
        if (myUsers.length == 0) {
            myUsers = gson.fromJson("[ { score : 12, name : \"bananas\" }, { score : 17, name : \"apples\" }, { score : 33, name : \"pears\" }, { score : 74, name : \"grapefruit\" }, { score : 43, name : \"oranges\" }, { score : 52, name : \"watermelon\" }, { score : 19, name : \"strawberries\" }, { score : 11, name : \"blueberries\" }, { score : 83, name : \"kiwi\" }, { score : 54, name : \"tangerines\" }, { score : 14, name : \"cantaloupe\" } ]", MyUser[].class);
            int length = 0;
            for (MyUser user : myUsers) {
                user.image = getString(getResources().getIdentifier(user.name, "string", getPackageName()));
                user.image = user.image.substring(user.image.indexOf(",")  + 1);
                mEditor.putString(String.valueOf(length), gson.toJson(user));
                length++;
            }
            mEditor.putInt(LENGTH_KEY, length);
            mEditor.apply();
        } else {
            for (int i = 0; i < myUsers.length; i++) {
                myUsers[i] = gson.fromJson(mShared.getString(String.valueOf(i), "{ score : 0, name : \"null\" }"),  MyUser.class);
            }
        }
        List<MyUser> mUsersList = new ArrayList<>(Arrays.asList(myUsers));

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        UsersAdapter mUsersAdapter = new UsersAdapter(mUsersList, this);
        mUsersAdapter.notifyDataSetChanged();

        RecyclerView.LayoutManager rvlManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(rvlManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(mUsersAdapter);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1888);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            UsersAdapter.holders.get(requestCode).mImageView.setImageBitmap(photo);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            assert photo != null;
            photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            MyUser user = UsersAdapter.usersList.get(requestCode);
            user.image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            mEditor.putString(String.valueOf(requestCode), gson.toJson(user));
            mEditor.apply();
        }
    }

}
