package com.example.cinema;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Seat {
    int row;
    int column;
    int price;

    @Override
    public String toString() {
        return "{\"row\":" + row + ", \"column\":" + column + ", \"price\":" + price + "}";
    }
}
