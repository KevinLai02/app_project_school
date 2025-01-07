package com.example.application_project;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ColorChangeActivity extends AppCompatActivity {

    private FrameLayout colorDisplayArea;
    private String selectedMode;
    private String selectedOrder;
    private List<Integer> colorList;
    private int currentColorIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_change);

        // 獲取從 ColorFlipperActivity 傳遞過來的資料
        selectedMode = getIntent().getStringExtra("mode");
        selectedOrder = getIntent().getStringExtra("order");

        colorDisplayArea = findViewById(R.id.colorDisplayArea);

        // 根據選擇的模式來初始化顏色列表
        colorList = new ArrayList<>();
        switch (selectedMode) {
            case "Rainbow":
                colorList = getRainbowColors();
                break;
            case "Gradient":
                colorList = getGradientColors();
                break;
            case "Scenes":
                colorList = getSceneColors();
                break;
            default:
                Toast.makeText(this, "Invalid color mode", Toast.LENGTH_SHORT).show();
                return;
        }

        // 根據選擇的順序（隨機或依序）來設定顏色的排列
        if (selectedOrder.equals("Random")) {
            Collections.shuffle(colorList);
        }

        // 點擊顯示區域來改變顏色
        colorDisplayArea.setOnClickListener(v -> changeColor());
    }

    // 顏色變換的邏輯
    private void changeColor() {
        if (currentColorIndex < colorList.size()) {
            colorDisplayArea.setBackgroundColor(colorList.get(currentColorIndex));
            currentColorIndex++;
        } else {
            currentColorIndex = 0; // 如果顏色顯示完，重置顏色列表
            if (selectedOrder.equals("Sequential")) {
                Toast.makeText(ColorChangeActivity.this, "Color change completed sequentially", Toast.LENGTH_SHORT).show();
            }
            // 如果選擇的是隨機排列，則再次隨機化顏色順序
            if (selectedOrder.equals("Random")) {
                Collections.shuffle(colorList);
            }
        }
    }

    // 彩虹顏色列表
    private List<Integer> getRainbowColors() {
        List<Integer> rainbowColors = new ArrayList<>();
        rainbowColors.add(Color.RED); // 紅色
        rainbowColors.add(Color.rgb(255, 165, 0)); // 橙色
        rainbowColors.add(Color.YELLOW); // 黃色
        rainbowColors.add(Color.GREEN); // 綠色
        rainbowColors.add(Color.BLUE); // 藍色
        rainbowColors.add(Color.rgb(75, 0, 130)); // 靛藍色（藍紫色）
        rainbowColors.add(Color.rgb(238, 130, 238)); // 紫羅蘭色（使用 rgb 設置）
        return rainbowColors;
    }

    private List<Integer> getGradientColors() {
        List<Integer> gradientColors = new ArrayList<>();

        // 定義放鬆顏色漸變範例 - 淡藍到深藍
        int startColor = Color.parseColor("#F8BBD0"); // 淺藍色
        int endColor = Color.parseColor("#D81B60");   // 深藍色

        // 設定變化步數，這裡設為 100 步，代表從淺藍到深藍漸變
        int steps = 30;

        // 產生細微的漸變顏色
        for (int i = 0; i < steps; i++) {
            gradientColors.add(createGradualChangeColor(startColor, endColor, i, steps));
        }

        return gradientColors;
    }

    // 用來生成漸變顏色的函數，從startColor變化到endColor
    private int createGradualChangeColor(int startColor, int endColor, int step, int totalSteps) {
        // 獲取開始顏色和結束顏色的RGB組成
        int startRed = Color.red(startColor);
        int startGreen = Color.green(startColor);
        int startBlue = Color.blue(startColor);

        int endRed = Color.red(endColor);
        int endGreen = Color.green(endColor);
        int endBlue = Color.blue(endColor);

        // 計算每一步顏色變化的幅度
        int redChange = (int) (startRed + (float) (endRed - startRed) * (step / (float) totalSteps));
        int greenChange = (int) (startGreen + (float) (endGreen - startGreen) * (step / (float) totalSteps));
        int blueChange = (int) (startBlue + (float) (endBlue - startBlue) * (step / (float) totalSteps));

        // 返回改變後的顏色
        return Color.rgb(redChange, greenChange, blueChange);
    }


    // 使用場景顏色列表（自定義場景）
    private List<Integer> getSceneColors() {
        List<Integer> sceneColors = new ArrayList<>();
        sceneColors.add(Color.parseColor("#FFEB3B")); // 太陽黃
        sceneColors.add(Color.parseColor("#8BC34A")); // 草地綠
        sceneColors.add(Color.parseColor("#00BCD4")); // 天空藍
        sceneColors.add(Color.parseColor("#F44336")); // 火焰紅
        sceneColors.add(Color.parseColor("#673AB7")); // 紫色
        sceneColors.add(Color.parseColor("#FFCDD2")); // 粉沙紅
        sceneColors.add(Color.parseColor("#C8E6C9")); // 薄荷綠
        sceneColors.add(Color.parseColor("#B3E5FC")); // 輕藍
        sceneColors.add(Color.parseColor("#FFE0B2")); // 柔橙
        sceneColors.add(Color.parseColor("#D1C4E9")); // 薄紫
        sceneColors.add(Color.parseColor("#F0F4C3")); // 柔黃綠
        sceneColors.add(Color.parseColor("#BBDEFB")); // 淡藍
        sceneColors.add(Color.parseColor("#FFAB91")); // 淡珊瑚橘
        sceneColors.add(Color.parseColor("#B2DFDB")); // 淺水綠
        sceneColors.add(Color.parseColor("#FFF9C4")); // 柔亮黃
        sceneColors.add(Color.parseColor("#FFCCBC")); // 溫暖橘
        sceneColors.add(Color.parseColor("#E1BEE7")); // 柔粉紫
        sceneColors.add(Color.parseColor("#CFD8DC")); // 柔霧灰
        sceneColors.add(Color.parseColor("#FFECB3")); // 蜜橙黃
        sceneColors.add(Color.parseColor("#F8BBD0")); // 溫柔粉
        return sceneColors;
    }

    // 重寫返回按鈕的功能，防止顏色變換中斷

    public void go_back(View view) {
        finish();
    }
}
