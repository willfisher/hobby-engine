package com.retrochicken.engine.fx;

import com.retrochicken.engine.Renderer;

public class FadeText {
	
	private String text;
	private float r1, r2, g1, g2, b1, b2;
	private float red, green, blue;
	
	private float x, y;
	
	private float fadePeriod;
	private long periodStart;
	private boolean fadingOut = true;
	
	public FadeText(float x, float y, String text, int color, int fadeColor, float fadePeriod) {
		this.text = text;
		red = r1 = Pixel.getRed(color);
		green = g1 = Pixel.getGreen(color);
		blue = b1 = Pixel.getBlue(color);
		r2 = Pixel.getRed(fadeColor);
		g2 = Pixel.getGreen(fadeColor);
		b2 = Pixel.getBlue(fadeColor);
		this.x = x;
		this.y = y;
		this.fadePeriod = fadePeriod;
		periodStart = System.currentTimeMillis();
	}
	
	public void update() {
		if((System.currentTimeMillis() - periodStart)/1000.0f >= fadePeriod) {
			periodStart = System.currentTimeMillis();//(long)(periodStart + fadePeriod * 1000);
			fadingOut = !fadingOut;
		}
		
		float percent = (System.currentTimeMillis() - periodStart)/(1000 * fadePeriod);
		red = fadingOut ? r2 + (r1 - r2) * percent : r1 + (r2 - r1) * percent;
		green = fadingOut ? g2 + (g1 - g2) * percent : g1 + (g2 - g1) * percent;
		blue = fadingOut ? b2 + (b1 - b2) * percent : b1 + (b2 - b1) * percent;
	}
	
	public void render(Renderer renderer) {
		renderer.drawString(text, Pixel.getColor(1, red, green, blue), (int)(x - renderer.stringWidth(text)/2.0f), (int)(y - renderer.stringHeight(text)/2.0f));
	}
}
