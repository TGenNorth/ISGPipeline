/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

/**
 * Stores various info for making an ambiguous call.
 * 
 * @author jbeckstrom
 */
public class MarkAmbiguousInfo {

    public final int maxNumAlt;
    public final int minQual;
    public final int minGQ;
    public final int minDP;
    public final double minAF;

    private MarkAmbiguousInfo(int maxNumAlt, int minQual, int minGQ, int minDP, double minAF) {
        this.maxNumAlt = maxNumAlt;
        this.minQual = minQual;
        this.minGQ = minGQ;
        this.minDP = minDP;
        this.minAF = minAF;
    }

    public static final class Builder {

        private int maxNumAlt;
        private int minQual;
        private int minGQ;
        private int minDP;
        private double minAF;

        public Builder maxNumAlt(int maxNumAlt) {
            this.maxNumAlt = maxNumAlt;
            return this;
        }

        public Builder minAF(double minAF) {
            this.minAF = minAF;
            return this;
        }

        public Builder minDP(int minDP) {
            this.minDP = minDP;
            return this;
        }

        public Builder minGQ(int minGQ) {
            this.minGQ = minGQ;
            return this;
        }

        public Builder minQual(int minQual) {
            this.minQual = minQual;
            return this;
        }
        
        public MarkAmbiguousInfo build(){
            return new MarkAmbiguousInfo(maxNumAlt, minQual, minGQ, minDP, minAF);
        }
    }
}
