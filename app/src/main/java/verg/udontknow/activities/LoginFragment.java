package verg.udontknow.activities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import verg.udontknow.R;
import verg.udontknow.provider.IProvider;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;
    private Button mBtnConfirm;
    private EditText mEditText;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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

        MainActivity mainActivity = (MainActivity) getActivity();

        FloatingActionButton fab = mainActivity.getFab();
        if (fab != null) {
            fab.setVisibility(View.GONE);
        }

        SearchView searchView = mainActivity.getSearchView();
        if (searchView != null) {
            searchView.setVisibility(View.GONE);
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        //Find the +1 button
        mBtnConfirm = view.findViewById(R.id.btnConfirm);
        mBtnConfirm.setOnClickListener(this);

        mEditText = view.findViewById(R.id.editText);

        return view;

    }

    public void onButtonPressed(String strUri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(strUri);
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

    @Override
    public void onClick(View v) {
        v.setEnabled(false);

        String strUri = "content://" + IProvider.PROVIDER_NAME + "/customers?" + IProvider.PWD + "=" + mEditText.getText();

        Uri uir = Uri.parse(strUri);
        try {
            Cursor countCursor = getActivity().getContentResolver().query(uir,
                    new String[]{"count(*) AS count"},
                    null,
                    null,
                    null);
            onButtonPressed(strUri);
        } catch (Exception e) {
            Snackbar.make(mBtnConfirm, R.string.wrong_password, Snackbar.LENGTH_SHORT).show();
        }finally {
            v.setEnabled(true);
        }

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
        void onFragmentInteraction(String strUri);
    }
}
