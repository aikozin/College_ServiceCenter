package ru.ermilov.servicecenter;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentClients extends Fragment {

    private CardView buttonAddClient;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_client, container, false);

        buttonAddClient = view.findViewById(R.id.addClient);
        buttonAddClient.setOnClickListener(v -> {


            FragmentAddClient fragmentAddClient = new FragmentAddClient();
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.add_client, fragmentAddClient);
            ft.commit();
        });

        return view;
    }
}