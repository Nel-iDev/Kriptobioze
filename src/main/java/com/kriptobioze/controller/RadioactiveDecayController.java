package com.kriptobioze.controller;

import com.kriptobioze.model.Isotope;
import com.kriptobioze.service.RadioactiveDecayService;
import com.kriptobioze.service.RadioactiveDecayService.DecayResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/radioactive-decay")
@CrossOrigin(origins = "*")
public class RadioactiveDecayController {

    @Autowired
    private RadioactiveDecayService radioactiveDecayService;

    @GetMapping("/isotopes")
    public ResponseEntity<List<Isotope>> getAllIsotopes() {
        return ResponseEntity.ok(radioactiveDecayService.getAllIsotopes());
    }

    @GetMapping("/isotopes/{symbol}")
    public ResponseEntity<Isotope> getIsotope(@PathVariable String symbol) {
        return ResponseEntity.ok(radioactiveDecayService.getIsotopeBySymbol(symbol));
    }

    @PostMapping("/calculate")
    public ResponseEntity<DecayResult> calculateDecay(
            @RequestParam String isotopeSymbol,
            @RequestParam Double timePeriodYears) {
        try {
            DecayResult result = radioactiveDecayService.calculateDecay(isotopeSymbol, timePeriodYears);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}