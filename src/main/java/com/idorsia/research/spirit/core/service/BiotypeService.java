package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.security.entity.User;
import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.DataType;
import com.idorsia.research.spirit.core.dao.BiotypeDao;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.view.BiosampleQuery;
import com.idorsia.research.spirit.core.model.Biotype;
import com.idorsia.research.spirit.core.model.BiotypeMetadata;
import com.idorsia.research.spirit.core.util.Cache;
import com.idorsia.research.spirit.core.util.MiscUtils;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class BiotypeService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 3613008156130940055L;
	@Autowired
	private BiotypeDao biotypeDao;
	@Autowired
	private BiotypeMetadataService biotypeMetadataService;
	@Autowired
	private BiosampleService biosampleService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, BiotypeDto> idTobiotype = (Map<Integer, BiotypeDto>) getCacheMap(BiotypeDto.class);
	
	public Biotype get(Integer id) {
		return biotypeDao.get(id);
	}
	
	public Integer addBiotype(Biotype biotype) {
		return biotypeDao.addBiotype(biotype);
	}

	public Biotype getByName(String name) {
		return biotypeDao.getByName(name);
	}

	public List<Biotype> getByParentId(Integer parentId) {
		return biotypeDao.getByParentId(parentId);
	}

	public List<Biotype> list() {
		return biotypeDao.list();
	}

	public int getCount() {
		return biotypeDao.getCount();
	}

	public Integer saveOrUpdate(Biotype biotype) {
		return biotypeDao.saveOrUpdate(biotype);
	}
	
	@Transactional
	public void save(BiotypeDto biotype) throws Exception {
		save(biotype, false);
	}

	@Transactional
	public void save(Collection<BiotypeDto> biotypes) {
		for(BiotypeDto biotype : biotypes) {
			try {		
				save(biotype, true);
			} catch(Exception e) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
		}
		AbstractService.clearSavedItem();
		AbstractService.clearTransient(true);
	}

	@SuppressWarnings("deprecation")
	protected void save(BiotypeDto biotype, Boolean cross) throws Exception {
		try {
			if (biotype != null && !savedItems.contains(biotype)) {
				savedItems.add(biotype);
				presave(biotype);
				if(biotype.getId()!=Constants.NEWTRANSIENTID) {
					deleteChildren(biotype);
				}
				if (biotype.getParent() != null
						&& biotype.getParent().getId() == Constants.NEWTRANSIENTID)
					save(biotype.getParent(), true);
				biotype.setUpdDate(new Date());
				biotype.setUpdUser(UserUtil.getUsername());
				if(biotype.getId().equals(Constants.NEWTRANSIENTID)) {
					biotype.setCreDate(new Date());
					biotype.setCreUser(UserUtil.getUsername());
				}
				biotype.setId(saveOrUpdate(dozerMapper.map(biotype, Biotype.class, "biotypeCustomMapping")));
				idTobiotype.put(biotype.getId(), biotype);
				if(biotype.getChildrenNoMapping()!=null)
					for (BiotypeDto child : biotype.getChildren())
							save(child, true);
				if(biotype.getMetadatasNoMapping()!=null)
					for (BiotypeMetadataDto metadata : biotype.getMetadatas())
						biotypeMetadataService.save(metadata, true);
			}
		}catch(Exception e) {
			if (!cross) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
			throw e;
		}finally{
			if (!cross) {
				AbstractService.clearSavedItem();
				AbstractService.clearTransient(true);
			}
		}
	}

	private void presave(BiotypeDto biotype) throws Exception {
		if(biotype.getCategory()==null) throw new Exception("The category is required");
		if(biotype.getName()==null || biotype.getName().length()==0) throw new Exception("The name is required");

		//Update the bidirectional link
		for (BiotypeMetadataDto m : biotype.getMetadatas()) {
			if(m.getName().trim().length()==0) throw new Exception("The Metadata name cannot be empty");
			if(m.getDatatype()==null) throw new Exception("The Metadata datatype cannot be empty");
			m.setBiotype(biotype);
		}

		//Set a prefic if it is null or non empty
		if(biotype.getPrefix()==null || biotype.getPrefix().length()==0) {
			biotype.setPrefix(biotype.getName().substring(0, Math.min(3, biotype.getName().length())));
		}
	}

	@SuppressWarnings("deprecation")
	private void deleteChildren(BiotypeDto biotype) {
		if(biotype.getMetadatasNoMapping()!=null) {
			for(BiotypeMetadata metadata : biotypeMetadataService.getByBiotype(biotype.getId())) {
				Boolean found = false;
				for(BiotypeMetadataDto child: biotype.getMetadatas()) {
					if(metadata.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					biotypeMetadataService.delete(biotypeMetadataService.map(metadata));
				}
			}
		}
	}

	public BiotypeDao getBiotypeDao() {
		return biotypeDao;
	}

	public void setBiotypeDao(BiotypeDao biotypeDao) {
		this.biotypeDao = biotypeDao;
	}

	public synchronized BiotypeDto map(Biotype biotype) {
		BiotypeDto biotypeDto = idTobiotype.get(biotype.getId());
		if(biotypeDto==null) {
			biotypeDto=dozerMapper.map(biotype, BiotypeDto.class,"biotypeCustomMapping");
			if(idTobiotype.get(biotype.getId())==null) { 
				idTobiotype.put(biotype.getId(), biotypeDto);
			}
			else
				biotypeDto=idTobiotype.get(biotype.getId());
		}
		return biotypeDto;
	}
	

	public Biotype map(BiotypeDto biotypeDto) {
		Biotype biotype = dozerMapper.map(biotypeDto, Biotype.class,"biotypeCustomMapping");
		return biotype;
	}
	
	public List<BiotypeDto> map(Collection<Biotype> biotypes) {
		List<BiotypeDto> biotypesDto = new ArrayList<>();
		for(Biotype biotype : biotypes) {
			biotypesDto.add(map(biotype));
		}
		return biotypesDto;
	}

	public BiotypeMetadataDto getMetadataByName(BiotypeDto biotype, String name) {
		for(BiotypeMetadataDto metadata : biotype.getMetadatas()) {
			if(metadata.getName().equals(name))
				return metadata;
		}
		return null;
	}

	public BiotypeMetadataDto getMetadataById(BiotypeDto biotype, int id) {
		for(BiotypeMetadataDto metadata : biotype.getMetadatas()) {
			if(metadata.getId().equals(id))
				return metadata;
		}
		return null;
	}
	
	public BiotypeDto getBiotypeDto(Integer id) {
		return map(get(id));
	}
	
	public List<BiotypeDto> removeAbstract(List<BiotypeDto> biotypes) {
		List<BiotypeDto> res  = new ArrayList<>();
		if(biotypes==null) 
			return res;
		for (BiotypeDto biotype : biotypes) {
			if(!biotype.getIsAbstract()) 
				res.add(biotype);
		}
		return res;
	}

	public BiotypeDto getBiotype(String name) {
		if(name==null) return null;

		Map<Integer, BiotypeDto> id2biotype = getId2Biotype();
		for (BiotypeDto t : id2biotype.values()) {
			if(t.getName().equals(name)) 
				return t;
		}
		return null;
	}
	
	public List<BiotypeDto> getBiotypes(){
		return getBiotypes(false);
	}
			
	public List<BiotypeDto> getBiotypes(boolean showHidden) {
		Map<Integer, BiotypeDto> id2biotype = getId2Biotype();
		List<BiotypeDto> todo = new ArrayList<>(id2biotype.values());
		Collections.sort(todo);

		List<BiotypeDto> types = new ArrayList<>();
		while(todo.size()>0) {
			for (Iterator<BiotypeDto> iterator = todo.iterator(); iterator.hasNext();) {
				BiotypeDto biotype = iterator.next();
				if(biotype.getParent()==null) {
					biotype.setDepth(0);
					types.add(biotype);
					iterator.remove();
				} else {
					int index = types.indexOf(biotype.getParent());
					if(index>=0) {
						int depth = types.get(index).getDepth() + 1;
						index++;
						while(index<types.size() && biotype.getParent().equals(types.get(index).getParent())) {
							index++;
						}
						biotype.setDepth(depth);
						types.add(index, biotype);
						iterator.remove();
					}
				}
			}
		}
		if(!showHidden) {
			types = removeHidden(types);
		}
		return types;
	}
	
	public static List<BiotypeDto> removeHidden(Collection<BiotypeDto> biotypes) {
		List<BiotypeDto> res  = new ArrayList<>();
		if(biotypes==null) 
			return res;
		for (BiotypeDto biotype : biotypes) {
			if(!biotype.getIsHidden()) 
				res.add(biotype);
		}
		return res;
	}
	
	public Map<Integer, BiotypeDto> getId2Biotype() {
		mapIds(map(list()));
		return idTobiotype;
	}

	public void mapIds(List<BiotypeDto> biotypes) {
		for(BiotypeDto biotype : biotypes) {
			idTobiotype.put(biotype.getId(), biotype);
		}
	}

	@SuppressWarnings("unchecked")
	public Set<String> getAutoCompletionFieldsForName(BiotypeDto biotype, StudyDto study) {
		if(biotype==null || biotype.getId()<=0) return new TreeSet<String>();

		//Use Cache
		String key = "biotype_autocompletion_name_"+biotype.getId()+"_"+(study==null?"": study.getId());
		Set<String> res = (Set<String>) Cache.getInstance().get(key);
		if(res==null) {
			List<String> values = biotypeDao.getAutoCompletionFieldsForName(biotype, study);
			res = new TreeSet<String>(CompareUtils.STRING_COMPARATOR);
			res.addAll(values);
			Cache.getInstance().add(key, res, Cache.FAST);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public Set<String> getAutoCompletionFields(BiotypeMetadataDto metadataType, StudyDto study) {
		if(metadataType==null || metadataType.getId()<=0) return new TreeSet<String>();

		//Use Cache
		String key = "biotype_autocompletion_"+metadataType.getId()+"_"+(study==null?"": study.getId());
		Set<String> res = (Set<String>) Cache.getInstance().get(key);
		if(res==null) {
			res = new TreeSet<String>(CompareUtils.STRING_COMPARATOR);
			res.addAll(biotypeDao.getAutoCompletionFields(metadataType, study));
			Cache.getInstance().add(key, res, 60);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public Set<String> getAutoCompletionFieldsForSampleId(BiotypeDto biotype, BiotypeMetadataDto fromAgregated, StudyDto study) {
		if(biotype==null || biotype.getId()<=0) return new TreeSet<String>();
		//Use Cache
		String key = "biotype_autocompletion_sampleid_"+biotype.getId()+"_"+fromAgregated+"_"+study;
		Set<String> res = (Set<String>) Cache.getInstance().get(key);
		if(res==null) {
			res = new TreeSet<String>(CompareUtils.STRING_COMPARATOR);
			res.addAll(biotypeDao.getAutoCompletionFieldsForSampleId(biotype, fromAgregated, study));
			Cache.getInstance().add(key, res, Cache.FAST);
		}
		return res;
	}

	public List<BiotypeDto> getHierarchy(BiotypeDto biotype) {
		List<BiotypeDto> res = new ArrayList<>();
		BiotypeDto b = biotype;
		while(b!=null) {
			res.add(0, b);
			b = b.getParent();
		}
		return res;
	}

	public List<String> extractChoices(BiotypeMetadataDto biotype) {
		return MiscUtils.splitChoices(biotype.getParameters());
	}

	public boolean isAnimal(BiotypeDto biotype) {
		if (biotype == null) return false;
		if (Constants.ANIMAL.equals(biotype.getName())) return true;
		return false;
	}

	public void mapMetadatas(BiotypeDto biotype) {
		List<BiotypeMetadataDto> metadatas = biotypeMetadataService.map(biotypeMetadataService.getByBiotype(biotype.getId()));
		Collections.sort(metadatas);
		biotype.setMetadatas(metadatas);
	}

	public void mapChildren(BiotypeDto biotype) {
		List<BiotypeDto> children = new ArrayList<>();
		for(Biotype child : getByParentId(biotype.getId())) {
			children.add(map(child));
		}
		biotype.setChildren(children);
	}

	@SuppressWarnings("unchecked")
	public Collection<String> getAutoCompletionFieldsForComments(BiotypeDto biotype, StudyDto study) {
		if (biotype == null || biotype.getId() <= 0)
			return new TreeSet<String>();
		// Use Cache
		String key = "biotype_autocompletion_comments_" + biotype.getId() + "_" + (study == null ? "" : study.getId());
		Set<String> res = (Set<String>) Cache.getInstance().get(key);
		if (res == null) {
			res = new TreeSet<String>(CompareUtils.STRING_COMPARATOR);
			res.addAll(biotypeDao.getAutoCompletionFieldsForName(biotype, study));
			Cache.getInstance().add(key, res, Cache.FAST);
		}
		return res;
	}

	public int countRelations(BiotypeDto biotype) {
		if (biotype == null || biotype.getId() <= 0) return 0;
		int id = biotype.getId();
		return biotypeDao.countRelation(id);
	}

	public void moveMetadataToName(BiotypeMetadataDto biotypeMetadata, User user) throws Exception {
		if(user==null || !user.isSuperAdmin()) throw new Exception("You must be an admin to rename a metadata");
		BiotypeDto biotype = biotypeMetadata.getBiotype();
		if(biotype.getNameLabel()!=null) 
			throw new Exception(biotype+" has already a MainField");

		Date now = new Date();

		//Add the name
		biotype.setNameLabel(biotypeMetadata.getName());
		biotype.setNameAutoComplete(biotypeMetadata.getDatatype()==DataType.AUTO);
		biotype.setNameRequired(biotypeMetadata.getRequired());
		biotype.setUpdDate(now);
		biotype.setUpdUser(user.getUsername());

		//Query and Update the biosamples
		BiosampleQuery q = new BiosampleQuery();
		q.setBiotype(biotype);
		List<BiosampleDto> biosamples = biosampleService.queryBiosamples(q, null);
		for(BiosampleDto b: biosamples) {
			String m = biosampleService.getMetadataValue(b, biotypeMetadata);
			if(m!=null) {
				b.setLocalId(m);
				b.setUpdUser(user.getUsername());
				b.setUpdDate(now);
			}
		}
		///Remove the metadata
		biotype.getMetadatas().remove(getMetadataByName(biotype, biotypeMetadata.getName()));
		save(biotype);
	}

	public void moveNameToMetadata(BiotypeDto biotype, User user) throws Exception {
		if(user==null || !user.isSuperAdmin()) throw new Exception("You must be an admin to rename a metadata");
		//Add the metadata
		String newMetadata = biotype.getNameLabel();
		if(getMetadataByName(biotype, newMetadata)!=null) 
			throw new Exception(newMetadata+" is already a metadata");
		BiotypeMetadataDto bm = new BiotypeMetadataDto();
		bm.setName(newMetadata);
		bm.setDatatype(biotype.getNameAutoComplete()? DataType.AUTO: DataType.ALPHA);
		bm.setBiotype(biotype);
		bm.setRequired(biotype.getNameRequired());
		biotype.getMetadatas().add(bm);

		//Query and Update the biosamples
		BiosampleQuery q = new BiosampleQuery();
		q.setBiotype(biotype);
		List<BiosampleDto> biosamples = biosampleService.queryBiosamples(q, null);
		for(BiosampleDto b: biosamples) {
			biosampleService.setMetadataValue(b, newMetadata, b.getLocalId());
			biosampleService.save(b);
		}
		///Remove the name
		biotype.setNameLabel(null);
		save(biotype);
	}


	public int renameNames(BiotypeDto biotype, String value, String newValue, User user) throws Exception {
		if(user==null || !user.isSuperAdmin()) 
			throw new Exception("You must be an admin to rename a name");
		List<BiosampleDto> biosamples = biosampleService.getBiosampleByBiotypeName(value);
		for (BiosampleDto b : biosamples) {
			b.setUpdUser(user.getUsername());
			b.setLocalId(newValue);
			biosampleService.save(b);
		}
		return biosamples.size();
	}

	public int renameMetadata(BiotypeMetadataDto att, String value, String newValue, User user) throws Exception {
		if(user==null || !user.isSuperAdmin())
			throw new Exception("You must be an admin to rename a metadata");
		List<BiosampleDto> biosamples = biosampleService.getBiosampleByMetadataValue(value);
		for (BiosampleDto b : biosamples) {
			if(biosampleService.getMetadataValue(b, att).equals(value)) {
				biosampleService.setMetadataValue(b, att, newValue);
				biosampleService.save(b);
			}
		}
		return biosamples.size();
	}

	public List<Biotype> getBiotypes(String studyId, Set<Integer> assayIds) {
		return biotypeDao.getBiotypes(studyId, assayIds);
	}

	public BiotypeDto getTopParent(BiotypeDto biotype) {
		BiotypeDto top = biotype;
		int count = 0;
		while(top.getParent()!=null && count++<10) {
			top = top.getParent();
		}
		return top;
	}

	public List<String> getNames(Collection<BiotypeDto> biotypes) {
		List<String> res = new ArrayList<>();
		if(biotypes==null) return res;
		for (BiotypeDto s : biotypes) {
			res.add(s.getName());
		}
		return res;
	}
}
