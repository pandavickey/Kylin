package com.panda.kylintest;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.panda.kylin.PatchLoader;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PatchLoader.loadPatch(getApplication(), Environment.getExternalStorageDirectory().getPath() + File.separator + "patch_dex.dex");
                Toast.makeText(MainActivity.this, "load success", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, getToastString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public String getToastString() {
        return "hello world";
    }
}
