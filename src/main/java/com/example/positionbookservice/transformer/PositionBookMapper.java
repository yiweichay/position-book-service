package com.example.positionbookservice.transformer;

import com.example.positionbookservice.dto.*;
import com.example.positionbookservice.entity.*;

public class PositionBookMapper {

    public static Events toEventDomain(final EventsDto eventsDto) {
        return Events.builder()
                .events(eventsDto.getEvents().stream().map(PositionBookMapper::toEventDomain).toList())
                .build();
    }

    public static Event toEventDomain(final EventDto eventDto) {
        return Event.builder()
                .id(eventDto.getId())
                .action(toActionTypeDomain(eventDto.getAction()))
                .account(eventDto.getAccount())
                .security(eventDto.getSecurity())
                .quantity(eventDto.getQuantity())
                .build();
    }

    public static PositionsDto toPositionsDto(final Positions positions) {
        return (positions.getPositions() != null) ? PositionsDto.builder()
                .positions(positions.getPositions().stream().map(PositionBookMapper::toPositionDto).toList())
                .build() : null;
    }

    private static PositionDto toPositionDto(final Position position) {
        return PositionDto.builder()
                .account(position.getAccount())
                .security(position.getSecurity())
                .quantity(position.getQuantity())
                .events(position.getEvents().stream()
                        .map(event -> EventDto.builder()
                                .id(event.getId())
                                .action(ActionTypeDto.valueOf(event.getAction().name()))
                                .account(event.getAccount())
                                .security(event.getSecurity())
                                .quantity(event.getQuantity())
                                .build())
                        .toList())
                .build();
    }

    private static ActionType toActionTypeDomain(final ActionTypeDto actionTypeDto) {
        return ActionType.valueOf(actionTypeDto.name());
    }
}
