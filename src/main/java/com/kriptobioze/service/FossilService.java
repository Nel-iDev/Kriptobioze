package com.kriptobioze.service;

import com.kriptobioze.dto.EstratigrafiaDTO;
import com.kriptobioze.model.Camada;
import com.kriptobioze.model.Fossil;
import com.kriptobioze.repository.CamadaRepository;
import com.kriptobioze.repository.FossilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FossilService {

    @Autowired
    private FossilRepository fossilRepository;

    @Autowired
    private CamadaRepository camadaRepository;

    public List<Fossil> getAllFossils() {
        return fossilRepository.findAll();
    }

    public Fossil getFossilById(Long id) {
        return fossilRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Fóssil não encontrado com id: " + id));
    }

    public List<Fossil> getFossilsByLayer(Long camadaId) {
        return fossilRepository.findByCamadaId(camadaId);
    }

    public List<EstratigrafiaDTO> getEstratigrafia() {
        return fossilRepository.findAllEstratigrafiaOrderByProfundidadeDesc();
    }

    public Fossil createFossil(Fossil fossil) {
        return fossilRepository.save(fossil);
    }

    public Fossil updateFossil(Long id, Fossil fossilDetails) {
        Fossil fossil = getFossilById(id);
        fossil.setNomeCientifico(fossilDetails.getNomeCientifico());
        fossil.setTaxonomia(fossilDetails.getTaxonomia());
        fossil.setIdadeEstimada(fossilDetails.getIdadeEstimada());
        fossil.setCamada(fossilDetails.getCamada());
        fossil.setLocalidadeDescoberta(fossilDetails.getLocalidadeDescoberta());
        fossil.setDescricao(fossilDetails.getDescricao());
        return fossilRepository.save(fossil);
    }

    public void deleteFossil(Long id) {
        fossilRepository.deleteById(id);
    }

    public List<Camada> getAllLayers() {
        return camadaRepository.findAll();
    }

    public Camada getLayerById(Long id) {
        return camadaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Camada não encontrada com id: " + id));
    }

    public Camada createLayer(Camada layer) {
        return camadaRepository.save(layer);
    }

    public Camada updateLayer(Long id, Camada layerDetails) {
        Camada layer = getLayerById(id);
        layer.setNomeEra(layerDetails.getNomeEra());
        layer.setProfundidadeMedia(layerDetails.getProfundidadeMedia());
        layer.setTipoRocha(layerDetails.getTipoRocha());
        layer.setIdadeEstimadaMa(layerDetails.getIdadeEstimadaMa());
        layer.setDescricao(layerDetails.getDescricao());
        return camadaRepository.save(layer);
    }

    public void deleteLayer(Long id) {
        camadaRepository.deleteById(id);
    }
}