package com.example.application_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void goto_todolist(View view){
        Intent intent = new Intent(this, TodoActivity.class);
        startActivity(intent);
    }
    
    public void goto_accounting(View view) {
        Intent intent = new Intent(this, AccountingActivity.class);
        startActivity(intent);
    }

    public void goto_colorflipper(View view){
        Intent intent = new Intent(this, ColorFlipperActivity.class);
        startActivity(intent);
    }

    public void goto_qrcode_generator(View view) {
        Intent intent = new Intent(this, QRCodeGeneratorActivity.class);
        startActivity(intent);
    }

    public void goto_qrcode_loader(View view) {
        Intent intent = new Intent(this, QuotesGeneratorActivity.class);
        startActivity(intent);
    }
}