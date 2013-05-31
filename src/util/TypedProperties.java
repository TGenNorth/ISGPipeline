/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Properties;

/**
 * A wrapper for a Properties object that allows access to properties by other 
 * data-types than just String.
 * 
 * @author jbeckstrom
 */
public class TypedProperties {

    private final Properties props;

    public TypedProperties(Properties props) {
        this.props = props;
    }

    public Integer getInteger(final String key, final Integer dflt) {
        try {
            return Integer.valueOf(props.getProperty(key, dflt.toString()));
        } catch (Exception e) {
            return dflt;
        }
    }

    public Double getDouble(final String key, final Double dflt) {
        try {
            return Double.valueOf(props.getProperty(key, dflt.toString()));
        } catch (Exception e) {
            return dflt;
        }
    }

    public Byte getByte(final String key, final Byte dflt) {
        try {
            return Byte.valueOf(props.getProperty(key, dflt.toString()));
        } catch (Exception e) {
            return dflt;
        }
    }

    public String getString(final String key, final String dflt) {
        return props.getProperty(key, dflt);
    }

    public <E extends Enum<E>> E getEnum(final String key, final E dflt) {
        try {
            return Enum.valueOf(dflt.getDeclaringClass(), props.getProperty(key, dflt.name()));
        } catch (Exception e) {
            return dflt;
        }
    }
}
