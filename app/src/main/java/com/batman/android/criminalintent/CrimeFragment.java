package com.batman.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.UUID;



/**
 * Created by God on 28.09.2016.
 */
public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_CODE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;

    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Callbacks mCallbacks;

    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
        void onCrimeUpdated(Crime  crime);
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return  fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                          Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //TODO
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO
            }
        });

        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_CODE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //назначение флага раcкрытия преступления
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        mReportButton = (Button)v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .createChooserIntent();

                startActivity(intent);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        mCallButton = (Button) v.findViewById(R.id.call_suspect);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri provider = Phone.CONTENT_URI;

                String[] queriFields = new String[]{
                        Phone.NUMBER,
                        Phone.IS_PRIMARY
                };

                String mSelectiumClause = Phone.CONTACT_ID + " = ? AND "
                        + Phone.IS_PRIMARY + " = ?";

                String[] mSelectiumArgs = new String[]{
                        String.valueOf(mCrime.getSuspectId()),
                        String.valueOf(1)
                };

                Cursor cc = getContext().getContentResolver().query(
                        provider,
                        queriFields,
                        mSelectiumClause,
                        mSelectiumArgs,
                        null
                );

                try {
                    if (cc.getCount() == 0) {
                        Toast msq = Toast.makeText(getContext(), "No number )=" , Toast.LENGTH_SHORT);
                        msq.show();

                        return;
                    }

                    cc.moveToFirst();
                    Uri number = Uri.parse("tel:" + cc.getString(0));

                    Intent pickCall = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(pickCall);

                } finally {
                    cc.close();
                }
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                PhotoViewerFragment dialog = PhotoViewerFragment.newInstance(mPhotoFile.getPath());
                dialog.show(manager, "Photo_viewer");
            }
        });

        ViewTreeObserver vto = mPhotoView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver otv = mPhotoView.getViewTreeObserver();

                if (Build.VERSION.SDK_INT < 16) {
                    otv.removeGlobalOnLayoutListener(this);
                } else {
                    otv.removeOnGlobalLayoutListener(this);
                }

                updatePhotoView();
            }
        });

        updateCallButton();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).delCrime(mCrime);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();

            // Определение полей, значения которых должны быть
            // возвращены запросом.
            String[] queryFields = new String[] {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            // Выполнение запроса - contactUri здесь выполняет функции
            // условия "where"
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try {
                // Проверка получения результатов
                if (c.getCount() == 0) {
                    return;
                }
                // Извлечение первого столбца данных - имени подозреваемого.
                c.moveToFirst();
                long suspectId = c.getLong(0);
                String suspectName = c.getString(1);
                mCrime.setSuspectName(suspectName);
                mCrime.setSuspectId(suspectId);
                updateCrime();
                mSuspectButton.setText(suspectName);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            updateCrime();
            updatePhotoView();
        }
    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private void updateDate() {
        mDateButton.setText((DateFormat.format("EEEE, dd MM yyyy", mCrime.getDate())).toString());
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM, dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspectName = mCrime.getSuspectName();
        if (suspectName == null) {
            suspectName = getString(R.string.crime_report_no_suspect);
        } else {
            suspectName = getString(R.string.crime_report_suspect, suspectName);
        }

        String report = getString(R.string.crime_report,
                                mCrime.getTitle(),
                                dateString,
                                solvedString,
                                suspectName);

        return report;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
            mPhotoView.setClickable(false);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), mPhotoView.getWidth(), mPhotoView.getHeight());
            mPhotoView.setImageBitmap(bitmap);
            mPhotoView.setClickable(true);
        }
    }

    private void updateCallButton() {
        if (mCrime.getSuspectId() != 0) {
            mSuspectButton.setText(mCrime.getSuspectName());
            mCallButton.setEnabled(true);
        } else {
            mCallButton.setEnabled(false);
        }
    }

    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, null);
    }
}
