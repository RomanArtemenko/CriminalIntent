package com.batman.android.criminalintent;

import java.util.UUID;

/**
 * Created by God on 27.09.2016.
 */
public class Crime {
    private UUID mId;
    private String mTitle;

    public Crime() {
        //Генерирование уникального индентификатора
        mId = UUID.randomUUID();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
