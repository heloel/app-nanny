package com.sourcey.materiallogindemo.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.Parse;

import com.sourcey.materiallogindemo.R;
import com.sourcey.materiallogindemo.Utils.GeneralConstants;
import com.sourcey.materiallogindemo.Utils.ParseConstants;
import com.sourcey.materiallogindemo.Utils.DividerItemDecoration;
import com.sourcey.materiallogindemo.adapters.FriendsAdapter;

import java.util.List;

public class FriendsFragment extends Fragment {

    private RecyclerView friendsList;
    private TextView friendsEmpty;
    private ParseRelation<ParseObject> mRelation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsList = (RecyclerView)rootView.findViewById(R.id.recyclerViewFriends);
        friendsEmpty = (TextView)rootView.findViewById(R.id.empty_friends);

        //friendsList.setAdapter(null);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        friendsList.setLayoutManager(layoutManager);
        friendsList.setHasFixedSize(true);
        friendsList.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), LinearLayoutManager.VERTICAL));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!GeneralConstants.checkNetwork(getActivity().getBaseContext()))
            GeneralConstants.showMessageConnection(getActivity().getBaseContext());
        else {
            ParseUser mUser = ParseUser.getCurrentUser();
            mRelation = mUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

            getFriends();
        }
    }

    public void getFriends(){
        ParseQuery<ParseObject> query = mRelation.getQuery();

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> fList, ParseException e) {
                if(e == null){
                    if(fList == null || fList.isEmpty()){
                        friendsList.setVisibility(View.GONE);
                        friendsEmpty.setVisibility(View.VISIBLE);
                    }else{
                        if(friendsList.getAdapter() == null){
                            FriendsAdapter friendsAdapter = new FriendsAdapter(getActivity().getBaseContext(), fList);
                            friendsList.setAdapter(friendsAdapter);
                        }else{
                            ((FriendsAdapter)friendsList.getAdapter()).refill(fList);
                        }
                    }
                }else{
                    if(!getActivity().isFinishing()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getBaseContext());
                        builder.setMessage(e.getMessage())
                                .setTitle("Oops, algo salio mal!!")
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        friendsList.setVisibility(View.GONE);
                        friendsEmpty.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
