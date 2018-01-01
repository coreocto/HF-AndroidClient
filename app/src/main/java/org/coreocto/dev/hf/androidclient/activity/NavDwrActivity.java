package org.coreocto.dev.hf.androidclient.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
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
import android.view.View;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.gson.Gson;
import org.coreocto.dev.hf.androidclient.AppConstants;
import org.coreocto.dev.hf.androidclient.R;
import org.coreocto.dev.hf.androidclient.bean.AppSettings;
import org.coreocto.dev.hf.androidclient.benchmark.BenchmarkParam;
import org.coreocto.dev.hf.androidclient.benchmark.BenchmarkTask;
import org.coreocto.dev.hf.androidclient.crypto.AndroidAes128CbcImpl;
import org.coreocto.dev.hf.androidclient.crypto.AndroidMd5Impl;
import org.coreocto.dev.hf.androidclient.db.DatabaseHelper;
import org.coreocto.dev.hf.androidclient.fragment.*;
import org.coreocto.dev.hf.androidclient.fragment.cryptotest.ChartResultFragment;
import org.coreocto.dev.hf.androidclient.fragment.cryptotest.CryptoTestItem;
import org.coreocto.dev.hf.androidclient.fragment.cryptotest.CryptoTestItemFragment;
import org.coreocto.dev.hf.androidclient.fragment.cryptotest.CryptoTestItemRecyclerViewAdapter;
import org.coreocto.dev.hf.androidclient.util.AndroidBase64Impl;
import org.coreocto.dev.hf.clientlib.suise.SuiseClient;
import org.coreocto.dev.hf.clientlib.vasst.VasstClient;
import org.coreocto.dev.hf.commonlib.suise.util.SuiseUtil;
import org.coreocto.dev.hf.commonlib.util.ILogger;
import org.coreocto.dev.hf.commonlib.util.Registry;

import java.util.List;

public class NavDwrActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AddFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        TestFragment.OnFragmentInteractionListener,
        CryptoTestItemFragment.OnListFragmentInteractionListener,
        ChartResultFragment.OnFragmentInteractionListener,
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
            Log.i(TAG, "Signed in successfully.");
            // Use the last signed in account here since it already have a Drive scope.
            mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
            // Build a drive resource client.
            mDriveResourceClient = Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
        }
    }

    private FloatingActionButton fab = null;

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.content_view);
    }

    public Fragment goToFragment(int id, boolean addToBackStack) {

        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;

        if (fab.getVisibility() != View.INVISIBLE) {
            fab.setVisibility(View.INVISIBLE);
        }

        if (id == R.id.nav_settings) {
            fragmentClass = SettingsFragment.class;
        } else if (id == R.id.nav_search) {
            fragmentClass = SearchFragment.class;
        } else if (id == R.id.nav_add) {
            fragmentClass = AddFragment.class;
        }
//        else if (id == R.id.nav_log) {
//            fragmentClass = LogFragment.class;
//        }
        else if (id == R.id.nav_test) {
            fragmentClass = TestFragment.class;
        } else if (id == R.id.nav_crypto_test) {
            fragmentClass = CryptoTestItemFragment.class;
            fab.setVisibility(View.VISIBLE);
        } else if (id == AppConstants.FRAGMENT_CHART_RESULT) {
            fragmentClass = ChartResultFragment.class;
        } else if (id == R.id.nav_upload) {
            fragmentClass = UploadFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fragment == null) {
            Log.e("error", "fragment is null, please check");
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_view, fragment).commit();

        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_dwr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment currentFragment = getCurrentFragment();
                if (currentFragment instanceof CryptoTestItemFragment) {
                    CryptoTestItemRecyclerViewAdapter adapter = (CryptoTestItemRecyclerViewAdapter) ((CryptoTestItemFragment) currentFragment).getRecyclerView().getAdapter();
                    if (adapter.getCheckedCount() == 0) {
                        new AlertDialog.Builder(NavDwrActivity.this).setMessage("Please select at least one scheme!!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                }).create().show();

                    } else {

                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(NavDwrActivity.this);
                        String sRunCnt = settings.getString(AppConstants.PREF_CT_NUM_OF_EXEC, AppConstants.PREF_CT_DEFAULT_RUN_CNT);
                        String sDataSize = settings.getString(AppConstants.PREF_CT_DATA_SIZE, AppConstants.PREF_CT_DEFAULT_DATA_SIZE);
                        Boolean bAllocMem = settings.getBoolean(AppConstants.PREF_CT_ALLOC_MEM, false);
                        Boolean bExplicitGc = settings.getBoolean(AppConstants.PREF_CT_EXPLICIT_GC, false);

                        int runCnt = -1;
                        int dataSize = -1;

                        try {
                            runCnt = Integer.parseInt(sRunCnt);
                        } catch (NumberFormatException nfe) {
                            Log.d(TAG, nfe.getMessage());
                        }

                        try {
                            dataSize = Integer.parseInt(sDataSize);
                        } catch (NumberFormatException nfe) {
                            Log.d(TAG, nfe.getMessage());
                        }

                        BenchmarkParam param = new BenchmarkParam(dataSize, runCnt/*, bAllocMem, bExplicitGc*/);
                        List<CryptoTestItem> itemList = adapter.getCheckedItems();
                        for (CryptoTestItem item : itemList) {
                            param.addTest(item.getSchemeName());
                        }
                        new BenchmarkTask(NavDwrActivity.this).execute(param);
                    }
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //select the first fragment
        this.onNavigationItemSelected(navigationView.getMenu().getItem(0));

        SharedPreferences appPref = PreferenceManager.getDefaultSharedPreferences(NavDwrActivity.this);

        //load existing settings
        AppSettings appSettings = AppSettings.getInstance();
        appSettings.setAppPref(appPref);
        appSettings.setGson(new Gson());

        String key1 = appPref.getString(AppConstants.PREF_CLIENT_KEY1, null);
        String key2 = appPref.getString(AppConstants.PREF_CLIENT_KEY2, null);

        ILogger debugLogger = new ILogger() {
            @Override
            public void log(String s, String s1) {
                Log.d(s, s1);
            }
        };

        Registry registry = new Registry();
        registry.setBase64(new AndroidBase64Impl());
        registry.setHashFunc(new AndroidMd5Impl());
        registry.setBlockCipherCbc(new AndroidAes128CbcImpl());
        registry.setLogger(debugLogger);
        appSettings.setRegistry(registry);

        SuiseUtil suiseUtil = new SuiseUtil(registry);
//        SuiseUtil suiseUtil = new SuiseUtil(new AndroidBase64Impl(), new AndroidMd5Impl(), new NativeAes128CbcImpl());    //there are memory leak problem when using the native aes impl, i will fix it later

        if ((key1 == null || key1.isEmpty()) && (key2 == null || key2.isEmpty())) {
            appSettings.setSuiseClient(new SuiseClient(registry, suiseUtil));
            appSettings.setVasstClient(new VasstClient(registry));
        } else {

            byte[] key1Bytes = registry.getBase64().decodeToByteArray(key1);
            byte[] key2Bytes = registry.getBase64().decodeToByteArray(key2);

            appSettings.setSuiseClient(new SuiseClient(registry, suiseUtil, key1Bytes, key2Bytes, null, null));

            VasstClient vasstClient = new VasstClient(registry);
            vasstClient.setSecretKey(key1Bytes);
            appSettings.setVasstClient(vasstClient);
        }
        //end

        byte[] defaultIv = new byte[16];
        appSettings.getSuiseClient().setIv1(defaultIv);
        appSettings.getSuiseClient().setIv2(defaultIv);
        appSettings.getVasstClient().setIv(defaultIv);

        DatabaseHelper databaseHelper = new DatabaseHelper(this, AppConstants.LOCAL_APP_DB, null, 1);
        appSettings.setDatabaseHelper(databaseHelper);

        this.signIn();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        this.goToFragment(id, false);

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

    @Override
    public void onListFragmentInteraction(CryptoTestItem item) {

    }
}
