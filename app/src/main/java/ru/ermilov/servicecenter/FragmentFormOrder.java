package ru.ermilov.servicecenter;

import static android.app.Activity.RESULT_OK;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class FragmentFormOrder extends Fragment {

    private ImageView fotoClient;
    private CardView buttonFoto;
    private StorageReference mStorageRef;
    private Uri uploadUri;

    FirebaseStorage storage;
    StorageReference storageReference;

    List<String> categories = new ArrayList<>();
    List<String> categoriesKey = new ArrayList<>();
    String keyOrder = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form_order, container, false);

        TextView fioClient = view.findViewById(R.id.fioClientOrder);

        Bundle bundle = getArguments();
        keyOrder = bundle.getString("keyOrder");
        String fio = bundle.getString("fio");
        fioClient.setText("Создание нового заказа от клиента \n" + fio);

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String time = sdf.format(date);

        mStorageRef = FirebaseStorage.getInstance().getReference("ImageDB");
        fotoClient = view.findViewById(R.id.fotoClient);
        buttonFoto = view.findViewById(R.id.buttonFoto);

        buttonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try{
                    startActivityForResult(takePhotoIntent, 1);
                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                }
            }
        });


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());


        Spinner categoryList = view.findViewById(R.id.spinnerСategory);
        //получение категорий из БД
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Category").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    Categories category = ds.getValue(Categories.class);
                    category.key = ds.getKey();
                    categories.add(category.name);
                    categoriesKey.add(category.key);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(container.getContext(),
                        android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categoryList.setAdapter(adapter);
                if (bundle.containsKey("Category")) {
                    String categoryKey = bundle.getString("Category");
                    int position = categoriesKey.indexOf(categoryKey);
                    categoryList.setSelection(position);
                }
            }
        });

        TextView fioClientOrder = view.findViewById(R.id.fioClientOrder);
        EditText etDiscriptionСondition = view.findViewById(R.id.etDiscriptionСondition);
        EditText etDiscriptionProblem = view.findViewById(R.id.etDiscriptionProblem);
        EditText etDateStart = view.findViewById(R.id.etDateStart);
        EditText etDateEnd = view.findViewById(R.id.etDateEnd);

        etDateStart.setText(time);

        CardView btnCreateOrder = view.findViewById(R.id.btnCreateOrder);

        if(!bundle.getString("type", "").equals("edit")){
            btnCreateOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String Fio = bundle.getString("fio");
                    String CategoryKey = categoriesKey.get(categoryList.getSelectedItemPosition());
                    String DiscriptionСondition = etDiscriptionСondition.getText().toString();
                    String DiscriptionProblem = etDiscriptionProblem.getText().toString();
                    String DateStart = etDateStart.getText().toString();
                    String DateEnd = etDateEnd.getText().toString();

                    uploadData(Fio,CategoryKey, DiscriptionСondition, DiscriptionProblem, DateStart, DateEnd);

                    Toast.makeText(btnCreateOrder.getContext(), "Заказ сохраняется", Toast.LENGTH_SHORT).show();
                }

            });

        }else {

           String key1 = bundle.getString("key");
            String FioBundle = bundle.getString("Fio");
            String CategoryKeyBundle = bundle.getString("Category");
            String ConditionBundle = bundle.getString("Condition");
            String ProblemaBundle = bundle.getString("Problema");
            String DateStartBundle = bundle.getString("DateStart");
            String DateEndBundle = bundle.getString("DateEnd");
            String ImageUriBundle = bundle.getString("ImageUri");


            fioClientOrder.setText(FioBundle);
            etDiscriptionСondition.setText(ConditionBundle);
            etDiscriptionProblem.setText(ProblemaBundle);
            etDateStart.setText(DateStartBundle);
            etDateEnd.setText(DateEndBundle);
            Picasso.get().load(ImageUriBundle).into(fotoClient);

            TextView textSave = view.findViewById(R.id.textSave);

            textSave.setText("Сохранить");
           fioClientOrder.setText("Редактировать заказ");

            btnCreateOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String Fio = bundle.getString("Fio");
                    String Category = categoriesKey.get(categoryList.getSelectedItemPosition());
                    String DiscriptionСondition = etDiscriptionСondition.getText().toString();
                    String DiscriptionProblem = etDiscriptionProblem.getText().toString();
                    String DateStart = etDateStart.getText().toString();
                    String DateEnd = etDateEnd.getText().toString();

                    uploadData(Fio,Category, DiscriptionСondition, DiscriptionProblem, DateStart, DateEnd);

                    Toast.makeText(btnCreateOrder.getContext(), "Изменения сохраняются", Toast.LENGTH_SHORT).show();
                }

            });
        }



        CardView btnNazad = view.findViewById(R.id.button_nazad);
        btnNazad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListClients fragmentClients = new FragmentListClients();
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.add_client, fragmentClients);
                ft.commit();
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap thumbnailBitmap = (Bitmap) extras.get("data");
            fotoClient.setImageBitmap(thumbnailBitmap);

        }
    }

    private void uploadData(String Key, String CategoryKey, String DiscriptionСondition, String DiscriptionProblem,
                             String DateStart, String DateEnd) {
        Bitmap bitmap;
        if (fotoClient.getDrawable() == null) {
            bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = ((BitmapDrawable) fotoClient.getDrawable()).getBitmap();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] byteArray = baos.toByteArray();
        final StorageReference mRef = mStorageRef.child(System.currentTimeMillis() + "my_image");
        UploadTask up = mRef.putBytes(byteArray);
        Task<Uri> task = up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                uploadUri = task.getResult();

                Orders order = new Orders(Key,CategoryKey, DiscriptionСondition, DiscriptionProblem, DateStart, DateEnd, uploadUri.toString(), "Ожидает ремонта");

                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                if (keyOrder == null) {
                    db.child("Orders").push().setValue(order);
                } else {
                    db.child("Orders").child(keyOrder).setValue(order);
                }

                FragmentListOrder fragment_add_order = new FragmentListOrder();
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.add_client, fragment_add_order);
                ft.commit();
            }
        });
    }

}