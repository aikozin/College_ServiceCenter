package ru.ermilov.servicecenter;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FragmentFormClient extends Fragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_client, container, false);

        EditText etFio = view.findViewById(R.id.etFio);
        EditText etPhone = view.findViewById(R.id.etPhone);
        EditText etEmail = view.findViewById(R.id.etEmail);
        CardView btnCreateClient = view.findViewById(R.id.btnCreateClient);
        
        Bundle bundle = getArguments();
        if (bundle == null) {

            btnCreateClient.setOnClickListener(v -> {
                String fio = etFio.getText().toString();
                String phone = etPhone.getText().toString();
                String email = etEmail.getText().toString();
                if (fio.equals("") || phone.equals("") || email.equals("")) {
                    Toast.makeText(container.getContext(), "Не все данные заполнены", Toast.LENGTH_LONG).show();
                } else {

                    Clients client = new Clients(fio, phone, email);

                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    db.child("Clients").push().setValue(client);

                    Toast.makeText(btnCreateClient.getContext(), "Клиент добавлен", Toast.LENGTH_SHORT).show();

                    clickBack();
                }
            });
        } else {
            String key = bundle.getString("key");
            String fioBundle = bundle.getString("fio");
            String emailBundle = bundle.getString("email");
            String phoneBundle = bundle.getString("phone");
            etFio.setText(fioBundle);
            etEmail.setText(emailBundle);
            etPhone.setText(phoneBundle);


            btnCreateClient.setOnClickListener(v -> {
                String fio = etFio.getText().toString();
                String phone = etPhone.getText().toString();
                String email = etEmail.getText().toString();
                if (fio.equals("") || phone.equals("") || email.equals("")) {
                    Toast.makeText(container.getContext(), "Не все данные заполнены", Toast.LENGTH_LONG).show();
                } else {
                    Clients client = new Clients(fio, phone, email);

                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    db.child("Clients").child(key).setValue(client);

                    Toast.makeText(btnCreateClient.getContext(), "Клиент изменен", Toast.LENGTH_SHORT).show();

                    clickBack();
                }
            });
        }

        CardView btnNazad = view.findViewById(R.id.button_nazad);
        btnNazad.setOnClickListener(v -> clickBack());


        return view;
    }

    private void clickBack() {
        FragmentListClients fragmentClients = new FragmentListClients();
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.add_client, fragmentClients);
        ft.commit();
    }
}