/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isg.matrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jbeckstrom
 */
public class ISGMatrixReader {

    private BufferedReader reader;
    private ISGMatrixHeader header;

    public ISGMatrixReader(File file) throws FileNotFoundException, IOException {
        this.reader = new BufferedReader(new FileReader(file));
        this.header = readHeader();
    }

    private ISGMatrixHeader readHeader() throws IOException {
        String line = nextLine();
        int numSamples = -1;
        while (line.startsWith("#")) {
            if (line.length() > 1) {
                String[] split = line.substring(1).split("=");
                if (split.length == 2) {
                    if (split[0].trim().equals(ISGMatrixHeader.NUM_SAMPLES)) {
                        numSamples = Integer.parseInt(split[1].trim());
                    }
                }
            }
            line = nextLine();
        }
        List<String> sampleNames = new ArrayList<String>();
        List<String> additionalInfo = new ArrayList<String>();
        String[] split = line.split("\t");

        //parse samples
        int i = 3;
        int lastSampleIndex = (numSamples == -1) ? split.length : numSamples+i;
        for (; i < lastSampleIndex; i++) {
            if (split[i].equalsIgnoreCase(ISGMatrixHeader.PATTERN)) {
                break;
            }
            sampleNames.add(split[i]);
        }

        //parse additional info
        for (; i < split.length; i++) {
            additionalInfo.add(split[i]);
        }

        return new ISGMatrixHeader(sampleNames, additionalInfo);
    }

    public String nextLine() {
        String ret = null;
        try {
            ret = reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(ISGMatrixReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public ISGMatrixRecord nextRecord() {
        String line = nextLine();
        if (line != null) {
            String[] split = line.split("\t");
            String chr = split[0];
            int pos = Integer.parseInt(split[1]);
            char ref = split[2].charAt(0);
            List<Character> states = new ArrayList<Character>();
            Map<String, String> additionalInfo = new HashMap<String, String>();

            //parse samples
            int i = 0;
            for (; i < header.getNSamples(); i++) {
                char state = split[3 + i].charAt(0);
                states.add(state);
            }

            //parse addtional info
            for (String key : header.getAdditionalInfo()) {
                if (i >= split.length) {
                    break;
                }
                additionalInfo.put(key, split[i]);
                i++;
            }

            return new ISGMatrixRecord(chr, pos, ref, states, additionalInfo);
        }
        return null;
    }

    public ISGMatrixHeader getHeader() {
        return header;
    }
}
