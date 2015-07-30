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
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "DE")
@Data
@EqualsAndHashCode(callSuper = false, of = "id")
public class De implements Serializable {

	private static final long serialVersionUID = -2613894201967569401L;

	@NoArgsConstructor
	@AllArgsConstructor
	@Embeddable
	@Data
	@EqualsAndHashCode(callSuper = false)
	public static class DePk implements Serializable {

		private static final long serialVersionUID = -1465657459912772629L;

		@NotNull
		@Size(max = 11)
		@Column(name = "GOID", nullable = false, length = 11)
		private String goid;

		@NotNull
		@Size(max = 20)
		@Column(name = "ENSEMBL_P", nullable = false, length = 20)
		private String ensembl_p;

		@NotNull
		@Size(max = 4)
		@Column(name = "UPDOWN", nullable = false, length = 4)
		private String upDown;

		@NotNull
		@Size(max = 4)
		@Column(name = "FASE1", nullable = false, length = 4)
		private String fase1;

		@NotNull
		@Size(max = 4)
		@Column(name = "FASE2", nullable = false, length = 4)
		private String fase2;

	}

	@EmbeddedId
	private DePk id;

	@ManyToOne
	@JoinColumn(name = "GOID", insertable = false, updatable = false)
	private Go go;

	@ManyToOne
	@JoinColumn(name = "ENSEMBL_P", insertable = false, updatable = false)
	private Ensembl ensembl;

	@NotNull
	@Column(name = "FDR", nullable = false, precision = 38, scale = 30)
	private BigDecimal fdr;

	@NotNull
	@Column(name = "PVALUE", nullable = false, precision = 38, scale = 30)
	private BigDecimal pValue;

	@NotNull
	@Size(max = 5)
	@Column(name = "OVERUNDER", nullable = false, length = 5)
	private String overUnder;

	public void setEnsembl(Ensembl ensembl) {
		this.ensembl = ensembl;
		this.id.setEnsembl_p(ensembl.getEnsembl_p());
	}

	public void setGo(Go go) {
		this.go = go;
		this.id.setGoid(go.getGoid());
	}

	public De(DePk id, BigDecimal fdr, BigDecimal pValue, String overUnder) {
		super();
		this.id = id;
		this.fdr = fdr;
		this.pValue = pValue;
		this.overUnder = overUnder;
	}

}