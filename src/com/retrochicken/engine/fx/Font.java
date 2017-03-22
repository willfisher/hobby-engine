package com.retrochicken.engine.fx;

public enum Font {
	
	STANDARD("/fonts/standard.png");
	
	public final int NUM_UNICODES = 59;
	public int[] offsets = new int[NUM_UNICODES];
	public int[] widths = new int[NUM_UNICODES];
	public Image image;
	
	Font(String path) {
		image = new Image(path);
		
		int unicode = -1;
		
		for(int x = 0; x < image.width; x++) {
			int color = image.pixels[x];
			
			if(color == 0xff0000ff) {
				unicode++;
				offsets[unicode] = x;
			}
			
			if(color == 0xffffff00)
				widths[unicode] = x - offsets[unicode];
		}
	}
	
	public float getHeight(String string) {
		return image.height - 1;
	}
	
	public float getWidth(String string) {
		float result = 0;
		for(int i = 0; i < string.length(); i++)
			result += widths[string.codePointAt(i) - 32];
		return result;
	}
}
