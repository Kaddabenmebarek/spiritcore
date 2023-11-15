package com.idorsia.research.spirit.core.util;

import com.actelion.research.util.CompareUtils;

public class ObjectWrapper implements Comparable<ObjectWrapper>{

    public static enum ObjectType {
        KEY,
        VALUE
    }
    private ObjectType type;
    private Object object;
    public ObjectWrapper(Object object, ObjectType type) {
        this.object = object;
        this.type = type;
    }
    @Override
    public int compareTo(ObjectWrapper o) {
        if(o==null) return -1;

        int c = CompareUtils.compare(object, o.getObject());
        if(c!=0) return c;
        return CompareUtils.compare(type, o.getType());
    }
    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public ObjectType getType() {
        return type;
    }

    public String toString() {
        return  ""+object;
    }
    @Override
    public boolean equals(Object o) {
        ObjectWrapper w = (ObjectWrapper) o;
        return CompareUtils.compare(object, w.getObject())==0 && CompareUtils.compare(type, w.getType())==0;
    }
    @Override
    public int hashCode() {
        return ((object==null?0: object.hashCode()) + (type==null?0: type.hashCode()))%Integer.MAX_VALUE;
    }
}
