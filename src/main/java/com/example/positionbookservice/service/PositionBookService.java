package com.example.positionbookservice.service;

import com.example.positionbookservice.entity.*;
import com.example.positionbookservice.exception.InvalidTradeEventException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PositionBookService {

    private final Map<String, List<Event>> positionBook = new HashMap<>();
    private final Map<String, Integer> totalQuantityMap = new HashMap<>();

    public Positions createTradeEvent(final Events events){
        final Map<Integer, Event> idToEventMap = new HashMap<>();

        for ( final Event event: events.getEvents()) {
            final String key = event.getAccount() + "-" + event.getSecurity();
            final List<Event> persistedEventList = positionBook.computeIfAbsent(key, k -> new ArrayList<>());
            persistedEventList.add(event);

            int persistedTotal = totalQuantityMap.getOrDefault(key, 0);
            switch (event.getAction()) {
                case BUY:
                    persistedTotal += event.getQuantity();
                    idToEventMap.put(event.getId(), event);
                    break;
                case SELL:
                    persistedTotal -= event.getQuantity();
                    idToEventMap.put(event.getId(), event);
                    break;
                case CANCEL:
                    final Event originalEvent = idToEventMap.get(event.getId());
                    if (originalEvent != null) {
                        if (originalEvent.getAction() == ActionType.BUY) {
                            persistedTotal -= originalEvent.getQuantity();
                        } else if (originalEvent.getAction() == ActionType.SELL) {
                            persistedTotal += originalEvent.getQuantity();
                        }
                        idToEventMap.remove(event.getId());
                    }
                    break;
                default:
                    throw new InvalidTradeEventException("Unknown action type: " + event.getAction());
            }
            totalQuantityMap.put(key, persistedTotal);
        }
        return getAllPositions();
    }

    public Positions getAllPositions() {
        final Positions response = new Positions();
        for (Map.Entry<String, List<Event>> entry : positionBook.entrySet()) {
            final String key = entry.getKey();
            final List<Event> events = entry.getValue();
            final String[] parts = key.split("-", 2);
            final String account = parts[0];
            final String security = parts[1];
            final int quantity = totalQuantityMap.getOrDefault(key, 0);

            final Position position = Position.builder()
                    .account(account)
                    .security(security)
                    .quantity(quantity)
                    .events(events)
                    .build();
            response.getPositions().add(position);
        }
        return response;
    }
}
