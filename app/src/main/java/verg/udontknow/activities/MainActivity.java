package verg.udontknow.activities;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import verg.lib.VergLog;
import verg.lib.VolleyRequestQueue;
import verg.udontknow.R;

import static verg.lib.AppInfos.getVersionCode;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoginFragment.OnFragmentInteractionListener,
        MainFragment.OnFragmentInteractionListener,
        Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getName();

    private DrawerLayout mDrawer;
    private View mView;
    private FloatingActionButton fab;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        MobclickAgent.setDebugMode(true);

        mView = findViewById(R.id.main_container);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, LoginFragment.newInstance(null, null)).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUESTCODE && resultCode == RESULT_OK && data != null) {
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        VolleyRequestQueue.cancelAll(TAG);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            Snackbar.make(mView, "确认退出？", Snackbar.LENGTH_LONG)
                    .setAction("是的", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainActivity.super.onBackPressed();
                        }
                    }).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem menuItemSearchView = menu.findItem(R.id.menu_search);
//        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(menuItemSearchView);
        searchView = (SearchView) menuItemSearchView.getActionView();

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_manage:
//                Intent intent = new Intent(this, SettingsActivity.class);
//                startActivity(intent);
                break;
            case R.id.nav_check4updates:
                check4updates();
                break;
            case R.id.nav_share:
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void check4updates() {
        String url = "https://raw.githubusercontent.com/VergLsm/Udontknow/master/app/version.json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, this, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VergLog.v(TAG, error.toString());
            }
        });
        jsonObjectRequest.setTag(TAG);
        VolleyRequestQueue.add(this, jsonObjectRequest);
    }

    @Override
    public void onResponse(final JSONObject jsonObject) {
        int newVersion = 0;
        String url = null;
        try {
            newVersion = jsonObject.getInt("new_version");
            url = jsonObject.getString("apk_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VergLog.d(TAG, String.valueOf(newVersion));
        VergLog.d(TAG, url);
        if (getVersionCode(this) < newVersion) {
            final String finalUrl = url;
            Snackbar.make(mView, R.string.had_new_version, Snackbar.LENGTH_LONG).setAction(R.string.update, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VergLog.v(TAG, getString(R.string.update));
                    download(finalUrl);
                }
            }).show();
        } else {
            Snackbar.make(mView, R.string.no_new_version, Snackbar.LENGTH_LONG).show();
        }
    }

    protected void download(String url) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir(getString(R.string.app_name), url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("?")));
        request.setTitle(getString(R.string.app_name));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setMimeType("application/application/vnd.android.package-archive");
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        }
        Snackbar.make(mView, R.string.start_download, Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public void onFragmentInteraction(String strUri) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, MainFragment.newInstance(strUri, null)).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public FloatingActionButton getFab() {
        return fab;
    }

    public SearchView getSearchView() {
        return searchView;
    }
}
