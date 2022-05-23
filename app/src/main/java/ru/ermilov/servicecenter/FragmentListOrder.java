package ru.ermilov.servicecenter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class FragmentListOrder extends Fragment {

    private CardView buttonAddOrder;
    private DatabaseReference db;
    List<Orders> allOrdersList = new ArrayList<>();
    List<Orders> filterOrderList = new ArrayList<>();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.fragment_list_order, container, false);





        ListView listViewOrders = view.findViewById(R.id.listViewOrders);
         db = FirebaseDatabase.getInstance().getReference();
        db.child("Orders").get().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    allOrdersList.add(ds.getValue(Orders.class));
                    Orders orders = ds.getValue(Orders.class);
                    orders.key = ds.getKey();
                }
                filterOrderList = allOrdersList;
                listViewOrders.setAdapter(new AdapterOrders(container.getContext()));
            }

        } );

        EditText search = view.findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrderList = new ArrayList<>();
                for (Orders order : allOrdersList){
                    if (order.Fio.contains(s)){

                        filterOrderList.add(order);
                    }
                }

                listViewOrders.setAdapter(new AdapterOrders(container.getContext()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;

    }



    class AdapterOrders extends BaseAdapter{

        private LayoutInflater mLayoutInflater;

        public AdapterOrders (Context context){
            super();

            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return filterOrderList.size();
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
                view = mLayoutInflater.inflate(R.layout.item_order, null);

            ImageView ivFoto = view.findViewById(R.id.ivFoto);
            TextView tvFio = view.findViewById(R.id.tvFio);

            TextView tvCategory = view.findViewById(R.id.tvCategory);
            TextView tvCondition = view.findViewById(R.id.tvCondition);
            TextView tvProblema = view.findViewById(R.id.tvProblema);
            TextView tvDateStart = view.findViewById(R.id.tvDateStart);
            TextView tvDateEnd = view.findViewById(R.id.tvDateEnd);

            Picasso.get().load(filterOrderList.get(i).ImageUri).into(ivFoto);

            tvFio.setText(filterOrderList.get(i).Fio);
            tvCategory.setText(filterOrderList.get(i).Category);
            tvCondition.setText(filterOrderList.get(i).Condition);
            tvProblema.setText(filterOrderList.get(i).Problema);
            tvDateStart.setText(filterOrderList.get(i).DateStart + "/");
            tvDateEnd.setText(filterOrderList.get(i).DateEnd);

            CardView edit = view.findViewById(R.id.edit);

            edit.setOnClickListener(v -> {
                Fragment fragment = new FragmentFormOrder();

                Bundle bundleOrder = new Bundle();

                bundleOrder.putString("key", filterOrderList.get(i).key);
                bundleOrder.putString("type", "edit");
                bundleOrder.putString("Category", filterOrderList.get(i).Category);
                bundleOrder.putString("Fio", filterOrderList.get(i).Fio);
                bundleOrder.putString("Condition", filterOrderList.get(i).Condition);
                bundleOrder.putString("Problema", filterOrderList.get(i).Problema);
                bundleOrder.putString("DateStart", filterOrderList.get(i).DateStart);
                bundleOrder.putString("DateEnd", filterOrderList.get(i).DateEnd);
                bundleOrder.putString("ImageUri", filterOrderList.get(i).ImageUri);



                fragment.setArguments(bundleOrder);

                getParentFragmentManager().beginTransaction().replace(R.id.add_client, fragment).commit();
            });

            CardView delete = view.findViewById(R.id.delete);
            delete.setOnClickListener(v -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(delete.getContext());
                alert.setTitle("Подтверждение");
                alert.setMessage("Вы действительно хотите удалить данный заказ?");
                alert.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    String imageUri = filterOrderList.get(i).ImageUri;
                    db.child("Orders").orderByChild("ImageUri").equalTo(imageUri).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot item: snapshot.getChildren())
                                item.getRef().removeValue();

                            Toast.makeText(delete.getContext(), "Заказ удален", Toast.LENGTH_SHORT).show();

                            FragmentListOrder fragment_add_order = new FragmentListOrder();
                            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                            ft.replace(R.id.add_client, fragment_add_order);
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


}