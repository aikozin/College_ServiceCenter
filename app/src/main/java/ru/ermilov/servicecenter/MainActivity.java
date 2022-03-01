package ru.ermilov.servicecenter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static FragmentManager fm;
    private CardView buttonOpenClients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.add_client);
        if(fragment == null){

            fragment = new FragmentClients();
            fm.beginTransaction().replace(R.id.add_client,fragment).commit();

        }





        buttonOpenClients = (CardView) findViewById(R.id.client_CardView);

        buttonOpenClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = fm.findFragmentById(R.id.add_client);
                fragment = new FragmentClients();
                fm.beginTransaction().replace(R.id.add_client,fragment).commit();

            }
        });


    }
}