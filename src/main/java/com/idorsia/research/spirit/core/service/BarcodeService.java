package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Category;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.ContainerType;
import com.idorsia.research.spirit.core.dao.BarcodeSequenceDao;
import com.idorsia.research.spirit.core.dto.BarcodeSequenceDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.model.BarcodeSequence;
import com.idorsia.research.spirit.core.util.MiscUtils;

@Service
public class BarcodeService extends AbstractService implements Serializable{

	private static final long serialVersionUID = 5146102265889292047L;

	@SuppressWarnings("unchecked")
	private Map<Integer, BarcodeSequenceDto> idToBarcodeSequence = (Map<Integer, BarcodeSequenceDto>) getCacheMap(BarcodeSequenceDto.class);
	
	@Autowired
	BarcodeSequenceDao barcodeSequenceDao;
	
	private static Map<String, List<String>> prefix2PrecomputedIds = new HashMap<>();
	private static Map<String, String> lastUsedIds = new HashMap<>();

	public List<BarcodeSequence> getBarcodeSequencesByTypeAndCategory(String type, Integer category) {
		return barcodeSequenceDao.getBarcodeSequencesByTypeAndCategory(type, category);
	}

	public String getLastBiosampleBarcode(String pattern) {
		return barcodeSequenceDao.getLastBiosampleBarcode(pattern);
	}

	public String getLastLocationBarcode(String pattern) {
		return barcodeSequenceDao.getLastLocationBarcode(pattern);
	}

	public String getLastContainerBarcode(String pattern) {
		return barcodeSequenceDao.getLastContainerBarcode(pattern);
	}
	
	public void save(BarcodeSequenceDto barcodeSequence) {
		save(barcodeSequence, false);
	}

	@Transactional
	protected void save(BarcodeSequenceDto barcodeSequence, Boolean cross) {
		try {
			if(!savedItems.contains(barcodeSequence)) {
				savedItems.add(barcodeSequence);
				barcodeSequence.setId(saveOrUpdate(dozerMapper.map(barcodeSequence, BarcodeSequence.class, "barcodeSequenceCustomMapping")));
				idToBarcodeSequence.put(barcodeSequence.getId(), barcodeSequence);
			}
		}catch(Exception e) {
			if (!cross) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
			throw e;
		}finally{
			if(!cross) {
				AbstractService.clearSavedItem();
				AbstractService.clearTransient(true);
			}
		}
	}

	@Transactional
	public void delete(BarcodeSequenceDto barcodeSequence) {
		delete(barcodeSequence, false);
	}
	
	protected void delete(BarcodeSequenceDto barcodeSequence, Boolean cross) {
		barcodeSequenceDao.delete(barcodeSequence.getId());
	}
	
	public Integer saveOrUpdate(BarcodeSequence barcodeSequence) {
		return barcodeSequenceDao.saveOrUpdate(barcodeSequence);
	}
	
	public void reset() {
		prefix2PrecomputedIds.clear();
		lastUsedIds.clear();
	}
	
	public String getNextId(ContainerType locType) {
		String pattern = (locType.getName()+"XX").substring(0, 2).toUpperCase() + "######";
		if(locType.isMultiple()) pattern += "-N";
		return getNextId(Category.CONTAINER, pattern, null);
	}

	public String getNextId(BiosampleDto biosample) throws Exception {
		if(biosample==null) 
			throw new Exception("You must give a biosample");
		BiotypeDto biotype = biosample.getBiotype();
		if(biotype==null) throw new Exception("You must give a biotype");
		String prefix = biotype.getPrefix();
		if(prefix==null || prefix.length()==0) throw new Exception("SampleIds cannot be generated for " +biotype.getName()+" because the prefix is null");

		if(biotype.getPrefix().contains("{StudyId}") && biosample.getStudy()==null) throw new Exception("You nust select a study first");
		String nextId = getNextId(Category.BIOSAMPLE, prefix, biosample);
		return nextId;
	}
	
	private String getNextId(Category cat, String pattern, BiosampleDto context) {
		String formattedPattern = formatPattern(pattern, context);

		assert formattedPattern.contains("#");
		int firstIndex = formattedPattern.indexOf("#");
		int lastIndex = formattedPattern.lastIndexOf("#");
		int prefLength = firstIndex;
		int incrementLength = lastIndex-firstIndex+1;
		int suffLength = formattedPattern.length() - lastIndex-1;
		String suffix = formattedPattern.substring(formattedPattern.length()-suffLength);
		if(suffix.equals("-N")) suffix = "";

		//Retrieve next id
		List<String> list = prefix2PrecomputedIds.get(cat+"_"+formattedPattern);
		boolean newPrefix = list == null;

		//Generate next id
		if(list==null || list.size()==0) {


			list = new ArrayList<>();
			prefix2PrecomputedIds.put(cat+"_"+formattedPattern, list);

			int reserveN = cat== Category.BIOSAMPLE || cat== Category.CONTAINER? 10: 1;

			//Find the last used increment
			int startIncrement = 1;

			String lastUsed = lastUsedIds.get(cat+"_"+formattedPattern);
			if(lastUsed==null) {
				String lastDBBarcode = getLastBarcode(cat, formattedPattern);
				if(lastDBBarcode==null) {
					startIncrement = 1;
				} else {
					lastDBBarcode = MiscUtils.extractStartDigits(lastDBBarcode.substring(prefLength));
					try {
						startIncrement = lastDBBarcode.length()==0? 1: 1 + Integer.parseInt(MiscUtils.extractStartDigits(lastDBBarcode));
					} catch (Exception e) {
						System.err.println("Error in getting last barcode: "+e);
					}
				}
			}
			if(lastUsed!=null) {
				lastUsed = MiscUtils.extractStartDigits(lastUsed.substring(prefLength));
				try {
					int startIncrement2 = lastUsed.length()==0? 1: 1 + Integer.parseInt(lastUsed);
					startIncrement = Math.max(startIncrement, startIncrement2);
				} catch (Exception e) {
					System.err.println("Error in getting last barcode: "+e);
				}
			}


			//Find the theoretical last barcode, and update it.
				List<BarcodeSequence> barcodeSequences = getBarcodeSequencesByTypeAndCategory(formattedPattern, cat.getCat());
			if(barcodeSequences.size()==0) {
				//Create a new sequence
				String nextBarcode =  formattedPattern.substring(0, prefLength) + new DecimalFormat(MiscUtils.repeat("0", incrementLength)).format(startIncrement+reserveN-1) + suffix;
				BarcodeSequenceDto sequence = new BarcodeSequenceDto(cat, formattedPattern, nextBarcode);
				save(sequence);
			} else {
				BarcodeSequence sequence = barcodeSequences.get(0);
				String lastBarcode = sequence.getLastBarcode();
				int startBarcodeSeq;
				try {
					startBarcodeSeq = lastBarcode==null? 1: 1 + Integer.parseInt(MiscUtils.extractStartDigits(lastBarcode));
				} catch (Exception e) {
					startBarcodeSeq = startIncrement;
				}
				if(newPrefix) {
					if(startBarcodeSeq<startIncrement) {
						//The sequence number is smaller than the actual sampleId
						startBarcodeSeq = startIncrement;
					} else if(startIncrement>=0 && startBarcodeSeq> startIncrement + Constants.MAX_HOLE) {
						//Such a big hole in the sequence is very unlikely,
						startBarcodeSeq = startIncrement;
					}
				}
				//Start the sequence at ##1 if possible
				while(startBarcodeSeq%10!=1) startBarcodeSeq++;
				startIncrement = startBarcodeSeq;
				sequence.setLastBarcode(formattedPattern.substring(0, prefLength) + new DecimalFormat(MiscUtils.repeat("0", incrementLength)).format(startIncrement+reserveN-1) + suffix);
			}

			//Reserve the barcodes for future use
			for (int i = 0; i < reserveN; i++) {
				int nextId = startIncrement+i;
				String nextBarcode =  formattedPattern.substring(0, prefLength) + new DecimalFormat(MiscUtils.repeat("0", incrementLength)).format(nextId) + suffix;
				list.add(nextBarcode);
			}

		}

		String res = list.remove(0);
		lastUsedIds.put(cat+"_"+formattedPattern, res);
		return res;
	}
	
	public BarcodeSequenceDto map(BarcodeSequence barcodeSequence) {
		BarcodeSequenceDto barcodeSequenceDto = idToBarcodeSequence.get(barcodeSequence.getId());
		if(barcodeSequenceDto==null) {
			barcodeSequenceDto = dozerMapper.map(barcodeSequence,BarcodeSequenceDto.class,"barcodeSequenceCustomMapping");
			if(idToBarcodeSequence.get(barcodeSequence.getId())==null)
				idToBarcodeSequence.put(barcodeSequence.getId(), barcodeSequenceDto);
			else
				barcodeSequenceDto=idToBarcodeSequence.get(barcodeSequence.getId());
		}
		return barcodeSequenceDto;
	}
	
	public static String formatPattern(String pattern, BiosampleDto biosample) {
		//Replace patterns in the prefix
		if(pattern.indexOf("{StudyId}")>=0) {
			pattern = pattern.replace("{StudyId}", biosample==null || biosample.getStudy()==null || biosample.getStudy().getStudyId().length()==0? "NoStudy": biosample.getStudy().getStudyId());
		}
		if(pattern.indexOf("{")>=0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			pattern = pattern.replace("{YY}", new DecimalFormat("00").format(cal.get(Calendar.YEAR) % 100));
			pattern = pattern.replace("{YYYY}", new DecimalFormat("0000").format(cal.get(Calendar.YEAR)));
			pattern = pattern.replace("{MM}", new DecimalFormat("00").format(cal.get(Calendar.MONTH)+1));
			pattern = pattern.replace("{DD}", new DecimalFormat("00").format(cal.get(Calendar.DAY_OF_MONTH)));
		}
		if(!pattern.contains("#")) pattern += "######";
		return pattern;
	}
	
	private String getLastBarcode(Category cat, String pattern) {
		String lastBarcode;
		if(cat == Category.BIOSAMPLE) {
			lastBarcode = getLastBiosampleBarcode(pattern);
		} else if(cat== Category.LOCATION) {
			lastBarcode = getLastLocationBarcode(pattern);
		} else if(cat == Category.CONTAINER) {
			lastBarcode = getLastContainerBarcode(pattern);
		} else {
			throw new IllegalArgumentException("Invalid category: "+cat);
		}
		return lastBarcode;

	}

	public String getExample(String pattern) {
		String s = formatPattern(pattern, null);
		assert s.contains("#");
		int index = s.indexOf("#");
		int index2 = s.lastIndexOf("#");
		return s.substring(0, index) + new DecimalFormat(MiscUtils.repeat("0", index2-index+1)).format(1) + s.substring(index2+1);
	}
}
