package com.brait.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "ENRIQ_GO_PROT")
@Data
@EqualsAndHashCode(of = "id")
public class Enriquecimento implements Serializable {

	private static final long serialVersionUID = -6304401220772397256L;

	@NoArgsConstructor
	@AllArgsConstructor
	@Embeddable
	@Data
	@EqualsAndHashCode(of = { "idProt", "idGo" })
	public static class EnriquecimentoPk implements Serializable {

		private static final long serialVersionUID = -1907674178927666700L;

		@NotNull
		@Column(name = "ID_GO", nullable = false)
		private Long idGo;

		@NotNull
		@Column(name = "ID_PROT", nullable = false)
		private Long idProt;

	}

	@EmbeddedId
	private EnriquecimentoPk id;

	@ManyToOne
	@JoinColumn(name = "ID_GO", insertable = false, updatable = false)
	private Go go;

	@ManyToOne
	@JoinColumn(name = "ID_PROT", insertable = false, updatable = false)
	private Proteina proteina;

	public void setGo(Go go) {
		this.go = go;
		this.id.setIdGo(go.getId());
	}

	public void setProteina(Proteina proteina) {
		this.proteina = proteina;
		this.id.setIdProt(proteina.getId());
	}

	@NotNull
	@Column(name = "FDR", precision = 38, scale = 30, nullable = false)
	private BigDecimal fdr;

	@NotNull
	@Column(name = "PVALUE", precision = 38, scale = 30, nullable = false)
	private BigDecimal pValue;

	public Enriquecimento(EnriquecimentoPk id, BigDecimal fdr, BigDecimal pValue) {
		super();
		this.id = id;
		this.fdr = fdr;
		this.pValue = pValue;
	}

}
