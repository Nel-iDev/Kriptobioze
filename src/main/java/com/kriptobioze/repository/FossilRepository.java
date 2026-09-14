package com.kriptobioze.repository;

import com.kriptobioze.model.Fossil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FossilRepository extends JpaRepository<Fossil, Long> {
    List<Fossil> findByLayerId(Long layerId);
    List<Fossil> findByTaxonomy(String taxonomy);
    List<Fossil> findByAltitudeMetersBetween(Double minAltitude, Double maxAltitude);
}