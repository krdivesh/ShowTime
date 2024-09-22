package com.example.cinema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


@RestController
public class SeatController {

    private final CinemaSeats cinemaSeats = new CinemaSeats();

    private final ObjectMapper objectMapper;

    @Autowired
    public SeatController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping("/seats")
    public CinemaSeats getSeats() {
        return cinemaSeats;
    }

    @PostMapping("/purchase")
    public ResponseEntity<String> purchaseSeats(@RequestBody Seat purchaseSeat) throws JsonProcessingException {
        if (purchaseSeat.getRow()>9 || purchaseSeat.getColumn()>9 ||
                purchaseSeat.getRow()<1 || purchaseSeat.getColumn()<1) {
            return ResponseEntity.status(400).body("{\n" +
                    "    \"error\": \"The number of a row or a column is out of bounds!\"\n" +
                    "}");
        }

        synchronized (cinemaSeats) {
            for(Seat seat:cinemaSeats.getSeats()) {
                if (seat.getColumn() == purchaseSeat.getColumn()
                        && seat.getRow() == purchaseSeat.getRow()) {
                    cinemaSeats.getSeats().remove(seat);
                    String uuidToken = UUID.randomUUID().toString();
                    cinemaSeats.getTickets().put(uuidToken, seat);
                    cinemaSeats.setIncome(cinemaSeats.getIncome()+ seat.getPrice());
                    return ResponseEntity.ok().body(objectMapper.writeValueAsString(
                            Map.of("token", uuidToken,
                                    "ticket", seat)));
                }
            }
        }

        return ResponseEntity.status(400).body("{\n" +
                "    \"error\": \"The ticket has been already purchased!\"\n" +
                "}");
    }

    @PostMapping("/return")
    public ResponseEntity<String> returnTicket(@RequestBody Token token) throws JsonProcessingException {
        String tokenKey = token.getToken();
        synchronized (cinemaSeats) {
            if (cinemaSeats.getTickets().containsKey(tokenKey)) {
                Seat seatVacated = cinemaSeats.getTickets().get(tokenKey);
                cinemaSeats.getSeats().add(seatVacated);
                cinemaSeats.getTickets().remove(tokenKey);
                cinemaSeats.setIncome(cinemaSeats.getIncome()-seatVacated.getPrice());
                return ResponseEntity.ok().body(objectMapper
                        .writeValueAsString(Map.of("ticket", seatVacated)));
            } else {
                return ResponseEntity.status(400)
                        .body(objectMapper.writeValueAsString(Map.of("error", "Wrong token!")));
            }
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<String> getStats(@RequestParam(required = false) String password) throws JsonProcessingException {
        if (password!=null && password.equals("super_secret")) {
            return ResponseEntity.ok().body(objectMapper
                    .writeValueAsString(Map.of("income", cinemaSeats.getIncome(),
                            "available", cinemaSeats.getSeats().size(),
                            "purchased", cinemaSeats.getTickets().size())));
        }

        return ResponseEntity.status(401).body(objectMapper
                .writeValueAsString(Map.of("error","The password is wrong!")));
    }
}
