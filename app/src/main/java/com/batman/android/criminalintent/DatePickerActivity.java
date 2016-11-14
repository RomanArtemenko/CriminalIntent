package com.batman.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;

/**
 * Created by God on 13.11.2016.
 */

public class DatePickerActivity extends SingleFragmentActivity {

    private static final String EXTRA_DIALOG_DATE = "com.batman.android.criminaIntent.extra_dialog_date";

    public static Intent newIntent(Context packageContext, Date date) {
        Intent intent = new Intent(packageContext, DatePickerActivity.class);
        intent.putExtra(EXTRA_DIALOG_DATE, date);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Date date = (Date) getIntent().getSerializableExtra(EXTRA_DIALOG_DATE);
        return DatePickerFragment.newInstance(date);
    }
}