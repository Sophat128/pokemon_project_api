package com.example.pokemon_store.service;

import com.example.pokemon_store.model.Pokemon;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface PokemonService {
//    List<Pokemon> scrapePokemons() throws IOException;
    List<Element> getElementsByIdentifier(Document document , String identifier, IdentifierType identifiertype);
    Document getDocumentFromURL(URL resourceUrl) throws IOException;
    List<Element> crawl(URL initialUrl, int maxVisits, String pageLinkSelectorQuery) throws IOException;

    List<Pokemon> getAllDataPagination() throws IOException;

    Pokemon addPokemon(Pokemon pokemonRequest);

    List<Pokemon> getAllPokemon();

    Pokemon getPokemonByName(String name);

    Pokemon updatePokemon(String name, Pokemon requestPokemon);

    String deletePokemon(String name);
}

