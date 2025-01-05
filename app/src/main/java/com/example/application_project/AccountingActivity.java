package com.example.application_project;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AccountingActivity extends AppCompatActivity {
    private ArrayList<String> accountingList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accounting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView totalPrice = findViewById(R.id.total_price);
        loadAccountingData();
        double total = calculateTotalAmount();
        totalPrice.setText("總金額: $" + total);

        ListView recordList = findViewById(R.id.accounting_list);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, accountingList);
        recordList.setAdapter(adapter);

        recordList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 彈出確認刪除對話框
                showDeleteDialog(position);
                return true; // 表示長按事件已處理，不再觸發點擊事件
            }
        });
    }

    public void go_back(View view) {
        finish();
    }
    public void add_one(View view) {
        EditText priceText = findViewById(R.id.priceText);
        EditText itemNameText = findViewById(R.id.itemNameText);
        TextView totalPrice = findViewById(R.id.total_price);

        String price = priceText.getText().toString();
        String itemName = itemNameText.getText().toString();

        if (!price.isEmpty() && !itemName.isEmpty()) {
            accountingList.add("品項: " + itemName + "  金額: " + price);
            adapter.notifyDataSetChanged();

            double total = calculateTotalAmount();
            totalPrice.setText("總金額: $" + total);

            priceText.setText("");
            itemNameText.setText("");
            saveAccountingData();
        }
    }
    public double calculateTotalAmount() {
        double total = 0;

        for (String entry : accountingList) {
            // 找出 "金額: " 開始的位置
            int amountIndex = entry.indexOf("金額: ");
            if (amountIndex != -1) {
                // 提取金額部分
                String amountString = entry.substring(amountIndex + 4).trim();
                try {
                    // 轉換為數字並累加
                    total += Double.parseDouble(amountString);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        return total;
    }
    private void saveAccountingData() {
        SharedPreferences sharedPreferences = getSharedPreferences("AccountingData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 將列表轉為 JSON 字串
        Gson gson = new Gson();
        String json = gson.toJson(accountingList);

        editor.putString("accountingList", json);
        editor.apply(); // 保存變更
    }
    private void loadAccountingData() {
        SharedPreferences sharedPreferences = getSharedPreferences("AccountingData", MODE_PRIVATE);

        // 讀取 JSON 字串並轉換為列表
        Gson gson = new Gson();
        String json = sharedPreferences.getString("accountingList", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        accountingList = gson.fromJson(json, type);

        // 如果沒有資料，初始化為空列表
        if (accountingList == null) {
            accountingList = new ArrayList<>();
        }
    }
    private void showDeleteDialog(int position) {
        TextView totalPrice = findViewById(R.id.total_price);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("刪除品項");
        builder.setMessage("確定要刪除這個品項嗎？");
        builder.setPositiveButton("刪除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                accountingList.remove(position);            // 刪除選定的項目
                adapter.notifyDataSetChanged();       // 通知適配器更新資料
                double total = calculateTotalAmount();
                totalPrice.setText("總金額: $" + total);
                saveAccountingData();
            }
        });
        builder.setNegativeButton("取消", null); // 點擊取消時不執行任何操作
        builder.show(); // 顯示對話框
    }
}