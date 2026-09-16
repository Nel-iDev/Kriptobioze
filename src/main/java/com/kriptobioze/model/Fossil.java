package com.kriptobioze.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "fossil")
public class Fossil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "nome_cientifico")
    private String nomeCientifico;

    @NotBlank
    private String taxonomia;

    @NotNull
    @Column(name = "idade_estimada")
    private Double idadeEstimada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_camada", nullable = false)
    private Camada camada;

    @Column(name = "localidade_descoberta")
    private String localidadeDescoberta;

    private String descricao;

    public Fossil() {}

    public Fossil(String nomeCientifico, String taxonomia, Double idadeEstimada, Camada camada, String localidadeDescoberta, String descricao) {
        this.nomeCientifico = nomeCientifico;
        this.taxonomia = taxonomia;
        this.idadeEstimada = idadeEstimada;
        this.camada = camada;
        this.localidadeDescoberta = localidadeDescoberta;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeCientifico() { return nomeCientifico; }
    public void setNomeCientifico(String nomeCientifico) { this.nomeCientifico = nomeCientifico; }

    public String getTaxonomia() { return taxonomia; }
    public void setTaxonomia(String taxonomia) { this.taxonomia = taxonomia; }

    public Double getIdadeEstimada() { return idadeEstimada; }
    public void setIdadeEstimada(Double idadeEstimada) { this.idadeEstimada = idadeEstimada; }

    public Camada getCamada() { return camada; }
    public void setCamada(Camada camada) { this.camada = camada; }

    public String getLocalidadeDescoberta() { return localidadeDescoberta; }
    public void setLocalidadeDescoberta(String localidadeDescoberta) { this.localidadeDescoberta = localidadeDescoberta; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}