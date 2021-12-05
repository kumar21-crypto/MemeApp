package com.simplestudio.memeapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import soup.neumorphism.NeumorphImageView;
import soup.neumorphism.NeumorphTextView;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    String previousMeme,memeUrl;
    NeumorphImageView share;
    RecyclerView recyclerView;
    MemeListAdaptor memeListAdaptor;
    ArrayList<Meme> arrayList;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView img;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        progressBar = (ProgressBar) findViewById(R.id.loader);
        progressBar.setVisibility(View.VISIBLE);
        textView = findViewById(R.id.textview);
        img = (ImageView) findViewById(R.id.memeImageView);
        arrayList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.memeRecyclerView);
        SnapHelper mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);
        memeListAdaptor = new MemeListAdaptor(MainActivity.this,arrayList);
        recyclerView.setAdapter(memeListAdaptor);
        loadMeme();

        textView.setOnClickListener(v -> {

        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMeme();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }



    private void loadMeme() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://meme-api.herokuapp.com/gimme/50";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("memes");

                            for(int i=0 ; i<jsonArray.length(); i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                Meme meme = new Meme(jsonObject.getString("title"),jsonObject.getString("url")
                                ,jsonObject.getString("author"));

                                arrayList.add(meme);
                            }
                            progressBar.setVisibility(View.GONE);
                            memeListAdaptor.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonObjectRequest);

    }

    @Override
    protected void onStop() {
        super.onStop();
        deleteCache(MainActivity.this);
        
    }

    public static void deleteCache(Context context) {
        try {
                  File dir = context.getCacheDir();
                  deleteDir(dir);
        }
        catch (Exception e)
        {
              e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if(dir != null && dir.isDirectory()) {
            String[] children = dir.list();

            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));

                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        else if(dir!=null && dir.isFile())
        {
            return dir.delete();
        }
        else{
            return false;
        }


    }
}

