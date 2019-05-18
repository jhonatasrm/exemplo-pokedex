package com.jhonatasrm.exemplo_pokedex;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailsClass extends AppCompatActivity {

    String pokemon;
    TextView pokemonName, ability, type, comboMoves, movesText;
    WebView description;
    ImageView pokemonImage;
    Spinner movesPokemon;
    LinearLayout linearLayout, linearLayoutProgress;
    String typesText, abilitiesText, getImage, textDescription, arrayCombo, getURL, arrayComboText, movesPokemonText;
    int id;
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);
        initFindViewById();

        Intent intent = getIntent();
        pokemon = intent.getStringExtra("pokemon_name");

        pokemonName.setText(pokemon.toUpperCase());

        initAPIPokedex(pokemon);
    }

    public void initAPIPokedex(String pokemon) {

        // remove espaços em branco na String
        pokemon = pokemon.replaceAll("\\s+", "");

        Ion.with(this)
                .load("https://pokeapi.co/api/v2/pokemon/" + pokemon.toLowerCase() + "/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        typesText = "     ";
                        abilitiesText = " ";

                        // array de tipos
                        JsonArray arrayTypes = result.getAsJsonArray("types");

                        for (int i = 0; i < arrayTypes.size(); i++) {
                            String types = result
                                    .getAsJsonArray("types")
                                    .get(i)
                                    .getAsJsonObject()
                                    .getAsJsonObject("type")
                                    .get("name")
                                    .getAsString();

                            typesText = typesText + types + "\n     ";
                        }

                        for (int i = 0; i < arrayTypes.size(); i++) {
                            String abilities = result
                                    .getAsJsonArray("abilities")
                                    .get(i)
                                    .getAsJsonObject()
                                    .getAsJsonObject("ability")
                                    .get("name")
                                    .getAsString();

                            abilitiesText = abilitiesText + abilities + "\n ";
                        }
                        ability.setText(abilitiesText);
                        type.setText(typesText);

                        // Pegar a imagem
                        getImage = result
                                .get("sprites")
                                .getAsJsonObject()
                                .get("front_default")
                                .getAsString();

                        // apresenta a imagem no ImageView
                        Picasso.get().load(getImage).resize(450, 450).into(pokemonImage);

                        // link para a descrição
                        getURL = result
                                .getAsJsonObject("species")
                                .get("url")
                                .getAsString();
                        // pega URL para descrição
                        description(getURL);

                        // pega movimentos do pokemon
                        JsonArray movesArray = result.getAsJsonArray("moves");
                        List<String> spinnerArray = new ArrayList<>();

                        for (int i = 0; i < movesArray.size(); i++) {
                            movesPokemonText = movesArray
                                    .get(i)
                                    .getAsJsonObject()
                                    .get("move")
                                    .getAsJsonObject()
                                    .get("name")
                                    .getAsString();
                            spinnerArray.add(movesPokemonText);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(DetailsClass.this, android.R.layout.simple_spinner_item, spinnerArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        movesPokemon.setAdapter(adapter);

                        // pegar id
                        id = result.get("id").getAsInt();
                        moves(id);
                    }
                });
    }

    public void description(String link) {

//        final TextView tvdescricao = findViewById(R.id.descricoes);
        Ion.with(this)
                .load(link)// pega todos os poquemons
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        JsonArray arraylinguagem = result.getAsJsonArray("flavor_text_entries");
                        for (int i = 0; i < arraylinguagem.size(); i++) {

                            String language = arraylinguagem
                                    .get(i)
                                    .getAsJsonObject()
                                    .get("language")
                                    .getAsJsonObject()
                                    .get("name")
                                    .getAsString();

                            if (language.equals("en")) {
                                textDescription = arraylinguagem
                                        .get(i)
                                        .getAsJsonObject()
                                        .get("flavor_text")
                                        .getAsString();
                                check = true;

                                // justifica o texto dentro do WebView description
                                String text;
                                text = "<html><body><p align=\"justify\">";
                                text += textDescription;
                                text += "</p></body></html>";
                                description.loadData(text, "text/html", "utf-8");
                            }
                        }

                        if (check == false) {
                            String text;
                            text = "<html><body><p align=\"justify\">";
                            text += textDescription;
                            text += "</p></body></html>";
                            description.loadData(text, "text/html", "utf-8");
                        }
                    }
                });

    }

    public void moves(int idReceived) {
        Ion.with(this)
                .load("https://pokeapi.co/api/v2/move/" + idReceived + "/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        arrayComboText = "  ";

                        try {
                            JsonArray arrayCombos = result.getAsJsonObject("contest_combos")
                                    .getAsJsonObject("normal")
                                    .getAsJsonArray("use_after");

                            for (int i = 0; i < arrayCombos.size(); i++) {

                                arrayCombo = arrayCombos
                                        .get(i)
                                        .getAsJsonObject()
                                        .get("name")
                                        .getAsString();
                                arrayComboText = arrayComboText + arrayCombo + "\n  ";

                            }
                            comboMoves.setText(arrayComboText);

                        } catch (Exception error) {
                            error.printStackTrace();
                            movesText.setVisibility(View.INVISIBLE);
                            comboMoves.setVisibility(View.INVISIBLE);
                        }
                    }
                });

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(5000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        linearLayoutProgress.setVisibility(View.INVISIBLE);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    // inicializa findViewById's
    public void initFindViewById() {
        pokemonName = findViewById(R.id.pokemon_name);
        ability = findViewById(R.id.ability);
        type = findViewById(R.id.type);
        pokemonImage = findViewById(R.id.pokemon_image);
        linearLayoutProgress = findViewById(R.id.linearLayoutProgress);
        linearLayout = findViewById(R.id.linearLayout);
        description = findViewById(R.id.description);
        comboMoves = findViewById(R.id.moves);
        movesText = findViewById(R.id.moves_text);
        movesPokemon = findViewById(R.id.moves_pokemon);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DetailsClass.this, MainActivity.class));
        this.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
