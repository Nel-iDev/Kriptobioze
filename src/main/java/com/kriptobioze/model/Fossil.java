package com.kriptobioze.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "fossils")
public class Fossil {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String taxonomy;
    
    @NotNull
    private Double altitudeMeters;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "layer_id", nullable = false)
    private GeologicalLayer layer;
    
    private String discoveryLocation;
    
    private String description;

    public Fossil() {}

    public Fossil(String name, String taxonomy, Double altitudeMeters, GeologicalLayer layer, String discoveryLocation, String description) {
        this.name = name;
        this.taxonomy = taxonomy;
        this.altitudeMeters = altitudeMeters;
        this.layer = layer;
        this.discoveryLocation = discoveryLocation;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTaxonomy() { return taxonomy; }
    public void setTaxonomy(String taxonomy) { this.taxonomy = taxonomy; }

    public Double getAltitudeMeters() { return altitudeMeters; }
    public void setAltitudeMeters(Double altitudeMeters) { this.altitudeMeters = altitudeMeters; }

    public GeologicalLayer getLayer() { return layer; }
    public void setLayer(GeologicalLayer layer) { this.layer = layer; }

    public String getDiscoveryLocation() { return discoveryLocation; }
    public void setDiscoveryLocation(String discoveryLocation) { this.discoveryLocation = discoveryLocation; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}