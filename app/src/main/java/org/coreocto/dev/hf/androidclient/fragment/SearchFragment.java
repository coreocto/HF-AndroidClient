package org.coreocto.dev.hf.androidclient.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.google.gson.Gson;
import okhttp3.*;
import org.coreocto.dev.hf.androidclient.Constants;
import org.coreocto.dev.hf.androidclient.R;
import org.coreocto.dev.hf.androidclient.bean.AppSettings;
import org.coreocto.dev.hf.androidclient.bean.SearchResponse;
import org.coreocto.dev.hf.androidclient.view.SearchResultAdapter;
import org.coreocto.dev.hf.clientlib.suise.SuiseClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
    private ArrayAdapter<String> arrayAdapter = null;
    private List<String> fileList = null;

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
        final Activity ctx = getActivity();

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        this.etKeyword = (EditText) view.findViewById(R.id.etKeyword);
        this.bSearch = (Button) view.findViewById(R.id.bSearch);
        this.lvFileList = (ListView) view.findViewById(R.id.lvFileList);

        this.fileList = new ArrayList<>();

        this.arrayAdapter = new SearchResultAdapter(ctx, android.R.layout.simple_list_item_1, fileList);

        lvFileList.setAdapter(arrayAdapter);

        final AppSettings appSettings = AppSettings.getInstance();

        final Gson gson = appSettings.getGson();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder = builder.connectTimeout(120, TimeUnit.SECONDS);
        builder = builder.readTimeout(120, TimeUnit.SECONDS);
        builder = builder.writeTimeout(120, TimeUnit.SECONDS);

        final OkHttpClient httpClient = builder.build();

        final SuiseClient client = appSettings.getSuiseClient();

        final FragmentActivity activity = getActivity();

        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
                if (msg.what == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Search result")
                            .setMessage("No documents were found.")
                            .setCancelable(false)
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Search result")
                            .setMessage(msg.what + " documents were found.")
                            .setCancelable(false)
                            .setPositiveButton("OK", null)
                            .show();
                }
                arrayAdapter.notifyDataSetChanged();
            }
        };

        this.bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String token = client.SearchToken(etKeyword.getText().toString()).getSearchToken();//ClientUtil.encryptStr(client.getKey1(), etKeyword.getText().toString());

                String query = null;

                try {
                    query = URLEncoder.encode(token, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

//                RequestQueue queue = Volley.newRequestQueue(ctx);

                String queryId = "";//ClientUtil.ID();

                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                formBodyBuilder = formBodyBuilder.add("q", query);
                formBodyBuilder = formBodyBuilder.add("qid", queryId);

                RequestBody requestBody = formBodyBuilder.build();

                final String url = appSettings.getAppPref().getString(Constants.PREF_SERVER_HOSTNAME, null) + "/search";

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

                            SearchResponse searchResponse = null;

                            String respStr = response.body().string();

                            Log.d(TAG, respStr);

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

//                                            appDb.insert(BenchmarkReaderContract.BenchmarkEntry.TABLE_NAME, null, uploadStopWatch.getContentValues());
                        // Upload successful
                    }
                });

                // Request a string response from the provided URL.
//                StringRequest stringRequest = new StringRequest(Request.Method.GET, appSettings.getHostname() + "/search?q=" + query,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//
//                                SearchResponse searchResponse = null;
//
//                                try {
//                                    searchResponse = gson.fromJson(response, SearchResponse.class);
//                                } catch (Exception ex) {
//                                    Log.e("searchResponse", "error when parsing response into object", ex);
//                                }
//
//                                fileList.clear();
//
//                                if (searchResponse != null) {
//                                    Log.d("searchResponse", "status = " + searchResponse.getStatus());
//                                    Log.d("searchResponse", "count = " + searchResponse.getCount());
//                                    Log.d("searchResponse", "files = " + searchResponse.getFiles());
//
//                                    if (searchResponse.getFiles() != null && searchResponse.getFiles().size() > 0) {
//                                        fileList.addAll(searchResponse.getFiles());
//                                    }
//                                }
//
//                                arrayAdapter.notifyDataSetChanged();
//
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("Volley", "That didn't work!");
//                    }
//                });
//                // Add the request to the RequestQueue.
//                queue.add(stringRequest);
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

