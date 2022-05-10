package com.example.download;

import android.app.Dialog;
import android.app.ProgressDialog;
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
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        protected void onProgressUpdate(String... progress) {
            ProgressBar simpleProgressBar=(ProgressBar) findViewById(R.id.progressBar);
            simpleProgressBar.setMax(100);
            simpleProgressBar.setProgress(Integer.parseInt(progress[0]));
            if (Integer.parseInt(progress[0])==100){
                Toast.makeText(getApplicationContext(),"Download Abgeschlossen",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count = 0;
            String typ="";
            String name="";
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                int length = conection.getContentLength();

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
                }
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+name+typ);   //save to Downloads

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = stream.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / length));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                stream.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }
    }
}