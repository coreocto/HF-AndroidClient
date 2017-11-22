package org.coreocto.dev.hf.androidclient.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.*;
import hugo.weaving.DebugLog;
import org.coreocto.dev.hf.androidclient.R;
import org.coreocto.dev.hf.androidclient.activity.NavDwrActivity;
import org.coreocto.dev.hf.androidclient.bean.AppSettings;
import org.coreocto.dev.hf.commonlib.suise.util.SuiseUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestFragment extends Fragment {

    private static final String TAG = "TestFragment";

    private OnFragmentInteractionListener mListener;

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.d(TAG, "Error while trying to create the file");
                        return;
                    }

                    DriveId driveId = result.getDriveFile().getDriveId();
                    driveId.getResourceId();

                    Log.d(TAG, "Created a file with content: " + driveId);
                    Log.d(TAG, "Resource Id: " + driveId.getResourceId());

                    DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, driveId);
                    file.addChangeSubscription(mGoogleApiClient);
                }
            };

    public TestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestFragment newInstance(String param1, String param2) {
        TestFragment fragment = new TestFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Button bTestAdd;
    private Button bTestHugo;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        mGoogleApiClient = ((NavDwrActivity) this.getActivity()).getGoogleApiClient();

        this.bTestAdd = view.findViewById(R.id.bTestAdd);
        this.bTestAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFileToGDrive(new java.io.File(Environment.getExternalStorageDirectory() + File.separator + "Download" + File.separator + "ThinkCentre M910z AIO.pdf"));
            }
        });
        this.bTestHugo = view.findViewById(R.id.bTestHugo);
        this.bTestHugo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    new Thread(new Runnable() {
                        @DebugLog
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void saveFileToGDrive(final File srcFile) {

        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything and must fail.
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Failed to create new contents.");
                            return;
                        }

                        // Otherwise, we can write our data to the new contents.
//                                Log.i(TAG, "New contents created.");
                        // Get an output stream for the contents.
                        OutputStream outputStream = result.getDriveContents().getOutputStream();
                        // Write file data from it.
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(srcFile);

                            SuiseUtil suiseUtil = new SuiseUtil(AppSettings.getInstance().getRegistry());
                            suiseUtil.copy(4096, inputStream, outputStream);

                        } catch (Exception e1) {
                            Log.i(TAG, "Unable to write file contents.");
                        }

                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                            }
                        }

                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                            }
                        }

                        // Create the initial metadata - MIME type and title.
                        // Note that the user will be able to change the title later.
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setMimeType("application/octet-stream").setTitle("test").build();

                        // Create a file in the root folder
                        Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                .createFile(mGoogleApiClient, changeSet, result.getDriveContents(), new ExecutionOptions.Builder().setNotifyOnCompletion(true)
                                        .build())
                                .setResultCallback(fileCallback);
                    }
                });
    }
}
