package com.kriptobioze.controller;

import com.kriptobioze.dto.EstratigrafiaDTO;
import com.kriptobioze.model.Camada;
import com.kriptobioze.model.Fossil;
import com.kriptobioze.service.FossilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FossilController {

    @Autowired
    private FossilService fossilService;

    @GetMapping("/fossils")
    public ResponseEntity<List<Fossil>> getAllFossils() {
        return ResponseEntity.ok(fossilService.getAllFossils());
    }

    @GetMapping("/fossils/{id}")
    public ResponseEntity<Fossil> getFossilById(@PathVariable Long id) {
        return ResponseEntity.ok(fossilService.getFossilById(id));
    }

    @GetMapping("/fossils/layer/{layerId}")
    public ResponseEntity<List<Fossil>> getFossilsByLayer(@PathVariable Long layerId) {
        return ResponseEntity.ok(fossilService.getFossilsByLayer(layerId));
    }

    @GetMapping("/fossils/estratigrafia")
    public ResponseEntity<List<EstratigrafiaDTO>> getEstratigrafia() {
        return ResponseEntity.ok(fossilService.getEstratigrafia());
    }

    @PostMapping("/fossils")
    public ResponseEntity<Fossil> createFossil(@RequestBody Fossil fossil) {
        return ResponseEntity.ok(fossilService.createFossil(fossil));
    }

    @PutMapping("/fossils/{id}")
    public ResponseEntity<Fossil> updateFossil(@PathVariable Long id, @RequestBody Fossil fossil) {
        return ResponseEntity.ok(fossilService.updateFossil(id, fossil));
    }

    @DeleteMapping("/fossils/{id}")
    public ResponseEntity<Void> deleteFossil(@PathVariable Long id) {
        fossilService.deleteFossil(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/layers")
    public ResponseEntity<List<Camada>> getAllLayers() {
        return ResponseEntity.ok(fossilService.getAllLayers());
    }

    @GetMapping("/layers/{id}")
    public ResponseEntity<Camada> getLayerById(@PathVariable Long id) {
        return ResponseEntity.ok(fossilService.getLayerById(id));
    }

    @PostMapping("/layers")
    public ResponseEntity<Camada> createLayer(@RequestBody Camada layer) {
        return ResponseEntity.ok(fossilService.createLayer(layer));
    }

    @PutMapping("/layers/{id}")
    public ResponseEntity<Camada> updateLayer(@PathVariable Long id, @RequestBody Camada layer) {
        return ResponseEntity.ok(fossilService.updateLayer(id, layer));
    }

    @DeleteMapping("/layers/{id}")
    public ResponseEntity<Void> deleteLayer(@PathVariable Long id) {
        fossilService.deleteLayer(id);
        return ResponseEntity.noContent().build();
    }
}