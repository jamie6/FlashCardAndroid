package com.example.flashcards;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditDeckFragment extends Fragment {
    private List<Item> list = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_deck, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = getView().findViewById(R.id.new_card);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCard();
            }
        });
        List<Item> list = new ArrayList<>();
        addCard();
    }

    @Override
    public void onStop() {
        prepareList();
        String contents = getContents();
        Log.d("editdeck", contents);
        EditText nameEditText = getView().findViewById(R.id.edit_deck_name_edit_text);
        String deckName = nameEditText.getText().length() > 0 ? nameEditText.getText().toString() : getResources().getString(R.string.default_deck_name);
        deckName = DeckManager.incrementFileName(DeckManager.getDeckNamesInCurrentDirectory(), deckName);

        try {
            DeckManager.createNewDeck(deckName, contents);
        } catch (IOException e) {
            Log.d("editdeck", e.getMessage());
        }
        super.onStop();
    }

    private void prepareList() {
        View view = getView();
        LinearLayout linearLayout = view.findViewById(R.id.cards_linear_layout);
        View editCard;
        Item item;
        EditText termEditText;
        EditText definitionEditText;
        for (int i = 0; i < linearLayout.getChildCount() && i < list.size(); i++) {
            editCard = linearLayout.getChildAt(i);
            item = list.get(i);

            termEditText = editCard.findViewById(R.id.termEditText);
            definitionEditText = editCard.findViewById(R.id.definitionEditText);
            item.setTerm(termEditText.getText().toString());
            item.setDefinition(definitionEditText.getText().toString());
        }
    }

    private String getContents() {
        StringBuilder output = new StringBuilder();

        output.append("[\n");
        for (Item item : list) {
            output.append("{\n\"term\":\"");
            output.append(item.getTerm());
            output.append("\",\n");

            output.append("\"definition\":\"");
            output.append(item.getDefinition());
            output.append("\"\n},\n");
        }
        output.append("]");

        return output.toString();
    }

    private void addCard() {
        Context context = getContext();
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cardEditView = layoutInflater.inflate(R.layout.card_edit, null);

        Item item = new Item();
        list.add(item);
        cardEditView.setTag(item);

        View view = getView();
        LinearLayout linearLayout = view.findViewById(R.id.cards_linear_layout);
        linearLayout.addView(cardEditView);
    }

    public class Item {
        private String term, definition;

        public Item() {
            setTerm("");
            setDefinition("");
        }

        public Item(String term, String definition) {
            setTerm(term);
            setDefinition(definition);
        }
        public String getTerm() { return term; }
        public void setTerm(String term) { this.term = term; }
        public String getDefinition() { return definition; }
        public void setDefinition(String definition) { this.definition = definition; }
    }
}
