package com.example.flex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ClipData;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new HomeFragment()).commit();
        setActionListener();

    }
    private void setActionListener(){
        BottomNavigationView bnv=findViewById(R.id.bottom_bar);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {//here code in case of choosing item in bottom bar
                Fragment selectedFragment=null;
                switch(menuItem.getItemId()){
                    case R.id.action_account:
                        selectedFragment=new AccountFragment();
                        break;
                    case R.id.action_camera:
                        selectedFragment=new PhotoFragment();
                        break;
                    case R.id.action_home:
                        selectedFragment=new HomeFragment();
                        break;
                    case R.id.action_map:
                        selectedFragment=new MapFragment();
                        break;
                    case R.id.action_tv:
                        selectedFragment=new TvFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,selectedFragment).commit();
                return true;
            }
        });
    }
}
