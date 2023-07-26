package com.example.pokemon_store.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Pokemon {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique=true)
    private String name;
    private String price;
    private String imageUrl;
    private String type;
  @ElementCollection
    @CollectionTable(name = "pokemon_abilities", joinColumns = @JoinColumn(name = "pokemon_id"))
    @Column(name = "ability")
    private List<String> abilities;

    public Pokemon(String name, String price, String imageUrl, String type, List<String> abilities) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.type = type;
        this.abilities = abilities;
    }
}



