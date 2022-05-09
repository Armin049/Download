package com.example.download;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

   ActivityResultLauncher<Intent> activityResultLauncher;
   ActivityResultLauncher<String> stringActivityResultLauncher; //todo
   String[] permission={WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //todo
        stringActivityResultLauncher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                try {
                    save(result);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        takePerm();
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==MainActivity.RESULT_OK){
                    Toast.makeText(getApplicationContext(),"Permissions given",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void save(Uri result) {
        OutputStream out;
        ContentResolver resolver=getContentResolver();
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,System.currentTimeMillis());
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_DOWNLOADS);

    }

    public void takePerm(){
        if (checkPermissionIsGranted()){
            Toast.makeText(getApplicationContext(),"Permission Already Granted",Toast.LENGTH_SHORT).show();
        }
        else {
            takePermissions();
        }
    }

    public boolean checkPermissionIsGranted(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        }
        else{
            int writeCheck=ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
            return writeCheck == PackageManager.PERMISSION_GRANTED;
        }
    }

    public void takePermissions(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            try{
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.Default");
                intent.setData(Uri.parse(String.format("package:%s",new Object[]{getApplicationContext().getPackageName()})));
                activityResultLauncher.launch(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{WRITE_EXTERNAL_STORAGE},101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0){
            if (requestCode==101){
                boolean readExternalStorage=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if (readExternalStorage){
                    Toast.makeText(getApplicationContext(),"Permisson Granted",Toast.LENGTH_SHORT).show();
                }
                else{
                    takePerm();
                }
            }
        }
    }
}