package com.example.positionbookservice.service;

import com.example.positionbookservice.entity.*;
import com.example.positionbookservice.exception.InvalidTradeEventException;
import com.example.positionbookservice.exception.TradeEventNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PositionBookService {

    private final Map<PositionBookKey, List<Event>> positionBook = new HashMap<>();
    private final Map<PositionBookKey, Integer> totalQuantityMap = new HashMap<>();
    private final Map<Integer, Event> idEventMap = new HashMap<>();

    public Positions createTradeEvent(final Events events){
        for ( final Event event: events.getEvents()) {
            addSingleTradeEvent(event);
        }
        return getAllPositions();
    }

    public Positions getAllPositions() {
        final Positions response = new Positions();
        for (Map.Entry<PositionBookKey, List<Event>> entry : positionBook.entrySet()) {
            final PositionBookKey key = entry.getKey();
            final List<Event> events = entry.getValue();
            final String account = key.getAccount();
            final String security = key.getSecurity();
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

    public Positions createSingleTradeEvent(final Event event) {
        addSingleTradeEvent(event);
        return getAllPositions();
    }

    private void addSingleTradeEvent(final Event event) {
        final PositionBookKey positionBookKey = new PositionBookKey(event.getAccount(), event.getSecurity());
        final List<Event> persistedEventList = positionBook.computeIfAbsent(positionBookKey, k -> new ArrayList<>());
        persistedEventList.add(event);

        int persistedTotal = totalQuantityMap.getOrDefault(positionBookKey, 0);
        switch (event.getAction()) {
            case BUY:
                persistedTotal += event.getQuantity();
                idEventMap.put(event.getId(), event);
                break;
            case SELL:
                persistedTotal -= event.getQuantity();
                idEventMap.put(event.getId(), event);
                break;
            case CANCEL:
                final Event originalEvent = idEventMap.get(event.getId());
                if (originalEvent != null) {
                    if (originalEvent.getAction() == ActionType.BUY) {
                        persistedTotal -= originalEvent.getQuantity();
                    } else if (originalEvent.getAction() == ActionType.SELL) {
                        persistedTotal += originalEvent.getQuantity();
                    }
                    idEventMap.remove(event.getId());
                } else {
                    throw new TradeEventNotFoundException("Event with id " + event.getId() + " not found");
                }
                break;
            default:
                throw new InvalidTradeEventException("Unknown action type: " + event.getAction());
        }
        totalQuantityMap.put(positionBookKey, persistedTotal);
    }
}
