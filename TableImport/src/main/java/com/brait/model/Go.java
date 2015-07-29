package com.brait.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "GO")
@Data
@EqualsAndHashCode(of = "goid")
public class Go implements Serializable {

	private static final long serialVersionUID = -2017977112671459318L;

	@Id
	@Size(max = 11)
	@Column(name = "GOID", length = 11)
	private String goid;

	@NotNull
	@Size(max = 200)
	@Column(name = "TERM", nullable = false, length = 200)
	private String term;

	@NotNull
	@Size(max = 1)
	@Column(name = "CATEGORY", nullable = false, columnDefinition = "char", length = 1)
	private String category;

	@NotNull
	@Column(name = "FDR", nullable = false, precision = 12, scale = 6)
	private BigDecimal fdr;

	@NotNull
	@Column(name = "PVALUE", nullable = false, precision = 12, scale = 6)
	private BigDecimal pValue;

	@NotNull
	@Size(max = 5)
	@Column(name = "OVERUNDER", nullable = false, length = 5)
	private String overUnder;

	@ManyToMany
	@JoinTable(name = "GO_ENSEMBL", joinColumns = { @JoinColumn(name = "GOID") }, inverseJoinColumns = { @JoinColumn(name = "ENSEMBL_P") })
	private List<Ensembl> ensembl_ps;

}