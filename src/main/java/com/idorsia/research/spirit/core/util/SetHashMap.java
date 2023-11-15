package com.idorsia.research.spirit.core.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SetHashMap<KEY, VALUE> extends LinkedHashMap<KEY, Set<VALUE>> {

	private static final long serialVersionUID = -8402059311690364432L;

	public SetHashMap() {
	}

	public SetHashMap(SetHashMap<KEY, VALUE> copy) {
		addAll(copy);
	}

	public void add(KEY key, VALUE value) {
		Set<VALUE> l = get(key);
		if (l == null) {
			l = new LinkedHashSet<VALUE>();
			put(key, l);
		}
		l.add(value);
	}

	public void delete(KEY key, VALUE value) {
		Set<VALUE> l = get(key);
		if (l == null) {
			return;
		}
		l.remove(value);
	}

	public void addAll(KEY key, Collection<VALUE> values) {
		Set<VALUE> l = get(key);
		if (l == null) {
			l = new LinkedHashSet<VALUE>();
			put(key, l);
		}
		l.addAll(values);
	}

	public void addAll(SetHashMap<KEY, VALUE> values) {
		for (KEY key : values.keySet()) {
			addAll(key, values.get(key));
		}
	}

	public void addAll(Map<KEY, Collection<VALUE>> values) {
		for (KEY key : values.keySet()) {
			addAll(key, values.get(key));
		}
	}

}
