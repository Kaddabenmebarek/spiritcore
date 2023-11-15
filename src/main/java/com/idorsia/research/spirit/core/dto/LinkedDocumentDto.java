package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;

public class LinkedDocumentDto implements Serializable {

	private static final long serialVersionUID = 145343509892719204L;
	private Integer id = Constants.NEWTRANSIENTID;
	private BiosampleDto biosample;
	private DocumentDto linkedDocument;
	private BiotypeMetadataDto metadata;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	
	public LinkedDocumentDto() {
	}

	public LinkedDocumentDto(BiosampleDto biosample, DocumentDto linkedDocument, BiotypeMetadataDto metadata) {
		this.biosample=biosample;
		this.linkedDocument=linkedDocument;
		this.metadata=metadata;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BiosampleDto getBiosample() {
		return biosample;
	}
	public void setBiosample(BiosampleDto biosample) {
		this.biosample = biosample;
	}
	public DocumentDto getLinkedDocument() {
		return linkedDocument;
	}
	public void setLinkedDocument(DocumentDto linkedDocument) {
		this.linkedDocument = linkedDocument;
	}
	public BiotypeMetadataDto getMetadata() {
		return metadata;
	}
	public void setMetadata(BiotypeMetadataDto metadata) {
		this.metadata = metadata;
	}

	public Date getCreDate() {
		return creDate;
	}

	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}

	public String getCreUser() {
		return creUser;
	}

	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}

	public Date getUpdDate() {
		return updDate;
	}

	public void setUpdDate(Date updDate) {
		this.updDate = updDate;
	}

	public String getUpdUser() {
		return updUser;
	}

	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}
}
