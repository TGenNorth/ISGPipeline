/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.matrix;

/**
 *
 * @author jbeckstrom
 */
public interface HeaderAttribute {
    public static final String PATTERN_STR = "pat";
    public static final String PAT_NUM_STR = "patNum";
    public static final HeaderAttribute STATUS = new HeaderAttributeImpl("Status");
    public static final HeaderAttribute MISMATCH = new HeaderAttributeImpl("Mismatch");
    public static final HeaderAttribute PAT_NUM = new HeaderAttributeImpl(PAT_NUM_STR);
    
    public String getName();
    
}
