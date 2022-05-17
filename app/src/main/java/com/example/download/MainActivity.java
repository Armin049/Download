package com.example.download;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    String URL="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //gets the URL from the editText/ creates a Toast to infor the user/ starts the Download process
    public void Download(View view){
        EditText editText=findViewById(R.id.URL);
        URL=editText.getText().toString();
        if (URL!=""||URL!=null){
            new DownloadFileFromURL().execute(URL);
            Toast.makeText(getApplicationContext(),"Download gestartet",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Bitte gib eine URL an",Toast.LENGTH_SHORT).show();
        }
        Intent intent= new Intent(this, DownloadService.class);
        startForegroundService(intent); // starts the foreground Service to allow the application to run in even afer the user has closed or stopped it
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        //takes the progress as an input an sets the Progressbar according to it
        protected void onProgressUpdate(String... progress) {
            ProgressBar simpleProgressBar=(ProgressBar) findViewById(R.id.progressBar);
            simpleProgressBar.setMax(100);
            simpleProgressBar.setProgress(Integer.parseInt(progress[0]));
            if (Integer.parseInt(progress[0])==100){
                Toast.makeText(getApplicationContext(),"Download Abgeschlossen",Toast.LENGTH_SHORT).show();
            }//creates a Toast when the Download is complete
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count = 0;
            String typ="";
            String name="";
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();//start connection to the URL

                int length = conection.getContentLength();//get conntent length, used for progressbar

                InputStream stream = new BufferedInputStream(url.openStream(),
                        8192);


                if(URL.contains(".")) {
                    typ = URL.substring(URL.lastIndexOf("."));
                    if(typ.indexOf("/")!=-1) {
                        typ = typ.substring(0, typ.indexOf("/"));
                    }
                    name = URL.substring(URL.lastIndexOf("/"));
                    if(name.indexOf("/")!=-1) {
                        name = name.substring(0, name.indexOf("."));
                    }
                }//gets the name and the Datatyp from the URL   todo replace with URI.getFile... if possible
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+name+typ);   //save to Downloads directory

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = stream.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / length));
                    output.write(data, 0, count);
                }           //calculate Progress
                output.flush();
                output.close();
                stream.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }//error log
            return null;
        }
    }
}