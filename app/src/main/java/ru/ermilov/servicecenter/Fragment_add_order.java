package ru.ermilov.servicecenter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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


public class Fragment_add_order extends Fragment {

    private CardView buttonAddOrder;
    private DatabaseReference db;
    List<Orders> mOrdersList = new ArrayList<>();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_add_order, container, false);



       buttonAddOrder = (CardView) view.findViewById(R.id.addOrder);
       buttonAddOrder.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               FragmentFormOrder fragmentFormOrder= new FragmentFormOrder();
               FragmentTransaction ft = getParentFragmentManager().beginTransaction();
               ft.replace(R.id.add_client,fragmentFormOrder);
               ft.commit();

           }
       });

        ListView listViewOrders = view.findViewById(R.id.listViewOrders);
         db = FirebaseDatabase.getInstance().getReference();
        db.child("Orders").get().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    mOrdersList.add(ds.getValue(Orders.class));
                }
                listViewOrders.setAdapter(new AdapterOrders(container.getContext()));
            }

        } );



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
            return mOrdersList.size();
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
            TextView tvCategory = view.findViewById(R.id.tvCategory);
            TextView tvCondition = view.findViewById(R.id.tvCondition);
            TextView tvProblema = view.findViewById(R.id.tvProblema);
            TextView tvDateStart = view.findViewById(R.id.tvDateStart);
            TextView tvDateEnd = view.findViewById(R.id.tvDateEnd);

            Picasso.get().load(mOrdersList.get(i).ImageUri).into(ivFoto);


           tvCategory.setText(mOrdersList.get(i).Category);
            tvCondition.setText(mOrdersList.get(i).Condition);
            tvProblema.setText(mOrdersList.get(i).Problema);
            tvDateStart.setText(mOrdersList.get(i).DateStart + "/");
            tvDateEnd.setText(mOrdersList.get(i).DateEnd);

            CardView delete = view.findViewById(R.id.delete);
            delete.setOnClickListener(v -> {
                String imageUri = mOrdersList.get(i).ImageUri;
                db.child("Orders").orderByChild("ImageUri").equalTo(imageUri).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item: snapshot.getChildren())
                            item.getRef().removeValue();

                        Toast.makeText(delete.getContext(), "Заказ удален", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });


            return view;
        }
    }


}