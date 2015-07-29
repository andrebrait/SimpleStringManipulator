package com.brait.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

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

	@ManyToMany(mappedBy = "ensembl_ps")
	private List<Go> goIds;

}
