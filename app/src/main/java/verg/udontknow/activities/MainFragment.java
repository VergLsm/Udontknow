package verg.udontknow.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import verg.lib.VergLog;
import verg.udontknow.R;
import verg.udontknow.activities.main.AccountListLoader;
import verg.udontknow.activities.main.NewAdapter;
import verg.udontknow.entity.AccountEntity;
import verg.udontknow.provider.IDB;
import verg.udontknow.provider.IProvider;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<AccountEntity>>,
        SearchView.OnQueryTextListener,
        View.OnClickListener,
        NewAdapter.ItemClickListener,
        NewAdapter.ItemLongClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";
    private String mParam1;
    private String mParam2;

    private final String TAG = this.getClass().getName();
    public static final int REQUESTCODE = 12300;

    private OnFragmentInteractionListener mListener;

    private RecyclerView mRecyclerView;
    private NewAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();

        FloatingActionButton fab = mainActivity.getFab();
        if (fab != null) {
            fab.setOnClickListener(this);
            fab.setVisibility(View.VISIBLE);
        }

        SearchView searchView = mainActivity.getSearchView();
        if (searchView != null) {
            searchView.setVisibility(View.VISIBLE);
            searchView.setOnQueryTextListener(this);
        }

        mSwipeRefreshLayout = view.findViewById(R.id.my_swipeRefreshLayout);
        assert mSwipeRefreshLayout != null;
        mSwipeRefreshLayout.setEnabled(false);

        mAdapter = new NewAdapter(null);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);

        mRecyclerView = view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                getLoaderManager().initLoader(0, null, MainFragment.this);
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

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
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v) {
        startActivityForResult(new Intent(getActivity(), EditorActivity.class), REQUESTCODE);
    }

    @NonNull
    @Override
    public Loader<List<AccountEntity>> onCreateLoader(int id, Bundle args) {

        Uri uri = Uri.parse(mParam1);

        // This is the select criteria

        return new AccountListLoader(getActivity(), uri,
                IDB.ROWS_NAME, null, null, IDB.ROWS_NAME[IDB.DBID] + " DESC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<AccountEntity>> loader, List<AccountEntity> data) {
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
    public void onLoaderReset(@NonNull Loader<List<AccountEntity>> loader) {
        mAdapter.setModels(null);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(AccountEntity.TAG, mAdapter.getItemEntity(position));
        startActivity(intent);
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
                        int rowsDeleted = getActivity().getContentResolver().delete(
                                IProvider.CONTENT_URI,
                                mSelectionClause,
                                mSelectionArgs);
                        VergLog.v(TAG, "rowsDeleted:" + rowsDeleted);
                        mAdapter.remove(position);
                    }
                }).show();
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

}
