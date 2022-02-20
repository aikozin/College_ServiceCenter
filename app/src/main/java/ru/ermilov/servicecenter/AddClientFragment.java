package ru.ermilov.servicecenter;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AddClientFragment extends Fragment {

    private CardView addClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_client, container, false);

        addClient = view.findViewById(R.id.addClient);
        addClient.setOnClickListener(v -> {
            MainActivity.setFragmentFormClient();
        });

        return view;
    }
}