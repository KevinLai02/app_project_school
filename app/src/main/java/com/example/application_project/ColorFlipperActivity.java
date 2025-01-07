package com.example.application_project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ColorFlipperActivity extends AppCompatActivity {

    private Button btnRainbow, btnGradient, btnScenes, btnStartColorChange;
    private RadioGroup radioGroupOrder;
    private RadioButton rbSequential, rbRandom;
    private String selectedMode = "";
    private String selectedOrder = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_flipper);

        // 初始化視圖
        btnRainbow = findViewById(R.id.btnRainbow);
        btnGradient = findViewById(R.id.btnGradient);
        btnScenes = findViewById(R.id.btnScenes);
        btnStartColorChange = findViewById(R.id.btnStartColorChange);
        radioGroupOrder = findViewById(R.id.radioGroupOrder);
        rbSequential = findViewById(R.id.rbSequential);
        rbRandom = findViewById(R.id.rbRandom);

        // 設置顏色模式按鈕的點擊事件
        btnRainbow.setOnClickListener(v -> onColorModeSelected("Rainbow"));
        btnGradient.setOnClickListener(v -> onColorModeSelected("Gradient"));
        btnScenes.setOnClickListener(v -> onColorModeSelected("Scenes"));

        // 設置開始顏色變換按鈕的點擊事件
        btnStartColorChange.setOnClickListener(v -> onStartColorChange());
    }

    // 顏色模式選擇的處理邏輯
    public void onColorModeSelected(String mode) {
        selectedMode = mode;
        Toast.makeText(this, "Selected Mode: " + selectedMode, Toast.LENGTH_SHORT).show();
    }

    // 開始顏色變換的處理邏輯
    public void onStartColorChange() {
        // 檢查選擇的顏色排列方式
        int selectedOrderId = radioGroupOrder.getCheckedRadioButtonId();
        if (selectedOrderId == rbSequential.getId()) {
            selectedOrder = "Sequential";
        } else if (selectedOrderId == rbRandom.getId()) {
            selectedOrder = "Random";
        }

        // 確保選擇了顏色模式和排列方式
        if (selectedMode.isEmpty()) {
            Toast.makeText(this, "Please select a color mode!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedOrder.isEmpty()) {
            Toast.makeText(this, "Please select an order!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 跳轉到新活動並傳遞選擇的顏色模式和排列方式
        Intent intent = new Intent(this, ColorChangeActivity.class);
        intent.putExtra("mode", selectedMode);
        intent.putExtra("order", selectedOrder);
        startActivity(intent);
    }
}
