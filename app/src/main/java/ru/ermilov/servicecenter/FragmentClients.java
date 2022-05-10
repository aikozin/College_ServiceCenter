package ru.ermilov.servicecenter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FragmentClients extends Fragment {

    private DatabaseReference db;
    private CardView buttonAddClient;
    private EditText searchListViev;
    Clients clients;
    List<Clients> allClientsList = new ArrayList<>();
    List<Clients> filterClientsList = new ArrayList<>();
    private ArrayList <Clients> mClient = new ArrayList<>();

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

        /*DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Query query = ref.child("Clients");

        
        Button delete = view.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren())
                        {
                            appleSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });*/

        ListView listViewClients = view.findViewById(R.id.listViewClients);
        db = FirebaseDatabase.getInstance().getReference();
        try {
            db.child("Clients").get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    for (DataSnapshot ds : task.getResult().getChildren()) {
                        Clients clients = ds.getValue(Clients.class);
                        clients.key = ds.getKey();
                        allClientsList.add(clients);
                    }
                    filterClientsList = allClientsList;
                    listViewClients.setAdapter(new AdapterClients(container.getContext()));
                }
            });
        } catch (Exception e) {
            Toast.makeText(container.getContext(), "Произошла ошибка при получении данных", Toast.LENGTH_SHORT).show();
        }

       /* listViewClients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/

        EditText search = view.findViewById(R.id.searchListViev);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterClientsList = new ArrayList<>();
                for (Clients client : allClientsList) {
                    if (client.fio.toLowerCase(Locale.ROOT).contains(s.toString().toLowerCase(Locale.ROOT)))
                        filterClientsList.add(client);
                }
                listViewClients.setAdapter(new AdapterClients(container.getContext()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    class AdapterClients extends BaseAdapter {
        private LayoutInflater mLayoutInflater;

        public AdapterClients(Context context) {
            super();

            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return filterClientsList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            if (view == null)
                view = mLayoutInflater.inflate(R.layout.item_client, null);

            TextView tvFio = view.findViewById(R.id.tvFio);
            TextView tvPhone = view.findViewById(R.id.tvPhone);
            TextView tvEmail = view.findViewById(R.id.tvEmail);
            CardView delete = view.findViewById(R.id.delete);
            CardView order = view.findViewById(R.id.order);
            
            tvFio.setText(filterClientsList.get(i).fio);
            tvPhone.setText(filterClientsList.get(i).phone);
            tvEmail.setText(filterClientsList.get(i).email);

            order.setOnClickListener(v -> {
                Fragment fragment = new FragmentFormOrder();

                Bundle bundle = new Bundle();
                bundle.putString("fio", filterClientsList.get(i).fio);

              //  Bundle bundleFio = new Bundle();
               // bundleFio.putString("fio", filterClientsList.get(i).fio);

                fragment.setArguments(bundle);
               // fragment.setArguments(bundleFio);
                getParentFragmentManager().beginTransaction().replace(R.id.add_client, fragment).commit();
            });
            
            delete.setOnClickListener(v -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(delete.getContext());
                alert.setTitle("Подтверждение");
                alert.setMessage("Вы действительно хотите удалить клиента " + filterClientsList.get(i).fio + "?");
                alert.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    String phone = filterClientsList.get(i).phone;
                    db.child("Clients").orderByChild("phone").equalTo(phone).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot item: snapshot.getChildren())
                                item.getRef().removeValue();

                            Toast.makeText(delete.getContext(), "Клиент удален", Toast.LENGTH_SHORT).show();

                            FragmentClients fragmentClients = new FragmentClients();
                            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                            ft.replace(R.id.add_client, fragmentClients);
                            ft.commit();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                });
                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.show();
            });

            return view;
        }
    }



    /*private static  class DeleteHolder extends RecyclerView.ViewHolder{

        TextView tvFio, tvPhone, tvEmail;
        Button delete;

        public DeleteHolder(View view){
            super(view);

             tvFio = view.findViewById(R.id.tvFio);
             tvPhone = view.findViewById(R.id.tvPhone);
             tvEmail = view.findViewById(R.id.tvEmail);
             delete = view.findViewById(R.id.delete);
        }
    }*/


}