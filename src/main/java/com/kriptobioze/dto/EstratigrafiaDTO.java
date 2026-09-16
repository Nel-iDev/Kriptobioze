package com.kriptobioze.dto;

public record EstratigrafiaDTO(
        Long fossilId,
        String nomeCientifico,
        String taxonomia,
        Double idadeEstimadaMa,
        String nomeEra,
        Double profundidadeMedia,
        String tipoRocha) {
}