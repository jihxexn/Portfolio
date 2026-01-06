package com.example.my11;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.*;

public class DiaryActivity extends AppCompatActivity {

    private SaveManager saveManager;
    private GridLayout diaryGrid;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        saveManager = new SaveManager(this);
        diaryGrid = findViewById(R.id.diaryGrid);

        /* Button writeBtn = findViewById(R.id.writeDiaryButton);
        writeBtn.setOnClickListener(v -> {
            String today = sdf.format(new Date());
            Map<String, DiaryEntry> diaryMap = saveManager.loadUserDiaryMap();
            if (diaryMap.containsKey(today)) {
                new AlertDialog.Builder(this)
                        .setTitle("작성 불가")
                        .setMessage("이미 오늘의 일기가 작성되었습니다!")
                        .setPositiveButton("확인", null)
                        .show();
            } else {
                openDiaryInputDialog(today, null); // 오늘 날짜로 새 일기 작성
            }
        });
        */


        loadDiary();
    }

    // 현재 돌 이미지 리소스 ID를 가져오는 메서드
    private int getCurrentStoneImageResId() {
        // 실제론 MainActivity에서 넘겨받는 구조로 확장 가능
        return R.drawable.stone_blink;
    }

    // 일기 작성 or 수정 다이얼로그
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
                        loadDiary();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void loadDiary() {
        diaryGrid.removeAllViews();
        Map<String, DiaryEntry> diaryMap = new TreeMap<>(saveManager.loadUserDiaryMap());

        for (Map.Entry<String, DiaryEntry> entry : diaryMap.entrySet()) {
            String date = entry.getKey();
            DiaryEntry diary = entry.getValue();

            // 이미지 버튼
            ImageButton imageButton = new ImageButton(this);
            imageButton.setImageResource(diary.getStoneImageResId());
            imageButton.setBackground(null);
            imageButton.setAdjustViewBounds(true);
            imageButton.setMaxWidth(200);
            imageButton.setMaxHeight(200);
            imageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageButton.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle(date + "의 일기")
                        .setMessage(diary.getContent())
                        .setPositiveButton("닫기", null)
                        .show();
            });

            // 날짜 텍스트
            TextView dateText = new TextView(this);
            dateText.setText(date);
            dateText.setGravity(Gravity.CENTER);
            dateText.setTextSize(14);
            dateText.setPadding(0, 8, 0, 0);

            // 하나의 항목 레이아웃
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            itemLayout.setPadding(16, 16, 16, 16);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(8, 8, 8, 8);
            itemLayout.setLayoutParams(params);


            imageButton.setOnLongClickListener(null);

            imageButton.setOnLongClickListener(v-> {
                String today = sdf.format(new Date());
                if(date.equals(today)){
                    openDiaryInputDialog(date, diary.getContent());
                } else {
                    Toast.makeText(this, "오늘의 일기만 수정할 수 있습니다." ,Toast.LENGTH_SHORT).show();
                } return true;
            });

            itemLayout.addView(imageButton);
            itemLayout.addView(dateText);
            diaryGrid.addView(itemLayout);
        }
    }
}
