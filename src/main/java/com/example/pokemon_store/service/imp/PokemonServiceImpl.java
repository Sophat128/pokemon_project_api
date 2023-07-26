package com.example.pokemon_store.service.imp;

import com.example.pokemon_store.exception.InternalServerErrorException;
import com.example.pokemon_store.exception.NotFoundExceptionClass;
import com.example.pokemon_store.model.Pokemon;
import com.example.pokemon_store.model.PokemonData;
import com.example.pokemon_store.repository.PokemonRepository;
import com.example.pokemon_store.service.IdentifierType;
import com.example.pokemon_store.service.PokemonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@AllArgsConstructor
public class PokemonServiceImpl implements PokemonService {
    private final PokemonRepository pokemonRepository;


    // Crawl function API
    public List<Element> crawl(URL initialUrl, int maxVisits, String pageLinkSelectorQuery) throws IOException {

        List<Element> scrapedElements = new ArrayList<>();
        Set<String> visitedPages = new HashSet<>();
        crawlPages(initialUrl.toString(), visitedPages, maxVisits, pageLinkSelectorQuery, scrapedElements);
        return scrapedElements;
    }

    @Override
    public List<Pokemon> getAllDataPagination() throws IOException {
        String pageLinkCSSQuery = ".page-numbers>li>a";
        int maxVisits = 48;
        String firstPageUrl = "https://scrapeme.live/shop/page/1/";
        List<Element> elements = crawl(new URL(firstPageUrl), maxVisits, pageLinkCSSQuery);

        List<Pokemon> allPokemonData = new ArrayList<>();
        for (Element element : elements) {
            String productNameClassName = "woocommerce-loop-product__title";
            String productPriceClassName = "woocommerce-Price-amount amount";
            String extractedProductName = element.getElementsByClass(productNameClassName).text();
            String extractedProductPrice = element.getElementsByClass(productPriceClassName).text();
            String extractedProductImage = element.selectFirst("img.attachment-woocommerce_thumbnail").attr("src");
            String typeAndAbilities = getTypeAndAbilities(extractedProductName);
            System.out.println(typeAndAbilities);
            PokemonData pokemonData = extractTypeAndAbilities(typeAndAbilities);
            String type = pokemonData.getType();
            List<String> abilities = pokemonData.getAbilities();
            Pokemon pokemon = new Pokemon(extractedProductName, extractedProductPrice, extractedProductImage, type, abilities);
            addPokemon(pokemon);
            allPokemonData.add(pokemon);
        }
        System.out.println("Total Products Scraped from crawling " + maxVisits + " pages: " + elements.size());
        return allPokemonData;
    }

    @Override
    public Pokemon addPokemon(Pokemon pokemonRequest) {
        Pokemon existName = pokemonRepository.findPokemonByName(pokemonRequest.getName());
        if(existName == null){
        return pokemonRepository.save(pokemonRequest);

        }else {
            throw new InternalServerErrorException("This name is token!");
        }
    }

    @Override

    public List<Pokemon> getAllPokemon() {
        return pokemonRepository.findAll();
    }

    @Override
    public Pokemon getPokemonByName(String name) {
        if (pokemonRepository.findPokemonByName(name) == null) {
            throw new NotFoundExceptionClass("This name doesn't exist!");
        }
        return pokemonRepository.findPokemonByName(name);
    }

    @Override
    public Pokemon updatePokemon(String name, Pokemon requestPokemon) {
        Pokemon existName = pokemonRepository.findPokemonByName(requestPokemon.getName());

        Pokemon updatePokemon = getPokemonByName(name);
        updatePokemon.setName(requestPokemon.getName());
        updatePokemon.setPrice(requestPokemon.getPrice());
        updatePokemon.setType(requestPokemon.getType());
        updatePokemon.setImageUrl(requestPokemon.getImageUrl());
        updatePokemon.setAbilities(requestPokemon.getAbilities());
        if(existName == null){
        return pokemonRepository.save(updatePokemon);

        }else{
            throw new InternalServerErrorException("This name is token!");
        }
    }

    @Override
    @Transactional
    public String deletePokemon(String name) {
        getPokemonByName(name);
        pokemonRepository.deletePokemonByName(name);
        return "Delete Successfully";
    }


    // Helper function to recursively visit pages
    private void crawlPages(String currUrl, Set<String> visited, int maxVisits, String pageLinkSelectorQuery, List<Element> elements) throws IOException {

        if (visited.size() == maxVisits) {
            return;
        }
        // mark the url visited
        visited.add(currUrl);

        // get page links
        Document currPageHtml = getDocumentFromURL(new URL(currUrl));
        List<Element> pageLinks = currPageHtml.select(pageLinkSelectorQuery);

        // populate elements
        String classNameForProductCard = "product";
        List<Element> productCards = getElementsByIdentifier(currPageHtml, classNameForProductCard, IdentifierType.CLASS);

        // add curr page elements to elements list
        elements.addAll(productCards);

        for (Element link : pageLinks) {
            String nextUrl = link.attr("href");
            if (!visited.contains(nextUrl)) {
                crawlPages(nextUrl, visited, maxVisits, pageLinkSelectorQuery, elements);
            }
        }
    }

    public Document getDocumentFromURL(URL resourceUrl) throws IOException {
        return Jsoup.connect(resourceUrl.toString()).get();
    }

    public List<Element> getElementsByIdentifier(Document document, String identifier, IdentifierType identifiertype) {
        List<Element> elements = new ArrayList<>();

        switch (identifiertype) {
            case ID -> {
                elements.add(document.getElementById(identifier));
                return elements;
            }
            case TAG -> {
                return document.getElementsByTag(identifier);
            }
            case ATTRIBUTE -> {
                return document.getElementsByAttribute(identifier);
            }
            case CLASS -> {
                return document.getElementsByClass(identifier);
            }
            default -> System.out.println("Not a valid Identifier type");
        }

        return elements;
    }


    private String getTypeAndAbilities(String name) {
        OpenAiService service = new OpenAiService("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");
        // Create a Map representing the JSON object
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> jsonMap = Map.of(
                "name", name,
                "type", "",
                "abilities", "[]"
        );

        // Convert the Map to a JSON string
//        String jsonString = "";
//        try {
//            jsonString = objectMapper.writeValueAsString(jsonMap);
//        } catch (Exception e) {
//            System.err.println("Error converting to JSON: " + e.getMessage());
//            return e.getMessage();
//        }
//        ChatMessage chatMessages = new ChatMessage(ChatMessageRole.USER.value(), "Provide the data in this format " + jsonString);
        ChatMessage chatMessages = new ChatMessage(ChatMessageRole.USER.value(), "Generate only the name, type and provide the abilities as an array for " + name + " in json format  of");
        ChatCompletionRequest completionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo-0613")
                .messages(List.of(chatMessages))
                .build();
        Stream<String> stringStream = service.createChatCompletion(completionRequest).getChoices().
                stream().map(chatCompletionChoice -> chatCompletionChoice.getMessage().getContent());
        return stringStream.collect(Collectors.joining());
    }

    private PokemonData extractTypeAndAbilities(String data) throws JsonProcessingException {
        // Create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        // Deserialize the JSON string to a Pokemon object
        return objectMapper.readValue(data, PokemonData.class);

//        Gson gson = new Gson();
//        return gson.fromJson(data, PokemonData.class);
    }


//
}
