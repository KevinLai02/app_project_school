package com.example.application_project;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Color;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class QRCodeGeneratorActivity extends AppCompatActivity {
    private static final String ALGORITHM = "AES";
    private Bitmap qrCodeBitmap = null;
    private UUID randomID = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qrcode_generator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.qrcode_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        EditText qrCodeContentInput = findViewById(R.id.qrcode_content_input);
        Button generateSecretKeyButton = findViewById(R.id.qrcode_generator_generate_secret_key_button);
        ImageView qrCodeImage = findViewById(R.id.imageView);
        TextView secretKeyView = findViewById(R.id.secret_key_view);
        TextView alertView = findViewById(R.id.alert_view);
        TextView longClickTipsView = findViewById(R.id.long_click_tips_view);
        TextView secretKeyLongClickTipsView = findViewById(R.id.secret_key_long_click_tips_view);
        Button clearSecretKeyButton = findViewById(R.id.qrcode_generator_clear_secret_key_button);

        clearSecretKeyButton.setOnClickListener(v -> {
            //清除secretKey，重新生產QR Code
            if(randomID == null){
                return;
            }
            randomID = null;
            secretKeyView.setText("");
            String qrCodeContent = qrCodeContentInput.getText().toString();
            if(!qrCodeContent.trim().isEmpty()) {
                qrCodeBitmap = generateQRCode(qrCodeContent);
                qrCodeImage.setImageBitmap(qrCodeBitmap);
                alertView.setText("");
                longClickTipsView.setVisibility(View.VISIBLE);
            }
            secretKeyLongClickTipsView.setVisibility(View.INVISIBLE);
        });

        generateSecretKeyButton.setOnClickListener(v -> {
            //生產UUID作為secret key，並加密QR Code內容
            randomID = UUID.randomUUID();
            secretKeyView.setText(randomID.toString());
            String qrCodeContent = qrCodeContentInput.getText().toString();
            if(!qrCodeContent.trim().isEmpty()) {
                String encryptedText = encryptData(qrCodeContent, randomID); // 加密處理
                qrCodeBitmap = generateQRCode(encryptedText);
                qrCodeImage.setImageBitmap(qrCodeBitmap);
                alertView.setText("");
                longClickTipsView.setVisibility(View.VISIBLE);
            }
            secretKeyLongClickTipsView.setVisibility(View.VISIBLE);
        });

        qrCodeImage.setOnLongClickListener((v) -> {
            if(qrCodeBitmap == null) {
                return true;
            }
            saveImageToGallery(qrCodeBitmap);
            return true;
        });


        qrCodeContentInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable editableData) {
                String qrCodeContent = qrCodeContentInput.getText().toString();
                if(qrCodeContent.trim().isEmpty()) {
                    alertView.setText(R.string.qrcode_generator_empty_content);
                    initialQRCodeImageView(qrCodeImage);
                    return ;
                }

                if(randomID == null){
                    qrCodeBitmap = generateQRCode(qrCodeContent);
                }else{
                    String encryptedText = encryptData(qrCodeContent, randomID); // 加密處理
                    qrCodeBitmap = generateQRCode(encryptedText);
                }
                qrCodeImage.setImageBitmap(qrCodeBitmap);
                alertView.setText("");
                longClickTipsView.setVisibility(View.VISIBLE);
            }
        });

        secretKeyView.setOnLongClickListener(view -> {
            String secretKey = secretKeyView.getText().toString();
            if (secretKey.isEmpty()){
                return true;
            }
            //長按複製SecretKey
            copyToClipboard(secretKeyView.getText().toString());
            return true;
        });
    }

    private void copyToClipboard(String text) {
        // 獲取剪貼板管理器
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 將文字放入剪貼板
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
        // 提示用戶已複製
        Toast.makeText(this, "金鑰已複製", Toast.LENGTH_SHORT).show();
    }

    public void initialQRCodeImageView (ImageView imageView) {
        int color = ContextCompat.getColor(this, com.google.android.material.R.color.material_dynamic_neutral90);
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        bitmap.setPixel(0, 0, color);
        imageView.setImageBitmap(bitmap);
    }

    public void go_back(View view) {
        finish();
    }

    private Bitmap generateQRCode(String text) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix qrCode = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512);

            int width = qrCode.getWidth();
            int height = qrCode.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            //轉換為黑白色
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, qrCode.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            Log.d("encryptLog", "generateQRCodeError: " + e);
            e.printStackTrace();
            return null;
        }
    }

    public static String encryptData(String data, UUID key) {
        try {
            //根據使用者輸入的密鑰生產AES Key 用於加密
            SecretKey secretKey = generateSecretKey(key);
            //設定加密方式
            Cipher encryption = Cipher.getInstance(ALGORITHM);
            encryption.init(Cipher.ENCRYPT_MODE, secretKey);
            //加密QRCode內容
            byte[] encryptedBytes = encryption.doFinal(data.getBytes());
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        } catch (Exception e) {
            Log.d("encryptLog", "encryptDataError: " + e);
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptData(String data, UUID key) {
        try {
            //根據使用者輸入的密鑰生產AES Key 用於解密
            SecretKey secretKey = generateSecretKey(key);
            //設定加密方式
            Cipher encryption = Cipher.getInstance(ALGORITHM);
            encryption.init(Cipher.DECRYPT_MODE, secretKey);
            //解密QRCode內容
            byte[] decryptedBytes = encryption.doFinal(Base64.decode(data, Base64.DEFAULT));
            return new String(decryptedBytes);
        } catch (Exception e) {
            Log.d("encryptLog", "decryptDataError: " + e);
            e.printStackTrace();
            return null;
        }
    }

    private static SecretKey generateSecretKey(UUID key) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(16);
            byteBuffer.putLong(key.getMostSignificantBits());
            byteBuffer.putLong(key.getLeastSignificantBits());
            byte[] keyBytes = byteBuffer.array();
            return new SecretKeySpec(keyBytes, ALGORITHM);
        } catch (Exception e) {
            Log.d("encryptLog", "generateSecretKeyError: " + e);
            e.printStackTrace();
            return null;
        }
    }

    private void saveImageToGallery(Bitmap bitmap) {
        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();
        String displayName = "IMG_" + System.currentTimeMillis() + ".png";
        String mimeType = "image/png";

        // 設置圖片的文件信息
        values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QRCodeGenerator"); // 自定義文件夾

        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try (OutputStream out = resolver.openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // 保存為 PNG 格式
            Toast.makeText(this, "圖片已保存至相簿", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d("encryptLog", "saveImageError: " + e);
            e.printStackTrace();
            Toast.makeText(this, "圖片保存失敗", Toast.LENGTH_SHORT).show();
        }
    }
}