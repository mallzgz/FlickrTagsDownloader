package com.mall.flickrdownloadtags.RecyclerViewComponents;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mall.flickrdownloadtags.Interfaces.OnPhotoSelectedListener;
import com.mall.flickrdownloadtags.Models.Photo;
import com.mall.flickrdownloadtags.R;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.io.File;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {



    private WeakReference<Context> mContextReference;
    private WeakReference<OnPhotoSelectedListener> mListener;
    private ArrayList<Photo> mPhotos;

    public PhotoAdapter(Context context, ArrayList<Photo> photos, OnPhotoSelectedListener listener) {
        mContextReference = new WeakReference<>(context);
        mListener = new WeakReference<>(listener);
        mPhotos = photos;
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContextReference.get()).inflate(R.layout.item_image, parent, false);
        return new PhotoHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoHolder holder, int position) {
        holder.bindView(mPhotos.get(position));

    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public void setPhotoDataSet(ArrayList<Photo> photos){
        if(photos != null){
            mPhotos = photos;
        }
        else{
            mPhotos = new ArrayList<>();
        }
    }


    public class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imageView;
        private Photo photo;

        public PhotoHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            itemView.setOnClickListener(this);
        }

        //If we have the local photo, we use it instead of going to Internet
        public void bindView(final Photo photo) {
            this.photo = photo;
            if (this.photo.getPath().equals("")){
                //We have not this image at local (maybe because an error) so we show the online image
                Picasso.with(mContextReference.get())
                        .load(photo.getUrlString())
                        .placeholder(R.drawable.ic_loading)
                        .error(R.drawable.ic_error_loading)
                        .into(imageView);
            }
            else {
                String movies = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();
                String path = movies + File.separator + photo.getId()+".jpg";

                Picasso.with(mContextReference.get())
                        .load(new File(path))
                        .placeholder(R.drawable.ic_loading)
                        .error(R.drawable.ic_error_loading)
                        .into(imageView);
            }
        }

        @Override
        public void onClick(View view) {
            //We dont do anything
        }
    }
}
