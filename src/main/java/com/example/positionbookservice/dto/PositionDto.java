package com.example.positionbookservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionDto {
    private String account;
    private String security;
    private int quantity;
    private List<EventDto> events;
}
