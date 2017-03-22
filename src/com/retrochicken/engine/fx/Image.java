package com.retrochicken.engine.fx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.retrochicken.engine.Renderer;

public class Image {
	
	public int width, height;
	public ShadowType shadowType = ShadowType.NONE;
	public int[] pixels;
	
	public Image() {
		
	}
	
	public Image(String path) {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(Image.class.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		width = image.getWidth();
		height = image.getHeight();
		//Scans in entire image: May change for sprite tiling
		pixels = image.getRGB(0, 0, width, height, null, 0, width);
		
		image.flush();
	}
	
	public Image(String path, ShadowType shadowType) {
		this(path);
		this.shadowType = shadowType;
	}
	
	public void drawCopies(Renderer renderer, int horizontalCopies, int verticalCopies, int offX, int offY) {
		for(int x = 0; x < horizontalCopies; x++) {
			for(int y = 0; y < verticalCopies; y++) {
				renderer.drawImage(this, offX + x * width, offY + y * height);
			}
		}
	}
}
