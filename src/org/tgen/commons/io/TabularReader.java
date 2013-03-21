/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.io;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.broad.tribble.CloseableTribbleIterator;
import org.broad.tribble.TribbleIndexedFeatureReader;
import org.tgen.commons.feature.TableCodec;
import org.tgen.commons.feature.TableFeature;

/**
 *
 * @author jbeckstrom
 */
public class TabularReader {

    private CloseableTribbleIterator iter;
    private List<String> header;
//
//    public TabularReader(File file, int chrCol, int startCol, int endCol) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
//        TribbleIndexedFeatureReader<TableFeature> source = new TribbleIndexedFeatureReader<TableFeature>(file.getAbsolutePath(), new TableCodec(chrCol, startCol, endCol), false);
//        header = (List<String>) source.getHeader();
//        iter = source.iterator();
//    }
//    
//    public TabularReader(File file, int chrCol, int startCol, int endCol, String comment) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
//        BasicFeatureSource source = BasicFeatureSource.getFeatureSource(file.getAbsolutePath(), new TableCodec(chrCol, startCol, endCol, comment), false);
//        header = (List<String>) source.getHeader();
//        iter = source.iterator();
//    }

    public List<String> getHeader() {
        return header;
    }

    public TableFeature next() {
        TableFeature ret = null;
        boolean bad = false;
        while (true) {
            try {
                if(bad) iter.next();
                ret = (TableFeature) iter.next();
                break;
            } catch (Exception e) {
                System.out.println("skipping... "+e.getMessage());
                bad = true;
            }
        }
        return ret;
    }
}
