package com.brait.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "EXCLUSIVO")
@Data
@EqualsAndHashCode(of = "id")
public class Exclusivo implements Serializable {

	private static final long serialVersionUID = 1111389456353324504L;

	@Embeddable
	@Data
	public static class ExPk implements Serializable {

		private static final long serialVersionUID = -8043622830364121375L;

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

		@NotNull
		@Size(max = 11)
		@Column(name = "GOID", nullable = false, length = 11)
		private String goid;

		@NotNull
		@Size(max = 20)
		@Column(name = "ENSEMBL_P", nullable = false, length = 20)
		private String ensembl_p;

	}

	@EmbeddedId
	private ExPk id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "GOID", insertable = false, updatable = false)
	private Go goId;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "ENSEMBL_P", insertable = false, updatable = false)
	private Ensembl ensembl_p;

	public void setGoEnsembl_P(Ensembl dsLite) {
		this.ensembl_p = dsLite;
		this.id.setEnsembl_p(dsLite.getEnsembl_p());
	}

	public void setGoId(Go goId) {
		this.goId = goId;
		this.id.setGoid(goId.getGoid());
	}
}