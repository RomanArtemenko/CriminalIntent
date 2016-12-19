package com.batman.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.zip.Inflater;

/**
 * Created by roma on 19.12.16.
 */

public class PhotoViewerFragment extends DialogFragment {

    private ImageView mImageView;

    public static PhotoViewerFragment newInstance(Path)

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).
                inflate(R.layout.dialog_photo_viewer, null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_photo_viewer, container, false);

        mImageView = (ImageView) v.findViewById(R.id.crime_photo_viewer);

        return v;
    }*/
}
