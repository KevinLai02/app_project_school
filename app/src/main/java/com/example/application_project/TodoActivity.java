package com.example.application_project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class TodoActivity extends AppCompatActivity {

    private ArrayList<String> taskList = new ArrayList<>(); // 儲存待辦事項
    private ArrayAdapter<String> adapter;
    private Calendar selectedTime; // 存储用户选择的时间

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
        Button buttonSetTime = findViewById(R.id.buttonSetTime); // 设置提醒时间的按钮
        ListView listViewTasks = findViewById(R.id.listViewTasks);
        loadTasks();

        // 設定適配器
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskList);
        listViewTasks.setAdapter(adapter);

        // 設定提醒時間按鈕
        buttonSetTime.setOnClickListener(view -> showTimePickerDialog());

        // 新增任務按鈕
        buttonAddTask.setOnClickListener(view -> {
            String task = editTextTask.getText().toString();
            if (!task.isEmpty()) {
                taskList.add(task);
                adapter.notifyDataSetChanged();
                saveTasks();
                editTextTask.setText("");

                if (selectedTime != null) {
                    setAlarm(task, selectedTime.getTimeInMillis());
                    selectedTime = null; // 重置選擇的時間
                }
            }
        });

        // 設定 ListView 長按事件來刪除任務
        listViewTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteDialog(position);
            return true;
        });
    }

    // 彈出刪除確認對話框
    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("刪除待辦事項");
        builder.setMessage("確定要刪除這個待辦事項嗎？");
        builder.setPositiveButton("刪除", (dialog, which) -> {
            taskList.remove(position);
            adapter.notifyDataSetChanged();
            saveTasks();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    // 保存任務到 SharedPreferences
    private void saveTasks() {
        SharedPreferences sharedPreferences = getSharedPreferences("TodoApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> taskSet = new HashSet<>(taskList);
        editor.putStringSet("tasks", taskSet);
        editor.apply();
    }

    // 從 SharedPreferences 加載任務
    private void loadTasks() {
        SharedPreferences sharedPreferences = getSharedPreferences("TodoApp", MODE_PRIVATE);
        Set<String> taskSet = sharedPreferences.getStringSet("tasks", new HashSet<>());
        taskList.clear();
        taskList.addAll(taskSet);
    }

    // 顯示時間選擇對話框
    private void showTimePickerDialog() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        new android.app.TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            // 保存用户选择的时间
            selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
            selectedTime.set(Calendar.MINUTE, selectedMinute);
            selectedTime.set(Calendar.SECOND, 0);

            Toast.makeText(this, "提醒時間已設定: " + selectedHour + ":" + selectedMinute, Toast.LENGTH_SHORT).show();
        }, hour, minute, true).show();
    }

    // 設置提醒
    private void setAlarm(String task, long triggerTime) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("task", task);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }

        Toast.makeText(this, "提醒已設定！", Toast.LENGTH_SHORT).show();
    }

    public class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String task = intent.getStringExtra("task");
            Toast.makeText(context, "提醒時間到了！待辦事項：" + task, Toast.LENGTH_LONG).show();
        }
    }
    // 切換回主畫面的按鈕
    public void button2_click(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
