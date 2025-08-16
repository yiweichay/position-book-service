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
        final Positions response = new Positions();
        final Map<String, List<Event>> tempPositionBook = new HashMap<>();
        final Map<String, Integer> tempQuantity = new HashMap<>();
        final Map<Integer, Event> idToEventMap = new HashMap<>();

        for ( final Event event: events.getEvents()) {
            final String key = event.getAccount() + "-" + event.getSecurity();
            final List<Event> eventList = tempPositionBook.computeIfAbsent(key, k -> new ArrayList<>());
            final List<Event> persistedEventList = positionBook.computeIfAbsent(key, k -> new ArrayList<>());
            eventList.add(event);
            persistedEventList.add(event);

            int currentTotal = tempQuantity.getOrDefault(key, 0);
            int persistedTotal = totalQuantityMap.getOrDefault(key, 0);
            switch (event.getAction()) {
                case BUY:
                    currentTotal += event.getQuantity();
                    persistedTotal += event.getQuantity();
                    idToEventMap.put(event.getId(), event);
                    break;
                case SELL:
                    currentTotal -= event.getQuantity();
                    persistedTotal -= event.getQuantity();
                    idToEventMap.put(event.getId(), event);
                    break;
                case CANCEL:
                    final Event originalEvent = idToEventMap.get(event.getId());
                    if (originalEvent != null) {
                        if (originalEvent.getAction() == ActionType.BUY) {
                            currentTotal -= originalEvent.getQuantity();
                            persistedTotal -= originalEvent.getQuantity();
                        } else if (originalEvent.getAction() == ActionType.SELL) {
                            currentTotal += originalEvent.getQuantity();
                            persistedTotal += originalEvent.getQuantity();
                        }
                        idToEventMap.remove(event.getId());
                    }
                    break;
                default:
                    throw new InvalidTradeEventException("Unknown action type: " + event.getAction());
            }
            tempQuantity.put(key, currentTotal);
            totalQuantityMap.put(key, persistedTotal);
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
