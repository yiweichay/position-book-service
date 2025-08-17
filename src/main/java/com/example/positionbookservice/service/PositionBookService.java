package com.example.positionbookservice.service;

import com.example.positionbookservice.entity.*;
import com.example.positionbookservice.exception.DuplicatedEventIDBadRequestException;
import com.example.positionbookservice.exception.InvalidTradeEventException;
import com.example.positionbookservice.exception.TradeEventIDNotFoundException;
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

    public Positions createSingleTradeEvent(final Event event) {
        addSingleTradeEvent(event);
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

    private void addSingleTradeEvent(final Event event) {
        checkIfIDExists(event);
        final PositionBookKey positionBookKey = new PositionBookKey(event.getAccount(), event.getSecurity());
        final List<Event> eventList = positionBook.computeIfAbsent(positionBookKey, k -> new ArrayList<>());
        eventList.add(event);

        int totalQuantity = totalQuantityMap.getOrDefault(positionBookKey, 0);
        switch (event.getAction()) {
            case BUY:
                totalQuantity += event.getQuantity();
                idEventMap.put(event.getId(), event);
                break;
            case SELL:
                totalQuantity -= event.getQuantity();
                idEventMap.put(event.getId(), event);
                break;
            case CANCEL:
                final Event originalEvent = idEventMap.get(event.getId());
                if (originalEvent != null) {
                    if (originalEvent.getAction() == ActionType.BUY) {
                        totalQuantity -= originalEvent.getQuantity();
                    } else if (originalEvent.getAction() == ActionType.SELL) {
                        totalQuantity += originalEvent.getQuantity();
                    }
                    idEventMap.remove(event.getId());
                } else {
                    throw new TradeEventIDNotFoundException("Event with id " + event.getId() + " not found for trade cancellation");
                }
                break;
            default:
                throw new InvalidTradeEventException("Unknown action type: " + event.getAction());
        }
        totalQuantityMap.put(positionBookKey, totalQuantity);
    }

    private void checkIfIDExists(final Event event) {
        if (idEventMap.containsKey(event.getId()) && event.getAction() != ActionType.CANCEL) {
            throw new DuplicatedEventIDBadRequestException("Event with ID " + event.getId() + " already exists");
        }
    }
}
