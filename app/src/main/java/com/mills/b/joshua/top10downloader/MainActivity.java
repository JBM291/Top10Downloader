package com.mills.b.joshua.top10downloader;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listApps;
    FeedAdapter feedAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = findViewById(R.id.xmlListView);
        downloadUrl("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String feedUrl;
        switch (id){
            case R.id.mnuFree:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml";
                break;
            case R.id.mnuPaid:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=10/xml";
                break;
            case R.id.mnuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=10/xml";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        downloadUrl(feedUrl);
        return true;
    }
    private void downloadUrl(String url){
        DownloadData downloadData = new DownloadData();
        downloadData.execute(url);
    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: parameter is" + s);

//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(
//                    MainActivity.this,R.layout.list_item,parseApplications.getApplications());
//            listApps.setAdapter(arrayAdapter);
            ParseApplications parseApplications = new ParseApplications();

            feedAdapter = new FeedAdapter(
                    MainActivity.this,
                    R.layout.list_record,
                    parseApplications.getApplications());
            listApps.setAdapter(feedAdapter);
            parseApplications.parse(s,feedAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: Starts with  " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if(rssFeed == null){
                Log.e(TAG, "doInBackground: Error downloading");
            }
            return rssFeed;
        }

        private String downloadXML(String urlPath){
            StringBuilder xmlResult = new StringBuilder();
            try{
                URL url =new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response is "+ response);
                //InputStream inputStream = connection.getInputStream();
                //InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                //BufferedReader reader = new BufferedReader(inputStreamReader);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                int charsRead;
                char[] inputBuffer = new char[500];
                while(true){
                    charsRead = reader.read(inputBuffer);
                    if(charsRead<0){
                        break;
                    }
                    if(charsRead>0){
                        xmlResult.append(String.copyValueOf(inputBuffer,0,charsRead));
                    }
                }
                reader.close();
                return xmlResult.toString();
            }catch (MalformedURLException e){
                Log.e(TAG, "downloadXML: MalformedURLException" + e.getMessage());
            }catch (IOException e){
                Log.e(TAG, "downloadXML: IOException" + e.getMessage());
            }catch (SecurityException e){
                Log.e(TAG, "downloadXML: SecurityException" + e.getMessage());
            }

            return null;
        }

    }

}