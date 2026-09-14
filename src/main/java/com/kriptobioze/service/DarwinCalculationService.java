package com.kriptobioze.service;

import org.springframework.stereotype.Service;

@Service
public class DarwinCalculationService {

    public CalculationResult calculateTimeToAltitude(Double altitudeMeters, Double elevationRatePerCentury) {
        if (altitudeMeters == null || altitudeMeters <= 0) {
            throw new IllegalArgumentException("Altitude deve ser maior que zero");
        }
        if (elevationRatePerCentury == null || elevationRatePerCentury <= 0) {
            throw new IllegalArgumentException("Taxa de elevação deve ser maior que zero");
        }

        Double elevationRatePerYear = elevationRatePerCentury / 100.0;
        Double yearsRequired = altitudeMeters / elevationRatePerYear;
        Double millionsOfYears = yearsRequired / 1_000_000.0;
        String classification = classifyTimeScale(millionsOfYears);

        return new CalculationResult(altitudeMeters, elevationRatePerCentury, yearsRequired, millionsOfYears, classification);
    }

    private String classifyTimeScale(Double millionsOfYears) {
        if (millionsOfYears < 0.001) {
            return "Escala humana - resultado em décadas/séculos";
        } else if (millionsOfYears < 1) {
            return "Escala geológica curta - milhares a centenas de milhares de anos";
        } else if (millionsOfYears < 100) {
            return "Escala geológica média - milhões de anos (período)";
        } else if (millionsOfYears < 500) {
            return "Escala geológica longa - dezenas de milhões de anos (era)";
        } else {
            return "Escala geológica profunda - centenas de milhões a bilhões de anos";
        }
    }

    public static class CalculationResult {
        private Double altitudeMeters;
        private Double elevationRatePerCentury;
        private Double yearsRequired;
        private Double millionsOfYears;
        private String classification;

        public CalculationResult(Double altitudeMeters, Double elevationRatePerCentury, Double yearsRequired, Double millionsOfYears, String classification) {
            this.altitudeMeters = altitudeMeters;
            this.elevationRatePerCentury = elevationRatePerCentury;
            this.yearsRequired = yearsRequired;
            this.millionsOfYears = millionsOfYears;
            this.classification = classification;
        }

        public Double getAltitudeMeters() { return altitudeMeters; }
        public Double getElevationRatePerCentury() { return elevationRatePerCentury; }
        public Double getYearsRequired() { return yearsRequired; }
        public Double getMillionsOfYears() { return millionsOfYears; }
        public String getClassification() { return classification; }
    }
}