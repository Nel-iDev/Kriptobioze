package com.kriptobioze.repository;

import com.kriptobioze.model.Camada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CamadaRepository extends JpaRepository<Camada, Long> {
    List<Camada> findByProfundidadeMediaBetween(Double minDepth, Double maxDepth);
    List<Camada> findByTipoRocha(String tipoRocha);
}