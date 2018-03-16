package org.coreocto.dev.hf.androidclient.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import org.coreocto.dev.hf.androidclient.AppConstants;
import org.coreocto.dev.hf.androidclient.R;
import org.coreocto.dev.hf.androidclient.bean.AppSettings;
import org.coreocto.dev.hf.androidclient.db.DatabaseHelper;
import org.coreocto.dev.hf.androidclient.fragment.SearchFragment;
import org.coreocto.dev.hf.androidclient.fragment.SettingsFragment;
import org.coreocto.dev.hf.androidclient.fragment.UploadFragment;
import org.coreocto.dev.hf.androidclient.util.AndroidBase64Impl;
import org.coreocto.dev.hf.androidclient.wrapper.Chlh2ClientW;
import org.coreocto.dev.hf.androidclient.wrapper.SuiseClientW;
import org.coreocto.dev.hf.androidclient.wrapper.VasstClientW;
import org.coreocto.dev.hf.commonlib.sse.suise.util.SuiseUtil;
import org.coreocto.dev.hf.commonlib.util.IBase64;
import org.coreocto.dev.hf.commonlib.util.ILogger;
import org.coreocto.dev.hf.perfmon.aspect.TraceAspect;

public class NavDwrActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchFragment.OnFragmentInteractionListener,
        UploadFragment.OnFragmentInteractionListener {

    private static final String TAG = "NavDwrActivity";
    private static final int REQUEST_CODE_SIGN_IN = 0;

    public GoogleSignInClient getGoogleSignInClient() {
        return mGoogleSignInClient;
    }

    public DriveClient getDriveClient() {
        return mDriveClient;
    }

    public DriveResourceClient getDriveResourceClient() {
        return mDriveResourceClient;
    }

    private GoogleSignInClient mGoogleSignInClient;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        Context context = NavDwrActivity.this;

        int failedCode = -1;

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

            // Use the last signed in account here since it already have a Drive scope.
            mDriveClient = Drive.getDriveClient(context, account);
            // Build a drive resource client.
            mDriveResourceClient = Drive.getDriveResourceClient(context, account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "google sign in failed, status code=" + e.getStatusCode());
            failedCode = e.getStatusCode();
        }

        if (failedCode > 0) {
            if (failedCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                new AlertDialog.Builder(context)
                        .setMessage("You must sign in your Google account to use this app.\nPlease try again.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                signIn();
                            }
                        })
                        .show();
            } else {
                new AlertDialog.Builder(context)
                        .setTitle("Error")
                        .setMessage("Sorry, we have some problem when signing you in.\nPlease try again later.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }

        }
    }

//    private FloatingActionButton fab = null;

    public Fragment replaceFragment(int id, boolean addToBackStack) {

        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        String fragmentTag = null;

//        if (fab != null && fab.getVisibility() != View.INVISIBLE) {
//            fab.setVisibility(View.INVISIBLE);
//        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_settings) {
            fragmentTag = TAG_SETTINGS_FRAGMENT;
            fragment = fragmentManager.findFragmentByTag(fragmentTag);
            if (fragment == null) {
                fragment = new SettingsFragment();
            }
        } else if (id == R.id.nav_search) {
            fragmentTag = TAG_SEARCH_FRAGMENT;
            fragment = fragmentManager.findFragmentByTag(fragmentTag);
            if (fragment == null) {
                fragment = new SearchFragment();
            }
        } else if (id == R.id.nav_upload) {
            fragmentTag = TAG_UPLOAD_FRAGMENT;
            fragment = fragmentManager.findFragmentByTag(fragmentTag);
            if (fragment == null) {
                fragment = new UploadFragment();
            }
        }

        if (fragment == null) {
            Log.e(TAG, "fragment is null, please check");
        } else {
            // Insert the fragment by replacing any existing fragment
            fragmentManager.beginTransaction()//.add(R.id.content_view, fragment)
                    .replace(R.id.content_view, fragment, fragmentTag)
                    .commit();
        }

        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_dwr);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
//            fab = (FloatingActionButton) findViewById(R.id.fab);
//            fab.setVisibility(View.INVISIBLE);
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Fragment currentFragment = getCurrentFragment();
//                    if (currentFragment instanceof CryptoTestItemFragment) {
//                        CryptoTestItemRecyclerViewAdapter adapter = (CryptoTestItemRecyclerViewAdapter) ((CryptoTestItemFragment) currentFragment).getRecyclerView().getAdapter();
//                        if (adapter.getCheckedCount() == 0) {
//                            new AlertDialog.Builder(NavDwrActivity.this).setMessage("Please select at least one scheme!!")
//                                    .setCancelable(false)
//                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.dismiss();
//                                        }
//                                    }).create().show();
//
//                        } else {
//
//                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(NavDwrActivity.this);
//                            String sRunCnt = settings.getString(AppConstants.PREF_CT_NUM_OF_EXEC, AppConstants.PREF_CT_DEFAULT_RUN_CNT);
//                            String sDataSize = settings.getString(AppConstants.PREF_CT_DATA_SIZE, AppConstants.PREF_CT_DEFAULT_DATA_SIZE);
//                            Boolean bAllocMem = settings.getBoolean(AppConstants.PREF_CT_ALLOC_MEM, false);
//                            Boolean bExplicitGc = settings.getBoolean(AppConstants.PREF_CT_EXPLICIT_GC, false);
//
//                            int runCnt = -1;
//                            int dataSize = -1;
//
//                            try {
//                                runCnt = Integer.parseInt(sRunCnt);
//                            } catch (NumberFormatException nfe) {
//                                Log.d(TAG, nfe.getMessage());
//                            }
//
//                            try {
//                                dataSize = Integer.parseInt(sDataSize);
//                            } catch (NumberFormatException nfe) {
//                                Log.d(TAG, nfe.getMessage());
//                            }
//
//                            BenchmarkParam param = new BenchmarkParam(dataSize, runCnt/*, bAllocMem, bExplicitGc*/);
//                            List<CryptoTestItem> itemList = adapter.getCheckedItems();
//                            for (CryptoTestItem item : itemList) {
//                                param.addTest(item.getSchemeName());
//                            }
//                            new BenchmarkTask(NavDwrActivity.this).execute(param);
//                        }
//                    }
//                }
//            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            //select the first fragment
            this.onNavigationItemSelected(navigationView.getMenu().getItem(0));

            SharedPreferences appPref = PreferenceManager.getDefaultSharedPreferences(NavDwrActivity.this);

            //load existing settings
            AppSettings appSettings = AppSettings.getInstance();
            appSettings.setAppPref(appPref);
            appSettings.setGson(new Gson());

            String key1 = appPref.getString(AppConstants.PREF_CLIENT_KEY1, null);
            String key2 = appPref.getString(AppConstants.PREF_CLIENT_KEY2, null);

//        String key3 = appPref.getString(AppConstants.PREF_CLIENT_KEY3, null);
//        String key4 = appPref.getString(AppConstants.PREF_CLIENT_KEY4, null);
//        String keyD = appPref.getString(AppConstants.PREF_CLIENT_KEYD, null);
//        String keyC = appPref.getString(AppConstants.PREF_CLIENT_KEYC, null);
//        String keyL = appPref.getString(AppConstants.PREF_CLIENT_KEYL, null);

            boolean statEnabled = appPref.getBoolean(AppConstants.PREF_SERVER_RPT_STAT, true);

            TraceAspect.setEnabled(statEnabled);

            boolean dataProtectEnabled = appPref.getBoolean(AppConstants.PREF_CLIENT_DATA_PROTECT, true);

            ILogger debugLogger = new ILogger() {
                @Override
                public void log(String s, String s1) {
                    Log.d(s, s1);
                }
            };

            IBase64 base64 = new AndroidBase64Impl();

            SuiseUtil suiseUtil = new SuiseUtil();

            String fprS = appPref.getString(AppConstants.PREF_CLIENT_CHLH_FPR, "0.1");
            float fpr = 0.1f;
            try {
                fpr = Float.parseFloat(fprS);
            } catch (NumberFormatException nfe) {
                Log.d(TAG, "failed to parse floating point number", nfe);
            }

            String expectDocCntS = appPref.getString(AppConstants.PREF_CLIENT_CHLH_EXPECT_DOC_CNT, "1000");
            int expectDocCnt = 1000;
            try {
                expectDocCnt = Integer.parseInt(expectDocCntS);
            } catch (NumberFormatException nfe) {
                Log.d(TAG, "failed to parse integer", nfe);
            }

            SuiseClientW suiseClient = new SuiseClientW(suiseUtil, base64);
            VasstClientW vasstClient = new VasstClientW(base64);
            Chlh2ClientW chlh2Client = new Chlh2ClientW(base64, fpr, expectDocCnt);

//        McesClient mcesClient = new McesClient(base64);

            suiseClient.setDataProtected(dataProtectEnabled);
            vasstClient.setDataProtected(dataProtectEnabled);

            if (key1 != null && !key1.isEmpty()) {
                byte[] key1Bytes = base64.decodeToByteArray(key1);
                suiseClient.setKey1(key1Bytes);
                vasstClient.setSecretKey(key1Bytes);
//            mcesClient.setK1(key1Bytes);
                chlh2Client.setSecretKey(key1Bytes);
            }

            if (key2 != null && !key2.isEmpty()) {
                byte[] key2Bytes = base64.decodeToByteArray(key2);
                suiseClient.setKey2(key2Bytes);
//            mcesClient.setK2(key2Bytes);
            }

//        if (key3!=null && !key3.isEmpty()){
//            byte[] key3Bytes = registry.getBase64().decodeToByteArray(key3);
//            mcesClient.setK3(key3Bytes);
//        }
//
//        if (key4!=null && !key4.isEmpty()){
//            byte[] key4Bytes = registry.getBase64().decodeToByteArray(key4);
//            mcesClient.setK4(key4Bytes);
//        }
//
//        if (keyD!=null && !keyD.isEmpty()){
//            byte[] keyDBytes = registry.getBase64().decodeToByteArray(keyD);
//            mcesClient.setKd(keyDBytes);
//        }
//
//        if (keyC!=null && !keyC.isEmpty()){
//            byte[] keyCBytes = registry.getBase64().decodeToByteArray(keyC);
//            mcesClient.setKc(keyCBytes);
//        }
//
//        if (keyL!=null && !keyL.isEmpty()){
//            byte[] keyLBytes = registry.getBase64().decodeToByteArray(keyL);
//            mcesClient.setKl(keyLBytes);
//        }

            appSettings.setSuiseClient(suiseClient);
            appSettings.setVasstClient(vasstClient);
//        appSettings.setMcesClient(mcesClient);
            appSettings.setChlh2Client(chlh2Client);
            //end

//        byte[] defaultIv = new byte[16];
//        appSettings.getSuiseClient().setIv1(defaultIv);
//        appSettings.getSuiseClient().setIv2(defaultIv);
//        appSettings.getVasstClient().setIv(defaultIv);

            DatabaseHelper databaseHelper = new DatabaseHelper(this, AppConstants.LOCAL_APP_DB, null, 1);
            appSettings.setDatabaseHelper(databaseHelper);

            this.signIn();
        }
    }

    /**
     * Start sign in activity.
     */
    private void signIn() {
        Log.i(TAG, "Start sign in");
        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    /**
     * Build a Google SignIn client.
     */
    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_dwr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_signout) {
            new AlertDialog.Builder(this)
                    //.setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Confirm Sign Out")
                    .setMessage("Are you sure you want to sign out from this app?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mGoogleSignInClient.signOut();
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    private static final String TAG_SETTINGS_FRAGMENT = "SettingsFragment";
    private static final String TAG_SEARCH_FRAGMENT = "SearchFragment";
    private static final String TAG_UPLOAD_FRAGMENT = "UploadFragment";

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        this.replaceFragment(id, false);

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
