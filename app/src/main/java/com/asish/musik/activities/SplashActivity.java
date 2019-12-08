package com.asish.musik.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.asish.musik.R;

public class SplashActivity extends AppCompatActivity {

    // Permission List..
    String permissionString[] = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Used to add Status Bar Color..
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.fourth));
        }

        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        // Permission check..
        if(!hasPermission(SplashActivity.this, permissionString)) {
            // Request for permission..
            ActivityCompat.requestPermissions(SplashActivity.this, permissionString, 131);
        } else {
            // Wait for 1 second then move to MainActivity..
            final Runnable r = new Runnable() {
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            };
            new Handler().postDelayed(r, 1500);
        }
    }

    // Check for permission..


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 131: {
                if(grantResults.length != 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED
                        && grantResults[5] == PackageManager.PERMISSION_GRANTED)
                {
                    // Wait for 1 second then move to MainActivity..
                    final Runnable r = new Runnable() {
                        public void run() {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    };
                    new Handler().postDelayed(r, 1500);
                }
                else {
                    Toast.makeText(SplashActivity.this,"Please grant all the permissions", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            }
            default:
                Toast.makeText(SplashActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
                this.finish();
                break;
        }
    }

    // Check for permission..
    public boolean hasPermission(Context context, String ... premissions) {
        boolean hasAllPermission = true;
        for(String permission : premissions) {
            if(context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                hasAllPermission = false;
            }
        }
        return hasAllPermission;
    }
}
