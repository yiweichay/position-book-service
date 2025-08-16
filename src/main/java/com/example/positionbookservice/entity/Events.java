package com.example.positionbookservice.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Events {
    private List<Event> events = new ArrayList<>();
}
