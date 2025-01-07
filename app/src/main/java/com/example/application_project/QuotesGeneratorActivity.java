package com.example.application_project;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuotesGeneratorActivity extends AppCompatActivity {
    Spinner userMoodSpinner;
    Button generateQuotesButton;
    TextView quotesView;
    private List<Mood> moodQuotesList;
    public static class Mood {
        String mood;
        List<String> quotes;

        Mood(String mood, List<String> quotes) {
            this.mood = mood;
            this.quotes = quotes;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quotes_generator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.qrcode_loader_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userMoodSpinner = findViewById(R.id.user_mood_spinner);
        generateQuotesButton = findViewById(R.id.generate_quotes_button);
        quotesView = findViewById(R.id.quotes_view);
        moodQuotesList = Arrays.asList(
                new Mood("happy", Arrays.asList(
                        "人生短暫，何不微笑。",
                        "笑是心靈的陽光，照亮黑暗。",
                        "今天就是最好的時光。",
                        "讓我們珍惜當下，享受每一刻。",
                        "喜悅源自內心的平和。",
                        "保持笑容，生活就會多姿多彩。",
                        "無論如何，保持一顆快樂的心。",
                        "每一天都值得被感謝。",
                        "心境愉快，世界也會跟著變得美好。",
                        "快樂不在於外界，而是內心的感受。"
                )),
                new Mood("sad", Arrays.asList(
                        "即使傷心，也要學會微笑。",
                        "有些痛，只有時間能抚平。",
                        "世界上最遠的距離是心與心的距離。",
                        "淚水洗不去的，是心中的傷痕。",
                        "每一次的心痛，都是成長的一部分。",
                        "孤獨不是什麼都不做，而是什麼都無法改變。",
                        "心碎的時候，世界彷彿都變得灰暗。",
                        "有時候，最痛的不是失去，而是無法挽回。",
                        "每個悲傷的背後，都是一段未完的故事。",
                        "讓眼淚流過，心才會更堅強。"
                )),
                new Mood("angry", Arrays.asList(
                        "憤怒只是無力的表現。",
                        "愤怒無法解決問題，冷靜才是關鍵。",
                        "生氣不過是對自己的一種折磨。",
                        "用平和的心態面對，世界會更美好。",
                        "憤怒來得快，過去得也快。",
                        "生氣，只會讓自己更難受。",
                        "每一次的怒火，都是一種無助。",
                        "冷靜下來，才能看清楚真相。",
                        "面對困難，愤怒無法改變什麼。",
                        "只要心平氣和，事事都能迎刃而解。"
                )),
                new Mood("fearful", Arrays.asList(
                        "面對恐懼，勇氣是最大的力量。",
                        "恐懼並不可怕，勇敢面對才是最重要的。",
                        "每個人都會有害怕的時候，但這不代表我們會退縮。",
                        "勇敢不是不害怕，而是即使害怕也不放棄。",
                        "恐懼的根源，常常來自於對未知的無知。",
                        "怕是因為心中還有希望。",
                        "面對恐懼，最好的辦法是行動。",
                        "恐懼讓你停滯，勇氣讓你前進。",
                        "每一個害怕的背後，都有一次成長的機會。",
                        "學會在恐懼中前行，才是真正的勇者。"
                )),
                new Mood("love", Arrays.asList(
                        "愛，是心靈的牽絆。",
                        "真正的愛是不計較得失的付出。",
                        "愛是一種無法言喻的美麗。",
                        "愛使我們變得更堅強。",
                        "愛是能夠治癒一切的力量。",
                        "愛，是給予而不是索取。",
                        "愛讓我們學會包容。",
                        "愛的力量能夠戰勝一切。",
                        "愛是生活中最美好的奇蹟。",
                        "愛，永遠不會過時。"
                ))
        );

        ArrayAdapter<String> moodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Arrays.asList("開心", "傷心", "生氣", "恐懼", "愛"));
        moodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userMoodSpinner.setAdapter(moodAdapter);

        generateQuotesButton.setOnClickListener(v -> {
            String selectedMood = userMoodSpinner.getSelectedItem().toString();
            // 根據選擇的中文心情找到對應的英文心情
            String moodKey = getMoodKeyByTranslation(selectedMood);

            if (moodKey != null) {
                Mood tagetMood = getMoodByName(moodKey);
                // 隨機選取語錄
                String randomQuote = getRandomQuote(tagetMood);
                quotesView.setText(randomQuote);
            } else {
                Toast.makeText(this, "未找到對應心情！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void go_back(View view) {
        finish();
    }

    private String getMoodKeyByTranslation(String translation) {
        Map<String, String> moodTranslation = new HashMap<>();
        moodTranslation.put("happy", "開心");
        moodTranslation.put("sad", "傷心");
        moodTranslation.put("angry", "生氣");
        moodTranslation.put("fearful", "恐懼");
        moodTranslation.put("love", "愛");

        for (Map.Entry<String, String> entry : moodTranslation.entrySet()) {
            if (entry.getValue().equals(translation)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Mood getMoodByName(String moodName) {
        for (Mood mood : moodQuotesList) {
            if (mood.mood.equalsIgnoreCase(moodName)) {
                return mood;
            }
        }
        return null;
    }

    private String getRandomQuote(Mood mood) {
        Random random = new Random();
        int index = random.nextInt(mood.quotes.size());
        return mood.quotes.get(index);
    }
}
