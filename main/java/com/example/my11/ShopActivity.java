package com.example.my11;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class ShopActivity extends AppCompatActivity {

    private SaveManager saveManager;
    private GridView decorationGrid, backgroundGrid;
    private List<ShopItem> decorationItems;
    private List<ShopItem> backgroundItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        saveManager = new SaveManager(this);
        decorationGrid = findViewById(R.id.decorationGrid);
        backgroundGrid = findViewById(R.id.backgroundGrid);

        decorationItems = Arrays.asList(
                new ShopItem("모자", "hat", R.drawable.hat, 10),
                new ShopItem("꽃", "flower", R.drawable.flower, 15),
                new ShopItem("불가사리", "starfish", R.drawable.starfish, 20),
                new ShopItem("잎사귀", "leaf", R.drawable.leaf, 20),
                new ShopItem("망치", "hammer", R.drawable.hammer, 30),
                new ShopItem("연필", "pencil", R.drawable.pencil, 30)
        );

        backgroundItems = Arrays.asList(
                new ShopItem("바다", "bg_ocean", R.drawable.bg_ocean, 20),
                new ShopItem("산", "bg_mountain", R.drawable.bg_mountain, 20),
                new ShopItem("메모지", "bg_note", R.drawable.bg_note, 20),
                new ShopItem("우주", "bg_space", R.drawable.bg_space, 20)
        );

        decorationGrid.setAdapter(new ShopAdapter(decorationItems));
        backgroundGrid.setAdapter(new ShopAdapter(backgroundItems));

        decorationGrid.setOnItemClickListener((parent, view, position, id) -> handlePurchase(decorationItems.get(position)));
        backgroundGrid.setOnItemClickListener((parent, view, position, id) -> handlePurchase(backgroundItems.get(position)));
    }

    private void handlePurchase(ShopItem item) {
        int[] data = saveManager.load();
        int userCoins = data[3];
        List<String> ownedList = Arrays.asList(saveManager.loadOwnedItems());

        if (ownedList.contains(item.key)) {
            Toast.makeText(this, "이미 구매한 아이템입니다!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userCoins < item.price) {
            Toast.makeText(this, "코인이 부족합니다!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (item.key.startsWith("bg_")) {
            saveManager.saveBackground(item.key);
            Toast.makeText(this, "배경이 적용되었습니다!", Toast.LENGTH_SHORT).show();
        } else {
            saveManager.saveOwnedItem(item.key);
            Toast.makeText(this, "아이템이 구매되었습니다!", Toast.LENGTH_SHORT).show();
        }

        saveManager.save(data[0], data[1], data[2], userCoins - item.price);
        finish();
    }

    class ShopItem {
        String name;
        String key;
        int imageResId;
        int price;

        ShopItem(String name, String key, int imageResId, int price) {
            this.name = name;
            this.key = key;
            this.imageResId = imageResId;
            this.price = price;
        }
    }

    class ShopAdapter extends BaseAdapter {
        private final List<ShopItem> items;

        ShopAdapter(List<ShopItem> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout layout = new LinearLayout(ShopActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(android.view.Gravity.CENTER);
            layout.setPadding(16, 16, 16, 16);

            ImageView imageView = new ImageView(ShopActivity.this);
            imageView.setImageResource(items.get(position).imageResId);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(220, 220));

            TextView nameText = new TextView(ShopActivity.this);
            nameText.setText(items.get(position).name);
            nameText.setTextSize(14);
            nameText.setGravity(android.view.Gravity.CENTER);

            TextView priceText = new TextView(ShopActivity.this);
            priceText.setText(items.get(position).price + "코인");
            priceText.setTextSize(14);
            priceText.setGravity(android.view.Gravity.CENTER);

            layout.addView(imageView);
            layout.addView(nameText);
            layout.addView(priceText);
            return layout;
        }
    }
}

