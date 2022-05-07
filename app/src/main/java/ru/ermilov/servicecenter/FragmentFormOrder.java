package ru.ermilov.servicecenter;

import static android.app.Activity.RESULT_OK;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.util.ArrayList;
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
    List<String> categoriesSearch = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        for (String item: categories) {
//            if (item.equals("Комп"))
//                categoriesSearch.add(item);
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
//                android.R.layout.simple_spinner_item, categoriesSearch);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        categoryList.setAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form_order, container, false);

        TextView fioClient = view.findViewById(R.id.fioClientOrder);

        Bundle bundle = getArguments();
        String fio = bundle.getString("fio");

        fioClient.setText(""+fio);


      //  Bundle bundleFio = getArguments();
       // String fio = bundleFio.getString("fio");

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
                    categories.add(ds.getValue(Categories.class).name);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(container.getContext(),
                        android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categoryList.setAdapter(adapter);
            }
        });

//        DatabaseReference db2 = FirebaseDatabase.getInstance().getReference();
//        db2.child("Client").get().addOnCompleteListener(task -> {
//            if (!task.isSuccessful()) {
//                Log.e("firebase", "Error getting data", task.getException());
//            }
//            else {
//                for (DataSnapshot ds : task.getResult().getChildren()) {
//                    Clients client = ds.getValue(Clients.class);
//                    if (client.fio.equals("Иванов Иван Иванович"))
//                        ds.getRef().removeValue();
//                }
//            }
//        });
    
        EditText etDiscriptionСondition = view.findViewById(R.id.etDiscriptionСondition);
        EditText etDiscriptionProblem = view.findViewById(R.id.etDiscriptionProblem);
        EditText etDateStart = view.findViewById(R.id.etDateStart);
        EditText etDateEnd = view.findViewById(R.id.etDateEnd);

        CardView btnCreateOrder = view.findViewById(R.id.btnCreateOrder);
        btnCreateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Bundle bundle = getArguments();
                String Fio = bundle.getString("fio");

                String Category = categoryList.getSelectedItem().toString();
                String DiscriptionСondition = etDiscriptionСondition.getText().toString();
                String DiscriptionProblem = etDiscriptionProblem.getText().toString();
                String DateStart = etDateStart.getText().toString();
                String DateEnd = etDateEnd.getText().toString();

                uploadImage(Fio,Category, DiscriptionСondition, DiscriptionProblem, DateStart, DateEnd);

                Toast.makeText(btnCreateOrder.getContext(), "Заказ добавлен", Toast.LENGTH_SHORT).show();

                Fragment_add_order fragment_add_order = new Fragment_add_order();
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.add_client, fragment_add_order);
                ft.commit();
            }

        });

        CardView btnNazad = view.findViewById(R.id.button_nazad);
        btnNazad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentClients fragmentClients = new FragmentClients();
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
            // Фотка сделана, извлекаем миниатюру картинки
            Bundle extras = data.getExtras();
            Bitmap thumbnailBitmap = (Bitmap) extras.get("data");
            fotoClient.setImageBitmap(thumbnailBitmap);

        }
    }

    private void uploadImage(String Key, String Category, String DiscriptionСondition, String DiscriptionProblem,
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

                Orders order = new Orders(Key,Category, DiscriptionСondition, DiscriptionProblem, DateStart, DateEnd, uploadUri.toString());

                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                db.child("Orders").push().setValue(order);
            }
        });
    }

}