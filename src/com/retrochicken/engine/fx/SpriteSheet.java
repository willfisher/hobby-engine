package com.retrochicken.engine.fx;

import java.util.ArrayList;
import java.util.List;

import com.retrochicken.engine.Renderer;

public class SpriteSheet {
	
	private Image image;
	private int[] offsets;
	private int[] widths;
	
	public SpriteSheet(String path) {
		image = new Image(path);
		
		List<Integer> offsetList = new ArrayList<>();
		List<Integer> widthList = new ArrayList<>();
		
		for(int x = 0; x < image.width; x++) {
			int color = image.pixels[x];
			
			if(color == 0xff0000ff) {
				offsetList.add(x);
			}
			
			if(color == 0xffffff00)
				widthList.add(x - offsetList.get(offsetList.size() - 1));
		}
		
		offsets = new int[offsetList.size()];
		widths = new int[widthList.size()];
		
		for(int i = 0; i < offsetList.size(); i++) {
			offsets[i] = offsetList.get(i);
			widths[i] = widthList.get(i);
		}
	}
	
	public void drawSprite(Renderer renderer, int spriteId, boolean flipped, int offX, int offY) {
		if(spriteId < 0 || spriteId >= offsets.length || spriteId >= widths.length)
			return;
		
		if(!flipped) {
			for(int x = offsets[spriteId]; x <= offsets[spriteId] + widths[spriteId]; x++) {
				for(int y = 1; y < image.height; y++) {
					renderer.setPixel(x - offsets[spriteId] + offX, y - 1 + offY, image.pixels[x + y * image.width], image.shadowType);
				}
			}
		} else {
			for(int x = offsets[spriteId] + widths[spriteId]; x >= offsets[spriteId]; x--) {
				for(int y = 1; y < image.height; y++) {
					renderer.setPixel(offsets[spriteId] + widths[spriteId] - x + offX, y - 1 + offY, image.pixels[x + y * image.width], image.shadowType);
				}
			}
		}
	}
	
	public void setShadowType(ShadowType shadowType) {
		this.image.shadowType = shadowType;
	}
	
	public int getWidth(int frame) {
		if(frame < 0 || frame >= widths.length)
			return 0;
		
		return widths[frame];
	}
	
	public int getHeight(int frame) {		
		return image.height - 1;
	}
}
