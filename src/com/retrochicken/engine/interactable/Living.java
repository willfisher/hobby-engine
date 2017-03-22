package com.retrochicken.engine.interactable;

import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.ShadowType;
import com.retrochicken.engine.physics.GameObject;
import com.retrochicken.engine.physics.Physics;

public abstract class Living extends GameObject {
	
	private float maxY = Integer.MAX_VALUE;
	private static final int FALL_THRESHOLD = (int)(Physics.GRAVITY / 5.0f);
	
	private int maxHealth = 100;
	private int health = 100;
	
	private boolean isDead = false;
	
	public abstract void dead();
	
	public Living(float x, float y) {
		super(x, y, true);
	}
	
	public Living(float x, float y, boolean isKinematic) {
		super(x, y, isKinematic);
	}
	
	public void update(float timePassed) {
		super.update(timePassed);
		
		if(!super.grounded && super.getY() < maxY)
			maxY = super.getY();
		if(super.grounded && maxY - super.getY() != 0) {
			float fallDist = Math.abs(maxY - super.getY());
			maxY = super.getY();
			if(fallDist >= FALL_THRESHOLD) {
				float meters = fallDist * Physics.PIXELS_TO_METERS;
				recieveDamage(meters * maxHealth / 20.0f);
			}
		}
	}
	
	public void recieveDamage(float damage) {
		health -= (int)damage;
		if(health <= 0) {
			health = 0;
			isDead = true;
			dead();
		} else if(health > maxHealth)
			health = maxHealth;
	}
	
	public void recieveHealth(float health) {
		recieveDamage(-health);
	}
	
	public void drawHealthBar(Renderer renderer, int barWidth, int barHeight, int startX) {
		int redLength = (int)(barWidth * (float)health / maxHealth);
		for(int i = 0; i < barWidth; i++) {
			for(int j = 0; j < barHeight; j++) {
				renderer.setPixel(startX + i, (int)super.getY() - 2 * barHeight + j, (i < redLength ? 0xffff0000 : 0xff808080), ShadowType.UNAFFECTED);
			}
		}
	}
	
	public void drawHealthBar(Renderer renderer, int startX) {
		drawHealthBar(renderer, 30, 2, startX);
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
		if(health <= 0) {
			health = 0;
			isDead = true;
			dead();
		} else if(health > maxHealth)
			health = maxHealth;
	}
	
	public void restoreFullHealth() {
		health = maxHealth;
	}

	public boolean isDead() {
		return isDead;
	}
	
	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}
}
