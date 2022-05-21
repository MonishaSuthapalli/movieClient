package com.monisha.movieclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class movieLibraryAdapter extends RecyclerView.Adapter<movieLibraryAdapter.ViewHolder> {
    private String TAG = "movieLibraryAdapter";
    private ArrayList<String> movieTitles, directorNames;
    private ArrayList<Bitmap> thumbnails;
    private com.monisha.movieclient.RVClickListener listener;

    // Constructor for Array Adapter
    public movieLibraryAdapter(ArrayList<String> movieTitles,
                               ArrayList<String> directorNames,
                               ArrayList<Bitmap> thumbnails,
                               com.monisha.movieclient.RVClickListener listener) {
        this.movieTitles = movieTitles;
        this.directorNames = directorNames;
        this.thumbnails = thumbnails;
        this.listener = listener;

        System.out.println("movieLibraryAdapter: " + movieTitles.get(0));
    }

    // ViewHolder Definition
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View listView = inflater.inflate(R.layout.movie_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listView, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int index) {
        holder.setdirectorName(this.directorNames.get(index));
        holder.setmovieTitle(this.movieTitles.get(index));
        holder.setThumbnail(this.thumbnails.get(index));
    }

    //  Number of movie titles
    @Override
    public int getItemCount() {
        return movieTitles.size();
    }


    // View Holder Class for Recycler View
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView thumbnail;
        public TextView movieTitle;
        public TextView directorName;
        private RVClickListener listener;

        public ViewHolder(View view, RVClickListener listener) {
            super(view);
            thumbnail = view.findViewById(R.id.movieCover);
            movieTitle = view.findViewById(R.id.movieTitle);
            directorName = view.findViewById(R.id.directorName);
            this.listener = listener;
            view.setOnClickListener(this);
        }

        public void setmovieTitle(String movieTitle) {
            this.movieTitle.setText(movieTitle);
        }

        public void setdirectorName(String directorName) {
            this.directorName.setText(directorName);
        }

        public void setThumbnail(Bitmap thumbnail) {
            if(thumbnail != null) {
                this.thumbnail.setImageBitmap(thumbnail);
            } else {
                int defImg = getAdapterPosition()%2 == 0?
                                R.drawable.samplesecond : R.drawable.samplefirst;
                    this.thumbnail.setImageResource(defImg);
            }
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
