package com.batman.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.zip.Inflater;

/**
 * Created by roma on 19.12.16.
 */

public class PhotoViewerFragment extends DialogFragment {
    private static final String FILE_PATH = "path";

    private ImageView mImageView;

    public static PhotoViewerFragment newInstance(String file) {
        Bundle args = new Bundle();
        args.putSerializable(FILE_PATH, file);

        PhotoViewerFragment fragment = new PhotoViewerFragment();
        fragment.setArguments(args);

        return fragment;
    }

/*
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String path = (String) getArguments().getSerializable(FILE_PATH);

        View v = LayoutInflater.from(getActivity()).
                inflate(R.layout.dialog_photo_viewer, null);

        mImageView = (ImageView) v.findViewById(R.id.crime_photo_viewer);
        mImageView.setImageURI(Uri.parse(path));

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }
*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String path = (String) getArguments().getSerializable(FILE_PATH);

        View v = inflater.inflate(R.layout.dialog_photo_viewer, container, false);

        mImageView = (ImageView) v.findViewById(R.id.crime_photo_viewer);
        mImageView.setImageURI(Uri.parse(path));

        return v;
    }
}
