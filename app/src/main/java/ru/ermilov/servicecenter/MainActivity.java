package ru.ermilov.servicecenter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static FragmentManager fm;
    private CardView buttonOpenClients,buttonOpenOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.add_client);



        if(fragment == null){

            fragment = new FragmentListClients();
            fm.beginTransaction().replace(R.id.add_client,fragment).commit();

        }





        buttonOpenClients = (CardView) findViewById(R.id.client_CardView);

        buttonOpenClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new FragmentListClients();
                fm.beginTransaction().replace(R.id.add_client,fragment).commit();

            }
        });

       buttonOpenOrder = (CardView) findViewById(R.id.remont_CardView);
        buttonOpenOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new FragmentListOrder();
                fm.beginTransaction().replace(R.id.add_client,fragment).commit();
            }
        });

    }

    @Override
   public void onBackPressed(){ }
}