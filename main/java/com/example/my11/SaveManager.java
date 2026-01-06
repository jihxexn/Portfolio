package com.example.my11;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SaveManager {

    private static final String PREFS_NAME = "StonePrefs";
    private SharedPreferences prefs;

    public SaveManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void save(int hunger, int exp, int level, int coins) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("hunger", hunger);
        editor.putInt("exp", exp);
        editor.putInt("level", level);
        editor.putInt("coins", coins);
        editor.apply();
    }

    public int[] load() {
        int hunger = prefs.getInt("hunger", 100);
        int exp = prefs.getInt("exp", 0);
        int level = prefs.getInt("level", 1);
        int coins = prefs.getInt("coins", 0);
        return new int[]{hunger, exp, level, coins};
    }
    // 날짜별 일기 저장
    public void saveDiaryByDate(String dateKey, String content, int stoneImageResId) {
        try {
            String json = prefs.getString("diary_map", "{}");
            JSONObject obj = new JSONObject(json);

            JSONObject diaryObj = new JSONObject();
            diaryObj.put("content", content);
            diaryObj.put("stoneImageResId", stoneImageResId);

            obj.put(dateKey, diaryObj);
            prefs.edit().putString("diary_map", obj.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 날짜별 일기 불러오기
    public Map<String, DiaryEntry> loadUserDiaryMap() {
        Map<String, DiaryEntry> map = new HashMap<>();

        try {
            String json = prefs.getString("diary_map", "{}");
            JSONObject obj = new JSONObject(json);
            Iterator<String> keys = obj.keys();

            while (keys.hasNext()) {
                String key = keys.next();

                try {
                    Object val = obj.get(key);

                    if (val instanceof JSONObject) {
                        JSONObject diaryObj = (JSONObject) val;
                        String content = diaryObj.getString("content");
                        int imageResId = diaryObj.optInt("stoneImageResId", R.drawable.flower);
                        map.put(key, new DiaryEntry(content, imageResId));
                    } else if (val instanceof String) {
                        // 이전 버전의 단순 문자열 형식 데이터 처리
                        String content = (String) val;
                        map.put(key, new DiaryEntry(content, R.drawable.flower)); // 기본 이미지 사용
                    } else {
                        // 알 수 없는 데이터 타입인 경우 기본 처리 (생략 또는 로그)
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // 문제 있는 항목은 무시하고 다음으로 넘어감
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }


    // 사용자 작성 일기 저장
    public void addUserDiary(String entry) {
        String all = prefs.getString("user_diaries", "");
        all += entry + ";;";
        prefs.edit().putString("user_diaries", all).apply();
    }

    public List<String> loadUserDiaries() {
        String all = prefs.getString("user_diaries", "");
        if (all.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(all.split(";;")));
    }

    // 수집 일기 저장
    public void addCollectionEntry(String imageName, String message) {
        String existing = prefs.getString("collection", "");
        String entry = imageName + "|" + message + ";";
        prefs.edit().putString("collection", existing + entry).apply();
    }

    public String[] loadCollection() {
        String all = prefs.getString("collection", "");
        return all.isEmpty() ? new String[0] : all.split(";");
    }

    public void saveOwnedItem(String itemName) {
        String owned = prefs.getString("owned_items", "");
        if (!owned.contains(itemName + ";")) {
            owned += itemName + ";";
            prefs.edit().putString("owned_items", owned).apply();
        }
    }

    public String[] loadOwnedItems() {
        String owned = prefs.getString("owned_items", "");
        return owned.isEmpty() ? new String[0] : owned.split(";");
    }

    // 장착 아이템 저장
    public void setEquippedItem(String item) {
        prefs.edit().putString("equipped_item", item).apply();
    }

    public String getEquippedItem() {
        return prefs.getString("equipped_item", "");
    }

    public void saveBackground(String bgName) {
        prefs.edit().putString("background", bgName).apply();
    }

    public String loadBackground() {
        return prefs.getString("background", "bg_ocean");
    }

    public int getFeedBonus() {
        return prefs.getInt("feed_bonus", 10); // 기본 10
    }

    public void upgradeFeedBonus() {
        int current = getFeedBonus();
        if (current < 100) {
            prefs.edit().putInt("feed_bonus", current + 10).apply();
        }
    }
}
