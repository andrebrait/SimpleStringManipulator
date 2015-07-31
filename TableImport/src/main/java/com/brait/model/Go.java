package com.brait.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@EqualsAndHashCode(of = "codigo")
public class Go implements Serializable {

	private static final long serialVersionUID = -7593036545247322391L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@NotNull
	@Size(max = 15)
	@Column(name = "GOID", length = 15, unique = true, nullable = false)
	private String codigo;

	@NotNull
	@Size(max = 300)
	@Column(name = "TERM", nullable = false, length = 300)
	private String term;

	@NotNull
	@Size(max = 1)
	@Column(name = "CATEGORY", nullable = false, columnDefinition = "char", length = 1)
	private String category;

	@OneToMany(mappedBy = "go")
	private List<Enriquecimento> enriquecimentos;

	public Go(String codigo, String term, String category) {
		super();
		this.codigo = codigo;
		this.term = term;
		this.category = category;
	}

}