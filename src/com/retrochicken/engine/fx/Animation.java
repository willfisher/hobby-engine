package com.retrochicken.engine.fx;

import java.util.Arrays;

import com.retrochicken.engine.Renderer;

public class Animation {
	
	private SpriteSheet sprites;
	private int[] frames;
	private boolean[] flip;
	private float animTime;
	
	private float elapsedTime;
	private int frame = 0;
	
	private boolean isPlaying = false;
	
	public Animation(SpriteSheet sprites, int[] frames, float animTime) {
		this.sprites = sprites;
		this.frames = frames;
		this.flip = new boolean[frames.length];
		this.animTime = animTime;
	}
	
	public Animation(SpriteSheet sprites, int[] frames, float animTime, boolean[] flip) {
		this(sprites, frames, animTime);
		
		if(flip.length == this.flip.length)
			this.flip = flip;
		else {
			for(int i = 0; i < Math.min(flip.length, this.flip.length); i++)
				this.flip[i] = flip[i];
		}
	}
	
	public Animation(SpriteSheet sprites, int[] frames, float animTime, boolean flipAll) {
		this(sprites, frames, animTime);
		
		if(flipAll)
			Arrays.fill(this.flip, flipAll);
	}
	
	public void play() {
		if(isPlaying)
			return;
		
		isPlaying = true;
		elapsedTime = 0;
		frame = 0;
	}
	
	public void stop() {
		isPlaying = false;
	}
	
	public void update(float timePassed) {
		if(!isPlaying)
			return;
		
		elapsedTime += timePassed;
		if(elapsedTime >= animTime / frames.length) {
			elapsedTime = 0;
			frame++;
			if(frame >= frames.length) frame = 0;
		}
	} 
	
	public void render(Renderer renderer, int offX, int offY) {
		if(!isPlaying)
			return;
		
		sprites.drawSprite(renderer, frames[frame], flip[frame], offX, offY);
	}
	
	public void setShadowType(ShadowType shadowType) {
		this.sprites.setShadowType(shadowType);
	}

	public float getAnimTime() {
		return animTime;
	}

	public void setAnimTime(float animTime) {
		this.animTime = animTime;
	}
	
	public int getWidth(int frame) {
		if(frame < 0 || frame >= frames.length)
			return 0;
		
		return sprites.getWidth(frames[frame]);
	}
	
	public int getHeight(int frame) {
		if(frame < 0 || frame >= frames.length)
			return 0;
		
		return sprites.getHeight(frames[frame]);
	}
	
	public int getWidth() {
		return sprites.getWidth(frames[frame]);
	}
	
	public int getHeight() {
		return sprites.getHeight(frames[frame]);
	}
}
