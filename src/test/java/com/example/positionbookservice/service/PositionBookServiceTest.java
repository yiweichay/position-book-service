package com.example.positionbookservice.service;

import com.example.positionbookservice.entity.ActionType;
import com.example.positionbookservice.entity.Event;
import com.example.positionbookservice.entity.Events;
import com.example.positionbookservice.entity.Positions;
import com.example.positionbookservice.entity.Position;
import com.example.positionbookservice.exception.DuplicatedEventIDBadRequestException;
import com.example.positionbookservice.exception.InvalidTradeEventException;
import com.example.positionbookservice.exception.TradeEventIDNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PositionBookServiceTest {

    @Test
    void testAddBuyEvent() {
        final PositionBookService positionBookService = new PositionBookService();

        Event buyEvent = new Event(1, ActionType.BUY, "ACC1", "SEC1", 100);
        Events events = new Events();
        events.getEvents().add(buyEvent);

        Positions positions = positionBookService.createTradeEvent(events);
        assertNotNull(positions);
        assertEquals(100, positions.getPositions().getFirst().getQuantity());
    }

    @Test
    void testAddSellEvent() {
        final PositionBookService positionBookService = new PositionBookService();

        Event buyEvent = new Event(1, ActionType.BUY, "ACC1", "SEC1", 100);
        Event sellEvent = new Event(2, ActionType.SELL, "ACC1", "SEC1", 50);
        Events events = new Events();
        events.getEvents().add(buyEvent);
        events.getEvents().add(sellEvent);

        Positions positions = positionBookService.createTradeEvent(events);
        assertNotNull(positions);
        assertEquals(50, positions.getPositions().getFirst().getQuantity());
    }

    @Test
    void testAddCancelEvent() {
        final PositionBookService positionBookService = new PositionBookService();

        Event buyEvent = new Event(1, ActionType.BUY, "ACC1", "SEC1", 100);
        Event cancelEvent = new Event(1, ActionType.CANCEL, "ACC1", "SEC1", 0);
        Events events = new Events();
        events.getEvents().add(buyEvent);
        events.getEvents().add(cancelEvent);

        Positions positions = positionBookService.createTradeEvent(events);
        assertNotNull(positions);
        assertEquals(0, positions.getPositions().getFirst().getQuantity());
    }

    @Test
    void testGetPositionSummary() {
        final PositionBookService positionBookService = new PositionBookService();

        Event buyEvent1 = new Event(1, ActionType.BUY, "ACC1", "SEC1", 100);
        Event buyEvent2 = new Event(2, ActionType.BUY, "ACC1", "SEC2", 200);
        Events events = new Events();
        events.getEvents().add(buyEvent1);
        events.getEvents().add(buyEvent2);

        positionBookService.createTradeEvent(events);
        Positions positions = positionBookService.getAllPositions();
        assertNotNull(positions);
        assertEquals(2, positions.getPositions().size());

        for (Position position : positions.getPositions()) {
            if (position.getSecurity().equals("SEC1")) {
                assertEquals(100, position.getQuantity());
            } else if (position.getSecurity().equals("SEC2")) {
                assertEquals(200, position.getQuantity());
            }
        }
    }

    @Test
    void testGetSpecificPositionSummary() {
        final PositionBookService positionBookService = new PositionBookService();

        Event buyEvent1 = new Event(1, ActionType.BUY, "ACC1", "SEC1", 100);
        Event buyEvent2 = new Event(2, ActionType.BUY, "ACC1", "SEC1", 200);
        Events events = new Events();
        events.getEvents().add(buyEvent1);
        events.getEvents().add(buyEvent2);

        positionBookService.createTradeEvent(events);

        assertEquals(300, positionBookService.getSpecificPosition("ACC1", "SEC1").getQuantity());
    }

    @Test
    void throwExceptionWhenIDExists() {
        final PositionBookService positionBookService = new PositionBookService();

        Event buyEvent1 = new Event(1, ActionType.BUY, "ACC1", "SEC1", 100);
        Event buyEvent2 = new Event(1, ActionType.BUY, "ACC1", "SEC1", 50);
        Events events = new Events();
        events.getEvents().add(buyEvent1);
        events.getEvents().add(buyEvent2);

        try {
            positionBookService.createTradeEvent(events);
        } catch (final DuplicatedEventIDBadRequestException e) {
            assertEquals("Event with ID 1 already exists", e.getMessage());
        }
    }

    @Test
    void throwTradeEventIDNotFoundException() {
        final PositionBookService positionBookService = new PositionBookService();

        Event buyEvent1 = new Event(1, ActionType.BUY, "ACC1", "SEC1", 100);
        Event cancelEvent = new Event(2, ActionType.CANCEL, "ACC1", "SEC1", 0);
        Events events = new Events();
        events.getEvents().add(buyEvent1);
        events.getEvents().add(cancelEvent);

        try {
            positionBookService.createTradeEvent(events);
        } catch (final TradeEventIDNotFoundException e) {
            assertEquals("Event with id 2 not found for trade cancellation", e.getMessage());
        }
    }

    @Test
    void throwInvalidTradeEventExceptionWhenCancelWithDifferentAccountOrSecurity() {
        final PositionBookService positionBookService = new PositionBookService();

        Event buyEvent1 = new Event(1, ActionType.BUY, "ACC1", "SEC1", 100);
        Event cancelEvent = new Event(1, ActionType.CANCEL, "ACC2", "SEC2", 0);
        Events events = new Events();
        events.getEvents().add(buyEvent1);

        try {
            positionBookService.createTradeEvent(events);
            positionBookService.addSingleTradeEvent(cancelEvent);
        } catch (final InvalidTradeEventException e) {
            assertEquals("Cannot cancel event with different account or security", e.getMessage());
        }
    }
}
