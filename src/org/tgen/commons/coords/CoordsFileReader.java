package org.tgen.commons.coords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Vector;
import org.tgen.commons.feature.Coord;

/*
 *     [S1]     [E1]  |     [S2]     [E2]  |  [LEN 1]  [LEN 2]  |  [% IDY]  | [TAGS]
=====================================================================================
1    34198  |        1    34198  |    34198    34198  |    99.97  | SaUSA300_FPR3757	SaCOL
24178    24249  |  1940188  1940117  |       72       72  |   100.00  | SaUSA300_FPR3757	SaCOL
 */
public class CoordsFileReader {

    private BufferedReader reader;

    public CoordsFileReader(File file) {
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String removeContig(String str) {
        int index = str.indexOf('.');
        if (index != -1) {
            str = str.substring(0, index);
        }
        return str;
    }

    public CoordsRecord next() {

        try {

            String line = null;

            while ((line = reader.readLine()) != null) {

                String[] split = line.trim().split("\\s+");

                if (split.length == 13) {

                    try {

                        int s1 = Integer.parseInt(split[0]);
                        int e1 = Integer.parseInt(split[1]);

                        int s2 = Integer.parseInt(split[3]);
                        int e2 = Integer.parseInt(split[4]);

                        int len1 = Integer.parseInt(split[6]);
                        int len2 = Integer.parseInt(split[7]);

                        double idy = Double.parseDouble(split[9]);

                        String t1 = split[11];
                        String t2 = split[12];

                        /*
                        t1 = removeContig(t1);
                        t2 = removeContig(t2);
                         */

                        boolean reversed = false;
                        if (s2 > e2) {
                            int tmp = s2;
                            s2 = e2;
                            e2 = tmp;
                            reversed = true;
                        }


                        Coord c1 = new Coord(t1, s1, e1, len1, false);
                        Coord c2 = new Coord(t2, s2, e2, len2, reversed);

                        return new CoordsRecord(c1, c2, idy);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
