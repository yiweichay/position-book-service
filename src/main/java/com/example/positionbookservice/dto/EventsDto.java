package com.example.positionbookservice.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventsDto {
    private List<EventDto> events;
}
