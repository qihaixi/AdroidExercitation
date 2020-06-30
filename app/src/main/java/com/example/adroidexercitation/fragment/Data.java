package com.example.adroidexercitation.fragment;

public class Data {
    private String aName, aSpeak;
    private int aIcon;

    public Data(String str, String s, int i) {
        aName = str;
        aSpeak = s;
        aIcon = i;
    }

    public String getaName() {
        return aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getaSpeak() {
        return aSpeak;
    }

    public void setaSpeak(String aSpeak) {
        this.aSpeak = aSpeak;
    }

    public int getaIcon() {
        return aIcon;
    }

    public void setaIcon(int aIcon) {
        this.aIcon = aIcon;
    }
}
