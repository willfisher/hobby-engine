package com.retrochicken.engine.interactable;

import com.retrochicken.engine.physics.Collider;

public class EnemyAI {
	
	private Living enemy;
	private Living target;
	
	private float attackDamage;
	private float attackSpeed;
	private float attElapsedTime;
	
	private boolean moving = false;
	private boolean movingLeft = false;
	
	private float speed;
	
	private boolean dead = false;
	
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public EnemyAI(Living enemy, Living target, float speed, float attackDamage, float attackSpeed) {
		this.enemy = enemy;
		this.target = target;
		this.speed = speed;
		this.attackDamage = attackDamage;
		this.attackSpeed = attackSpeed;
	}
	
	public void update(float timePassed) {
		if(enemy.isDead() || dead)
			return;
		
		attElapsedTime += timePassed;
		
		if(enemy.getX() + enemy.getWidth() < target.getX()) {
			enemy.setX(enemy.getX() + speed * timePassed);
			moving = true;
			movingLeft = false;
		} else if(target.getX() + target.getWidth() < enemy.getX()) {
			enemy.setX(enemy.getX() - speed * timePassed);
			moving = true;
			movingLeft = true;
		} else
			moving = false;
		
		if(Math.min(Math.abs(enemy.getX() + enemy.getWidth() - target.getX()), Math.abs(target.getX() + target.getWidth() - enemy.getX())) < 5
		   && target.getY() + target.getHeight() > enemy.getY() && enemy.getY() + enemy.getHeight() > target.getY()) {
			if(attElapsedTime >= attackSpeed) {
				target.recieveDamage(attackDamage);
				attElapsedTime = 0;
			}
		}
	}
	
	public void collided(Collider collider) {
		
	}

	public boolean isMoving() {
		return moving;
	}

	public boolean isMovingLeft() {
		return movingLeft;
	}
}
