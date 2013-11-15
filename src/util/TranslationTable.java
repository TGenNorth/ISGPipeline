/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 *
 * @author jbeckstrom
 */
public final class TranslationTable {
    private final Map<String, String> map = new HashMap<String, String>();

    public TranslationTable(Properties props) {
        for (Entry<Object, Object> entry : props.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            map.put(key, value);
            map.put(value, key);
        }
    }

    public static TranslationTable load(File propertiesFile) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(propertiesFile));
            return new TranslationTable(props);
        } catch (IOException ex) {
            throw new RuntimeException("An error occurred trying to load file: " + propertiesFile.getAbsolutePath());
        }
    }

    public String translate(String str) {
        if (!map.containsKey(str)) {
            throw new RuntimeException("Could not find '" + str + "' in translation table!");
        }
        return map.get(str);
    }
    
}
