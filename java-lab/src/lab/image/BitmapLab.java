package lab.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.Test;

import utils.ByteUtils;

public class BitmapLab {
	
	@Test
	public void hack() throws Exception {
		InputStream in = new FileInputStream(new File("D:\\Media\\Images\\sample2_bmp_24.bmp"));
		byte[] buf = new byte[128];
		in.read(buf);
		System.out.println(ByteUtils.asHex(buf, ","));
		
		
		BufferedImage image = ImageIO.read(new File("D:\\Media\\Images\\sample_bmp_24.bmp"));
		
		int width = image.getWidth();
		int height = image.getHeight();
		System.out.println("Image width="+width+", height="+height);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, "bmp", bos);
		
		byte[] bytes = bos.toByteArray();
		bytes[2484] = 0x00;
		bytes[2485] = 0x00;
		bytes[2486] = 0x00;
		
		FileOutputStream fos = new FileOutputStream(new File("D:\\Media\\Images\\test.bmp"));
		fos.write(bytes);
		fos.flush();
		fos.close();
	}

}
