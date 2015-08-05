package utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.junit.Test;

import static org.junit.Assert.*;

public class ByteUtils {
	
	/**
     * Converts a hex string representation to a byte array.
     * 
     * @param hex the string holding the hex values
     * @return the resulting byte array
     */
    public static byte[] asByteArray(String hex) {
    	byte[] bts = new byte[hex.length() / 2];
    	for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }

        return bts;
    }
    
    /**
     * Write a 32 bit int as LITTLE_ENDIAN.
     * 
     * @param v the int to write
     */
    public final static byte[] writeInt(int v) {
        return writeInt(v, new byte[4], 0);
    }

    /**
     * Write a 32 bit int as LITTLE_ENDIAN to
     * the given array <code>b</code> at offset <code>offset</code>.
     * 
     * @param v the int to write
     * @param b the byte array to write to
     * @param offset the offset at which to start writing in the array
     */
    public final static byte[] writeInt(int v, byte[] b, int offset) {
        b[offset] = (byte) v;
        b[offset + 1] = (byte) (v >> 8);
        b[offset + 2] = (byte) (v >> 16);
        b[offset + 3] = (byte) (v >> 24);

        return b;
    }
    
    public static String toBitString(byte[] bytes, String separator) {
    	StringBuffer buf = new StringBuffer();
    	for(int i=0; i<bytes.length; i++) {
    		buf.append(toBitString(bytes[i]));
    		
    		if(i<bytes.length-1) {
    			buf.append(separator);
    		}
    	}
    	
    	return buf.toString();
    }
    
    public static String toBitString(byte b) {
    	StringBuilder buf = new StringBuilder();

    	for(int i=0; i<8; i++) {
    		byte flag = (byte)(b & 0x80);
    		buf.append(flag==0?"0":"1");
  
    		b <<= 1;
    	}
    	
    	return buf.toString();
    }
    
    /**
     * Returns a hexadecimal representation of the given byte array.
     * 
     * @param bytes the array to output to an hex string
     * @param separator the separator to use between each byte in the output
     * string. If null no char is inserted between each byte value. 
     * @return the hex representation as a string
     */
    public static String asHex(byte[] bytes, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String code = Integer.toHexString(bytes[i] & 0xFF);
            if ((bytes[i] & 0xFF) < 16) {
                sb.append('0');
            }

            sb.append(code);

            if (separator != null && i < bytes.length - 1) {
                sb.append(separator);
            }
        }

        return sb.toString();
    }
    
    @Test
    public void test() throws Exception {
    	String bitString = ByteUtils.toBitString((byte)0);
    	assertEquals("00000000", bitString);
    	
    	bitString = ByteUtils.toBitString((byte)1);
    	assertEquals("00000001", bitString);
    	
    	bitString = ByteUtils.toBitString((byte)0xFF); // 255
    	assertEquals("11111111", bitString);
    	
    	bitString = ByteUtils.toBitString((byte)0x80);
    	assertEquals("10000000", bitString);
    	
    	byte[] bytes = ByteUtils.writeInt(0x3456); // little-endian
    	bitString = ByteUtils.toBitString(bytes, ", ");
    	assertEquals("01010110, 00110100, 00000000, 00000000", bitString);
    	
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	DataOutputStream out = new DataOutputStream(bos);
    	out.writeInt(0x3456);  // big-endian
    	out.flush();
    	
    	bytes = bos.toByteArray();
    	bitString = ByteUtils.toBitString(bytes, ", ");
    	assertEquals("00000000, 00000000, 00110100, 01010110", bitString);
    }

}
