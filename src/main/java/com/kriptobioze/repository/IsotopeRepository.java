package com.kriptobioze.repository;

import com.kriptobioze.model.Isotope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IsotopeRepository extends JpaRepository<Isotope, Long> {
    Optional<Isotope> findBySymbol(String symbol);
    Optional<Isotope> findByName(String name);
}