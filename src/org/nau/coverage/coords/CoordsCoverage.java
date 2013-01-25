/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.coverage.coords;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author jbeckstrom
 */
public class CoordsCoverage {

    public static void main(String[] args) throws IOException {
        File f = new File("test/LVS:Fth257.coords");
        CoordsCoverageRunner runner = new CoordsCoverageRunner(f, new File("test"));
        runner.run();
    }
}
