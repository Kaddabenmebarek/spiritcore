package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.SpiritPropertyDao;
import com.idorsia.research.spirit.core.model.SpiritProperty;
import com.idorsia.research.spirit.core.util.MiscUtils;
import com.idorsia.research.spirit.core.util.SpiritRights.ActionType;
import com.idorsia.research.spirit.core.util.SpiritRights.UserType;
import com.idorsia.research.spirit.core.util.property.PropertyKey;

@Service
public class SpiritPropertyService implements Serializable {
	
	private static final long serialVersionUID = 4470199738205372120L;

	private List<SpiritProperty> properties;

	@Autowired
	private SpiritPropertyDao spiritPropertyDao;

	public SpiritProperty getPropertyById(String id) {
		return spiritPropertyDao.getById(id);
	}

	public List<SpiritProperty> getAllPorperties(){
		if(properties==null)
			properties = spiritPropertyDao.getAll();
		return properties;
	}
	
	public void setPropertyValue(String id, String value) {
		if(properties==null)
			properties=getAllPorperties();
		for(SpiritProperty sp : properties) {
			if(sp.getId().equals(id))
				sp.setValue(value);
				return;
		}
		properties.add(new SpiritProperty(id, value));
	}
	
	public String getPropertyValue(String id) {
		if(properties==null)
			properties=getAllPorperties();
		for(SpiritProperty sp : properties) {
			if(sp.getId().equals(id))
				return sp.getValue();
		}
		return null;
	}
	
	/**
	 * Gets the value of a simple property or the default value
	 * @param p
	 * @return
	 */
	public String getValue(PropertyKey p) {
		assert p.getParentProperty()==null;
		assert p!=PropertyKey.DB_VERSION;

		String key = p.getKey();
		String v = getPropertyValue(key);
		if(v==null) v = p.getDefaultValue();
		return v;
	}

	public int getValueInt(PropertyKey p) {
		try {
			return Integer.parseInt(getValue(p));
		} catch(Exception e) {
			return Integer.parseInt(p.getDefaultValue());
		}
	}

	/**
	 * Gets the value of a simple property or the default value
	 * The result is split by ','
	 */
	public String[] getValues(PropertyKey p) {
		return MiscUtils.split(getValue(p), ",");
	}

	public String getValue(PropertyKey p, String nestedValue) {
		return getValue(p, new String[]{nestedValue});
	}

	/**
	 * Gets the value of a nested property or the default value. One must give the values of all the parent properties
	 */
	public String getValue(PropertyKey p, String[] nestedValues) {
		LinkedList<PropertyKey> list = new LinkedList<>();
		PropertyKey tmp = p.getParentProperty();
		while(tmp!=null) {
			list.addFirst(tmp);
			tmp = tmp.getParentProperty();
		}

		assert list.size()==nestedValues.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i).getKey());
			sb.append("." + nestedValues[i]);
		}
		sb.append("." + p.getKey());

		//Retrieve the value first from the adapter, then from the DB
		String key = sb.toString();
		String v = getPropertyValue(key);
		if(v==null) v = p.getDefaultValue(nestedValues);
		return v;
	}

	public String[] getValues(PropertyKey p, String nestedValue) {
		if(nestedValue==null) return new String[0];
		return getValues(p, new String[]{nestedValue});
	}

	/**
	 * Gets the value of a nested property or the default value. One must give the values of all the parent properties
	 * The result is splitted by ','
	 */
	public String[] getValues(PropertyKey p, String[] nestedValues) {
		return MiscUtils.split(getValue(p, nestedValues), ",");
	}

	/**
	 * Returns true if the given property is checked
	 */
	public boolean isChecked(PropertyKey p) {
		return "true".equals(getValue(p));
	}

	/**
	 * Returns true if the given property is checked
	 */
	public boolean isChecked(PropertyKey p, String nestedValues) {
		return "true".equals(getValue(p, nestedValues));
	}

	/**
	 * Returns true if the given property is checked
	 */
	public boolean isChecked(PropertyKey p, String[] nestedValues) {
		return "true".equals(getValue(p, nestedValues));
	}

	public boolean isSelected(PropertyKey p, String val) {
		return MiscUtils.contains(getValues(p), val);
	}

	public boolean isSelected(PropertyKey p, String nestedValues, String val) {
		return MiscUtils.contains(getValues(p, nestedValues), val);
	}

	public boolean isSelected(PropertyKey p, String[] nestedValues, String val) {
		return MiscUtils.contains(getValues(p, nestedValues), val);
	}


	/**
	 * Set the value of a simple property (without saving)
	 * @param p
	 * @param v
	 */
	public void setValue(PropertyKey p, String v) {
		setValue(p, new String[]{}, v);
	}

	/**
	 * Set the value of a nested property (without saving)
	 * @param p
	 * @param v
	 */
	public void setValue(PropertyKey p, String nested, String v) {
		setValue(p, new String[]{nested}, v);
	}

	/**
	 * Set the value of a simple or nested property (without saving)
	 * @param p
	 * @param nested an array, whose length is equal to the number of parents of p
	 * @param v
	 */
	public void setValue(PropertyKey p, String[] nested, String v) {
		StringBuilder propertyKey = new StringBuilder();
		propertyKey.append(p.getKey());

		PropertyKey c = p.getParentProperty();
		for (int i = nested.length-1; i >= 0; i--) {
			assert c!=null;
			propertyKey.insert(0, c.getKey() + "." + nested[i] + ".");
			c = c.getParentProperty();
		}
		assert c==null;

		//Make sure the user is not allowed to update this property
		String key = propertyKey.toString();
		properties.add(new SpiritProperty(key, v));
	}

	/**
	 * Gets all values in a String map.
	 * The returned values may be overidden by the adapter
	 * @return
	 */
	public List<SpiritProperty> getValues() {
		List<SpiritProperty> values = new ArrayList<>();
		values.addAll(properties);
		LoggerFactory.getLogger(getClass()).debug("properties="+properties);
		return values;
	}

	public String[] getUserRoles() {
		Set<String> roles = new LinkedHashSet<>();
		roles.add(Constants.ADMIN);
		for (String string : MiscUtils.split(getValue(PropertyKey.USER_ROLES), ",")) {
			roles.add(string);
		}
		return roles.toArray(new String[roles.size()]);
	}

	public boolean isOpen() {
		try {
			return "open".equals(getValue(PropertyKey.USER_OPENBYDEFAULT));
		} catch (Exception e) {
			return "true".equals(PropertyKey.USER_OPENBYDEFAULT.getDefaultValue());
		}
	}

	/**
	 * Is the software running in advanced mode. The advanced mode allows the following extra features:
	 * - edit biosample in form mode
	 *
	 * @return
	 */
	public boolean isAdvancedMode() {
		return isChecked(PropertyKey.SYSTEM_ADVANCED);
	}


	/**
	 * Check the user rights for the given actionType and userType.
	 */
	public boolean isChecked(ActionType action, UserType userType) {
		return isChecked(action, userType, null);
	}

	/**
	 * Check the user rights for the given actionType and role.
	 */
	public boolean isChecked(ActionType action, String role) {
		return isChecked(action, null, role);
	}

	public static String getKey(ActionType action, UserType userType, String role) {
		String key = "rights." + action + "_" + (userType==null? "role_" + role: userType);
		return key;
	}

	/**
	 * Check the user rights for the given actionType and userType.
	 * If userType==null, then a proper role must be given
	 *
	 * By default:
	 * - the admin can do everything
	 * - everybody can create samples
	 * - the owner, the updater have edit rights
	 *
	 * @param action
	 * @param userType
	 * @param role
	 */
	public boolean isChecked(ActionType action, UserType userType, String role) {
		boolean defaultValue;
		if(Constants.ADMIN.equals(role)) {
			defaultValue = true;
		} else if(action.name().contains("CREATE")) {
			defaultValue = true;
		} else if(action.name().contains("READ")) {
			defaultValue = isOpen() || userType== UserType.CREATOR || userType==UserType.UPDATER;
		} else if(action.name().contains("WORK")) {
			defaultValue = userType== UserType.CREATOR || userType==UserType.UPDATER;
		} else if(action.name().contains("EDIT")) {
			defaultValue = userType== UserType.CREATOR || userType==UserType.UPDATER;
		} else if(action.name().contains("DELETE")) {
			defaultValue = userType== UserType.CREATOR;
		} else {
			defaultValue = false;
		}

		return isChecked(action, userType, role, defaultValue);
	}

	/**
	 * Check the user rights for the given actionType and userType.
	 * If userType==null, then a proper role must be given
	 * If the settings are not saved, returns the default value
	 * @param action
	 * @param userType
	 * @param role
	 * @param defaultValue
	 * @return
	 */
	public boolean isChecked(ActionType action, UserType userType, String role, boolean defaultValue) {
		String key = getKey(action, userType, role);
		String v = getPropertyValue(key);
		boolean res = v==null? defaultValue: "true".equals(v);
		return res;
	}

	public void setChecked(ActionType action, UserType userType, boolean val) {
		setChecked(action, userType, null, val);
	}

	public void setChecked(ActionType action, String role, boolean val) {
		setChecked(action, null, role, val);
	}
	/**
	 * Sets the user rights for the given actionType and userType.
	 * If userType==null, then a proper role must be given
	 * Calling this function does not save the properties. use "saveProperties" to save
	 * @param action
	 * @param userType
	 * @param role
	 * @param val
	 */
	public void setChecked(ActionType action, UserType userType, String role, boolean val) {
		String key = getKey(action, userType, role);
		setPropertyValue(key, val?"true":"false");
	}
}
