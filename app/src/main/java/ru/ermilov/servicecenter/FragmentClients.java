package ru.ermilov.servicecenter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class FragmentClients extends Fragment {

    private DatabaseReference db;
    private CardView buttonAddClient;
    private EditText searchListViev;
    Clients clients;
    List<Clients> mClientsList = new ArrayList<>();
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

        ListView listViewClients = view.findViewById(R.id.listViewClients);
         db = FirebaseDatabase.getInstance().getReference();
        db.child("Clients").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    mClientsList.add(ds.getValue(Clients.class));
                }
                listViewClients.setAdapter(new AdapterClients(getContext()));
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
            return mClientsList.size();
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
            
            tvFio.setText(mClientsList.get(i).fio);
            tvPhone.setText(mClientsList.get(i).phone);
            tvEmail.setText(mClientsList.get(i).email);

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