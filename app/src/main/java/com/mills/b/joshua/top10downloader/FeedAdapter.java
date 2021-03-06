package com.mills.b.joshua.top10downloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

/**
 * Created by joshua on 03/14/2018.
 */

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    public List<FeedEntry> applications;

    public FeedAdapter(Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FeedEntry currentApp = applications.get(position);
        viewHolder.tvRecordNumber.setText(Integer.toString(position+1));
        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvReleaseDate.setText(currentApp.getReleaseDate());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());
        viewHolder.imAppImage.setImageBitmap(currentApp.getBitmap());


        return convertView;
    }

    private class ViewHolder{
        final TextView tvRecordNumber;
       final TextView tvName;
       final TextView tvReleaseDate;
       final TextView tvArtist;
       final TextView tvSummary;
       final ImageView imAppImage;

       ViewHolder(View v){
           this.tvRecordNumber = v.findViewById(R.id.tvRecordNumber);
           this.tvName = v.findViewById(R.id.tvName);
           this.tvReleaseDate = v.findViewById(R.id.tvReleaseDate);
           this.tvArtist = v.findViewById(R.id.tvArtist);
           this.tvSummary = v.findViewById(R.id.tvSummary);
           this.imAppImage = v.findViewById(R.id.imAppImage);
       }
    }

}
