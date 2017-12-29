package org.coreocto.dev.hf.androidclient.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import okhttp3.*;
import org.coreocto.dev.hf.androidclient.AppConstants;
import org.coreocto.dev.hf.androidclient.R;
import org.coreocto.dev.hf.androidclient.activity.NavDwrActivity;
import org.coreocto.dev.hf.androidclient.bean.AppSettings;
import org.coreocto.dev.hf.androidclient.bean.FileInfo;
import org.coreocto.dev.hf.androidclient.bean.SearchResponse;
import org.coreocto.dev.hf.androidclient.view.SearchResultAdapter;
import org.coreocto.dev.hf.clientlib.suise.SuiseClient;
import org.coreocto.dev.hf.clientlib.vasst.VasstClient;
import org.coreocto.dev.hf.commonlib.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private OnFragmentInteractionListener mListener;
    private EditText etKeyword = null;
    private Button bSearch = null;
    private ListView lvFileList = null;
    private ArrayAdapter<FileInfo> arrayAdapter = null;
    private List<FileInfo> fileList = null;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final NavDwrActivity ctx = (NavDwrActivity) getActivity();

        final AppSettings appSettings = AppSettings.getInstance();

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        this.etKeyword = (EditText) view.findViewById(R.id.etKeyword);
        this.bSearch = (Button) view.findViewById(R.id.bSearch);
        this.lvFileList = (ListView) view.findViewById(R.id.lvFileList);

        this.fileList = new ArrayList<>();

        this.arrayAdapter = new SearchResultAdapter(ctx, fileList);

        final SuiseClient client = appSettings.getSuiseClient();
        final VasstClient vasstClient = appSettings.getVasstClient();

        lvFileList.setAdapter(arrayAdapter);
        lvFileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileInfo fileInfo = arrayAdapter.getItem(position);
                final String docId = fileInfo.getName();
                final int fileType = fileInfo.getType();

                Log.d(TAG, "docId: " + docId);

                SQLiteDatabase database = appSettings.getDatabaseHelper().getReadableDatabase();
                Cursor c = database.rawQuery("select cremoteid from " + AppConstants.TABLE_REMOTE_DOCS + " where cremotename=?", new String[]{docId});

                boolean recExists = false;
                String remoteId = null;

                while (c.moveToNext()) {
                    remoteId = c.getString(c.getColumnIndex("cremoteid"));
                }

                c.close();

                Log.d(TAG, "remoteId: " + remoteId);

                DriveId driveId = DriveId.decodeFromString(remoteId);

                DriveFile driveFile = driveId.asDriveFile();

                final DriveResourceClient driveResourceClient = ctx.getDriveResourceClient();

                Task<DriveContents> openFileTask =
                        driveResourceClient.openFile(driveFile, DriveFile.MODE_READ_ONLY);

                openFileTask
                        .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                            @Override
                            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                                DriveContents contents = task.getResult();

                                File extStor = Environment.getExternalStorageDirectory();
                                File dataDir = new File(extStor, AppConstants.LOCAL_APP_FOLDER);
                                if (!dataDir.exists()) {
                                    dataDir.mkdir();
                                }

                                // Process contents...
                                // copy file content to temp file
                                File tempFile = File.createTempFile("hfac", AppConstants.FILE_EXT_ENCRYPTED, dataDir);
                                BufferedOutputStream os = null;
                                BufferedInputStream is = null;
                                try {
                                    os = new BufferedOutputStream(new FileOutputStream(tempFile));
                                    is = new BufferedInputStream(contents.getInputStream());
                                    int data = -1;
                                    while ((data = is.read()) != -1) {
                                        os.write(data);
                                    }
                                } catch (IOException e) {
                                    Log.e(TAG, "error when copy file to temp storage", e);
                                }
                                if (is != null) {
                                    try {
                                        is.close();
                                    } catch (IOException e) {
                                    }
                                }
                                if (os != null) {
                                    try {
                                        os.close();
                                    } catch (IOException e) {
                                    }
                                }
                                // end copy file content to temp file

                                // decrypt the file
                                File decFile = new File(dataDir, docId + AppConstants.FILE_EXT_DECRYPTED);

                                client.Dec(tempFile, decFile);
                                // end decrypt the file

                                // display the file to user
                                Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                                if (fileType == Constants.FILE_TYPE_PDF) {
                                    viewIntent.setDataAndType(Uri.fromFile(decFile), Constants.MIME_TYPE_PDF);
                                } else if (fileType == Constants.FILE_TYPE_DOC) {
                                    viewIntent.setDataAndType(Uri.fromFile(decFile), Constants.MIME_TYPE_DOC);
                                } else {
                                    viewIntent.setDataAndType(Uri.fromFile(decFile), "text/plain");
                                }
                                try {
                                    startActivity(viewIntent);
                                } catch (Exception ex) {
                                    Log.e(TAG, "unable to open file: " + tempFile.getAbsolutePath());
                                }
                                // end display the file to user

                                Task<Void> discardTask = driveResourceClient.discardContents(contents);
                                return discardTask;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                                Log.e(TAG, "failed to get document from google drive", e);
                            }
                        });


//                if (recExists){
//                    DriveId driveId = new DriveId();
//                }

            }

//            private void retrieveContents(DriveFile file) {
//                // [START read_with_progress_listener]
//                OpenFileCallback openCallback = new OpenFileCallback() {
//                    @Override
//                    public void onProgress(long bytesDownloaded, long bytesExpected) {
//                        // Update progress dialog with the latest progress.
//                        int progress = (int) (bytesDownloaded * 100 / bytesExpected);
//                        Log.d(TAG, String.format("Loading progress: %d percent", progress));
//                        mProgressBar.setProgress(progress);
//                    }
//
//                    @Override
//                    public void onContents(@NonNull DriveContents driveContents) {
//                        // onProgress may not be called for files that are already
//                        // available on the device. Mark the progress as complete
//                        // when contents available to ensure status is updated.
//                        mProgressBar.setProgress(100);
//                        // Read contents
//                        // [START_EXCLUDE]
//                        try {
//                            try (BufferedReader reader = new BufferedReader(
//                                    new InputStreamReader(driveContents.getInputStream()))) {
//                                StringBuilder builder = new StringBuilder();
//                                String line;
//                                while ((line = reader.readLine()) != null) {
//                                    builder.append(line);
//                                }
//                                showMessage(getString(R.string.content_loaded));
//                                mFileContents.setText(builder.toString());
//                                getDriveResourceClient().discardContents(driveContents);
//                            }
//                        } catch (IOException e) {
//                            onError(e);
//                        }
//                        // [END_EXCLUDE]
//                    }
//
//                    @Override
//                    public void onError(@NonNull Exception e) {
//                        // Handle error
//                        // [START_EXCLUDE]
//                        Log.e(TAG, "Unable to read contents", e);
//                        showMessage(getString(R.string.read_failed));
//                        finish();
//                        // [END_EXCLUDE]
//                    }
//                };
//
//                getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY, openCallback);
//                // [END read_with_progress_listener]
//            }

        });


        final Gson gson = appSettings.getGson();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder = builder.connectTimeout(120, TimeUnit.SECONDS);
        builder = builder.readTimeout(120, TimeUnit.SECONDS);
        builder = builder.writeTimeout(120, TimeUnit.SECONDS);

        final OkHttpClient httpClient = builder.build();

        final FragmentActivity activity = getActivity();

        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
//                if (msg.what == 0) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                    builder.setTitle("Search result")
//                            .setMessage("No documents were found.")
//                            .setCancelable(false)
//                            .setPositiveButton("OK", null)
//                            .show();
//                } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Search result")
                        .setMessage(msg.what + " documents were found.")
                        .setCancelable(false)
                        .setPositiveButton("OK", null)
                        .show();
//                }
                arrayAdapter.notifyDataSetChanged();
            }
        };

        this.bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String sseType = appSettings.getAppPref().getString(AppConstants.PREF_CLIENT_SSE_TYPE, AppConstants.PREF_CLIENT_SSE_TYPE_SUISE);

                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                formBodyBuilder = formBodyBuilder.add("type", sseType);


                String token = null;

                if (sseType.equalsIgnoreCase(AppConstants.PREF_CLIENT_SSE_TYPE_SUISE)) {
                    token = client.SearchToken(etKeyword.getText().toString()).getSearchToken();//ClientUtil.encryptStr(client.getKey1(), etKeyword.getText().toString());

                    formBodyBuilder = formBodyBuilder.add("q", token);
                    formBodyBuilder = formBodyBuilder.add("st", Constants.SSE_TYPE_SUISE + "");

                } else if (sseType.equalsIgnoreCase(AppConstants.PREF_CLIENT_SSE_TYPE_VASST)) {
                    String searchStr = etKeyword.getText().toString();

                    // TODO: need to think of a method to ensure same x when performing search
                    byte x = vasstClient.getSecretKey()[0];

                    /*
                    // we need to provide the "x" in order to get correct result
                    SQLiteDatabase database = appSettings.getDatabaseHelper().getReadableDatabase();
                    Cursor c = database.rawQuery("select cx from "+AppConstants.TABLE_REMOTE_DOCS+" where cremotename=?", new String[]{docId});

                    boolean recExists = false;
                    String remoteId = null;

                    while (c.moveToNext()){
                        remoteId = c.getString(c.getColumnIndex("cremoteid"));
                    }

                    c.close();

                    Log.d(TAG, "remoteId: "+remoteId);

                    byte x = 0;
                    */

                    List<String> encTokens = vasstClient.CreateReq(searchStr, x);

                    for (int i = encTokens.size() - 1; i >= 0; i--) {
                        String encToken = encTokens.get(i);
                        formBodyBuilder = formBodyBuilder.add("q", encToken);
                    }

                    formBodyBuilder = formBodyBuilder.add("st", Constants.SSE_TYPE_VASST + "");

                } else {
                    throw new UnsupportedOperationException();
                }

                RequestBody requestBody = formBodyBuilder.build();

                final String url = appSettings.getAppPref().getString(AppConstants.PREF_SERVER_HOSTNAME, null) + "/search";

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

//                final SearchStopWatch searchStopWatch = new SearchStopWatch();
//                searchStopWatch.start();

                httpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
//                        searchStopWatch.stop();
                        Log.e(TAG, "call failed");
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            // Handle the error
                            Log.i(TAG, "error when executing query");
                        } else {
                            Log.i(TAG, "http request ok");

                            String respStr = response.body().string();

                            Log.d(TAG, respStr);

                            SearchResponse searchResponse = null;

                            try {
                                searchResponse = gson.fromJson(respStr, SearchResponse.class);
                            } catch (Exception ex) {
                                Log.e(TAG, "error when parsing response into object");
                                ex.printStackTrace();
                            }

                            fileList.clear();

                            if (searchResponse != null) {
                                Log.d(TAG, "status = " + searchResponse.getStatus());
                                Log.d(TAG, "count = " + searchResponse.getCount());
                                Log.d(TAG, "files = " + searchResponse.getFiles());

                                if (searchResponse.getFiles() != null && searchResponse.getFiles().size() > 0) {
                                    fileList.addAll(searchResponse.getFiles());
                                }
                            }

                            mHandler.sendEmptyMessage(fileList.size());
                        }

//                        searchStopWatch.stop();

//                        if (enableStatRpt) {
//                            pushStat(uploadStopWatch, "encrypt");
//                        }

                        response.close();

                        //appDb.insert(BenchmarkReaderContract.BenchmarkEntry.TABLE_NAME, null, uploadStopWatch.getContentValues());
                        // Upload successful
                    }
                });

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
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
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

