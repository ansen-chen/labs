package lab.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Test;

public class ImageLab {
	
	private static final int IMG_WIDTH = 1080;
	private static final int IMG_HEIGHT = 720;
	
	@Test
	public void metadataTest() throws Exception {
		BufferedImage originalImage = ImageIO.read(new File("e:\\temp\\demo.jpg"));
		metadata(originalImage, "jpg");
		
		
	}
	
	private void metadata(BufferedImage image, String formatName) throws Exception {
		int width = image.getWidth();
		int height = image.getHeight();
		System.out.println("Image width="+width+", height="+height);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, formatName, bos);
		byte[] bytes = bos.toByteArray();
		System.out.println("Image bytes length="+bytes.length);
		
		// 分辨率
		int rgb = image.getRGB(0, 0);
	}
	
	@Test
	public void resizeImage() throws Exception {
		BufferedImage originalImage = ImageIO.read(new File("e:\\temp\\demo.jpg"));
		int type = originalImage.getType()==BufferedImage.TYPE_CUSTOM?BufferedImage.TYPE_INT_ARGB:originalImage.getType();
		
		BufferedImage resizedImage = resizeImage(originalImage, type);
		ImageIO.write(resizedImage, "jpg", new File("e:\\temp\\demo_jpg.jpg"));
		
		resizedImage = resizeImageWithHint(originalImage, type);
		ImageIO.write(resizedImage, "jpg", new File("e:\\temp\\demo_hint_jpg.jpg"));
		
		resizedImage = resizeImage(originalImage, type);
		ImageIO.write(resizedImage, "png", new File("e:\\temp\\demo_png.png"));
		
		resizedImage = resizeImageWithHint(originalImage, type);
		ImageIO.write(resizedImage, "png", new File("e:\\temp\\demo_hint_png.png"));
	}
	
	private BufferedImage resizeImage(BufferedImage originalImage, int type) {
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();
		
		return resizedImage;
	}
	
	private BufferedImage resizeImageWithHint(BufferedImage originalImage, int type) {
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		Graphics2D g = resizedImage.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		
		g.dispose();
		
		return resizedImage;
	}

}
