package com.brait.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Entity
@Table(name = "PROTEINA")
@Data
@ToString(callSuper = false, exclude = { "transcrito", "enriquecimentos" })
@EqualsAndHashCode(of = "codigo")
public class Proteina implements Serializable {

	private static final long serialVersionUID = 8093855517497691018L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@NotNull
	@Size(max = 20)
	@Column(name = "CODIGO", length = 20, unique = true, nullable = false)
	private String codigo;

	@Size(max = 20000)
	@Column(name = "SEQUENCE", nullable = true, length = 20000)
	private String sequence;

	@ManyToMany(mappedBy = "proteina")
	private List<Transcrito> transcrito;

	@OneToMany(mappedBy = "proteina")
	private List<Enriquecimento> enriquecimentos;

	public Proteina(String codigo) {
		super();
		this.codigo = codigo;
	}

}