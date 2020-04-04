package com.example.flashcards;

import android.content.Context;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {
    private int selectionCount = 0;
    private ActionMode actionMode;
    private ActionMode.Callback callback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.dir_select_menu, menu);
            mode.setTitle("Select items");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()) {
                case R.id.delete_item:
                    selectionCount = 0;
                    deleteItem();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            setFloatingActionButtonVisibility(View.VISIBLE);
            setDirectoryItemCheckboxVisibility(View.INVISIBLE);
            setAllCheckBoxes(false);
            actionMode = null;
        }
    };

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

        fab = getView().findViewById(R.id.new_folder_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFolderName = getResources().getString(R.string.default_new_folder_name);
                newFolderName = DeckManager.incrementFileName(DeckManager.getFolderNamesInCurrentDirectory(), newFolderName);
                View dirItem = addDirItem(newFolderName);
                EditText editText = dirItem.findViewById(R.id.dir_item_edit_name);
                editText.setText(newFolderName);
                setNameEditVisibility(dirItem, true);
                setFloatingActionButtonVisibility(View.GONE);
            }
        });
    }

    private void setNameEditVisibility(View dirItem, boolean isVisible) {
        if (isVisible) {
            TextView textView = dirItem.findViewById(R.id.dir_item_name);
            textView.setVisibility(View.GONE);
            EditText editText = dirItem.findViewById(R.id.dir_item_edit_name);
            editText.setVisibility(View.VISIBLE);
            ImageButton imageButton = dirItem.findViewById(R.id.dir_item_name_edit_button);
            imageButton.setVisibility(View.VISIBLE);
        } else {
            TextView textView = dirItem.findViewById(R.id.dir_item_name);
            textView.setVisibility(View.VISIBLE);
            EditText editText = dirItem.findViewById(R.id.dir_item_edit_name);
            editText.setVisibility(View.GONE);
            ImageButton imageButton = dirItem.findViewById(R.id.dir_item_name_edit_button);
            imageButton.setVisibility(View.GONE);
        }
    }

    private View addDirItem(String name) {
        Context context = getContext();
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dirItemView = layoutInflater.inflate(R.layout.directory_item, null);

        dirItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = v.findViewById(R.id.dir_item_select);
                if (checkBox.getVisibility() == View.VISIBLE) {
                    checkBox.setChecked(!checkBox.isChecked());
                } else {
                    // open deck reader/view page
                    TextView textView = v.findViewById(R.id.dir_item_name);
                    String deckName = textView.getText().toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("deckName", deckName);
                    Fragment viewDeckFragment = new ViewDeckFragment();
                    viewDeckFragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            viewDeckFragment).commit();
                }
            }
        });

        dirItemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (actionMode != null) {
                    return false;
                }

                actionMode = getActivity().startActionMode(callback);
                setFloatingActionButtonVisibility(View.INVISIBLE);
                setDirectoryItemCheckboxVisibility(View.VISIBLE);
                return true;
            }
        });

        CheckBox checkBox = dirItemView.findViewById(R.id.dir_item_select);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectionCount++;
                } else {
                    selectionCount--;
                }
                updateActionModeTitleCount();
            }
        });

        ImageView imageView = dirItemView.findViewById(R.id.dir_item_icon);
        TextView textView = dirItemView.findViewById(R.id.dir_item_name);
        if (name.contains(DeckManager.DECK_EXT)) {
            imageView.setBackgroundResource(R.drawable.ic_credit_card_black_24dp);
            name = name.substring(0, name.length()-DeckManager.DECK_EXT.length());
            textView.setTag(DeckManager.DECK_TAG);
        } else {
            imageView.setBackgroundResource(R.drawable.ic_folder_black_24dp);
            textView.setTag(DeckManager.FOLDER_TAG);
        }
        textView.setText(name);

        ImageButton imageButton = dirItemView.findViewById(R.id.dir_item_name_edit_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dirItem = (View)v.getParent();
                TextView tv = dirItem.findViewById(R.id.dir_item_name);
                EditText et = dirItem.findViewById(R.id.dir_item_edit_name);
                String newFolderName = et.getText().toString();
                newFolderName = DeckManager.incrementFileName(DeckManager.getFolderNamesInCurrentDirectory(), newFolderName);
                tv.setText(newFolderName);
                setNameEditVisibility(dirItem, false);
                DeckManager.createNewFolder(newFolderName);
                setFloatingActionButtonVisibility(View.VISIBLE);
            }
        });

        LinearLayout linearLayout = getView().findViewById(R.id.directory_linear_layout);
        linearLayout.addView(dirItemView);
        return dirItemView;
    }

    private void deleteItem() {
        LinearLayout linearLayout = getView().findViewById(R.id.directory_linear_layout);
        for ( int i = 0; i < linearLayout.getChildCount(); i++ ) {
            View view = linearLayout.getChildAt(i);
            TextView textView = view.findViewById(R.id.dir_item_name);
            switch(textView.getTag().toString()) {
                case DeckManager.DECK_TAG:
                    DeckManager.deleteItem(textView.getText().toString(), false);
                    break;
                case DeckManager.FOLDER_TAG:
                    DeckManager.deleteItem(textView.getText().toString(), true);
                    break;
                default:
                    break;
            }
        }
    }

    private void setDirectoryItemCheckboxVisibility(int visibility) {
        LinearLayout directoryLinearLayout = getView().findViewById(R.id.directory_linear_layout);
        View view;
        CheckBox checkBox;
        for ( int i = 0; i < directoryLinearLayout.getChildCount(); i++ ) {
            view = directoryLinearLayout.getChildAt(i);
            checkBox = view.findViewById(R.id.dir_item_select);
            checkBox.setVisibility(visibility);
        }
    }

    private void setAllCheckBoxes(boolean isChecked) {
        LinearLayout directoryLinearLayout = getView().findViewById(R.id.directory_linear_layout);
        View view;
        CheckBox checkBox;
        for ( int i = 0; i < directoryLinearLayout.getChildCount(); i++ ) {
            view = directoryLinearLayout.getChildAt(i);
            checkBox = view.findViewById(R.id.dir_item_select);
            checkBox.setChecked(isChecked);
        }
        if (isChecked) {
            selectionCount = directoryLinearLayout.getChildCount();
        } else {
            selectionCount = 0;
        }
    }

    private void setFloatingActionButtonVisibility(int visibility) {
        View view = getView();
        FloatingActionButton newDeckFab = view.findViewById(R.id.new_deck_fab);
        FloatingActionButton newFolderFab = view.findViewById(R.id.new_folder_fab);
        newDeckFab.setVisibility(visibility);
        newFolderFab.setVisibility(visibility);
    }

    private void updateActionModeTitleCount() {
        if (actionMode != null) {
            if (selectionCount > 0) {
                actionMode.setTitle(selectionCount + "selected");
            } else {
                actionMode.setTitle("Select items");
            }
        }
    }
}
