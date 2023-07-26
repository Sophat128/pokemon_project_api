package com.example.pokemon_store.repository;

import com.example.pokemon_store.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    Pokemon findPokemonByName(String name);

    void deletePokemonByName(String name);
}
