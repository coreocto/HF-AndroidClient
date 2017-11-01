package org.coreocto.dev.hf.androidclient.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.gson.Gson;
import okhttp3.*;
import org.coreocto.dev.hf.androidclient.R;
import org.coreocto.dev.hf.androidclient.activity.NavDwrActivity;
import org.coreocto.dev.hf.androidclient.bean.AppSettings;
import org.coreocto.dev.hf.androidclient.benchmark.AddTokenStopWatch;
import org.coreocto.dev.hf.androidclient.benchmark.DocEncryptStopWatch;
import org.coreocto.dev.hf.androidclient.benchmark.DocUploadStopWatch;
import org.coreocto.dev.hf.clientlib.suise.SuiseClient;
import org.coreocto.dev.hf.commonlib.suise.bean.AddTokenResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {

    public static final ExecutorService execService = Executors.newSingleThreadExecutor();
    private static final String TAG = "AddFragment";
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.d(TAG, "Error while trying to create the file");
                        return;
                    }
                    Log.d(TAG, "Created a file with content: " + result.getDriveFile().getDriveId());
                }
            };
    private OnFragmentInteractionListener mListener;
    private ListView lvUploadFileList;
    private Button bAdd;
    private ArrayAdapter<String> arrayAdapter = null;
    private List<String> uploadFileList = null;

    public AddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance() {
        AddFragment fragment = new AddFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        lvUploadFileList = (ListView) view.findViewById(R.id.lvUploadFileList);
        bAdd = (Button) view.findViewById(R.id.bAdd);

        final Context ctx = getActivity();

        this.uploadFileList = new ArrayList<>();

        {
            String extStore = Environment.getExternalStorageDirectory().toString();
            File dir = new File(extStore + "/Suise");
            File[] docList = dir.listFiles();
            for (File doc : docList) {
                this.uploadFileList.add(doc.getName());
            }
        }

        this.arrayAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1, uploadFileList);

        lvUploadFileList.setAdapter(arrayAdapter);

        final Gson gson = AppSettings.getInstance().getGson();

        bAdd.setOnClickListener(new View.OnClickListener() {

            private ProgressDialog progressDialog = null;
            private Handler dismissDialogHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
                    int max = progressDialog.getMax();
                    Log.d(TAG, "msg.what = " + msg.what);
                    Log.d(TAG, "progressDialog.max = " + max);
                    if (msg.what + 1 >= max) {
                        progressDialog.dismiss();// 关闭ProgressDialog
                    } else {
                        progressDialog.setMessage("Uploading documents (" + (msg.what + 1) + "/" + max + "), please wait...");
                        progressDialog.setProgress(msg.what);
                    }
                }
            };

            private void pushStat(Object obj, String type) {

                AppSettings appSettings = AppSettings.getInstance();

                final String statUrl = appSettings.getAppPref().getString(AppSettings.SERVER_HOSTNAME, null) + "/stat";

                RequestBody requestBody = new FormBody.Builder()
                        .add("data", gson.toJson(obj))
                        .add("type", type).build();

                Request request = new Request.Builder()
                        .url(statUrl)
                        .post(requestBody)
                        .build();

                new OkHttpClient().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            // Handle the error
                        }

                        response.close();
                    }
                });
            }

            private void saveEncFileToDrive(final java.io.File srcFile, final String docId, final SuiseClient suiseClient) {
                final GoogleApiClient mGoogleApiClient = ((NavDwrActivity) ctx).getGoogleApiClient();

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
                                Log.i(TAG, "New contents created.");
                                // Get an output stream for the contents.
                                OutputStream outputStream = result.getDriveContents().getOutputStream();
                                // Write file data from it.
                                FileInputStream inputStream = null;
                                try {
                                    inputStream = new FileInputStream(srcFile);

                                    suiseClient.Enc(inputStream, outputStream);

                                } catch (IOException e1) {
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
                                        .setMimeType("application/octet-stream").setTitle(docId).build();

                                // Create a file in the root folder
                                Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                        .createFile(mGoogleApiClient, changeSet, result.getDriveContents())
                                        .setResultCallback(fileCallback);
                            }
                        });
            }

            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(ctx, ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("Uploading...");
                progressDialog.setMessage("Uploading documents, please wait...");

                // newly added code
                progressDialog.setCancelable(false);
                // end

                final OkHttpClient httpClient = new OkHttpClient();

                final AppSettings appSettings = AppSettings.getInstance();
                final SuiseClient client = appSettings.getSuiseClient();

                final String extStore = Environment.getExternalStorageDirectory().toString();

                final String url = appSettings.getAppPref().getString(AppSettings.SERVER_HOSTNAME, null) + "/upload";

                final boolean enableStatRpt = appSettings.getAppPref().getBoolean(AppSettings.SERVER_RPT_STAT, false);

                String dirPath = extStore + "/Suise";
                File dir = new File(dirPath);
                final File[] docList = dir.listFiles();

                final int max = docList.length;

                progressDialog.setMax(max);
                progressDialog.show();

                execService.submit(
                        new Runnable() {
                            @Override
                            public void run() {

                                try {

                                    for (int i = 0; i < max; i++) {

                                        File srcFile = docList[i];

                                        String docId = null;

                                        FormBody.Builder formBodyBuilder = new FormBody.Builder();

                                        try {

                                            final AddTokenStopWatch adStopWatch = new AddTokenStopWatch();
                                            adStopWatch.start();

                                            // the token file
                                            AddTokenResult addTokenResult = client.AddToken(srcFile, false);//client.AddTokenSuffixes(srcFile);

                                            adStopWatch.stop();
                                            adStopWatch.setName(addTokenResult.getId());
                                            adStopWatch.setWordCount(addTokenResult.getC().size());

                                            docId = addTokenResult.getId();

                                            if (enableStatRpt) {
                                                pushStat(adStopWatch, "add-token-suffix");
                                            }

                                            Log.d(TAG, "adStopWatch = " + adStopWatch.toString());

                                            String token = gson.toJson(addTokenResult);

                                            Log.d(TAG, "token = " + token);
                                            Log.d(TAG, "docId = " + docId);

                                            formBodyBuilder = formBodyBuilder.add("token", token);
                                            formBodyBuilder = formBodyBuilder.add("docId", docId);

                                            final DocEncryptStopWatch encStopWatch = new DocEncryptStopWatch(docId, srcFile.length());
                                            encStopWatch.start();

                                            saveEncFileToDrive(srcFile, docId, client);

                                            encStopWatch.stop();

                                            if (enableStatRpt) {
                                                pushStat(encStopWatch, "encrypt");
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        RequestBody requestBody = formBodyBuilder.build();

                                        Request request = new Request.Builder()
                                                .url(url)
                                                .post(requestBody)
                                                .build();

                                        //Request request = new Request.Builder().url(url,)

                                        final DocUploadStopWatch uploadStopWatch = new DocUploadStopWatch(docId, 0);

                                        httpClient.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                uploadStopWatch.stop();
                                            }

                                            @Override
                                            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                                                if (!response.isSuccessful()) {
                                                    // Handle the error
                                                    Log.i(TAG, "error when executing http request");
                                                } else {
                                                    Log.i(TAG, "http request ok");
                                                }

                                                uploadStopWatch.stop();

                                                if (enableStatRpt) {
                                                    pushStat(uploadStopWatch, "encrypt");
                                                }

                                                response.close();
                                            }
                                        });

                                        uploadStopWatch.start();

                                        dismissDialogHandler.sendEmptyMessage(i);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
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
}

