package com.example.my11;

public class DiaryEntry {
    private final String content;
    private final int stoneImageResId;

    public DiaryEntry(String content, int stoneImageResId) {
        this.content = content;
        this.stoneImageResId = stoneImageResId;
    }

    public String getContent() {
        return content;
    }

    public int getStoneImageResId() {
        return stoneImageResId;
    }
}
