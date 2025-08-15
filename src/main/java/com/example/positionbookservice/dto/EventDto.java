package com.example.positionbookservice.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private int id;
    private ActionTypeDto action;
    private String account;
    private String security;
    private int quantity;
}
