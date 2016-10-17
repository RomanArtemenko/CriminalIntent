package com.batman.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by God on 11.10.2016.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
