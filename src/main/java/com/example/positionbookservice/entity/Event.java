package com.example.positionbookservice.entity;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private int id;
    private ActionType action;
    private String account;
    private String security;
    private int quantity;
}
