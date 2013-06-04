/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.broadinstitute.sting.commandline.Argument;
import java.util.Properties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class TypedPropertiesTest {
    
    @Argument
    public Integer testInteger;
    public Integer testIntegerWithDefault = 100;
    public TestEnum testEnum;
    
    public TypedPropertiesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Test
    public void testApplyToOptionalArgumentFieldsOfObjectUsingPrefix() {
        try {
            System.out.println("applyToOptionalArgumentFieldsOfObjectUsingPrefix");
            Properties props = new Properties();
            props.put("prefix.anOptionalArgument","10");
            props.put("prefix.aRequiredArgument","10");
            TypedProperties instance = new TypedProperties(props);
            ArgumentAnnotatedTestClass obj = new ArgumentAnnotatedTestClass();
            Integer expectedOptionalArgumentWithDefault = obj.anOptionalArgumentWithDefault;
            instance.applyToArgumentAnnotatedFieldsOfObjectUsingPrefix(obj, "prefix.");
            assertEquals(new Integer(10), obj.anOptionalArgument);
            assertEquals(expectedOptionalArgumentWithDefault, obj.anOptionalArgumentWithDefault);
            assertEquals(new Integer(10), obj.aRequiredArgument);
            assertTrue(obj.aBooleanWithDefault);
        } catch (Exception ex) {
            fail(ex.toString());
        }
    }
    
    @Test
    public void testGetValueForFieldUsingPrefix() {
        try {
            System.out.println("getValueForFieldUsingPrefix");
            
            Properties props = new Properties();
            props.put("prefix.testInteger","10");
            props.put("testEnum",TestEnum.Test2.name());
            TypedProperties instance = new TypedProperties(props);
            
            Field field = this.getClass().getField("testInteger");
            Integer expResult = 10;
            Object result = instance.getValueForFieldUsingPrefix(field, "prefix.");
            assertEquals(expResult, result);
            
            field = this.getClass().getField("testEnum");
            result = instance.getValueForFieldUsingPrefix(field, "");
            assertEquals(TestEnum.Test2, result);
        } catch (Exception ex) {
            fail(ex.toString());
        }
    }
    
    @Test
    public void getValueForFieldOfInstanceUsingPrefix() {
        try {
            System.out.println("getValueForFieldOfInstanceUsingPrefix");
            
            Properties props = new Properties();
            props.put("testEnum",TestEnum.Test2.name());
            TypedProperties instance = new TypedProperties(props);
            
            Field field = this.getClass().getField("testIntegerWithDefault");
            Integer expResult = 100;
            Object result = instance.getValueForFieldOfInstanceUsingPrefix(field, this, "prefix.");
            assertEquals(expResult, result);
            
//            field = this.getClass().getField("testEnum");
//            result = instance.getValueForFieldUsingPrefix(field, "");
//            assertEquals(TestEnum.Test2, result);
        } catch (Exception ex) {
            fail(ex.toString());
        }
    }

    /**
     * Test of getInteger method, of class TypedProperties.
     */
    @Test
    public void testGetInteger() {
        System.out.println("getInteger");
        String key = "testInteger";
        Properties props = new Properties();
        props.put(key, "1");
        Integer dflt = 2;
        TypedProperties instance = new TypedProperties(props);
        Integer expResult = 1;
        Integer result = instance.getInteger(key, dflt);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetIntegerDefault() {
        System.out.println("getIntegerDefault");
        String key = "testInteger";
        Properties props = new Properties();
        props.put(key, "1");
        Integer dflt = 2;
        TypedProperties instance = new TypedProperties(props);
        Integer expResult = dflt;
        Integer result = instance.getInteger(key+"_", dflt);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetIntegerDefaultInvalidProperty() {
        System.out.println("getIntegerDefaultInvalidProperty");
        String key = "testInteger";
        Properties props = new Properties();
        props.put(key, "1.0");
        Integer dflt = 2;
        TypedProperties instance = new TypedProperties(props);
        Integer expResult = dflt;
        Integer result = instance.getInteger(key, dflt);
        assertEquals(expResult, result);
    }

    /**
     * Test of getDouble method, of class TypedProperties.
     */
    @Test
    public void testGetDouble() {
        System.out.println("getDouble");
        String key = "testDouble";
        Properties props = new Properties();
        props.put(key, "0.1");
        Double dflt = 0.0;
        TypedProperties instance = new TypedProperties(props);
        Double expResult = 0.1;
        Double result = instance.getDouble(key, dflt);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetDoubleDefault() {
        System.out.println("getDoubleDefault");
        String key = "testDouble";
        Properties props = new Properties();
        props.put(key, "0.1");
        Double dflt = 0.0;
        TypedProperties instance = new TypedProperties(props);
        Double expResult = dflt;
        Double result = instance.getDouble(key+"_", dflt);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetDoubleDefaultInvalidProperty() {
        System.out.println("getDoubleDefaultInvalidProperty");
        String key = "testDouble";
        Properties props = new Properties();
        props.put(key, "asdf");
        Double dflt = 0.0;
        TypedProperties instance = new TypedProperties(props);
        Double expResult = dflt;
        Double result = instance.getDouble(key, dflt);
        assertEquals(expResult, result);
    }

    /**
     * Test of getByte method, of class TypedProperties.
     */
    @Test
    public void testGetByte() {
        System.out.println("getByte");
        String key = "testByte";
        Properties props = new Properties();
        props.put(key, "1");
        Byte dflt = 0;
        TypedProperties instance = new TypedProperties(props);
        Byte expResult = 1;
        Byte result = instance.getByte(key, dflt);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetByteDefault() {
        System.out.println("getByteDefault");
        String key = "testByte";
        Properties props = new Properties();
        props.put(key, "1");
        Byte dflt = 0;
        TypedProperties instance = new TypedProperties(props);
        Byte expResult = dflt;
        Byte result = instance.getByte(key+"_", dflt);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetByteDefaultInvalidProperty() {
        System.out.println("getByteDefaultInvalidProperty");
        String key = "testByte";
        Properties props = new Properties();
        props.put(key, "asdf");
        Byte dflt = 0;
        TypedProperties instance = new TypedProperties(props);
        Byte expResult = 0;
        Byte result = instance.getByte(key, dflt);
        assertEquals(expResult, result);
    }

    /**
     * Test of getString method, of class TypedProperties.
     */
    @Test
    public void testGetString() {
        System.out.println("getString");
        String key = "testString";
        Properties props = new Properties();
        props.put(key, "asdf");
        String dflt = "";
        TypedProperties instance = new TypedProperties(props);
        String expResult = "asdf";
        String result = instance.getString(key, dflt);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetStringDefault() {
        System.out.println("getStringDefault");
        String key = "testString";
        Properties props = new Properties();
        props.put(key, "asdf");
        String dflt = "";
        TypedProperties instance = new TypedProperties(props);
        String expResult = "";
        String result = instance.getString(key+"_", dflt);
        assertEquals(expResult, result);
    }

    /**
     * Test of getEnum method, of class TypedProperties.
     */
    @Test
    public void testGetEnum() {
        System.out.println("getEnum");
        String key = "testEnum";
        Properties props = new Properties();
        props.put(key, TestEnum.Test2.name());
        TestEnum dflt = TestEnum.Test1;
        TypedProperties instance = new TypedProperties(props);
        Enum expResult = TestEnum.Test2;
        Enum result = instance.getEnum(key, dflt, TestEnum.class);
        assertEquals(expResult, result);
        
        
    }
    
    @Test
    public void testGetEnumDefault() {
        System.out.println("getEnumDefault");
        String key = "testEnum";
        TestEnum dflt = TestEnum.Test1;
        TypedProperties instance = new TypedProperties(new Properties());
        Enum expResult = TestEnum.Test1;
        Enum result = instance.getEnum(key, dflt, TestEnum.class);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetEnumDefaultInvalidProperty() {
        System.out.println("getEnumDefaultInvalidProperty");
        String key = "testEnum";
        Properties props = new Properties();
        props.put(key, "asdf");
        TestEnum dflt = TestEnum.Test1;
        TypedProperties instance = new TypedProperties(props);
        Enum expResult = dflt;
        Enum result = instance.getEnum(key, dflt, TestEnum.class);
        assertEquals(expResult, result);
    }
    
    enum TestEnum{
        Test1,
        Test2;
    }
    
    private final class ArgumentAnnotatedTestClass{
        @Argument(required=false)
        public Integer anOptionalArgument;
        @Argument(required=false)
        public Integer anOptionalArgumentWithDefault = 100;
        @Argument(required=false)
        public Boolean aBooleanWithDefault = true;
        @Argument(required=true)
        public Integer aRequiredArgument;
    }
}
