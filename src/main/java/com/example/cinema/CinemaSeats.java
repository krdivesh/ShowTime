package com.example.cinema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class CinemaSeats {
    private int rows;
    private int columns;
    private final List<Seat> seats = new ArrayList<>();
    @JsonIgnore
    private final Map<String, Seat> tickets = new HashMap<>();
    @JsonIgnore
    private int income;

    public CinemaSeats() {
        this.rows = 9;
        this.columns = 9;
        this.income = 0;
        for(int i = 0; i< this.rows; i++) {
            int price = i<=3?10:8;
            for(int j = 0; j< this.columns; j++) {
                seats.add(new Seat(i+1, j+1, price));
            }
        }
    }
}
