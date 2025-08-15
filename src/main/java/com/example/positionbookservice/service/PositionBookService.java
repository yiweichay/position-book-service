package com.example.positionbookservice.service;

import com.example.positionbookservice.entity.Event;
import com.example.positionbookservice.entity.Events;
import com.example.positionbookservice.entity.Position;
import com.example.positionbookservice.entity.Positions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PositionBookService {

    // account -> security -> list of events
    private final Map<String, List<Event>> positionBook = new HashMap<>();

    public Positions createTradeEvent(final Events events){
        Positions response = new Positions();
        Map<String, List<Event>> tempPositionBook = new HashMap<>();
        Map<String, Integer> tempQuantity = new HashMap<>();

        for ( Event event: events.getEvents()) {
            final String key = event.getAccount() + "-" + event.getSecurity();
            List<Event> eventList = tempPositionBook.computeIfAbsent(key, k -> new ArrayList<>());
            eventList.add(event);

            int currentTotal = tempQuantity.getOrDefault(key, 0);
            switch (event.getAction()) {
                case BUY:
                    currentTotal += event.getQuantity();
                    break;
                case SELL:
                    currentTotal -= event.getQuantity();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown action type: " + event.getAction());
            }
            tempQuantity.put(key, currentTotal);
        }

        for (final String key: tempPositionBook.keySet()) {
            final List<Event> eventLIst = tempPositionBook.get(key);
            final String[] parts = key.split("-", 2);
            final String account = parts[0];
            final String security = parts[1];
            final Position position = Position.builder()
                    .account(account)
                    .security(security)
                    .quantity(tempQuantity.get(key))
                    .events(eventLIst)
                    .build();
            response.getPositions().add(position);
        }
        return response;
    }
}
