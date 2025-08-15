package com.example.positionbookservice.controller;

import com.example.positionbookservice.dto.EventsDto;
import com.example.positionbookservice.dto.PositionsDto;
import com.example.positionbookservice.entity.Positions;
import com.example.positionbookservice.service.PositionBookService;
import com.example.positionbookservice.transformer.PositionBookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
public class PositionBookController {

    @Autowired
    private final PositionBookService positionBookService;

    public PositionBookController(final PositionBookService positionBookService) {
        this.positionBookService = positionBookService;
    }

    @GetMapping("/getPositionSummary")
    public String getPositionSummary() {
        // This is a placeholder for the actual implementation
        return "Position Summary Data";
    }

    @PostMapping("/createEvent")
    public PositionsDto createEvent(@RequestBody EventsDto events) {
        Positions positions = positionBookService.createTradeEvent(PositionBookMapper.toEventDomain(events));
        return null;
    }
}
