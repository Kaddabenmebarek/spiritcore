package com.idorsia.research.spirit.core.dto;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import com.actelion.research.util.IOUtils;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.DocumentType;

public class DocumentDto implements IObject, Serializable{

	private static final long serialVersionUID = 5162231949522805204L;
	private Integer id = Constants.NEWTRANSIENTID;
	private DocumentType type;
	private byte[] bytes;
	private String filename;
	private String updUser;
	private Date updDate;
	private String creUser;
	private Date creDate;
	
	public DocumentDto() {
	}
	
	public DocumentDto(DocumentType type) {
		this.type = type;
	}

	public DocumentDto(File file) throws IOException {
		setFilename(file.getName());
		setBytes(IOUtils.getBytes(file));
	}
	
	public DocumentDto(String title, byte[] bytes) {
		setFilename(title);
		setBytes(bytes);
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public DocumentType getType() {
		return type;
	}

	public void setType(DocumentType type) {
		this.type = type;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUpdUser() {
		return updUser;
	}

	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}

	public Date getUpdDate() {
		return updDate;
	}

	public void setUpdDate(Date updDate) {
		this.updDate = updDate;
	}

	public String getCreUser() {
		return creUser;
	}

	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}

	public Date getCreDate() {
		return creDate;
	}

	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}
	
}
