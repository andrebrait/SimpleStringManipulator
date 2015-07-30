package com.brait.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
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

	@OneToMany(mappedBy = "go", fetch = FetchType.LAZY)
	private List<De> deList;

	@OneToMany(mappedBy = "go", fetch = FetchType.LAZY)
	private List<Exclusivo> exclusivoList;

	public Go(String goid, String term, String category) {
		super();
		this.goid = goid;
		this.term = term;
		this.category = category;
	}

}