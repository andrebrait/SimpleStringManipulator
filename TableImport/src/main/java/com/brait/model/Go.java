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
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Entity
@Table(name = "GO")
@Data
@ToString(callSuper = false, exclude = { "enriquecimentos" })
@EqualsAndHashCode(of = "codigo")
public class Go implements Serializable {

	private static final long serialVersionUID = -7593036545247322391L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@NotBlank
	@Size(max = 15)
	@Column(name = "GOID", length = 15, unique = true, nullable = false)
	private String codigo;

	@NotBlank
	@Size(max = 300)
	@Column(name = "TERM", nullable = false, length = 300)
	private String term;

	@NotBlank
	@Size(max = 1)
	@Column(name = "CATEGORY", nullable = false, columnDefinition = "char", length = 1)
	private String category;

	@NotBlank
	@Size(max = 50)
	@Column(name = "ORGANISM", nullable = false, length = 50)
	private String organism;

	@OneToMany(mappedBy = "go")
	private List<Enriquecimento> enriquecimentos;

	public Go(String codigo, String term, String category, String organism) {
		super();
		this.codigo = codigo;
		this.term = term;
		this.category = category;
		this.organism = organism;
	}

}