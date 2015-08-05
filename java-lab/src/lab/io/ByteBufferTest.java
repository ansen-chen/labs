/**
 * 
 */
package lab.io;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ansen
 *
 */
public class ByteBufferTest {
	
	@Test
	public void test() throws Exception {
		byte[] data = new byte[32];
		for(int i=0; i<data.length; i++) {
			data[i] = (byte)i;
		}
		
		ByteBuffer buf = ByteBuffer.wrap(data);
		byte[] tmp = new byte[4];
		buf.get(tmp);
		Assert.assertEquals(3, buf.position());
		
		buf.mark();
	}
	
	public static void main(String[] args) {
		System.out.println("Hello World!");
	}

}
