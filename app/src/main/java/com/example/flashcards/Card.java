package com.example.flashcards;

import org.json.JSONException;
import org.json.JSONObject;

public class Card {
    public static final String TERM = "term";
    public static final String DEFINITION = "definition";
    private String term, definition;
    public Card(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    public Card() {
        this("", "");
    }

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }

    public JSONObject getJSONObject() {
        JSONObject jsonCard = new JSONObject();
        try {
            jsonCard.put(TERM, getTerm());
            jsonCard.put(DEFINITION, getDefinition());
        } catch (JSONException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return jsonCard;
    }
}
