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
    TextView pokemonName, ability, type, ability1, ability2;
    WebView description, descriptionAbility1, descriptionAbility2;
    ImageView pokemonImage;
    Spinner movesPokemon;
    LinearLayout linearLayout, linearLayoutProgress;
    String typesText, abilitiesText, getImage, textDescription, getURL, movesPokemonText, textDescriptionAbility;
    JsonArray arrayTypes, positionAbilities;
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

        // se não existe é enviado para a MainActivity
        final String notFound = pokemon;

        Ion.with(this)
                .load("https://pokeapi.co/api/v2/pokemon/" + pokemon.toLowerCase() + "/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        typesText = "     ";
                        abilitiesText = " ";

                        try {
                            // array de tipos
                            arrayTypes = result.getAsJsonArray("types");
                        }catch (Exception error){
                            Intent pokemonNotFound = new Intent(DetailsClass.this, MainActivity.class);
                            pokemonNotFound.putExtra("notFound", notFound);
                            setResult(RESULT_OK, pokemonNotFound);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                            finish();
                        }

                        if(arrayTypes == null){
                            return;
                        }

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

                        // salva o Array abilities para comparar no laço for até chegar a seu tamanho
                        positionAbilities = result.getAsJsonArray("abilities");
                        for (int i = 0; i < positionAbilities.size(); i++) {
                            String abilities = result
                                    .getAsJsonArray("abilities")
                                    .get(i)
                                    .getAsJsonObject()
                                    .getAsJsonObject("ability")
                                    .get("name")
                                    .getAsString();

                            abilitiesText = abilitiesText + abilities + "\n ";

                            // pega as habilidades
                            if(i == 0){
                                ability1.setText(abilities);
                            }
                            if(i == 1){
                                // na posição 1 do Array deixamos o TextView ability2 visivel
                                ability2.setVisibility(View.VISIBLE);
                                ability2.setText(abilities);
                            }
                        }
                        ability.setText(abilitiesText);
                        type.setText(typesText);

                        for (int i = 0; i < positionAbilities.size(); i++) {
                            String abilitiesURL = result
                                    .getAsJsonArray("abilities")
                                    .get(i)
                                    .getAsJsonObject()
                                    .getAsJsonObject("ability")
                                    .get("url")
                                    .getAsString();

                            // envia a URL e a posição da habilidade
                            getAbilitiesDescription(abilitiesURL, i);
                        }

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
                    }
                });
    }

    // pegar a descrição do pokémon utilizando o link obtido da URL anterior
    public void description(String link) {

        Ion.with(this)
                .load(link)
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

        // Thread para garantir carregamento
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

    public void getAbilitiesDescription(String abilityURL, final int position){
        Ion.with(this)
                .load(abilityURL)
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
                                textDescriptionAbility = arraylinguagem
                                        .get(i)
                                        .getAsJsonObject()
                                        .get("flavor_text")
                                        .getAsString();
                                check = true;

                                // justifica o texto dentro do WebView descriptionAbility1 e descriptionAbility2
                                String text;
                                text = "<html><body><p align=\"justify\">";
                                text += textDescriptionAbility;
                                text += "</p></body></html>";

                                // verifica a posição passada
                                if(position == 0) {
                                    descriptionAbility1.loadData(text, "text/html", "utf-8");
                                }
                                if(position == 1){
                                    // na posição 1 do Array deixamos o TextView descriptionAbility2 visivel
                                    descriptionAbility2.setVisibility(View.VISIBLE);
                                    descriptionAbility2.loadData(text, "text/html", "utf-8");
                                }
                            }
                        }

                        if (check == false) {
                            String text;
                            text = "<html><body><p align=\"justify\">";
                            text += textDescriptionAbility;
                            text += "</p></body></html>";
                            description.loadData(text, "text/html", "utf-8");
                        }
                    }
                });

        // Thread para garantir carregamento
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
        movesPokemon = findViewById(R.id.moves_pokemon);
        descriptionAbility1 = findViewById(R.id.description_ability1);
        descriptionAbility2 = findViewById(R.id.description_ability2);
        ability1 = findViewById(R.id.ability1);
        ability2 = findViewById(R.id.ability2);
    }

    // método com animação na trasição entre activities
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DetailsClass.this, MainActivity.class));
        this.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
