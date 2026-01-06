package com.example.my11;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class WardrobeActivity extends AppCompatActivity {

    private ImageView bgImage, stoneImage, accessoryImage;
    private Button applyButton, resetButton;
    private Button tabAccessory, tabBackground, tabStone;
    private GridView gridItems;

    private SaveManager saveManager;

    private List<String> ownedItems;
    private List<String> currentCategoryItems;
    private BaseAdapter gridAdapter;

    private String selectedAccessory = "";
    private String selectedStone = "";
    private String selectedBackground = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);

        saveManager = new SaveManager(this);

        bgImage = findViewById(R.id.bgImage);
        stoneImage = findViewById(R.id.stoneImage);
        accessoryImage = findViewById(R.id.accessoryImage);

        applyButton = findViewById(R.id.applyButton);
        resetButton = findViewById(R.id.resetButton);
        tabAccessory = findViewById(R.id.tabAccessory);
        tabBackground = findViewById(R.id.tabBackground);
        tabStone = findViewById(R.id.tabStone);
        gridItems = findViewById(R.id.gridItems);

        ownedItems = Arrays.asList(saveManager.loadOwnedItems());

        String bg = saveManager.loadBackground();
        int bgRes = getResources().getIdentifier(bg, "drawable", getPackageName());
        bgImage.setImageResource(bgRes);

        tabAccessory.setOnClickListener(v -> showItems("accessory"));
        tabBackground.setOnClickListener(v -> showItems("background"));
        tabStone.setOnClickListener(v -> showItems("stone"));

        applyButton.setOnClickListener(v -> applySelection());
        resetButton.setOnClickListener(v -> resetSelection());

        findViewById(R.id.shopButton).setOnClickListener(v ->
                startActivity(new Intent(this, ShopActivity.class)));

        findViewById(R.id.diaryButton).setOnClickListener(v ->
                startActivity(new Intent(this, DiaryActivity.class)));

        findViewById(R.id.wardrobeButton).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));

        showItems("accessory");
    }

    private void showItems(String category) {
        currentCategoryItems = new ArrayList<>();

        for (String item : ownedItems) {
            if (category.equals("accessory") && !item.startsWith("bg_") && !item.startsWith("stone_")) {
                currentCategoryItems.add(item);
            } else if (category.equals("background") && item.startsWith("bg_")) {
                currentCategoryItems.add(item);
            } else if (category.equals("stone") && item.startsWith("stone_")) {
                currentCategoryItems.add(item);
            }
        }

        gridAdapter = new WardrobeAdapter(this, currentCategoryItems);
        gridItems.setAdapter(gridAdapter);

        gridItems.setOnItemClickListener((parent, view, position, id) -> {
            String selected = currentCategoryItems.get(position);
            int resId = getResources().getIdentifier(selected, "drawable", getPackageName());

            switch (category) {
                case "accessory":
                    accessoryImage.setImageResource(resId);
                    selectedAccessory = selected;
                    break;
                case "background":
                    bgImage.setImageResource(resId);
                    selectedBackground = selected;
                    break;
                case "stone":
                    stoneImage.setImageResource(resId);
                    selectedStone = selected;
                    break;
            }
        });
    }

    private void applySelection() {
        if (!selectedAccessory.isEmpty()) {
            // 보유 아이템에 저장
            saveManager.saveOwnedItem(selectedAccessory);
            // 장착 아이템으로 설정
            saveManager.setEquippedItem(selectedAccessory);
        }

        if (!selectedStone.isEmpty()) {
            // 추후 기능 확장용
        }

        if (!selectedBackground.isEmpty()) {
            saveManager.saveBackground(selectedBackground);
        }

        Toast.makeText(this, "적용되었습니다!", Toast.LENGTH_SHORT).show();
    }


    private void resetSelection() {
        selectedAccessory = "";
        selectedStone = "";
        selectedBackground = "";

        accessoryImage.setImageResource(android.R.color.transparent);
        stoneImage.setImageResource(R.drawable.stone_default);

        String bg = saveManager.loadBackground();
        int bgRes = getResources().getIdentifier(bg, "drawable", getPackageName());
        bgImage.setImageResource(bgRes);

        Toast.makeText(this, "초기화되었습니다.", Toast.LENGTH_SHORT).show();
    }
}

