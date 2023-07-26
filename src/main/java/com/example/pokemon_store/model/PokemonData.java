package com.example.pokemon_store.model;

import lombok.Data;

import java.util.List;
@Data
public class PokemonData {
    private String name;
    private String type;
    private List<String> abilities;
}
