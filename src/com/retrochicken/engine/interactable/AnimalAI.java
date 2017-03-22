package com.retrochicken.engine.interactable;

import java.awt.Rectangle;
import java.util.concurrent.ThreadLocalRandom;

import com.retrochicken.engine.physics.Collidable;
import com.retrochicken.engine.physics.Collider;
import com.retrochicken.engine.physics.CollisionManager;

public class AnimalAI implements Collidable {
	
	private Collider trigger;
	private Animal animal;
	private float xPadding, yPadding;
	
	private int roamRadius;
	private boolean canRoam;
	private int targetX;
	private int origX;
	private float waitTime;
	private long waitStart;
	private boolean waiting = false;
	private boolean walkingLeft = false;
	public boolean isWalkingLeft() {
		return walkingLeft;
	}
	
	public AnimalAI(Animal animal, Collider trigger, CollisionManager manager) {
		this.trigger = trigger;
		this.animal = animal;
		this.xPadding = trigger.getX() - animal.getX();
		this.yPadding = trigger.getY() - animal.getY();
		this.origX = (int)animal.getX();
		this.targetX = origX;
		manager.addCollidable(this);
	}
	
	public void update(float deltaTime) {
		trigger.setPos(animal.getX() + xPadding, animal.getY() + yPadding);
		if(canRoam) {
			if(!waiting) {
				if((int)animal.getX() == targetX) {
					waiting = true;
					waitStart = System.currentTimeMillis();
					waitTime = (float)(Math.random() * 2);
					targetX = origX + ThreadLocalRandom.current().nextInt(-roamRadius, roamRadius + 1);
				} else {
					walkingLeft = animal.getX() > targetX;
					animal.setX(animal.getX() + (walkingLeft ? -1 : 1) * 20 * deltaTime);
					if(walkingLeft && animal.getX() < targetX)
						animal.setX(targetX);
					else if(!walkingLeft && animal.getX() > targetX)
						animal.setX(targetX);
				}
			} else {
				if((System.currentTimeMillis() - waitStart)/1000f >= waitTime)
					waiting = false;
			}
		}
	}

	@Override
	public void onCollide(Collider collider, Rectangle collision, Collidable collidable) {
		if(collidable instanceof Living)
			animal.livingTrigger(collider);
	}

	@Override
	public Collider getCollider() {
		return trigger;
	}
	
	public void canRoam(boolean canRoam, int roamRadius) {
		this.canRoam = canRoam;
		this.roamRadius = roamRadius;
	}
}
