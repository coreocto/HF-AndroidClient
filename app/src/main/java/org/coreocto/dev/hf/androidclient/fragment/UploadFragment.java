package org.coreocto.dev.hf.androidclient.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.*;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.events.ChangeEvent;
import com.google.android.gms.drive.events.OnChangeListener;
import com.google.android.gms.tasks.*;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.commons.io.FilenameUtils;
import org.coreocto.dev.hf.androidclient.AppConstants;
import org.coreocto.dev.hf.androidclient.R;
import org.coreocto.dev.hf.androidclient.activity.NavDwrActivity;
import org.coreocto.dev.hf.androidclient.bean.AppSettings;
import org.coreocto.dev.hf.androidclient.bean.UploadItem;
import org.coreocto.dev.hf.androidclient.benchmark.AddTokenStopWatch;
import org.coreocto.dev.hf.androidclient.benchmark.DocEncryptStopWatch;
import org.coreocto.dev.hf.androidclient.benchmark.IndexUploadStopWatch;
import org.coreocto.dev.hf.androidclient.db.DatabaseHelper;
import org.coreocto.dev.hf.androidclient.parser.DocFileParserImpl;
import org.coreocto.dev.hf.androidclient.parser.PdfFileParserImpl;
import org.coreocto.dev.hf.androidclient.util.NetworkUtil;
import org.coreocto.dev.hf.androidclient.view.UploadItemArrayAdapter;
import org.coreocto.dev.hf.clientlib.parser.IFileParser;
import org.coreocto.dev.hf.clientlib.parser.TxtFileParserImpl;
import org.coreocto.dev.hf.clientlib.suise.SuiseClient;
import org.coreocto.dev.hf.clientlib.vasst.VasstClient;
import org.coreocto.dev.hf.commonlib.Constants;
import org.coreocto.dev.hf.commonlib.suise.bean.AddTokenResult;
import org.coreocto.dev.hf.commonlib.vasst.bean.TermFreq;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploadFragment extends Fragment {
    public static final ExecutorService execService = Executors.newSingleThreadExecutor();
    private static final String TAG = "UploadFragment";

    private AddFragment.OnFragmentInteractionListener mListener = null;
    private ListView lvProcessQueue = null;
    private Button bAddFile = null;
    private Button bProcessQueue = null;
    private UploadItemArrayAdapter arrayAdapter = null;
    private List<UploadItem> processList = null;
//    private GoogleApiClient mGoogleApiClient = null;

    public UploadFragment() {
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

    private GoogleSignInClient mGoogleSignInClient;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        lvProcessQueue = (ListView) view.findViewById(R.id.lvUploadFileList);
        bAddFile = (Button) view.findViewById(R.id.bAddFile);
        bProcessQueue = (Button) view.findViewById(R.id.bProcessQueue);

        final Context ctx = getActivity();
        final Activity activity = getActivity();

        final AppSettings appSettings = AppSettings.getInstance();

        mGoogleSignInClient = ((NavDwrActivity) ctx).getGoogleSignInClient();
        mDriveClient = ((NavDwrActivity) ctx).getDriveClient();
        mDriveResourceClient = ((NavDwrActivity) ctx).getDriveResourceClient();

        this.processList = new ArrayList<>();

        this.arrayAdapter = new UploadItemArrayAdapter(ctx, processList);

        lvProcessQueue.setAdapter(arrayAdapter);

        final Gson gson = appSettings.getGson();

        final DatabaseHelper databaseHelper = appSettings.getDatabaseHelper();

        bProcessQueue.setOnClickListener(new View.OnClickListener() {

            private ProgressDialog progressDialog = null;
            private Handler dismissDialogHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法

                    if (msg.what == AppConstants.ERR_CANNOT_CONNECT_SERVER) {
                        progressDialog.dismiss();// 关闭ProgressDialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setTitle("Error")
                                .setMessage("Cannot connect to server.")
                                .setCancelable(false)
                                .setPositiveButton("OK", null)
                                .show();
                    } else {

                        int max = progressDialog.getMax();
                        Log.d(TAG, "msg.what = " + msg.what);
//                    Log.d(TAG, "progressDialog.max = " + max);
                        if (msg.what + 1 >= max) {
                            progressDialog.dismiss();// 关闭ProgressDialog
                        } else {
                            progressDialog.setMessage("Uploading documents (" + (msg.what + 1) + "/" + max + "), please wait...");
                            progressDialog.setProgress(msg.what);
                        }
                    }
                }
            };

            private OkHttpClient httpClient = new OkHttpClient();

            private void pushStat(Object obj, String type) {

                final String statUrl = appSettings.getAppPref().getString(AppConstants.PREF_SERVER_HOSTNAME, null) + "/" + AppConstants.REQ_STAT_URL;

                RequestBody requestBody = new FormBody.Builder()
                        .add("data", gson.toJson(obj))
                        .add("type", type).build();

                Request request = new Request.Builder()
                        .url(statUrl)
                        .post(requestBody)
                        .build();

                httpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "error when pushing statistics to server");
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            // Handle the error
                            Log.e(TAG, "response.isSuccessful() = false");
                        }

                        response.close();
                    }
                });
            }

            private long getFileSize(Uri docUri) {
                long result = -1;
                Cursor returnCursor = ctx.getContentResolver().query(docUri, null, null, null, null);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                result = returnCursor.getLong(sizeIndex);
                returnCursor.close();
                return result;
            }

            private void saveEncFileToDrive(final Uri docUri, final String docId, final SuiseClient suiseClient, final boolean enableStatRpt) {

                final Task<DriveFolder> rootFolderTask = mDriveResourceClient.getRootFolder();
                final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();

                Tasks.whenAll(rootFolderTask, createContentsTask)
                        .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                            @Override
                            public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                                DriveFolder parent = rootFolderTask.getResult();
                                DriveContents contents = createContentsTask.getResult();
                                OutputStream outputStream = contents.getOutputStream();

                                final DocEncryptStopWatch encStopWatch = new DocEncryptStopWatch(docId, getFileSize(docUri));
                                encStopWatch.start();

                                InputStream inputStream = null;
                                try {
                                    inputStream = ctx.getContentResolver().openInputStream(docUri);

                                    suiseClient.Enc(inputStream, outputStream);

                                } catch (Exception e1) {
                                    Log.e(TAG, "Unable to write file contents.");
                                }

                                encStopWatch.stop();

                                if (enableStatRpt) {
                                    pushStat(encStopWatch, AppConstants.SW_TYPE_ENCRYPT);
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

                                //Create the initial metadata - MIME type and title.
                                //Note that the user will be able to change the title later.
                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setMimeType("application/octet-stream").setTitle(docId).build();

                                return mDriveResourceClient.createFile(parent, changeSet, contents);
                            }
                        }).addOnSuccessListener(activity, new OnSuccessListener<DriveFile>() {
                    @Override
                    public void onSuccess(DriveFile driveFile) {
                        Log.d(TAG, "onSuccess(), " + driveFile.getDriveId());

                        mDriveResourceClient.addChangeListener(driveFile, new OnChangeListener() {

                            /**
                             * A listener to handle file change events.
                             */
                            @Override
                            public void onChange(ChangeEvent changeEvent) {
                                Log.d(TAG, "docId: " + docId);
                                Log.d(TAG, "onChange(), " + changeEvent.getDriveId() + ", resourceId: " + changeEvent.getDriveId().getResourceId());

                                {
                                    ContentValues values = new ContentValues();
                                    values.put("cremoteid", changeEvent.getDriveId().encodeToString());
                                    long affectedRows = databaseHelper.getWritableDatabase().update(AppConstants.TABLE_REMOTE_DOCS, values, "cremotename=?", new String[]{docId});
                                    Log.d(TAG, "affectedRows(update): " + affectedRows);
                                }
                            }
                        });
                    }
                })
                        .addOnFailureListener(activity, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "unable to create file", e);
                            }
                        });

            }

            @Override
            public void onClick(View v) {

                if (!NetworkUtil.isNetworkConnected(ctx)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle("Error")
                            .setMessage("No network connection.")
                            .setCancelable(false)
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }

                final String hostname = appSettings.getAppPref().getString(AppConstants.PREF_SERVER_HOSTNAME, null);
                final String datadir = appSettings.getAppPref().getString(AppConstants.PREF_CLIENT_DATA_DIR, null);
                final String ssetype = appSettings.getAppPref().getString(AppConstants.PREF_CLIENT_SSE_TYPE, AppConstants.PREF_CLIENT_SSE_TYPE_SUISE);

                progressDialog = new ProgressDialog(ctx, ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("Uploading...");
                progressDialog.setMessage("Uploading documents, please wait...");

                // newly added code
                progressDialog.setCancelable(false);
                // end

                //moved variable into instance variable so that every method can access directly
                //final OkHttpClient httpClient = new OkHttpClient();

                final SuiseClient client = appSettings.getSuiseClient();
                final VasstClient vasstClient = appSettings.getVasstClient();

                final String extStore = Environment.getExternalStorageDirectory().toString();

                final String url = hostname + "/" + AppConstants.REQ_UPLOAD_URL;
                final String pingUrl = hostname + "/" + AppConstants.REQ_PING_URL;

                final boolean enableStatRpt = appSettings.getAppPref().getBoolean(AppConstants.PREF_SERVER_RPT_STAT, false);

                final int max = processList.size();

                progressDialog.setMax(max);
                progressDialog.show();

                execService.submit(
                        new Runnable() {
                            @Override
                            public void run() {

                                boolean pingOk = true;

                                //add service check to server application before doing anything
                                try {
                                    Request pingRequest = new Request.Builder().url(pingUrl).build();
                                    httpClient.newCall(pingRequest).execute();
                                } catch (IOException ex) {
                                    Log.e(TAG, "error when ping web server", ex);
                                    pingOk = false;
                                }

                                if (!pingOk) {
                                    dismissDialogHandler.sendEmptyMessage(AppConstants.ERR_CANNOT_CONNECT_SERVER);
                                    return;
                                }
                                //end of service check


                                for (int i = 0; i < max; i++) {

                                    UploadItem uploadItem = processList.get(i);

                                    if (uploadItem.getStatus() == UploadItem.Status.FINISHED) {
                                        continue;
                                    }

                                    try {

                                        Uri docUri = uploadItem.getUri();

                                        String docId = null;

                                        FormBody.Builder formBodyBuilder = new FormBody.Builder();

                                        IFileParser fileParser = null;
                                        String ext = FilenameUtils.getExtension(docUri.getPath());
                                        if ("pdf".equalsIgnoreCase(ext)) {
                                            fileParser = new PdfFileParserImpl();
                                            formBodyBuilder.add("ft", Constants.FILE_TYPE_PDF + "");
                                        } else if ("doc".equalsIgnoreCase(ext)) {
                                            fileParser = new DocFileParserImpl();
                                            formBodyBuilder.add("ft", Constants.FILE_TYPE_DOC + "");
                                        } else {
                                            fileParser = new TxtFileParserImpl();
                                            formBodyBuilder.add("ft", Constants.FILE_TYPE_TEXT + "");
                                        }

                                        try {

                                            docId = UUID.randomUUID().toString();

                                            {
                                                ContentValues values = new ContentValues();
                                                values.put("cremotename", docId);
                                                long id = databaseHelper.getWritableDatabase().insert(AppConstants.TABLE_REMOTE_DOCS, null, values);
                                                Log.d(TAG, "insert: id = " + id);
                                            }

                                            // moved measure code into saveEncFileToDrive
                                            //final DocEncryptStopWatch encStopWatch = new DocEncryptStopWatch(docId, srcFile.length());
                                            //encStopWatch.start();

                                            saveEncFileToDrive(docUri, docId, client, enableStatRpt);

                                            if (ssetype.equalsIgnoreCase(AppConstants.PREF_CLIENT_SSE_TYPE_SUISE)) {

                                                formBodyBuilder.add("st", Constants.SSE_TYPE_SUISE + "");

                                                // begin of create search token
                                                final AddTokenStopWatch adStopWatch = new AddTokenStopWatch();
                                                adStopWatch.start();

                                                // the token file
                                                AddTokenResult addTokenResult = client.AddToken(ctx.getContentResolver().openInputStream(docUri), false, docId, fileParser);

                                                adStopWatch.stop();
                                                adStopWatch.setName(addTokenResult.getId());
                                                adStopWatch.setWordCount(addTokenResult.getC().size());

                                                //docId = addTokenResult.getId();

                                                if (enableStatRpt) {
                                                    pushStat(adStopWatch, AppConstants.SW_TYPE_ADD_TOKEN);
                                                }
                                                // end of create search token

//                                            Log.d(TAG, "adStopWatch = " + adStopWatch.toString());

                                                String token = gson.toJson(addTokenResult);

//                                            Log.d(TAG, "token = " + token);
//                                            Log.d(TAG, "docId = " + docId);

                                                formBodyBuilder = formBodyBuilder.add("token", token);
                                                formBodyBuilder = formBodyBuilder.add("docId", docId);
                                            } else if (ssetype.equalsIgnoreCase(AppConstants.PREF_CLIENT_SSE_TYPE_VASST)) {

                                                formBodyBuilder.add("st", Constants.SSE_TYPE_VASST + "");

                                                // TODO: need to think of a method to ensure same x when performing search
                                                // current workaround, use the first byte of the secret key

                                                byte x = vasstClient.getSecretKey()[0]; //(byte)(Math.random()*127);

                                                {
                                                    ContentValues values = new ContentValues();
                                                    values.put("cx", x);
                                                    long affectedRows = databaseHelper.getWritableDatabase().update(AppConstants.TABLE_REMOTE_DOCS, values, "cremotename=?", new String[]{docId});
                                                    Log.d(TAG, "affectedRows(update): " + affectedRows);
                                                }

                                                final AddTokenStopWatch adStopWatch = new AddTokenStopWatch();
                                                adStopWatch.start();

                                                TermFreq termFreq = vasstClient.Preprocessing(ctx.getContentResolver().openInputStream(docUri), x, fileParser);

                                                adStopWatch.stop();
                                                adStopWatch.setName(docId);
                                                adStopWatch.setWordCount(termFreq.getTerms().size());   //this is not the actual size, will modify it later

                                                String terms = gson.toJson(termFreq);

                                                formBodyBuilder = formBodyBuilder.add("terms", terms);
                                                formBodyBuilder = formBodyBuilder.add("docId", docId);
                                            }

                                        } catch (Exception e) {
                                            Log.e(TAG, "error when performing cycle", e);
                                        }

                                        RequestBody requestBody = formBodyBuilder.build();

                                        Request request = new Request.Builder()
                                                .url(url)
                                                .post(requestBody)
                                                .build();

                                        final IndexUploadStopWatch uploadStopWatch = new IndexUploadStopWatch(docId, 0);

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

                                        uploadItem.setStatus(UploadItem.Status.FINISHED);

                                    } catch (Exception e) {
                                        Log.e(TAG, "error when uploading documents/creating index", e);
                                        uploadItem.setStatus(UploadItem.Status.ERROR);

                                    } finally {
                                        activity.runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                arrayAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }

                                }

                            }
                        }
                );
            }
        });

        bAddFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performFileSelection();
                Log.d(TAG, "after: performFileSelection()");
            }
        });

        return view;
    }

    private static final int READ_REQUEST_CODE = 42;

    private static final String[] mimeTypes =
            {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                    "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                    "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                    "text/plain",
                    "application/pdf",
                    "application/zip"};

    public void performFileSelection() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
//                Uri uri = resultData.getData();
//                Log.i(TAG, "Uri: " + uri.toString());
//                //showImage(uri);
//
//                //processList.add(new File(uri.getPath()).getAbsolutePath());
//                processList.add(uri);
//                arrayAdapter.notifyDataSetChanged();

                ClipData clipData = resultData.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item path = clipData.getItemAt(i);
                        UploadItem uploadItem = new UploadItem();
                        uploadItem.setUri(path.getUri());
                        uploadItem.setStatus(UploadItem.Status.PENDING);
                        processList.add(uploadItem);
                        Log.i(TAG, "Path: " + path.toString());
                    }
                } else {
                    Uri path = resultData.getData();
                    Log.i(TAG, "Path: " + path.toString());
                    UploadItem uploadItem = new UploadItem();
                    uploadItem.setUri(path);
                    uploadItem.setStatus(UploadItem.Status.PENDING);
                    processList.add(uploadItem);
                }
                arrayAdapter.notifyDataSetChanged();
            }
        }
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
        if (context instanceof AddFragment.OnFragmentInteractionListener) {
            mListener = (AddFragment.OnFragmentInteractionListener) context;
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
