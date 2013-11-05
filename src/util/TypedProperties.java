/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.queue.function.QFunction;

/**
 * A wrapper for a Properties object that allows access to properties by other 
 * data-types than just String.
 * 
 * @author jbeckstrom
 */
public class TypedProperties {

    private final Properties props;
    
    public TypedProperties(){
        this(new Properties());
    }

    public TypedProperties(Properties props) {
        this.props = props;
    }
    
    public void loadFromFile(File f){
        try {
            props.load(new FileInputStream(f));
        } catch (IOException ex) {
            Logger.getLogger(TypedProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sets all fields annotated with @Argument(required=false) of obj to the 
     * values of the properties corresponding to the field name prepended by 
     * prefix. So, if obj had a field named 'count' and a property existed 
     * 'prefix.count=100' then after calling this method with prefix='prefix.'
     * the value of the instance variable 'count' in obj would be set to 100.
     * 
     * @param <T>
     * @param obj
     * @param prefix 
     */
    public <T extends Annotation> void applyToArgumentAnnotatedFieldsOfObjectUsingPrefix(Object obj, String prefix) {
        for (Field field : obj.getClass().getFields()) {
            if (field.getAnnotation(Argument.class) != null) {
                field.setAccessible(true);
                try {
                    Object value = getValueForFieldOfInstanceUsingPrefix(field, obj, prefix);
                    field.set(obj, value);
                } catch (Exception ex) {
                    //do nothing
                }

            }
        }
    }

    public Object getValueForFieldUsingPrefix(final Field field, final String prefix) throws IllegalArgumentException, IllegalAccessException {
        return getValueForFieldOfInstanceUsingPrefix(field, null, prefix);
    }

    public Object getValueForFieldOfInstanceUsingPrefix(final Field field, final Object instance, final String prefix) throws IllegalArgumentException, IllegalAccessException {
        if (field.getType() == Integer.class) {
            Integer dflt = (Integer) getValueOfFieldOfInstance(field, instance);
            return getInteger(prefix + field.getName(), dflt);
        } else if (field.getType() == Double.class) {
            Double dflt = (Double) getValueOfFieldOfInstance(field, instance);
            return getDouble(prefix + field.getName(), dflt);
        } else if (field.getType() == Byte.class) {
            Byte dflt = (Byte) getValueOfFieldOfInstance(field, instance);
            return getByte(prefix + field.getName(), dflt);
        } else if (field.getType() == String.class) {
            String dflt = (String) getValueOfFieldOfInstance(field, instance);
            return getString(prefix + field.getName(), dflt);
        } else if (field.getType() == Boolean.class) {
            Boolean dflt = (Boolean) getValueOfFieldOfInstance(field, instance);
            return getBoolean(prefix + field.getName(), dflt);
        }else if (field.getType() == Float.class) {
            Float dflt = (Float) getValueOfFieldOfInstance(field, instance);
            return getFloat(prefix + field.getName(), dflt);
        }else if (field.getType().isEnum()) {
            Enum dflt = (Enum) getValueOfFieldOfInstance(field, instance);
            return getEnum(prefix + field.getName(), dflt, (Class<Enum>) field.getType());
        }else{
            throw new IllegalStateException("Unsupported field type: "+field.getType().getName());
        }
    }

    /**
     * @param field
     * @param instance
     * @return the value of the instance field or null if the value is undefined.
     * @throws IllegalArgumentException
     * @throws IllegalAccessException 
     */
    public Object getValueOfFieldOfInstance(final Field field, final Object instance) throws IllegalArgumentException, IllegalAccessException {
        try {
            return instance == null ? null : field.get(instance);
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public Integer getInteger(final String key, final Integer dflt) {
        try {
            return Integer.valueOf(props.getProperty(key));
        } catch (Exception e) {
            return dflt;
        }
    }

    public Double getDouble(final String key, final Double dflt) {
        try {
            return Double.valueOf(props.getProperty(key));
        } catch (Exception e) {
            return dflt;
        }
    }
    
    public Float getFloat(final String key, final Float dflt) {
        try {
            return Float.valueOf(props.getProperty(key));
        } catch (Exception e) {
            return dflt;
        }
    }

    public Byte getByte(final String key, final Byte dflt) {
        try {
            return Byte.valueOf(props.getProperty(key));
        } catch (Exception e) {
            return dflt;
        }
    }
    
    public Boolean getBoolean(final String key, final Boolean dflt) {
        String str = props.getProperty(key);
        if(str!=null){
            return Boolean.valueOf(str);
        }else{
            return dflt;
        }
    }

    public String getString(final String key, final String dflt) {
        return props.getProperty(key, dflt);
    }

    public <E extends Enum<E>> E getEnum(final String key, final E dflt, final Class<E> clazz) {
        try {
            return Enum.valueOf(clazz, props.getProperty(key));
        } catch (Exception e) {
            return dflt;
        }
    }
}
