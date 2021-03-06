package org.coreocto.dev.hf.androidclient.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import org.coreocto.dev.hf.androidclient.crypto.AesCbcPkcs5BcImpl;
import org.coreocto.dev.hf.androidclient.crypto.AesCbcPkcs5FcImpl;
import org.coreocto.dev.hf.androidclient.crypto.AesCtrNoPadBcImpl;
import org.coreocto.dev.hf.androidclient.crypto.HmacMd5Impl;
import org.coreocto.dev.hf.androidclient.util.AndroidBase64Impl;
import org.coreocto.dev.hf.androidclient.view.AutoCompleteAdapter;
import org.coreocto.dev.hf.androidclient.view.SearchResultAdapter;
import org.coreocto.dev.hf.androidclient.wrapper.SuiseClientW;
import org.coreocto.dev.hf.androidclient.wrapper.VasstClientW;
import org.coreocto.dev.hf.clientlib.LibConstants;
import org.coreocto.dev.hf.clientlib.sse.chlh.Chlh2Client;
import org.coreocto.dev.hf.commonlib.Constants;
import org.coreocto.dev.hf.commonlib.crypto.IByteCipher;
import org.coreocto.dev.hf.commonlib.crypto.IFileCipher;
import org.coreocto.dev.hf.commonlib.crypto.IKeyedHashFunc;
import org.coreocto.dev.hf.commonlib.util.IBase64;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private static final ExecutorService execService = Executors.newSingleThreadExecutor();

    private static final String TAG = "SearchFragment";
    private OnFragmentInteractionListener mListener;
    private AutoCompleteTextView etKeyword = null;
    private Button bSearch = null;
    private ListView lvFileList = null;
    private ArrayAdapter<FileInfo> arrayAdapter = null;
    private List<FileInfo> fileList = null;

    private ProgressDialog progressDialog = null;

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
        this.etKeyword = (AutoCompleteTextView) view.findViewById(R.id.etKeyword);
        this.etKeyword.setThreshold(1);
        this.etKeyword.setAdapter(new AutoCompleteAdapter(ctx, android.R.layout.simple_dropdown_item_1line));

        this.bSearch = (Button) view.findViewById(R.id.bSearch);
        this.lvFileList = (ListView) view.findViewById(R.id.lvFileList);

        this.fileList = new ArrayList<>();

        this.arrayAdapter = new SearchResultAdapter(ctx, fileList);

        final SuiseClientW client = appSettings.getSuiseClient();
        final VasstClientW vasstClient = appSettings.getVasstClient();
//        final McesClient mcesClient = appSettings.getMcesClient();
        final Chlh2Client chlh2Client = appSettings.getChlh2Client();

        progressDialog = new ProgressDialog(ctx, ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Downloading...");
        progressDialog.setMessage("Downloading encrypted document to local storage, please wait...");
        progressDialog.setCancelable(false);

        final Gson gson = appSettings.getGson();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder = builder.connectTimeout(120, TimeUnit.SECONDS);
        builder = builder.readTimeout(120, TimeUnit.SECONDS);
        builder = builder.writeTimeout(120, TimeUnit.SECONDS);

        final OkHttpClient httpClient = builder.build();

        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法

                if (msg.what <= AppConstants.ERR_CANNOT_CONNECT_SERVER) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                    if (msg.what == AppConstants.ERR_GOOGLE_DRIVE_FILE_NOT_READY) {
                        builder.setTitle("Error")
                                .setMessage("Your file is not yet ready on Google Drive.\nPlease try again later.")
                                .setCancelable(false)
                                .setPositiveButton("OK", null);
                    } else if (msg.what == AppConstants.ERR_CANNOT_CONNECT_SERVER) {
                        builder.setTitle("Error")
                                .setMessage("Cannot connect to server.")
                                .setCancelable(false)
                                .setPositiveButton("OK", null);
                    } else if (msg.what == AppConstants.ERR_GOOGLE_DRIVE_FILE_MISSING) {
                        builder.setTitle("Error")
                                .setMessage("Cannot find the specified file from google drive.")
                                .setCancelable(false)
                                .setPositiveButton("OK", null);
                    } else if (msg.what == AppConstants.ERR_GOOGLE_DRIVE_DL_FAILED) {
                        builder.setTitle("Error")
                                .setMessage("Error occured when downloading document from Google Drive.\nPlease try again later.")
                                .setCancelable(false)
                                .setPositiveButton("OK", null);
                    }

                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle("Search result")
                            .setMessage(msg.what + " documents were found.")
                            .setCancelable(false)
                            .setPositiveButton("OK", null)
                            .show();
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        };

        lvFileList.setAdapter(arrayAdapter);
        lvFileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileInfo fileInfo = arrayAdapter.getItem(position);

                String tmpDocId = fileInfo.getName();
                final int fileType = fileInfo.getType();
                final String feivS = fileInfo.getFeiv();

                final String hostname = appSettings.getAppPref().getString(AppConstants.PREF_SERVER_HOSTNAME, null);
                final String pingUrl = hostname + "/" + AppConstants.REQ_PING_URL;
                final String sseType = appSettings.getAppPref().getString(AppConstants.PREF_CLIENT_SSE_TYPE, AppConstants.PREF_CLIENT_SSE_TYPE_SUISE);

                final String docId = tmpDocId;

                Log.d(TAG, "docId: " + docId);

                execService.submit(new Runnable() {
                    @Override
                    public void run() {

                        SQLiteDatabase database = appSettings.getDatabaseHelper().getReadableDatabase();
                        Cursor c = database.rawQuery("select cremoteid from " + AppConstants.TABLE_REMOTE_DOCS + " where cremotename=?", new String[]{docId});

                        boolean recExists = false;
                        String remoteId = null;

                        while (c.moveToNext()) {
                            recExists = true;
                            remoteId = c.getString(c.getColumnIndex("cremoteid"));
                        }

                        c.close();
                        database.close();

                        //TODO: if the upload process does not complete, this remoteId could be null.
                        if (remoteId == null && recExists) {
                            mHandler.sendEmptyMessage(AppConstants.ERR_GOOGLE_DRIVE_FILE_NOT_READY);
                            return;
                        } else if (remoteId == null) {
                            mHandler.sendEmptyMessage(AppConstants.ERR_GOOGLE_DRIVE_FILE_MISSING);
                            return;
                        }

                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.show();
                            }
                        });

                        Log.d(TAG, "remoteId: " + remoteId);

                        DriveId driveId = DriveId.decodeFromString(remoteId);

                        DriveFile driveFile = driveId.asDriveFile();

                        final DriveResourceClient driveResourceClient = ctx.getDriveResourceClient();

                        final IBase64 base64 = new AndroidBase64Impl();

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
                                        final File tempFile = File.createTempFile("hfac", AppConstants.FILE_EXT_ENCRYPTED, dataDir);
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

                                        IFileCipher fileCipher = null;

                                        byte[] iv = base64.decodeToByteArray(feivS);

                                        Map<String, String> addInfo = new HashMap<>();
                                        if (sseType.equalsIgnoreCase(AppConstants.PREF_CLIENT_SSE_TYPE_SUISE)) {
                                            fileCipher = new AesCbcPkcs5FcImpl(client.getKey2(), iv);
                                            client.Dec(tempFile, decFile, fileCipher, addInfo);
                                        } else if (sseType.equalsIgnoreCase(AppConstants.PREF_CLIENT_SSE_TYPE_VASST)) {
                                            fileCipher = new AesCbcPkcs5FcImpl(vasstClient.getSecretKey(), iv);
                                            vasstClient.Decrypt(tempFile, decFile, fileCipher, addInfo);
                                        } else if (sseType.equalsIgnoreCase(AppConstants.PREF_CLIENT_SSE_TYPE_CHLH)) {
                                            fileCipher = new AesCbcPkcs5FcImpl(chlh2Client.getSecretKey(), iv);
                                            fileCipher.decrypt(tempFile, decFile);
                                        } else {
                                            throw new UnsupportedOperationException();
                                        }
                                        // end decrypt the file

                                        // display the file to user
                                        final Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                                        if (fileType == Constants.FILE_TYPE_PDF) {
                                            viewIntent.setDataAndType(Uri.fromFile(decFile), Constants.MIME_TYPE_PDF);
                                        } else if (fileType == Constants.FILE_TYPE_DOC) {
                                            viewIntent.setDataAndType(Uri.fromFile(decFile), Constants.MIME_TYPE_DOC);
                                        } else {
                                            viewIntent.setDataAndType(Uri.fromFile(decFile), Constants.MIME_TYPE_TEXT);
                                        }

                                        ctx.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.hide();
                                            }
                                        });

                                        // display the file to user
                                        try {
                                            startActivity(viewIntent);
                                        } catch (Exception ex) {
                                            Log.e(TAG, "unable to open file: " + tempFile.getAbsolutePath(), ex);
                                        }
                                        // end

                                        Task<Void> discardTask = driveResourceClient.discardContents(contents);
                                        return discardTask;
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "failed to get document from google drive", e);
                                        mHandler.sendEmptyMessage(AppConstants.ERR_GOOGLE_DRIVE_DL_FAILED);
                                    }
                                });
                    }
                });

            }

        });

        this.bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                execService.submit(new Runnable() {
                    @Override
                    public void run() {
                        final String hostname = appSettings.getAppPref().getString(AppConstants.PREF_SERVER_HOSTNAME, null);
                        final String pingUrl = hostname + "/" + AppConstants.REQ_PING_URL;

                        boolean pingOk = true;

                        //add service check to server application before doing anything

                        Response httpResponse = null;
                        try {
                            Request pingRequest = new Request.Builder().url(pingUrl).build();
                            httpResponse = new OkHttpClient().newCall(pingRequest).execute();
                        } catch (IOException ex) {
                            Log.e(TAG, "error when ping web server", ex);
                            pingOk = false;
                        } finally {
                            if (httpResponse != null) {
                                httpResponse.close();
                            }
                        }

                        if (!pingOk) {
                            mHandler.sendEmptyMessage(AppConstants.ERR_CANNOT_CONNECT_SERVER);
                            return;
                        }
                        //end of service check

                        final String sseType = appSettings.getAppPref().getString(AppConstants.PREF_CLIENT_SSE_TYPE, AppConstants.PREF_CLIENT_SSE_TYPE_SUISE);

                        FormBody.Builder formBodyBuilder = new FormBody.Builder();
                        formBodyBuilder.add("type", sseType);

                        String token = null;

                        Map<String, String> addInfo = new HashMap<>();

                        if (sseType.equalsIgnoreCase(AppConstants.PREF_CLIENT_SSE_TYPE_SUISE) ||
                                sseType.equalsIgnoreCase(AppConstants.PREF_CLIENT_SSE_TYPE_SUISE_2) ||
                                sseType.equalsIgnoreCase(AppConstants.PREF_CLIENT_SSE_TYPE_SUISE_3)) {

                            String keyword = etKeyword.getText().toString();

                            SQLiteDatabase db = null;

                            try {
                                db = appSettings.getDatabaseHelper().getWritableDatabase();
                                String[] param = new String[]{keyword};
                                db.execSQL("delete from " + AppConstants.TABLE_AUTO_COMPLETE + " where ckeyword = ?", param);
                                db.execSQL("insert into " + AppConstants.TABLE_AUTO_COMPLETE + " (ckeyword) values (?)", param);
                            } catch (Exception e) {
                                Log.e(TAG, "error when insert records to " + AppConstants.TABLE_AUTO_COMPLETE, e);
                            }

                            if (db != null) {
                                db.close();
                            }

                            IKeyedHashFunc keyedHashFunc = new HmacMd5Impl();

                            try {
                                token = client.SearchToken(keyword, keyedHashFunc, addInfo).getSearchToken();
                            } catch (Exception e) {
                                Log.e(TAG, "error when creating search token from keyword", e);
                            }

                            formBodyBuilder.add("q", token);
                            formBodyBuilder.add("st", Constants.SSE_TYPE_SUISE + "");

                        } else if (sseType.equalsIgnoreCase(AppConstants.PREF_CLIENT_SSE_TYPE_VASST) ||
                                sseType.equalsIgnoreCase(AppConstants.PREF_CLIENT_SSE_TYPE_VASST_2) ||
                                sseType.equalsIgnoreCase(AppConstants.PREF_CLIENT_SSE_TYPE_VASST_3)) {
                            String searchStr = etKeyword.getText().toString();

                            List<Integer> list_of_x = new ArrayList<>();

                            // we need to provide the "x" in order to get correct result
                            SQLiteDatabase database = appSettings.getDatabaseHelper().getReadableDatabase();
                            Cursor c = database.rawQuery("select cx from " + AppConstants.TABLE_REMOTE_DOCS + " where 1=1", null);

                            while (c.moveToNext()) {
                                list_of_x.add(c.getInt(c.getColumnIndex("cx")));
                            }

                            c.close();

                            Log.d(TAG, "list_of_x.size()=" + list_of_x.size());

                            IByteCipher byteCipher = new AesCtrNoPadBcImpl(vasstClient.getSecretKey(), new byte[16]);

                            List<String> encTokens = new ArrayList<>();
                            int list_of_x_sz = list_of_x.size();
                            for (int i = 0; i < list_of_x_sz; i++) {
                                BigDecimal x = new BigDecimal(list_of_x.get(i));
                                try {
                                    encTokens = vasstClient.CreateReq(searchStr, x, byteCipher, addInfo);
                                } catch (Exception e) {
                                    Log.e(TAG, "error when creating search token from keyword", e);
                                }
                            }

                            for (int i = encTokens.size() - 1; i >= 0; i--) {
                                String encToken = encTokens.get(i);
                                formBodyBuilder.add("q", encToken);
                            }

                            formBodyBuilder.add("st", Constants.SSE_TYPE_VASST + "");

                        } else if (sseType.equalsIgnoreCase(AppConstants.PREF_CLIENT_SSE_TYPE_CHLH)) {
                            String searchStr = etKeyword.getText().toString();
                            List<String> trapdoors = chlh2Client.Trapdoor(searchStr);
                            for (String trapdoor : trapdoors) {
                                formBodyBuilder = formBodyBuilder.add("q", trapdoor);
                            }
                            formBodyBuilder = formBodyBuilder.add("st", Constants.SSE_TYPE_CHLH + "");
                        } else {
                            throw new UnsupportedOperationException();
                        }

                        RequestBody requestBody = formBodyBuilder.build();

                        final String url = appSettings.getAppPref().getString(AppConstants.PREF_SERVER_HOSTNAME, null) + "/search";

                        Request request = new Request.Builder()
                                .url(url)
                                .post(requestBody)
                                .build();

                        httpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e(TAG, "call failed", e);
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
                                        Log.e(TAG, "error when parsing response into object", ex);
                                    }

                                    IBase64 base64 = new AndroidBase64Impl();

                                    if (sseType.equalsIgnoreCase(AppConstants.PREF_CLIENT_SSE_TYPE_CHLH)) {
                                        List<FileInfo> fileList = searchResponse.getFiles();
                                        IByteCipher byteCipher = null;
                                        for (FileInfo fileInfo : fileList) {
                                            String tmp = fileInfo.getName();
                                            byte[] iv = base64.decodeToByteArray(fileInfo.getWeiv());
                                            byteCipher = new AesCbcPkcs5BcImpl(chlh2Client.getSecretKey(), iv);
                                            String decId = null;
                                            try {
                                                decId = new String(byteCipher.decrypt(base64.decodeToByteArray(tmp)), LibConstants.ENCODING_UTF8);
                                            } catch (Exception e) {
                                                Log.e(TAG, "error when decrypting encrypted docId", e);
                                            }
                                            if (tmp != null) {
                                                fileInfo.setName(decId);
                                            }
                                        }
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

                                response.close();
                            }
                        });
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

