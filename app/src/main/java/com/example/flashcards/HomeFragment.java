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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;
import java.util.List;

public class HomeFragment extends Fragment {
    private int selectionCount = 0;
    private ActionMode actionMode;
    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.dir_select_menu, menu);
            mode.setTitle("Select items ");
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
            // actionMode = getActivity().startActionMode(navigationCallBack);
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
        updateBreadcrumbs();
        String[] list = DeckManager.getCurrentDirectoryList();
        for (String item : list) {
            addDirItem(item);
        }

        ImageButton dirNavBarBackButton = view.findViewById(R.id.dir_nav_bar_back_button);
        dirNavBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DeckManager.isCurrentDirectoryAtBaseDirectory()) {
                    DeckManager.moveUpDirectory();
                    updateDirectoryView();
                    updateBreadcrumbs();
                }
            }
        });

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

    @Override
    public void onStop() {
        if (actionMode != null) {
            actionMode.finish();
        }
        super.onStop();
    }

    private void updateBreadcrumbs() {
        setDirectoryBackButtonVisibility(!DeckManager.isCurrentDirectoryAtBaseDirectory());

        View view = getView();
        Context context = getContext();
        TextView currentFolderTextView = view.findViewById(R.id.dir_nav_bar_current_folder_text_view);
        currentFolderTextView.setText(DeckManager.getCurrentFolderName());

        LinearLayout linearLayout = view.findViewById(R.id.directory_nav_bar_breadcrumb_linear_layout);
        linearLayout.removeAllViews();
        for (final String breadcrumb : DeckManager.getBreadcrumbs()) {
            TextView breadcrumbTextView = new TextView(context);
            breadcrumbTextView.setTag(breadcrumb);
            breadcrumbTextView.setText(breadcrumb);
            breadcrumbTextView.setPadding(8, 8, 8, 8);
            breadcrumbTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag().equals(breadcrumb)) {
                        TextView textView = (TextView)v;
                        String currentBreadcrumb = textView.getText().toString();
                        String[] breadcrumbs = DeckManager.getBreadcrumbs();
                        if (!breadcrumbs[breadcrumbs.length-1].equals(currentBreadcrumb)) {
                            List<String> bcList = new LinkedList<>();
                            for (String bc : breadcrumbs) {
                                bcList.add(bc);
                                if (bc.equals(currentBreadcrumb)) {
                                    break;
                                }
                            }

                            int diff = breadcrumbs.length - bcList.size();
                            for ( int i = 0; i < diff; i++ ) {
                                DeckManager.moveUpDirectory();
                            }
                            updateBreadcrumbs();
                            updateDirectoryView();
                        }
                    }
                }
            });

            linearLayout.addView(breadcrumbTextView);
            if (!breadcrumb.equals(DeckManager.getCurrentFolderName())) {
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.ic_arrow_black_24dp);
                linearLayout.addView(imageView);
            }
        }
    }

    private void setDirectoryBackButtonVisibility(boolean isVisible) {
        ImageButton imageButton = getView().findViewById(R.id.dir_nav_bar_back_button);
        if (isVisible) {
            imageButton.setVisibility(View.VISIBLE);
        } else {
            imageButton.setVisibility(View.INVISIBLE);
        }
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
                    String tag = (String)textView.getTag();
                    if (tag.equals(DeckManager.DECK_TAG)) {
                        String deckName = textView.getText().toString();
                        Bundle bundle = new Bundle();
                        bundle.putString("deckName", deckName);
                        Fragment viewDeckFragment = new ViewDeckFragment();
                        viewDeckFragment.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                viewDeckFragment).commit();
                    } else if (tag.equals(DeckManager.FOLDER_TAG)) {
                        String folderName = textView.getText().toString();
                        DeckManager.setCurrentDirectoryPath(DeckManager.getCurrentDirectoryPath() + "/" + folderName);
                        updateDirectoryView();
                        updateBreadcrumbs();
                    }
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

        // this button shows when editing the name of a dir item
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

    private void updateDirectoryView() {
        LinearLayout linearLayout = getView().findViewById(R.id.directory_linear_layout);
        linearLayout.removeAllViews();
        for (String item : DeckManager.getCurrentDirectoryList()) {
            addDirItem(item);
        }
    }

    private void deleteItem() {
        List<View> deleteThisList = new LinkedList<>();
        LinearLayout linearLayout = getView().findViewById(R.id.directory_linear_layout);
        for ( int i = 0; i < linearLayout.getChildCount(); i++ ) {
            View view = linearLayout.getChildAt(i);
            CheckBox checkBox = view.findViewById(R.id.dir_item_select);
            if (checkBox.isChecked()) {
                deleteThisList.add(view);
                TextView textView = view.findViewById(R.id.dir_item_name);
                switch (textView.getTag().toString()) {
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

        for (int i = 0; i < deleteThisList.size(); i++ ) {
            linearLayout.removeView(deleteThisList.get(i));
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
                actionMode.setTitle(selectionCount + " selected");
            } else {
                actionMode.setTitle("Select items ");
            }
        }
    }
}
