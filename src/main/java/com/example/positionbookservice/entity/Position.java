package com.example.positionbookservice.entity;

import com.example.positionbookservice.dto.EventDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    private String account;
    private String security;
    private int quantity;
    private List<Event> events;
}
