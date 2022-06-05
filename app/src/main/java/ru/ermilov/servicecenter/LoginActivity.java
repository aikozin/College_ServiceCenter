package ru.ermilov.servicecenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Подключение к базе данных в FireBase
        db = FirebaseDatabase.getInstance().getReference();

        //Нажатие на кнопку "Войти" и проверка правильности введеных Логина и Пароля
        CardView buttonJoin = findViewById(R.id.bt_join);
        buttonJoin.setOnClickListener(v -> {
            //Получаем Логин и Пароль из полей для ввода в переменные
            String login = ((EditText) findViewById(R.id.et_login)).getText().toString();
            String password = ((EditText) findViewById(R.id.et_password)).getText().toString();

            db.child("Users").get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Ошибка получения данных", task.getException());
                }
                else {
                    DataSnapshot dataSnapshot = task.getResult();
                    boolean isLogin = false;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Users user = ds.getValue(Users.class);
                        if (user.name.equals(login) && user.password.equals(password)) {

                            isLogin = true;

                            // Переход на MainActivity
                            Intent intent = new Intent(this,MainActivity.class);
                            startActivity(intent);
                        }
                    }
                    if (!isLogin)
                        Toast.makeText(LoginActivity.this, "Неправильно введен логин или пароль", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}