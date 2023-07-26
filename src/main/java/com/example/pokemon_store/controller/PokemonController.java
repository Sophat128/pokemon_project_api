package com.example.pokemon_store.controller;

import com.example.pokemon_store.model.Pokemon;
import com.example.pokemon_store.model.response.ResponseMessages;
import com.example.pokemon_store.service.PokemonService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pokemon")
@AllArgsConstructor
public class PokemonController {

    private final PokemonService pokemonService;

    @GetMapping
    public ResponseEntity<?> getAllPokemon() throws IOException {
        ResponseMessages<?> responseData = new ResponseMessages<>(
                pokemonService.getAllPokemon(),
                true
        );
        return ResponseEntity.ok().body(responseData);
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getPokemonByName(@PathVariable String name) {
        ResponseMessages<?> responseData = new ResponseMessages<>(
                pokemonService.getPokemonByName(name),
                true
        );
        return ResponseEntity.ok().body(responseData);
    }

    @GetMapping("/getAllDadaPagination")
    public ResponseEntity<?> getAllDataPagination() throws IOException {
        ResponseMessages<?> responseData = new ResponseMessages<>(
                pokemonService.getAllDataPagination(),
                true
        );
        return ResponseEntity.ok().body(responseData);

    }

    @PostMapping("/addPokemon")
    public ResponseEntity<?> addPokemon(@RequestBody Pokemon pokemonRequest) {
        ResponseMessages<?> responseData = new ResponseMessages<>(
                pokemonService.addPokemon(pokemonRequest),
                true
        );
        return ResponseEntity.ok().body(responseData);

    }

    @PutMapping("/{name}")
    public ResponseEntity<?> updatePokemon(@PathVariable String name, @RequestBody Pokemon requestPokemon) {
        ResponseMessages<?> responseData = new ResponseMessages<>(
                pokemonService.updatePokemon(name, requestPokemon),
                true
        );
        return ResponseEntity.ok().body(responseData);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deletePokemon(@PathVariable String name) {
        ResponseMessages<?> responseData = new ResponseMessages<>(
                pokemonService.deletePokemon(name),
                true
        );
        return ResponseEntity.ok().body(responseData);
    }

}
