/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mummer.coords;

import com.google.common.collect.AbstractIterator;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * A wrapper closeable iterator for CoordsFileReader
 * 
 * @author jbeckstrom
 */
public class CoordsRecordIterator extends AbstractIterator<CoordsRecord> implements Closeable{

    private final CoordsFileReader reader;
    
    public CoordsRecordIterator(final File f){
        this(new CoordsFileReader(f));
    }
    
    public CoordsRecordIterator(final CoordsFileReader reader){
        this.reader = reader;
    }
    
    @Override
    protected CoordsRecord computeNext() {
        final CoordsRecord ret = reader.next();
        return (ret!=null) ? ret : endOfData();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
    
}
