package com.example.application_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class TodoActivity extends AppCompatActivity {

    private ArrayList<String> taskList = new ArrayList<>(); // 儲存f待辦事項
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_todo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText editTextTask = findViewById(R.id.editTextTask);
        Button buttonAddTask = findViewById(R.id.buttonAddTask);
        ListView listViewTasks = findViewById(R.id.listViewTasks);

        // 設定適配器
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskList);
        listViewTasks.setAdapter(adapter);

        // 新增任務按鈕
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task = editTextTask.getText().toString(); // 取得輸入文字
                if (!task.isEmpty()) {
                    taskList.add(task);                // 新增到待辦清單
                    adapter.notifyDataSetChanged();    // 通知 ListView 資料已改變
                    editTextTask.setText("");          // 清空輸入框
                }
            }
        });

        // 設定 ListView 長按事件來刪除任務
        listViewTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 彈出確認刪除對話框
                showDeleteDialog(position);
                return true; // 表示長按事件已處理，不再觸發點擊事件
            }
        });
    }

    // 彈出刪除確認對話框
    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("刪除待辦事項");
        builder.setMessage("確定要刪除這個待辦事項嗎？");
        builder.setPositiveButton("刪除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                taskList.remove(position);            // 刪除選定的項目
                adapter.notifyDataSetChanged();       // 通知適配器更新資料
            }
        });
        builder.setNegativeButton("取消", null); // 點擊取消時不執行任何操作
        builder.show(); // 顯示對話框
    }

    // 切換回主畫面的按鈕
    public void button2_click(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
