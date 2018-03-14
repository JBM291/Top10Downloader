package com.mills.b.joshua.top10downloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
/**
 * Created by Inferno on 3/12/2018.
 */

public class ParseApplications {
    private static final String TAG = "ParseApplications";
    private ArrayList<FeedEntry> applications;

    public ParseApplications() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmlData){
        boolean status = true;
        FeedEntry currentRecord = new FeedEntry();
        boolean inEntry = false;
        String textValue = "";
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                String tagName = xpp.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "parse: Staring: "+ tagName);
                        if("entry".equalsIgnoreCase(tagName)){
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "parse: End Tag");
                        if("entry".equalsIgnoreCase(tagName)){
                            applications.add(currentRecord);
                            inEntry =false;
                        }else if("name".equalsIgnoreCase(tagName)){
                            currentRecord.setName(textValue);
                        }else if("artist".equalsIgnoreCase(tagName)){
                            currentRecord.setArtist(textValue);
                        }else if("releaseDate".equalsIgnoreCase(tagName)){
                            currentRecord.setReleaseDate(textValue);
                        }else if("summary".equalsIgnoreCase(tagName)){
                            currentRecord.setSummary(textValue);
                        }else if("image".equalsIgnoreCase(tagName)){
                            if(currentRecord.getImageURL().equals("")) {
                                currentRecord.setImageURL(textValue);
                                new ParseApplications.DownloadImageTask(currentRecord)
                                        .execute(textValue);
                            }
                        }
                        break;
                    default:

                }
                eventType =xpp.next();
            }

        }catch (Exception e){
            status = false;
            e.printStackTrace();
        }

        for (FeedEntry feed: applications) {
            Log.d(TAG, "parse: "+feed.toString());
        }

        return status;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private FeedEntry feedEntry;
        public DownloadImageTask(FeedEntry entry) {
            this.feedEntry = entry;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            feedEntry.setBitmap(result);
        }
    }
}
