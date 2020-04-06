package com.example.flashcards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private boolean goBackToBaseDirectory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeckManager.setBaseDirectoryPath(getFilesDir().getAbsolutePath()+"/"+DeckManager.MAIN_DECKS_FOLDER);
        DeckManager.resetCurrentDirectoryToBaseDirectory();

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            if (goBackToBaseDirectory) {
                                DeckManager.resetCurrentDirectoryToBaseDirectory();
                            }

                            if (DeckManager.isCurrentDirectoryAtBaseDirectory()) {
                                goBackToBaseDirectory = false;
                            } else {
                                goBackToBaseDirectory = true;
                            }

                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_search:
                            goBackToBaseDirectory = false;
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.nav_new_folder:
                            goBackToBaseDirectory = false;
                            selectedFragment = new NewFolderFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };
}
