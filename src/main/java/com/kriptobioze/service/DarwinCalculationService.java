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

        // 1) Converter a altitude para centímetros (1 m = 100 cm) e a taxa
        //    de cm/século para cm/ano (1 século = 100 anos).
        Double altitudeCm = altitudeMeters * 100.0;
        Double elevationRatePerYear = elevationRatePerCentury / 100.0;

        // 2) Cálculo de proporção: idade mínima (anos) para a rocha se
        //    erguer até o topo = altitude (cm) / taxa de soerguimento (cm/ano).
        Double yearsRequired = altitudeCm / elevationRatePerYear;
        Double millionsOfYears = yearsRequired / 1_000_000.0;

        // 3) Estrutura de controle de fluxo condicional (if-else) que
        //    classifica o resultado conforme o texto da atividade.
        String classification = classifyTimeScale(yearsRequired);

        return new CalculationResult(altitudeMeters, elevationRatePerCentury, altitudeCm, yearsRequired, millionsOfYears, classification);
    }

    private String classifyTimeScale(Double yearsRequired) {
        if (yearsRequired < 6000.0) {
            return "ALERTA — escala dogmática e biologicamente inviável: menos de 6.000 anos são insuficientes para a evolução";
        } else if (yearsRequired > 1_000_000.0) {
            return "VALIDAÇÃO CIENTÍFICA — tempo profundo: mais de 1 milhão de anos confirmam a evolução e a idade da Terra";
        } else {
            return "Escala intermediária — entre 6.000 e 1 milhão de anos";
        }
    }

    public static class CalculationResult {
        private Double altitudeMeters;
        private Double elevationRatePerCentury;
        private Double altitudeCm;
        private Double yearsRequired;
        private Double millionsOfYears;
        private String classification;

        public CalculationResult(Double altitudeMeters, Double elevationRatePerCentury, Double altitudeCm, Double yearsRequired, Double millionsOfYears, String classification) {
            this.altitudeMeters = altitudeMeters;
            this.elevationRatePerCentury = elevationRatePerCentury;
            this.altitudeCm = altitudeCm;
            this.yearsRequired = yearsRequired;
            this.millionsOfYears = millionsOfYears;
            this.classification = classification;
        }

        public Double getAltitudeMeters() { return altitudeMeters; }
        public Double getElevationRatePerCentury() { return elevationRatePerCentury; }
        public Double getAltitudeCm() { return altitudeCm; }
        public Double getYearsRequired() { return yearsRequired; }
        public Double getMillionsOfYears() { return millionsOfYears; }
        public String getClassification() { return classification; }
    }
}