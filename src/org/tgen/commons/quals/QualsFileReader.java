/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tgen.commons.quals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jbeckstrom
 */
public class QualsFileReader {

    private File file;
    private BufferedReader reader;
    private String line;

    public QualsFileReader(File file) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(file));
        this.file = file;
        advanceLine();
    }


    private void advanceLine() {
        try {
            line = reader.readLine();
//            System.out.println(line);
        } catch (IOException ex) {
            line = null;
        }
    }

    public QualsRecord nextSequence() {

        //read header line
        String header = readSequenceHeader();
        if (header == null) {
            return null;
        }

        //read quals
        advanceLine();
        Integer[] quals = readQuals();

        return new QualsRecord(header, quals);
    }

    private String readSequenceHeader() {
        if (line == null) {
            return null;
        }
        if (!line.startsWith(">")) {
            throw new IllegalStateException("Format exception reading delta " + file + ".  Expected > at beginning of line: " + line);
        }
        String str = line.substring(1);
        return str;
    }

    private Integer[] readQuals(){
        List<Integer> quals = new ArrayList<Integer>();
        do{
            String[] split = line.split("\\s");
            for(String str: split){
                quals.add( Integer.parseInt(str) );
            }
            advanceLine();
        }while (line!=null && !line.startsWith(">"));

        Integer[] ret = new Integer[quals.size()];
        return quals.toArray(ret);
    }

    public static void main(String[] args) throws FileNotFoundException{
        File f = new File("test/G-1412.contigs.quals");
        QualsFileReader reader = new QualsFileReader(f);
        QualsRecord rec = null;
        while( (rec = reader.nextSequence()) !=null ){
            System.out.println(rec.getQuals().length);
        }

    }

}
