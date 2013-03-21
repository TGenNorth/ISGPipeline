/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.samtools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jbeckstrom
 */
public class SamTools {

    private File samtools;

    private String version;

    public static final String VERSION_PATTERN_STR = "Version\\:\\s+([0-9]+\\.[0-9]+\\.[0-9a-z]+)\\s+.*";
    public static final Pattern VERSION_PATTERN = Pattern.compile(VERSION_PATTERN_STR);

    public SamTools(File samtools) throws IOException {
        this.samtools = samtools;
        init();
    }

    private void init() throws IOException {
        ProcessBuilder builder = new ProcessBuilder(samtools.getAbsolutePath());
        builder.redirectErrorStream(true);
        Process process = builder.start();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;

        int count = 0;
        while ((line = br.readLine()) != null) {
            count++;
            if (count == 3) {

                Matcher matcher = VERSION_PATTERN.matcher(line);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    version = matcher.group(1);
                }else{
                    throw new IllegalStateException("Could not find version on line: "+count);
                }

            }
        }
    }

    public String getPath(){
        return samtools.getAbsolutePath();
    }

    public String getVersion(){
        return version;
    }

    public static void main(String[] args) throws IOException {
        File f = new File("/Users/jbeckstrom/Documents/tgen/samtools/samtools-0.1.12a/samtools");
        SamTools st = new SamTools(f);
        System.out.println(st.getVersion());
    }
}
