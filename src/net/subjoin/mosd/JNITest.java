package net.subjoin.mosd;

import static org.junit.Assert.*;

import org.junit.Test;

public class JNITest {
    
    static { System.loadLibrary("mosd"); }

	
    public static native String hello();
    
    public @Test void testJNI() {
	assertEquals("Hello from JNI!\n", hello());
    }
    
    public static void main(String[] args) {
	System.out.println(hello());
    }
}
