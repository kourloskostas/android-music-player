package com.example.musify;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//RecyclerView Adapter got track objects
public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    private List<Track> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;


    // data is passed into the constructor
    TrackAdapter(Context context, List<Track> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.track_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Track track = mData.get(position);

        holder.trackNameV.setText(track.getTrackName());
        holder.artistNameV.setText(track.getArtistName());

        String artworkpath = track.getArtworkPath();

        if (!artworkpath.equals("default")) { /* Default Artwork*/
            holder.artworkV.setImageURI(Uri.parse(track.getArtworkPath()));
        }




    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // convenience method for getting data at click position
    Track getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView trackNameV;
        TextView artistNameV;
        ImageView artworkV;
        ImageButton moreBtnV;

        ViewHolder(View itemView) {
            super(itemView);
            trackNameV = itemView.findViewById(R.id.title);
            artistNameV = itemView.findViewById(R.id.artist);
            artworkV = itemView.findViewById(R.id.artwork);
            moreBtnV = itemView.findViewById(R.id.moreBtn);

            moreBtnV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(context, moreBtnV);
                    //inflating menu from xml resource
                    popup.inflate(R.layout.track_more);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete_itm:
                                    //handle menu1 click
                                    return true;
                                case R.id.rename_itm:
                                    //handle menu2 click
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
            });


            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}