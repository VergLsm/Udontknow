package verg.udontknow.activities;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.List;

import verg.lib.VergLog;
import verg.lib.VolleyRequestQueue;
import verg.udontknow.R;
import verg.udontknow.activities.main.AccountListLoader;
import verg.udontknow.activities.main.NewAdapter;
import verg.udontknow.entity.AccountEntity;
import verg.udontknow.provider.IDB;
import verg.udontknow.provider.IProvider;

import static verg.lib.AppInfos.getVersionCode;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<List<AccountEntity>>,
        View.OnClickListener,
        SearchView.OnQueryTextListener,
        NewAdapter.ItemClickListener, NewAdapter.ItemLongClickListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getName();
    public static final int REQUESTCODE = 12300;
    private RecyclerView mRecyclerView;
    private NewAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(this);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.my_swipeRefreshLayout);
        assert mSwipeRefreshLayout != null;
        mSwipeRefreshLayout.setEnabled(false);

        mAdapter = new NewAdapter(null);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                getSupportLoaderManager().initLoader(0, null, MainActivity.this);
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        MobclickAgent.setDebugMode(true);
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
            Snackbar.make(mRecyclerView, "确认退出？", Snackbar.LENGTH_LONG)
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
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(menuItemSearchView);
        mSearchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Don't care about this.
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed.  Since this
        // is a simple array adapter, we can just have it do the filtering.
//        Log.d(TAG, newText);
//        mCurFilter = newText;
//        final List<AccountEntity> filteredModelList = filter(mModels, newText);
//        mAdapter.animateTo(filteredModelList);
        mAdapter.setFilter(newText);
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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

    @Override
    public Loader<List<AccountEntity>> onCreateLoader(int id, Bundle args) {

        Uri uri = IProvider.CONTENT_URI;

        // This is the select criteria

        return new AccountListLoader(this, uri,
                IDB.ROWS_NAME, null, null, IDB.ROWS_NAME[IDB.DBID] + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<List<AccountEntity>> loader, List<AccountEntity> data) {
        VergLog.v(TAG, "onLoadFinished()");
        if (data == null || data.isEmpty()) {
            Snackbar.make(mRecyclerView, "no database find.", Snackbar.LENGTH_LONG).show();
        } else {
            mAdapter.setModels(data);
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<AccountEntity>> loader) {
        mAdapter.setModels(null);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(AccountEntity.TAG, mAdapter.getItemEntity(position));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        startActivityForResult(new Intent(this, EditorActivity.class), REQUESTCODE);
    }

    @Override
    public void onItemLongClick(View view, final int position) {
        VergLog.v(TAG, "onItemLongClick()");
        Snackbar.make(view, "是否删除？", Snackbar.LENGTH_LONG)
                .setAction("是的", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Defines selection criteria for the rows you want to delete
                        String mSelectionClause = IDB.ROWS_NAME[IDB.DBID] + "= ?";
                        String[] mSelectionArgs = {String.valueOf(mAdapter.getItemEntity(position)._id)};

                        // Defines a variable to contain the number of rows deleted
                        int rowsDeleted = getContentResolver().delete(
                                IProvider.CONTENT_URI,
                                mSelectionClause,
                                mSelectionArgs);
                        VergLog.v(TAG, "rowsDeleted:" + rowsDeleted);
                        mAdapter.remove(position);
                    }
                }).show();
    }

    protected void check4updates() {
        String url = "https://raw.githubusercontent.com/VergLsm/Release/Udontknow/version";
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
            Snackbar.make(mRecyclerView, R.string.had_new_version, Snackbar.LENGTH_LONG).setAction(R.string.update, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VergLog.v(TAG, getString(R.string.update));
                    download(finalUrl);
                }
            }).show();
        } else {
            Snackbar.make(mRecyclerView, R.string.no_new_version, Snackbar.LENGTH_LONG).show();
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
        downloadManager.enqueue(request);
        Snackbar.make(mRecyclerView, R.string.start_download, Snackbar.LENGTH_SHORT).show();

    }
}
