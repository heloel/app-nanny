package com.sourcey.materiallogindemo.ui;

import android.app.AlertDialog;
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

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.sourcey.materiallogindemo.R;
import com.sourcey.materiallogindemo.Utils.DividerItemDecoration;
import com.sourcey.materiallogindemo.Utils.GeneralConstants;
import com.sourcey.materiallogindemo.Utils.ParseConstants;
import com.sourcey.materiallogindemo.adapters.PetsAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PetsFragment extends Fragment {

    private FloatingActionButton add_pet;
    private RecyclerView pets;
    private TextView empty_pets;

    private ParseRelation<ParseObject> mRelation;

    public PetsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pets, container, false);

        add_pet = (FloatingActionButton)rootView.findViewById(R.id.fab_add_pets);
        pets = (RecyclerView)rootView.findViewById(R.id.recyclerViewPets);
        empty_pets = (TextView)rootView.findViewById(R.id.empty_pets);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        pets.setLayoutManager(layoutManager);
        pets.setHasFixedSize(true);
        pets.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), LinearLayoutManager.VERTICAL));

        add_pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addPet = new Intent(getActivity().getBaseContext(), AddPetActivity.class);
                addPet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(addPet);
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!GeneralConstants.checkNetwork(getActivity().getBaseContext()))
            GeneralConstants.showMessageConnection(getActivity().getBaseContext());
        else {
            ParseUser mUser = ParseUser.getCurrentUser();
            mRelation = mUser.getRelation(ParseConstants.KEY_ANIMAL_RELATION);

            getPets();
        }
    }

    public void getPets(){

        ParseQuery<ParseObject> query = mRelation.getQuery();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> fList, ParseException e) {
                if (e == null) {
                    if (fList == null || fList.isEmpty()) {
                        pets.setVisibility(View.GONE);
                        empty_pets.setVisibility(View.VISIBLE);
                    } else {
                        if (pets.getAdapter() == null) {
                            PetsAdapter petsAdapter = new PetsAdapter(getActivity().getBaseContext(), fList);
                            pets.setAdapter(petsAdapter);
                        } else {
                            ((PetsAdapter)pets.getAdapter()).refill(fList);
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
                        pets.setVisibility(View.GONE);
                        empty_pets.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
