package com.kriptobioze.service;

import com.kriptobioze.model.Fossil;
import com.kriptobioze.model.GeologicalLayer;
import com.kriptobioze.repository.FossilRepository;
import com.kriptobioze.repository.GeologicalLayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FossilService {

    @Autowired
    private FossilRepository fossilRepository;

    @Autowired
    private GeologicalLayerRepository geologicalLayerRepository;

    public List<Fossil> getAllFossils() {
        return fossilRepository.findAll();
    }

    public Fossil getFossilById(Long id) {
        return fossilRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Fóssil não encontrado com id: " + id));
    }

    public List<Fossil> getFossilsByLayer(Long layerId) {
        return fossilRepository.findByLayerId(layerId);
    }

    public Fossil createFossil(Fossil fossil) {
        return fossilRepository.save(fossil);
    }

    public Fossil updateFossil(Long id, Fossil fossilDetails) {
        Fossil fossil = getFossilById(id);
        fossil.setName(fossilDetails.getName());
        fossil.setTaxonomy(fossilDetails.getTaxonomy());
        fossil.setAltitudeMeters(fossilDetails.getAltitudeMeters());
        fossil.setLayer(fossilDetails.getLayer());
        fossil.setDiscoveryLocation(fossilDetails.getDiscoveryLocation());
        fossil.setDescription(fossilDetails.getDescription());
        return fossilRepository.save(fossil);
    }

    public void deleteFossil(Long id) {
        fossilRepository.deleteById(id);
    }

    public List<GeologicalLayer> getAllLayers() {
        return geologicalLayerRepository.findAll();
    }

    public GeologicalLayer getLayerById(Long id) {
        return geologicalLayerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Camada não encontrada com id: " + id));
    }

    public GeologicalLayer createLayer(GeologicalLayer layer) {
        return geologicalLayerRepository.save(layer);
    }

    public GeologicalLayer updateLayer(Long id, GeologicalLayer layerDetails) {
        GeologicalLayer layer = getLayerById(id);
        layer.setName(layerDetails.getName());
        layer.setDepthMeters(layerDetails.getDepthMeters());
        layer.setRockType(layerDetails.getRockType());
        layer.setEstimatedAgeMillionsYears(layerDetails.getEstimatedAgeMillionsYears());
        layer.setDescription(layerDetails.getDescription());
        return geologicalLayerRepository.save(layer);
    }

    public void deleteLayer(Long id) {
        geologicalLayerRepository.deleteById(id);
    }
}