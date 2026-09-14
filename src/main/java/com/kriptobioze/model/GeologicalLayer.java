package com.kriptobioze.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "geological_layers")
public class GeologicalLayer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String name;
    
    @NotNull
    private Double depthMeters;
    
    @NotBlank
    private String rockType;
    
    @NotNull
    private Integer estimatedAgeMillionsYears;
    
    private String description;

    public GeologicalLayer() {}

    public GeologicalLayer(String name, Double depthMeters, String rockType, Integer estimatedAgeMillionsYears, String description) {
        this.name = name;
        this.depthMeters = depthMeters;
        this.rockType = rockType;
        this.estimatedAgeMillionsYears = estimatedAgeMillionsYears;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getDepthMeters() { return depthMeters; }
    public void setDepthMeters(Double depthMeters) { this.depthMeters = depthMeters; }

    public String getRockType() { return rockType; }
    public void setRockType(String rockType) { this.rockType = rockType; }

    public Integer getEstimatedAgeMillionsYears() { return estimatedAgeMillionsYears; }
    public void setEstimatedAgeMillionsYears(Integer estimatedAgeMillionsYears) { this.estimatedAgeMillionsYears = estimatedAgeMillionsYears; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}