package ru.ermilov.servicecenter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private CardView addClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addClient = (CardView) findViewById(R.id.client_CardView);

        addClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewFragment();
            }
        });
    }

    private void setNewFragment(){
        AddClientFragment addClientFragment = new AddClientFragment(); // создание объекта
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); // создание объекта для подгрузки фрагмента
        ft.replace(R.id.add_client,addClientFragment);//замена текущего содержимого на новое содержимое
        ft.commit();// сохранение
    }
}