package com.example.positionbookservice.controller;

import com.example.positionbookservice.dto.EventDto;
import com.example.positionbookservice.dto.EventsDto;
import com.example.positionbookservice.dto.PositionDto;
import com.example.positionbookservice.dto.PositionsDto;
import com.example.positionbookservice.service.PositionBookService;
import com.example.positionbookservice.transformer.PositionBookMapper;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class PositionBookController {

    private final PositionBookService positionBookService;

    public PositionBookController(final PositionBookService positionBookService) {
        this.positionBookService = positionBookService;
    }

    @GetMapping("/getPositionSummary")
    public PositionsDto getPositionSummary() {
        return PositionBookMapper.toPositionsDto(positionBookService.getAllPositions());
    }

    @GetMapping("/getPosition/{account}/{security}")
    public PositionDto getPosition(final @PathVariable("account") String account,
                                   final @PathVariable("security") String security) {
        return PositionBookMapper.toPositionDto(positionBookService.getSpecificPosition(account, security));
    }

    @PostMapping("/createEvent")
    public PositionsDto createEvent(final @RequestBody EventsDto events) {
        return PositionBookMapper.toPositionsDto(positionBookService.createTradeEvent(PositionBookMapper.toEventDomain(events)));
    }

    @PostMapping("/createSingleEvent")
    public PositionsDto createSingleEvent(final @RequestBody EventDto event) {
        return PositionBookMapper.toPositionsDto(positionBookService.createSingleTradeEvent(PositionBookMapper.toEventDomain(event)));
    }
}
