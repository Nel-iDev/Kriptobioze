package com.kriptobioze.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "isotopes")
public class Isotope {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String symbol;
    
    @NotNull
    private Double halfLifeYears;
    
    private String description;
    
    private String usedFor;

    public Isotope() {}

    public Isotope(String name, String symbol, Double halfLifeYears, String description, String usedFor) {
        this.name = name;
        this.symbol = symbol;
        this.halfLifeYears = halfLifeYears;
        this.description = description;
        this.usedFor = usedFor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public Double getHalfLifeYears() { return halfLifeYears; }
    public void setHalfLifeYears(Double halfLifeYears) { this.halfLifeYears = halfLifeYears; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUsedFor() { return usedFor; }
    public void setUsedFor(String usedFor) { this.usedFor = usedFor; }
}