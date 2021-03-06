package com.brait.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "TRANSCRITO")
@Data
@ToString(callSuper = false, exclude = { "proteina", "resultados" })
@EqualsAndHashCode(of = "codigo")
public class Transcrito implements Serializable {

	private static final long serialVersionUID = 4271090010236201592L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@NotBlank
	@Size(max = 20)
	@Column(name = "CODIGO", length = 20, unique = true, nullable = false)
	private String codigo;

	@Size(max = 300)
	@Column(name = "GENENAME", nullable = true, length = 300)
	private String geneName;

	@Size(max = 50)
	@Column(name = "ORGANISM", nullable = true, length = 50)
	private String organism;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "TRANSCRITO_PROTEINA", joinColumns = { @JoinColumn(name = "ID_TRANSCRITO") },
			inverseJoinColumns = { @JoinColumn(name = "ID_PROTEINA") })
	private List<Proteina> proteina;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "transcrito")
	private List<Resultado> resultados;

	public Transcrito(String codigo) {
		super();
		this.codigo = codigo;
	}

}
