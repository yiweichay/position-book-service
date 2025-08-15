package com.example.positionbookservice.transformer;

import com.example.positionbookservice.dto.ActionTypeDto;
import com.example.positionbookservice.dto.EventDto;
import com.example.positionbookservice.dto.EventsDto;
import com.example.positionbookservice.entity.ActionType;
import com.example.positionbookservice.entity.Event;
import com.example.positionbookservice.entity.Events;

public class PositionBookMapper {

    public static Events toEventDomain(final EventsDto eventsDto) {
        return Events.builder()
                .events(eventsDto.getEvents().stream()
                .map(PositionBookMapper::toEventDomain)
                .toList()).build();
    }

    private static Event toEventDomain(final EventDto eventDto) {
        return Event.builder()
                .id(eventDto.getId())
                .action(toActionTypeDomain(eventDto.getAction()))
                .account(eventDto.getAccount())
                .security(eventDto.getSecurity())
                .quantity(eventDto.getQuantity())
                .build();
    }

    private static ActionType toActionTypeDomain(final ActionTypeDto actionTypeDto) {
        return ActionType.valueOf(actionTypeDto.name());
    }
}
