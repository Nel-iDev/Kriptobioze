package com.kriptobioze.repository;

import com.kriptobioze.dto.EstratigrafiaDTO;
import com.kriptobioze.model.Fossil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FossilRepository extends JpaRepository<Fossil, Long> {
    List<Fossil> findByCamadaId(Long camadaId);
    List<Fossil> findByTaxonomia(String taxonomia);
    List<Fossil> findByIdadeEstimadaBetween(Double minIdade, Double maxIdade);

    @Query("""
        SELECT new com.kriptobioze.dto.EstratigrafiaDTO(
            f.id, f.nomeCientifico, f.taxonomia, f.idadeEstimada,
            c.nomeEra, c.profundidadeMedia, c.tipoRocha)
        FROM Fossil f
        INNER JOIN f.camada c
        ORDER BY c.profundidadeMedia DESC
    """)
    List<EstratigrafiaDTO> findAllEstratigrafiaOrderByProfundidadeDesc();
}