package com.brait.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
@Table(name = "RESULTADO")
@Data
@EqualsAndHashCode(of = "id")
public class Resultado implements Serializable {

	private static final long serialVersionUID = 6282544068988343859L;

	private final static BigDecimal TWO = new BigDecimal("2");
	private final static BigDecimal MINUS_TWO = new BigDecimal("-2");
	private final static BigDecimal POINT_OH_FIVE = new BigDecimal("0.05");
	private final static String NON_SIG = "NONSIG";

	@NoArgsConstructor
	@AllArgsConstructor
	@Embeddable
	@Data
	@EqualsAndHashCode(of = { "transcritoId", "fase1", "fase2" })
	public static class ResultadoPk implements Serializable {

		private static final long serialVersionUID = -1907674178927666700L;

		@NotNull
		@Column(name = "IDTRANSCRITO", nullable = false)
		private Long transcritoId;

		@NotNull
		@Column(name = "FASE1", nullable = false)
		private Long fase1;

		@NotNull
		@Column(name = "FASE2", nullable = false)
		private Long fase2;

	}

	@EmbeddedId
	private ResultadoPk id;

	@ManyToOne
	@JoinColumn(name = "IDTRANSCRITO", nullable = false, insertable = false, updatable = false)
	private Transcrito transcrito;

	@NotNull
	@Column(name = "MEANFASE1", nullable = false, scale = 15, precision = 2)
	private BigDecimal mean1;

	@NotNull
	@Column(name = "MEANFASE2", nullable = false, scale = 15, precision = 2)
	private BigDecimal mean2;

	@Size(max = 10)
	@Column(name = "INDICMUDANCA", nullable = true, length = 10)
	private String mudanca;

	@Column(name = "FOLDCHANGE", nullable = true, scale = 8, precision = 4)
	private BigDecimal foldChange;

	@Column(name = "LOG2FC", nullable = true, scale = 8, precision = 4)
	private BigDecimal log2foldChange;

	@NotNull
	@Size(max = 10)
	@Column(name = "SHEET", length = 10, nullable = false)
	private String sheet;

	public Resultado(ResultadoPk id, BigDecimal mean1, BigDecimal mean2, String sheet, BigDecimal fdr) {
		super();
		this.id = id;
		this.mean1 = mean1;
		this.mean2 = mean2;
		this.sheet = sheet;
		updateMudancaFoldChange(fdr);
	}

	private void updateMudancaFoldChange(BigDecimal fdr) {
		foldChange = null;
		log2foldChange = null;
		if (mean1.compareTo(BigDecimal.ZERO) == 0 && mean2.compareTo(BigDecimal.ZERO) == 0) {
			mudanca = "IND";
		} else if (mean1.compareTo(BigDecimal.ZERO) == 0) {
			if (fdr.compareTo(POINT_OH_FIVE) > 0) {
				mudanca = NON_SIG;
			} else {
				mudanca = "EXCL " + id.fase2;
			}
		} else if (mean2.compareTo(BigDecimal.ZERO) == 0) {
			if (fdr.compareTo(POINT_OH_FIVE) > 0) {
				mudanca = NON_SIG;
			} else {
				mudanca = "EXCL " + id.fase1;
			}
		} else {
			foldChange = mean2.divide(mean1, 4, RoundingMode.HALF_EVEN);
			log2foldChange = new BigDecimal(Double.toString(Math.log(foldChange.doubleValue()) / Math.log(2))).setScale(4, RoundingMode.HALF_EVEN);
			if (fdr.compareTo(POINT_OH_FIVE) > 0) {
				mudanca = NON_SIG;
			} else if (log2foldChange.compareTo(TWO) >= 0) {
				mudanca = "UP";
			} else if (log2foldChange.compareTo(MINUS_TWO) <= 0) {
				mudanca = "DOWN";
			} else {
				mudanca = "SAME";
			}
		}
	}
}
