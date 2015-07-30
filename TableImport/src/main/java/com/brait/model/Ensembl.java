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
@Table(name = "ENSEMBL")
@Data
@EqualsAndHashCode(of = "ensembl_p")
public class Ensembl implements Serializable {

	private static final long serialVersionUID = 1540830171248324992L;

	@Id
	@Size(max = 20)
	@Column(name = "ENSEMBL_P", length = 20)
	private String ensembl_p;

	@NotNull
	@Size(max = 20)
	@Column(name = "ENSEMBL_T", length = 20, nullable = false, unique = true)
	private String ensembl_t;

	@OneToMany(mappedBy = "ensembl", fetch = FetchType.LAZY)
	private List<De> deList;

	@OneToMany(mappedBy = "ensembl", fetch = FetchType.LAZY)
	private List<Exclusivo> exclusivoList;

	public Ensembl(String ensembl_p, String ensembl_t) {
		super();
		this.ensembl_p = ensembl_p;
		this.ensembl_t = ensembl_t;
	}

}
