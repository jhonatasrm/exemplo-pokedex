package com.jhonatasrm.exemplo_pokedex;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Button button_details;
    EditText pokemon;
    String editTextPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_details = findViewById(R.id.button_details);
        pokemon = findViewById(R.id.pokemon_name);
    }

    // testa se o editText está vazio e emite uma mensagem, caso contrário envia pra próxima tela
    public void getDetails(View view) {
        editTextPokemon = pokemon.getText().toString();
        if(TextUtils.isEmpty(editTextPokemon)){
            pokemon.setError("Empty Field !");
            return;
        }else {
            Intent intent = new Intent(MainActivity.this, DetailsClass.class);
            intent.putExtra("pokemon_name", String.valueOf(pokemon.getText()));
            startActivity(intent);
            this.finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
    }
}
