package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.BarcodeSequence;

public interface BarcodeSequenceDao {

	public String getLastBiosampleBarcode(String pattern);

	public String getLastLocationBarcode(String pattern);

	public String getLastContainerBarcode(String pattern);

	public List<BarcodeSequence> getBarcodeSequencesByTypeAndCategory(String type, Integer category);

	public Integer saveOrUpdate(BarcodeSequence barcodeSequence);

	public void delete(int id);

	int addBarcodeSequence(BarcodeSequence barcodeSequence);

}
