package com.example.my11;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private ImageView stoneImage, stoneItemImage, stoneBackgroundImage;
    private ProgressBar hungerBar, expBar;
    private TextView levelText, coinText;
    private Button feedButton, playButton, shopButton, diaryButton, wardrobeButton;

    private int hunger = 100, exp = 0, level = 1, coins = 0;
    private int maxHunger = 100;
    private Handler handler = new Handler();
    private SaveManager saveManager;
    private PointF touchPoint = new PointF();
    private Random random = new Random();

    private final int[] levelMilestones = {2000, 1000, 900, 800, 700, 600, 500, 400, 300, 200, 100, 50, 10};
    private String currentImageName = "stone_default";
    private boolean canRecord = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stoneImage = findViewById(R.id.stoneImage);
        stoneItemImage = findViewById(R.id.stoneItemImage);
        stoneBackgroundImage = findViewById(R.id.stoneBackgroundImage);
        hungerBar = findViewById(R.id.hungerBar);
        expBar = findViewById(R.id.expBar);
        levelText = findViewById(R.id.levelText);
        coinText = findViewById(R.id.coinText);
        feedButton = findViewById(R.id.feedButton);
        playButton = findViewById(R.id.playButton);
        shopButton = findViewById(R.id.shopButton);
        diaryButton = findViewById(R.id.diaryButton);
        wardrobeButton = findViewById(R.id.wardrobeButton);

        saveManager = new SaveManager(this);
        loadGame();

        feedButton.setOnClickListener(v -> feedStone());
        playButton.setOnClickListener(v -> playStone());
        shopButton.setOnClickListener(v -> startActivity(new Intent(this, ShopActivity.class)));
        diaryButton.setOnClickListener(v -> startActivity(new Intent(this, DiaryActivity.class)));
        wardrobeButton.setOnClickListener(v -> startActivity(new Intent(this, WardrobeActivity.class)));

        stoneImage.setOnLongClickListener(v -> {
            // 오늘 날짜
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // 기존에 저장된 일기 불러오기
            Map<String, DiaryEntry> diaryMap = saveManager.loadUserDiaryMap();

            // 오늘 일기가 있으면 기존 내용을 보여주면서 수정, 없으면 새 작성
            String existingDiary = null;
            if (diaryMap.containsKey(today)) {
                existingDiary = diaryMap.get(today).getContent();
            }

            openDiaryInputDialog(today, existingDiary);
            return true;  // 롱 클릭 이벤트 처리 완료
        });


        startGameLoop();
        startBlinking();
    }

    private void openDiaryInputDialog(String dateKey, String existingDiary) {
        int stoneImageResId = getCurrentStoneImageResId();

        // 수정 모드면 기존 이미지 유지
        if (existingDiary != null) {
            Map<String, DiaryEntry> map = saveManager.loadUserDiaryMap();
            DiaryEntry existingEntry = map.get(dateKey);
            if (existingEntry != null) {
                stoneImageResId = existingEntry.getStoneImageResId();
            }
        }

        Button dateBtn = new Button(this);
        dateBtn.setText(dateKey);
        dateBtn.setEnabled(false);

        EditText input = new EditText(this);
        input.setHint("일기 내용을 입력하세요");
        if (existingDiary != null) {
            input.setText(existingDiary);
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 0);
        layout.addView(dateBtn);
        layout.addView(input);

        int finalStoneImageResId = stoneImageResId;
        new AlertDialog.Builder(this)
                .setTitle(existingDiary != null ? "일기 수정" : "일기 작성")
                .setView(layout)
                .setPositiveButton("저장", (dialog, which) -> {
                    String entry = input.getText().toString().trim();
                    if (!entry.isEmpty()) {
                        saveManager.saveDiaryByDate(dateKey, entry, finalStoneImageResId);
                        Toast.makeText(this, "일기가 저장되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }
    private int getCurrentStoneImageResId() {
        // 메인화면 돌 이미지 리소스 아이디 반환
        return R.drawable.stone_blink;  // 혹은 현재 보여지는 돌 이미지에 맞게 조절
    }

    private void feedStone() {
        hunger = Math.min(maxHunger, hunger + 20);
        exp += 10;
        checkLevelUp();
        saveGame();
        updateUI();
    }

    private void playStone() {
        int hungerLoss = Math.max(1, maxHunger / 10);
        hunger = Math.max(0, hunger - hungerLoss);
        exp += 20;
        coins += 1;
        checkLevelUp();
        saveGame();
        updateUI();
    }

    private void checkLevelUp() {
        if (exp >= getExpThreshold()) {
            level++;
            exp = 0;
            maxHunger += 5;
        }

        if (hunger <= 0) {
            if (level > 1) {
                level--;
                hunger = maxHunger;
            }
        }
    }

    private void saveCollection() {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String diaryText = date + " - 자동 저장된 2000레벨 돌입니다!";
        saveManager.addCollectionEntry(currentImageName, diaryText);
    }

    private int getExpThreshold() {
        return 100 + (level * 5);
    }

    private void updateUI() {
        hungerBar.setMax(maxHunger);
        hungerBar.setProgress(hunger);
        expBar.setProgress(exp * 100 / getExpThreshold());
        levelText.setText("Level " + level);
        coinText.setText("Coins: " + coins);

        String bg = saveManager.loadBackground();
        int bgResId = getResources().getIdentifier(bg, "drawable", getPackageName());
        stoneBackgroundImage.setImageResource(bgResId);

        String item = saveManager.getEquippedItem();
        if (!item.isEmpty()) {
            int itemResId = getResources().getIdentifier(item, "drawable", getPackageName());
            stoneItemImage.setImageResource(itemResId);
            stoneItemImage.setVisibility(View.VISIBLE);
        } else {
            stoneItemImage.setVisibility(View.GONE);
        }

        currentImageName = "stone_default";
        int stoneRes = getResources().getIdentifier(currentImageName, "drawable", getPackageName());
        stoneImage.setImageResource(stoneRes);
    }
    private void startGameLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hunger = Math.max(0, hunger - 1);
                saveGame();
                updateUI();
                handler.postDelayed(this, 10000);
            }
        }, 10000);
    }


    private void saveGame() {
        saveManager.save(hunger, exp, level, coins);
    }
    private void startBlinking() {
        Handler blinkHandler = new Handler();
        Runnable blinkRunnable = new Runnable() {
            boolean isBlinking = false;

            @Override
            public void run() {
                if (!isBlinking) {
                    int blinkResId = getResources().getIdentifier("stone_blink", "drawable", getPackageName());
                    stoneImage.setImageResource(blinkResId);
                    isBlinking = true;
                    blinkHandler.postDelayed(this, 200); // 감는 시간
                } else {
                    int normalResId = getResources().getIdentifier(currentImageName, "drawable", getPackageName());
                    stoneImage.setImageResource(normalResId);
                    isBlinking = false;
                    blinkHandler.postDelayed(this, 4000 + new Random().nextInt(3000)); // 다시 깜박이기까지
                }
            }
        };
        blinkHandler.postDelayed(blinkRunnable, 4000);
    }
    private void loadGame() {
        int[] data = saveManager.load();
        hunger = data[0];
        exp = data[1];
        level = data[2];
        coins = data[3];
        maxHunger = 100 + (level * 5);
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGame();  // 상점 등에서 돌아왔을 때 UI 갱신
    }
}