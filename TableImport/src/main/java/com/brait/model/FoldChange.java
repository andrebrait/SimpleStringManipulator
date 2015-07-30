package com.brait.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "FOLDCHANGE")
@Data
@EqualsAndHashCode(of = "id")
public class FoldChange implements Serializable {

	private static final long serialVersionUID = 384914405160964954L;

	@NoArgsConstructor
	@AllArgsConstructor
	@Embeddable
	@Data
	@EqualsAndHashCode(callSuper = false)
	public static class FoldPk implements Serializable {

		private static final long serialVersionUID = -985947615974240897L;

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
	private FoldPk id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ENSEMBL_P", nullable = false, insertable = false, updatable = false)
	private Ensembl ensembl;

	@NotNull
	@Column(name = "FOLDCHANGE", nullable = false, precision = 12, scale = 8)
	private BigDecimal foldChange;

	@NotNull
	@Column(name = "LOG2FOLDCHANGE", nullable = false, precision = 12, scale = 8)
	private BigDecimal log2foldChange;

	@Size(max = 300)
	@Column(name = "GENENAME", nullable = true, length = 300)
	private String geneName;

	@Size(max = 20000)
	@Column(name = "SEQUENCE", nullable = true, length = 20000)
	private String sequence;

	public void setEnsembl(Ensembl ensembl) {
		this.ensembl = ensembl;
		this.id.setEnsembl_p(ensembl.getEnsembl_p());
	}

	public FoldChange(FoldPk id, BigDecimal foldChange, BigDecimal log2foldChange, String geneName, String sequence) {
		super();
		this.id = id;
		this.foldChange = foldChange;
		this.log2foldChange = log2foldChange;
		this.geneName = geneName;
		this.sequence = sequence;
	}

}
