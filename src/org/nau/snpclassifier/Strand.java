/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.snpclassifier;

/**
 *
 * @author jbeckstrom
 */
 enum Strand {
    POSITIVE("+"), NEGATIVE("-");

    private Strand(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
    private String symbol;
    
}
