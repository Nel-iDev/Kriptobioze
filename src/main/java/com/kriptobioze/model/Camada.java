package com.kriptobioze.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "camada")
public class Camada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "nome_era")
    private String nomeEra;

    @NotNull
    @Column(name = "profundidade_media")
    private Double profundidadeMedia;

    @NotBlank
    @Column(name = "tipo_rocha")
    private String tipoRocha;

    @NotNull
    @Column(name = "idade_estimada_ma")
    private Integer idadeEstimadaMa;

    private String descricao;

    public Camada() {}

    public Camada(String nomeEra, Double profundidadeMedia, String tipoRocha, Integer idadeEstimadaMa, String descricao) {
        this.nomeEra = nomeEra;
        this.profundidadeMedia = profundidadeMedia;
        this.tipoRocha = tipoRocha;
        this.idadeEstimadaMa = idadeEstimadaMa;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeEra() { return nomeEra; }
    public void setNomeEra(String nomeEra) { this.nomeEra = nomeEra; }

    public Double getProfundidadeMedia() { return profundidadeMedia; }
    public void setProfundidadeMedia(Double profundidadeMedia) { this.profundidadeMedia = profundidadeMedia; }

    public String getTipoRocha() { return tipoRocha; }
    public void setTipoRocha(String tipoRocha) { this.tipoRocha = tipoRocha; }

    public Integer getIdadeEstimadaMa() { return idadeEstimadaMa; }
    public void setIdadeEstimadaMa(Integer idadeEstimadaMa) { this.idadeEstimadaMa = idadeEstimadaMa; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}