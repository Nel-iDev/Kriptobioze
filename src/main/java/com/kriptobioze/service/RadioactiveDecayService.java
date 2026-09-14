package com.kriptobioze.service;

import com.kriptobioze.model.Isotope;
import com.kriptobioze.repository.IsotopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RadioactiveDecayService {

    @Autowired
    private IsotopeRepository isotopeRepository;

    public DecayResult calculateDecay(String isotopeSymbol, Double timePeriodYears) {
        if (isotopeSymbol == null || isotopeSymbol.isBlank()) {
            throw new IllegalArgumentException("Símbolo do isótopo é obrigatório");
        }
        if (timePeriodYears == null || timePeriodYears <= 0) {
            throw new IllegalArgumentException("Período de tempo deve ser maior que zero");
        }

        Optional<Isotope> isotopeOpt = isotopeRepository.findBySymbol(isotopeSymbol);
        if (isotopeOpt.isEmpty()) {
            throw new IllegalArgumentException("Isótopo não encontrado: " + isotopeSymbol);
        }

        Isotope isotope = isotopeOpt.get();
        Double halfLife = isotope.getHalfLifeYears();
        
        Double remainingFraction = Math.pow(0.5, timePeriodYears / halfLife);
        Double decayedFraction = 1.0 - remainingFraction;
        Double halfLivesElapsed = timePeriodYears / halfLife;

        List<DecayCheckpoint> checkpoints = generateCheckpoints(halfLife, timePeriodYears);

        return new DecayResult(isotope, timePeriodYears, remainingFraction, decayedFraction, halfLivesElapsed, checkpoints);
    }

    private List<DecayCheckpoint> generateCheckpoints(Double halfLife, Double totalTime) {
        List<DecayCheckpoint> checkpoints = new ArrayList<>();
        double step = totalTime / 10.0;
        
        for (double t = 0; t <= totalTime; t += step) {
            double remaining = Math.pow(0.5, t / halfLife);
            checkpoints.add(new DecayCheckpoint(t, remaining));
        }
        
        return checkpoints;
    }

    public List<Isotope> getAllIsotopes() {
        return isotopeRepository.findAll();
    }

    public Isotope getIsotopeBySymbol(String symbol) {
        return isotopeRepository.findBySymbol(symbol)
            .orElseThrow(() -> new IllegalArgumentException("Isótopo não encontrado: " + symbol));
    }

    public static class DecayResult {
        private Isotope isotope;
        private Double timePeriodYears;
        private Double remainingFraction;
        private Double decayedFraction;
        private Double halfLivesElapsed;
        private List<DecayCheckpoint> checkpoints;

        public DecayResult(Isotope isotope, Double timePeriodYears, Double remainingFraction, Double decayedFraction, Double halfLivesElapsed, List<DecayCheckpoint> checkpoints) {
            this.isotope = isotope;
            this.timePeriodYears = timePeriodYears;
            this.remainingFraction = remainingFraction;
            this.decayedFraction = decayedFraction;
            this.halfLivesElapsed = halfLivesElapsed;
            this.checkpoints = checkpoints;
        }

        public Isotope getIsotope() { return isotope; }
        public Double getTimePeriodYears() { return timePeriodYears; }
        public Double getRemainingFraction() { return remainingFraction; }
        public Double getDecayedFraction() { return decayedFraction; }
        public Double getHalfLivesElapsed() { return halfLivesElapsed; }
        public List<DecayCheckpoint> getCheckpoints() { return checkpoints; }
    }

    public static class DecayCheckpoint {
        private Double timeYears;
        private Double remainingFraction;

        public DecayCheckpoint(Double timeYears, Double remainingFraction) {
            this.timeYears = timeYears;
            this.remainingFraction = remainingFraction;
        }

        public Double getTimeYears() { return timeYears; }
        public Double getRemainingFraction() { return remainingFraction; }
    }
}