package com.batman.android.criminalintent;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;

/**
 * Created by roma on 22.12.16.
 */

public class PhotoViewerActivity extends SingleFragmentActivity {

    private static final String EXTRA_FILE_PATH = "com.batman.android.criminalintent.EXTRA_FILE_PATH";

    public static Intent newIntent(Context packageContext, String path) {
        Intent intent = new Intent(packageContext, PhotoViewerActivity.class);
        intent.putExtra(EXTRA_FILE_PATH, path);

        return intent;
    }

    @Override
    protected Fragment createFragment(){
        String path = (String) getIntent().getSerializableExtra(EXTRA_FILE_PATH);

        return PhotoViewerFragment.newInstance(path);
    }
}
