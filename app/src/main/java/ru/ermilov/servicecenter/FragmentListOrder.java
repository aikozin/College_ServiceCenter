package ru.ermilov.servicecenter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class FragmentListOrder extends Fragment {

    TextView tvDateEnd;
    private CardView buttonAddOrder;
    private DatabaseReference db;
    List<Orders> allOrdersList = new ArrayList<>();
    List<Orders> filterOrderList = new ArrayList<>();
    List<Categories> categories = new ArrayList<>();
    public static final int statys1 = 101;
    public static final int statys2 = 102;
    public static final int statys3 = 103;
    public String positionKeyOrder = "";
    public int positionIdOrder = 0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.fragment_list_order, container, false);



        db = FirebaseDatabase.getInstance().getReference();

        db.child("Category").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    Categories category = ds.getValue(Categories.class);
                    category.key = ds.getKey();
                    categories.add(category);
                }
            }
        });

        ListView listViewOrders = view.findViewById(R.id.listViewOrders);
        db.child("Orders").get().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    Orders orders = ds.getValue(Orders.class);
                    String categoryName = "";
                    for (Categories item : categories)
                        if (item.key.equals(orders.CategoryKey))
                            categoryName = item.name;
                    orders.Category = categoryName;
                    orders.keyOrder = ds.getKey();
                    allOrdersList.add(orders);
                }
                filterOrderList = allOrdersList;
                filterOrderList = sort(filterOrderList);
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


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(Menu.NONE, statys1, Menu.NONE, "Ожидает ремонта");
        menu.add(Menu.NONE, statys2, Menu.NONE, "Ремонтируется");
        menu.add(Menu.NONE, statys3, Menu.NONE, "Ремонт завершен");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        String status = item.getTitle().toString();
        Orders order = filterOrderList.get(positionIdOrder);
        order.Status = status;
        db.child("Orders").child(positionKeyOrder).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(), "Статус заказа изменен", Toast.LENGTH_SHORT).show();
                FragmentListOrder fragment = new FragmentListOrder();
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.add_client, fragment);
                ft.commit();
            }
        });
        return super.onContextItemSelected(item);
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
             tvDateEnd = view.findViewById(R.id.tvDateEnd);
            TextView statys = view.findViewById(R.id.statys);
            CardView statusColor = view.findViewById(R.id.statusColor);

            Picasso.get().load(filterOrderList.get(i).ImageUri).into(ivFoto);

            tvFio.setText(filterOrderList.get(i).Fio);
            tvCategory.setText(filterOrderList.get(i).Category);
            tvCondition.setText(filterOrderList.get(i).Condition);
            tvProblema.setText(filterOrderList.get(i).Problema);
            tvDateStart.setText(filterOrderList.get(i).DateStart + " | ");
            tvDateEnd.setText(filterOrderList.get(i).DateEnd);
            statys.setText(filterOrderList.get(i).Status);

            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String time = sdf.format(date);



            switch (filterOrderList.get(i).Status) {
                case "Ожидает ремонта":
                    statusColor.setCardBackgroundColor(Color.parseColor("#AC3E3E"));
                    break;
                case "Ремонтируется":
                    statusColor.setCardBackgroundColor(Color.parseColor("#FFC107"));
                    break;
                case "Ремонт завершен":
                    statusColor.setCardBackgroundColor(Color.parseColor("#4CAF50"));
                    tvDateEnd.setText(time);
                    break;
            }

            registerForContextMenu(statys);
            statys.setOnTouchListener((v, event) -> {
                positionKeyOrder = filterOrderList.get(i).keyOrder;
                positionIdOrder = i;
                return false;
            });

            CardView edit = view.findViewById(R.id.edit);

            edit.setOnClickListener(v -> {
                Fragment fragment = new FragmentFormOrder();

                Bundle bundleOrder = new Bundle();

                bundleOrder.putString("keyOrder", filterOrderList.get(i).keyOrder);
                bundleOrder.putString("type", "edit");
                bundleOrder.putString("Category", filterOrderList.get(i).CategoryKey);
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
    
    public List<Orders> sort(List<Orders> orders) {
        List<Orders> newOrders = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).Status.equals("Ожидает ремонта"))
                newOrders.add(orders.get(i));
        }
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).Status.equals("Ремонтируется"))
                newOrders.add(orders.get(i));
        }
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).Status.equals("Ремонт завершен"))
                newOrders.add(orders.get(i));

           // tvDateEnd.setText(time);

        }
        return newOrders;
    }
}