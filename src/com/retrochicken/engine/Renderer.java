package com.retrochicken.engine;

import java.awt.Rectangle;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import com.retrochicken.engine.fx.Font;
import com.retrochicken.engine.fx.Image;
import com.retrochicken.engine.fx.ImageTile;
import com.retrochicken.engine.fx.Light;
import com.retrochicken.engine.fx.LightRequest;
import com.retrochicken.engine.fx.Pixel;
import com.retrochicken.engine.fx.Settings;
import com.retrochicken.engine.fx.ShadowType;

public class Renderer {
	
	private GameContainer gc;
	
	private int width, height;
	private int[] pixels;
	private int[] lm;
	private ShadowType[] lb;
	private Font font = Font.STANDARD;
	
	private int transX, transY;
	
	private ArrayList<LightRequest> lightRequests = new ArrayList<LightRequest>();
	
	public Renderer(GameContainer gc) {
		this.gc = gc;
		width = gc.getWidth();
		height = gc.getHeight();
		pixels = ((DataBufferInt)gc.getWindow().getImage().getRaster().getDataBuffer()).getData();
		lm = new int[pixels.length];
		lb = new ShadowType[pixels.length];
	}
	
	//RGB ranges from 0 to 1 not 0 to 255
	public void setPixel(int x, int y, int color, ShadowType lightBlock) {
		x += transX;
		y += transY;
		
		if((x < 0 || x >= width || y < 0 || y >= height) || (byte)((color & 0xff000000) >> 24) == 0)
			return;
		
		pixels[x + y * width] = color;
		lb[x + y * width] = lightBlock;
	}
	
	public void setLightMap(int x, int y, int color) {
		x += transX;
		y += transY;
		
		if(x < 0 || x >= width || y < 0 || y >= height)
			return;
		
		lm[x + y * width] = Pixel.getMax(color, lm[x + y * width]);
	}
	
	public void drawString(String text, int color, int offX, int offY) {
		drawString(text, this.font, color, offX, offY);
	}
	
	public void drawString(String text, int color, int offX, int offY, Rectangle bounds) {
		drawString(text, this.font, color, offX, offY, bounds);
	}
	
	public float stringHeight(String string) {
		return this.font.getHeight(string);
	}
	
	public float stringWidth(String string) {
		return this.font.getWidth(string);
	}
	
	public ShadowType getLightBlock(int x, int y) {
		x += transX;
		y += transY;
		
		if(x < 0 || x >= width || y < 0 || y >= height)
			return ShadowType.TOTAL;
		return lb[x + y * width];
	}
	
	public void drawString(String text, Font font, int color, int offX, int offY, Rectangle bounds) {
		if(bounds == null)
			drawString(text, font, color, offX, offY);
		else {
			text = text.toUpperCase();
			
			int offset = 0;
			
			for(int i = 0; i < text.length(); i++) {
				int unicode = text.codePointAt(i) - 32;
				
				for(int x = 0; x < font.widths[unicode]; x++) {
					if(x + offX + offset < bounds.x || offX + x + offset + 3 > bounds.x + bounds.width)
						continue;
					for(int y = 1; y < font.image.height; y++) {
						if(y + offY < bounds.y || offY + y > bounds.y + bounds.height)
							continue;
						if((byte)((font.image.pixels[(x + font.offsets[unicode]) + y * font.image.width] & 0xff000000) >> 24) != 0)
							setPixel(x + offX + offset, y + offY - 1, color, ShadowType.UNAFFECTED);
					}
				}
				
				offset += font.widths[unicode];
			}
		}
	}
	
	public void drawString(String text, Font font, int color, int offX, int offY) {
		text = text.toUpperCase();
		
		int offset = 0;
		
		for(int i = 0; i < text.length(); i++) {
			int unicode = text.codePointAt(i) - 32;
			
			for(int x = 0; x < font.widths[unicode]; x++) {
				for(int y = 1; y < font.image.height; y++) {
					if((byte)((font.image.pixels[(x + font.offsets[unicode]) + y * font.image.width] & 0xff000000) >> 24) != 0)
						setPixel(x + offX + offset, y + offY - 1, color, ShadowType.UNAFFECTED);
				}
			}
			
			offset += font.widths[unicode];
		}
	}
	
	public void flushMaps() {
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				setPixel(x, y, Pixel.getLightBlend(pixels[x + y * width], lm[x + y * width], Settings.AMBIENT_COLOR, lb[x + y * width]), lb[x + y * width]);
				lm[x + y * width] = Settings.AMBIENT_COLOR;
				lb[x + y * width] = ShadowType.NONE;
			}
		}
	}
	
	public void clear() {
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(gc.isClearScreen())
					pixels[x + y * width] = gc.isDynamicLights() || gc.isLightingEnabled() ? 0xffffffff : Settings.AMBIENT_COLOR;
			}
		}
	}
	
	public void drawLightArray() {
		for(LightRequest lr : lightRequests) {
			drawLightRequest(lr.light, lr.x, lr.y);
		}
		
		lightRequests.clear();
	}
	
	public void drawImage(Image image, int offX, int offY) {
		for(int x = 0; x < image.width; x++) {
			for(int y = 0; y < image.height; y++) {
				setPixel(x + offX, y + offY, image.pixels[x + y * image.width], image.shadowType);
			}
		}
	}
	
	public void drawImage(Image image, int offX, int offY, Rectangle bounds) {
		if(bounds == null)
			drawImage(image, offX, offY);
		else {
			for(int x = 0; x < image.width; x++) {
				if(x + offX < bounds.x || offX > bounds.x + bounds.width)
					continue;
				for(int y = 0; y < image.height; y++) {
					if(y + offY < bounds.y || offY + y > bounds.y + bounds.height)
						continue;
					setPixel(x + offX, y + offY, image.pixels[x + y * image.width], image.shadowType);
				}
			}
		}
	}
	
	public void drawImage(Image image, int offX, int offY, boolean flipped) {
		if(!flipped) {
			drawImage(image, offX, offY);
		} else {
			for(int x = image.width - 1; x >= 0; x--) {
				for(int y = 0; y < image.height; y++) {
					setPixel(image.width - 1 - x + offX, y + offY, image.pixels[x + y * image.width], image.shadowType);
				}
			}
		}
	}
	
	public void drawImageTile(ImageTile image, int offX, int offY, int tileX, int tileY) {
		for(int x = 0; x < image.width; x++) {
			for(int y = 0; y < image.height; y++) {
				setPixel(x + offX, y + offY, image.pixels[(x + (tileX * image.tileWidth)) + (y + (tileY * image.tileHeight)) * image.width], image.shadowType);
			}
		}
	}
	
	public void drawLight(Light light, int offX, int offY) {
		lightRequests.add(new LightRequest(light, offX, offY));
	}
	
	private void drawLightRequest(Light light, int offX, int offY) {
		if(!gc.isLightingEnabled() && !gc.isDynamicLights())
			return;
		if(gc.isDynamicLights()) {
			for(int i = 0; i <= light.diameter; i++) {
				drawLightLine(light.radius, light.radius, i, 0, light, offX, offY);
				drawLightLine(light.radius, light.radius, i, light.diameter, light, offX, offY);
				drawLightLine(light.radius, light.radius, 0, i, light, offX, offY);
				drawLightLine(light.radius, light.radius, light.diameter, i, light, offX, offY);
			}
		} else {
			for(int x = 0; x < light.diameter; x++) {
				for(int y = 0; y < light.diameter; y++) {
					setLightMap(x + offX - light.radius, y + offY - light.radius, light.getLightValue(x, y));
				}
			}
		}
	}
	
	private void drawLightLine(int x0, int y0, int x1, int y1, Light light, int offX, int offY) {
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);
		
		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;
		
		int err = dx - dy;
		int e2;
		
		float power = 1.0f;
		boolean hit = false;
		
		while(true) {
			if(light.getLightValue(x0, y0) == 0xff000000) break;
			
			int screenX = x0 - light.radius + offX;
			int screenY = y0 - light.radius + offY;
			
			if(power == 1)
				setLightMap(screenX, screenY, light.getLightValue(x0, y0));
			else
				setLightMap(screenX, screenY, Pixel.getColorPower(light.getLightValue(x0, y0), power));
			
			if(x0 == x1 && y0 == y1) break;
			if(getLightBlock(screenX, screenY) == ShadowType.TOTAL) break;
			if(getLightBlock(screenX, screenY) == ShadowType.FADE) power -= 0.05f;
			if(getLightBlock(screenX, screenY) == ShadowType.HALF && hit == false) {power /= 2.0f; hit = true;}
			if(getLightBlock(screenX, screenY) == ShadowType.NONE && hit == true) hit = false;
			if(power <= 0.1f) break;
			
			e2 = 2 * err;
			
			if(e2 > -1 * dy) {
				err -= dy;
				x0 += sx;
			}
			
			if(e2 < dx) {
				err += dx;
				y0 += sy;
			}
		}
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public int getTransX() {
		return transX;
	}

	public void setTransX(int transX) {
		this.transX = transX;
	}

	public int getTransY() {
		return transY;
	}

	public void setTransY(int transY) {
		this.transY = transY;
	}
	
	public void drawRect(int x, int y, int width, int height, int color) {
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++)
				setPixel(x + i, y + j, color, ShadowType.UNAFFECTED);
		}
	}
	
	public void drawRect(int x, int y, int width, int height, int color, Rectangle bounds) {
		for(int i = 0; i < width; i++) {
			if(i + x < bounds.x || x + i > bounds.x + bounds.width)
				continue;
			for(int j = 0; j < height; j++) {
				if(j + y < bounds.y || y + j > bounds.y + bounds.height)
					continue;
				setPixel(x + i, y + j, color, ShadowType.UNAFFECTED);
			}
		}
	}
	
	public void drawEmptyRect(int x, int y, int width, int height, int color) {
		for(int i = 0; i < width; i++) {
			setPixel(x + i, y, color, ShadowType.UNAFFECTED);
			setPixel(x + i, y + height - 1, color, ShadowType.UNAFFECTED);
		}
		for(int i = 0; i < height; i++) {
			setPixel(x, y + i, color, ShadowType.UNAFFECTED);
			setPixel(x + width - 1, y + i, color, ShadowType.UNAFFECTED);
		}
	}
	
	public void drawEmptyRect(int x, int y, int width, int height, int color, Rectangle bounds) {
		if(bounds == null) {
			drawEmptyRect(x, y, width, height, color);
			return;
		}
		for(int i = 0; i < width; i++) {
			if(i + x < bounds.x || x + i > bounds.x + bounds.width)
				continue;
			if(y >= bounds.y && y <= bounds.y + bounds.height)
				setPixel(x + i, y, color, ShadowType.UNAFFECTED);
			if(y + height - 1 >= bounds.y && y + height - 1 <= bounds.y + bounds.height)
				setPixel(x + i, y + height - 1, color, ShadowType.UNAFFECTED);
		}
		for(int i = 0; i < height; i++) {
			if(y + i < bounds.y || y + i > bounds.y + bounds.height)
				continue;
			if(x >= bounds.x && x <= bounds.x + bounds.width)
				setPixel(x, y + i, color, ShadowType.UNAFFECTED);
			if(x + width - 1 >= bounds.x && x + width - 1 <= bounds.x + bounds.width)
				setPixel(x + width - 1, y + i, color, ShadowType.UNAFFECTED);
		}
	}
	
	public void drawTransparentRect(int color, float alpha, int x, int y, int width, int height) {
		//color = Pixel.getColorPower(color, alpha);
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				if(x + i + (y + j) * this.width >= pixels.length || x + i + (y + j) * this.width < 0)
					continue;
				setPixel(x + i, y + j, Pixel.getColorBlend(color, pixels[x + i + (y + j) * this.width]), ShadowType.UNAFFECTED);
			}
		}
	}
	
	public void drawImage(Image image) {drawImage(image, 0, 0);}
	public void drawImageTile(ImageTile image, int tileX, int tileY) {drawImageTile(image, 0, 0, tileX, tileY);}
	public void drawLight(Light light) {drawLight(light, 0, 0);}
}
