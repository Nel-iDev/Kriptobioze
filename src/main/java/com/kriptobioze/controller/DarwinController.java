package com.kriptobioze.controller;

import com.kriptobioze.service.DarwinCalculationService;
import com.kriptobioze.service.DarwinCalculationService.CalculationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/darwin")
@CrossOrigin(origins = "*")
public class DarwinController {

    @Autowired
    private DarwinCalculationService darwinCalculationService;

    @PostMapping("/calculate")
    public ResponseEntity<CalculationResult> calculate(
            @RequestParam Double altitudeMeters,
            @RequestParam Double elevationRatePerCentury) {
        try {
            CalculationResult result = darwinCalculationService.calculateTimeToAltitude(altitudeMeters, elevationRatePerCentury);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}