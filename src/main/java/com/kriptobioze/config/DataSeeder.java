package com.kriptobioze.config;

import com.kriptobioze.model.GeologicalLayer;
import com.kriptobioze.model.Isotope;
import com.kriptobioze.repository.GeologicalLayerRepository;
import com.kriptobioze.repository.IsotopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private IsotopeRepository isotopeRepository;

    @Autowired
    private GeologicalLayerRepository geologicalLayerRepository;

    @Override
    public void run(String... args) {
        if (isotopeRepository.count() == 0) {
            seedIsotopes();
        }
        if (geologicalLayerRepository.count() == 0) {
            seedLayers();
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

    private void seedLayers() {
        geologicalLayerRepository.save(new GeologicalLayer(
            "Cenozóico", 0.0, "Sedimentar", 66,
            "Era mais recente - inclui período quaternário e terciário"
        ));
        geologicalLayerRepository.save(new GeologicalLayer(
            "Mesozóico", 500.0, "Sedimentar", 252,
            "Era dos dinossauros - triássico, jurássico e cretáceo"
        ));
        geologicalLayerRepository.save(new GeologicalLayer(
            "Paleozóico", 1500.0, "Sedimentar", 541,
            "Era da vida antiga - cambriano até permiano"
        ));
        geologicalLayerRepository.save(new GeologicalLayer(
            "Proterozóico", 5000.0, "Metamórfica", 2500,
            "Era da vida primitiva - beginço da vida complexa"
        ));
        geologicalLayerRepository.save(new GeologicalLayer(
            "Arqueano", 10000.0, "Metamórfica", 4000,
            "Era mais antiga - formação da crosta terrestre"
        ));
    }
}