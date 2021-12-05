package com.simplestudio.memeapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class MemeListAdaptor extends RecyclerView.Adapter<MemeListAdaptor.MemeViewHolder> {


    Context context;
    ArrayList<Meme> arrayList;

    public MemeListAdaptor(Context context, ArrayList<Meme> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MemeListAdaptor.MemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meme_layout,parent,false);
        return new MemeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemeListAdaptor.MemeViewHolder holder, int position) {
        Meme meme = arrayList.get(position);

        holder.title.setText(meme.getTitle());

        Glide.with(context).load(meme.getImageUrl()).placeholder(R.drawable.photo).into(holder.memeImage);

        holder.itemView.setOnLongClickListener(v -> {

            BitmapDrawable drawable = (BitmapDrawable) holder.memeImage.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, getImageUri(context.getApplicationContext(), bitmap));
            context.startActivity(Intent.createChooser(intent, "please share"));

            return true;
        });

        holder.memeShare.setOnClickListener(v -> {
            BitmapDrawable drawable = (BitmapDrawable) holder.memeImage.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, getImageUri(context.getApplicationContext(), bitmap));
            context.startActivity(Intent.createChooser(intent, "please share"));
        });

    }

    public Uri getImageUri(Context context, Bitmap bitmap)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,"meme_" + Calendar.getInstance().getTime(),null);
        return Uri.parse(path);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MemeViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView memeImage;
        ImageButton memeShare;

        public MemeViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.memeTitle);
            memeImage = (ImageView) itemView.findViewById(R.id.memeImageView);
            memeShare = (ImageButton) itemView.findViewById(R.id.shareButton);
        }
    }
}