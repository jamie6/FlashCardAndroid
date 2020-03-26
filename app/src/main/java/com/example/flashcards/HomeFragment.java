package com.example.flashcards;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String[] list = DeckManager.getCurrentDirectoryList();
        for (String item : list) {
            addDirItem(item);
        }

        FloatingActionButton fab = getView().findViewById(R.id.new_deck_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = new EditDeckFragment();

                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
            }
        });
    }

    private void addDirItem(String name) {
        Context context = getContext();
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dirItemView = layoutInflater.inflate(R.layout.directory_item, null);

        dirItemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // show trash can icon at top, show checkbox
                CheckBox checkBox = v.findViewById(R.id.dir_item_select);
                checkBox.setVisibility(View.VISIBLE);
                return true;
            }
        });

        ImageView imageView = dirItemView.findViewById(R.id.dir_item_icon);
        TextView textView = dirItemView.findViewById(R.id.dir_item_name);
        if (name.contains(DeckManager.DECK_EXT)) {
            imageView.setBackgroundResource(R.drawable.ic_credit_card_black_24dp);
            name = name.substring(0, name.length()-DeckManager.DECK_EXT.length());
        } else {
            imageView.setBackgroundResource(R.drawable.ic_folder_black_24dp);
        }
        textView.setText(name);

        View view = getView();
        LinearLayout linearLayout = view.findViewById(R.id.directory_linear_layout);
        linearLayout.addView(dirItemView);
    }
}
