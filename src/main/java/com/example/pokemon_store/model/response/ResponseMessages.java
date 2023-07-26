package com.example.pokemon_store.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessages<T>{
    T payload;
    LocalDateTime date;
    boolean status;

    public ResponseMessages(T payload, boolean status) {
        this.payload = payload;
        this.date = LocalDateTime.now();
        this.status = status;
    }
}
