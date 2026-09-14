package com.kriptobioze.repository;

import com.kriptobioze.model.GeologicalLayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeologicalLayerRepository extends JpaRepository<GeologicalLayer, Long> {
    List<GeologicalLayer> findByDepthMetersBetween(Double minDepth, Double maxDepth);
    List<GeologicalLayer> findByRockType(String rockType);
}