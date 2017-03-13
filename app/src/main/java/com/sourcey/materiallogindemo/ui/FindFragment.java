package com.sourcey.materiallogindemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.AlertDialog;

import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.FindCallback;

import com.sourcey.materiallogindemo.R;
import com.sourcey.materiallogindemo.Utils.DividerItemDecoration;
import com.sourcey.materiallogindemo.Utils.GeneralConstants;
import com.sourcey.materiallogindemo.adapters.FindAdapter;

import java.util.List;

public class FindFragment extends Fragment {


    private RecyclerView find_friends;
    private TextView findEmpty;
    private ParseRelation<ParseObject> mRelation;

    public FindFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_find, container, false);

        find_friends = (RecyclerView)rootView.findViewById(R.id.recyclerViewFindFriends);
        findEmpty = (TextView)rootView.findViewById(R.id.empty_finds);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        find_friends.setLayoutManager(layoutManager);
        find_friends.setHasFixedSize(true);
        find_friends.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), LinearLayoutManager.VERTICAL));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!GeneralConstants.checkNetwork(getActivity().getBaseContext()))
            GeneralConstants.showMessageConnection(getActivity().getBaseContext());
        else {
            //ParseUser mUser = ParseUser.getCurrentUser();
            //mRelation = mUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

            getFriends();
        }
    }

    public void getFriends(){
        ParseQuery<ParseObject> query = new ParseQuery("Friends");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> fList, ParseException e) {
                if (e == null) {
                    if (fList == null || fList.isEmpty()) {
                        find_friends.setVisibility(View.GONE);
                        findEmpty.setVisibility(View.VISIBLE);
                    } else {
                        if (find_friends.getAdapter() == null) {
                            FindAdapter findAdapter = new FindAdapter(getActivity().getBaseContext(), fList);
                            find_friends.setAdapter(findAdapter);
                        } else {
                            ((FindAdapter) find_friends.getAdapter()).refill(fList);
                        }
                    }
                } else {
                    if (!getActivity().isFinishing()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getBaseContext());
                        builder.setMessage(e.getMessage())
                                .setTitle("Oops, algo salio mal!!")
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        find_friends.setVisibility(View.GONE);
                        findEmpty.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
