package com.kriptobioze.config;

import com.kriptobioze.model.Camada;
import com.kriptobioze.model.Fossil;
import com.kriptobioze.model.Isotope;
import com.kriptobioze.repository.CamadaRepository;
import com.kriptobioze.repository.FossilRepository;
import com.kriptobioze.repository.IsotopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private IsotopeRepository isotopeRepository;

    @Autowired
    private CamadaRepository camadaRepository;

    @Autowired
    private FossilRepository fossilRepository;

    @Override
    public void run(String... args) {
        if (isotopeRepository.count() == 0) {
            seedIsotopes();
        }
        if (camadaRepository.count() == 0) {
            seedCamadas();
        }
        if (fossilRepository.count() == 0) {
            seedFossils();
        }
    }

    private void seedIsotopes() {
        isotopeRepository.save(new Isotope(
            "Carbono-14", "C-14", 5730.0,
            "Isótopo radioativo do carbono utilizado para datar materiais orgânicos com até ~50.000 anos.",
            "Datação de fósseis e materiais orgânicos recentes"
        ));
        isotopeRepository.save(new Isotope(
            "Urânio-235", "U-235", 703800000.0,
            "Isótopo radioativo do urânio com meia-vida de ~703,8 milhões de anos.",
            "Datação de rochas muito antigas e meteoritos"
        ));
        isotopeRepository.save(new Isotope(
            "Urânio-238", "U-238", 4468000000.0,
            "Isótopo mais abundante do urânio natural com meia-vida de ~4,468 bilhões de anos.",
            "Datação de rochas ígneas e metamórficas"
        ));
        isotopeRepository.save(new Isotope(
            "Potássio-40", "K-40", 1250000000.0,
            "Isótopo radioativo do potássio com meia-vida de ~1,25 bilhão de anos.",
            "Datação de rochas minerais e fósseis antigos"
        ));
        isotopeRepository.save(new Isotope(
            "Rubídio-87", "Rb-87", 48800000000.0,
            "Isótopo radioativo do rubídio com meia-vida de ~48,8 bilhões de anos.",
            "Datação de rochas muito antigas e evolução estelar"
        ));
        isotopeRepository.save(new Isotope(
            "Samário-147", "Sm-147", 106000000000.0,
            "Isótopo radioativo do samário com meia-vida de ~106 bilhões de anos.",
            "Datação de meteoritos e formação do sistema solar"
        ));
    }

    private void seedCamadas() {
        camadaRepository.save(new Camada(
            "Cenozóico", 0.0, "Sedimentar", 66,
            "Era mais recente - inclui período quaternário e terciário"
        ));
        camadaRepository.save(new Camada(
            "Mesozóico", 500.0, "Sedimentar", 252,
            "Era dos dinossauros - triássico, jurássico e cretáceo"
        ));
        camadaRepository.save(new Camada(
            "Paleozóico", 1500.0, "Sedimentar", 541,
            "Era da vida antiga - cambriano até permiano"
        ));
        camadaRepository.save(new Camada(
            "Proterozóico", 5000.0, "Metamórfica", 2500,
            "Era da vida primitiva - começo da vida complexa"
        ));
        camadaRepository.save(new Camada(
            "Arqueano", 10000.0, "Metamórfica", 4000,
            "Era mais antiga - formação da crosta terrestre"
        ));
    }

    private void seedFossils() {
        Map<String, Camada> camadasPorEra = camadaRepository.findAll().stream()
            .collect(Collectors.toMap(Camada::getNomeEra, Function.identity()));

        fossilRepository.save(new Fossil(
            "Mammuthus primigenius", "Mammalia · Proboscidea", 0.04,
            camadasPorEra.get("Cenozóico"),
            "Sibéria, Norte da Ásia",
            "Mamute-lanudo do fim do Quaternário"
        ));
        fossilRepository.save(new Fossil(
            "Tyrannosaurus rex", "Dinosauria · Theropoda", 66.0,
            camadasPorEra.get("Mesozóico"),
            "Formação Hell Creek, EUA",
            "Cretáceo Superior, era dos dinossauros"
        ));
        fossilRepository.save(new Fossil(
            "Triceratops horridus", "Dinosauria · Ceratopsidae", 68.0,
            camadasPorEra.get("Mesozóico"),
            "Formação Lance, EUA",
            "Cretáceo Superior, contemporâneo do T. rex"
        ));
        fossilRepository.save(new Fossil(
            "Paradoxides sp.", "Trilobita · Paradoxididae", 505.0,
            camadasPorEra.get("Paleozóico"),
            "Folhelhos do Cambriano, Europa",
            "Trilobite típico da Explosão Cambriana"
        ));
        fossilRepository.save(new Fossil(
            "Dickinsonia costata", "Proarticulata · Dickinsoniidae", 560.0,
            camadasPorEra.get("Proterozóico"),
            "Bioma de Ediacara, Austrália",
            "Animal de corpo mole do Ediacarano"
        ));
        fossilRepository.save(new Fossil(
            "Stromatólito de cianobactérias", "Cyanobacteria (microbialito fóssil)", 3500.0,
            camadasPorEra.get("Arqueano"),
            "Pilbara Craton, Austrália",
            "Vestígios de vida fotossintética do Arqueano"
        ));
    }
}