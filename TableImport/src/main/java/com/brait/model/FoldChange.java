package com.brait.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "FOLDCHANGE")
@Data
@EqualsAndHashCode(of = "ensembl_p")
public class FoldChange implements Serializable {

	private static final long serialVersionUID = 384914405160964954L;

	@Id
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ENSEMBL_P", nullable = false, insertable = true, updatable = true)
	private Ensembl ensembl_p;

	@NotNull
	@Column(name = "FOLDCHANGE", nullable = false, precision = 12, scale = 6)
	private BigDecimal foldChange;

	@NotNull
	@Column(name = "LOG2FOLDCHANGE", nullable = false, precision = 12, scale = 6)
	private BigDecimal log2foldChange;

	@Size(max = 300)
	@Column(name = "GENENAME", nullable = true, length = 300)
	private String geneName;

	@Size(max = 20000)
	@Column(name = "SEQUENCE", nullable = true, length = 20000)
	private String sequence;

}
