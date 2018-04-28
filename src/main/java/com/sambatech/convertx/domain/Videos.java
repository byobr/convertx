package com.sambatech.convertx.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Videos.
 */
@Entity
@Table(name = "videos")
public class Videos implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "nome_pasta", length = 255, nullable = false)
    private String nomePasta;

    @Size(max = 255)
    @Column(name = "titulo", length = 255)
    private String titulo;

    @NotNull
    @Size(max = 255)
    @Column(name = "nome_arquivo", length = 255, nullable = false)
    private String nomeArquivo;

    @NotNull
    @Size(max = 4)
    @Column(name = "extensao_arquivo", length = 4, nullable = false)
    private String extensaoArquivo;

    @NotNull
    @Column(name = "data_enviado", nullable = false)
    private ZonedDateTime dataEnviado;

    @NotNull
    @Column(name = "visualizacoes", nullable = false)
    private Long visualizacoes;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomePasta() {
        return nomePasta;
    }

    public Videos nomePasta(String nomePasta) {
        this.nomePasta = nomePasta;
        return this;
    }

    public void setNomePasta(String nomePasta) {
        this.nomePasta = nomePasta;
    }

    public String getTitulo() {
        return titulo;
    }

    public Videos titulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public Videos nomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
        return this;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getExtensaoArquivo() {
        return extensaoArquivo;
    }

    public Videos extensaoArquivo(String extensaoArquivo) {
        this.extensaoArquivo = extensaoArquivo;
        return this;
    }

    public void setExtensaoArquivo(String extensaoArquivo) {
        this.extensaoArquivo = extensaoArquivo;
    }

    public ZonedDateTime getDataEnviado() {
        return dataEnviado;
    }

    public Videos dataEnviado(ZonedDateTime dataEnviado) {
        this.dataEnviado = dataEnviado;
        return this;
    }

    public void setDataEnviado(ZonedDateTime dataEnviado) {
        this.dataEnviado = dataEnviado;
    }

    public Long getVisualizacoes() {
        return visualizacoes;
    }

    public Videos visualizacoes(Long visualizacoes) {
        this.visualizacoes = visualizacoes;
        return this;
    }

    public void setVisualizacoes(Long visualizacoes) {
        this.visualizacoes = visualizacoes;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Videos videos = (Videos) o;
        if (videos.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), videos.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Videos{" +
            "id=" + getId() +
            ", nomePasta='" + getNomePasta() + "'" +
            ", titulo='" + getTitulo() + "'" +
            ", nomeArquivo='" + getNomeArquivo() + "'" +
            ", extensaoArquivo='" + getExtensaoArquivo() + "'" +
            ", dataEnviado='" + getDataEnviado() + "'" +
            ", visualizacoes=" + getVisualizacoes() +
            "}";
    }
}
