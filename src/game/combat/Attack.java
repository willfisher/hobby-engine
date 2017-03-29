package game.combat;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.retrochicken.engine.Input;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Animation;
import com.retrochicken.engine.interactable.Consumable;
import com.retrochicken.engine.physics.CollisionManager;
import com.retrochicken.engine.physics.GameObject;

import game.interactable.Bullet;

public class Attack {
	public static enum DefaultTypes {
		SHOOTING, KNIFING;
	}
	
	private DefaultTypes type;
	
	private Animation[] animations;
	
	private ArrayList<Consumable> attackSpawns = new ArrayList<>();
	private CollisionManager manager;
	
	private GameObject attacker;
	
	private float attackSpeed;
	private float attackWait;
	private boolean attacking = false;
	private int currentAnim = -1;
	
	public Attack(Animation[] animations, DefaultTypes type, CollisionManager manager, GameObject attacker, float attackSpeed) {
		this.animations = animations;
		this.type = type;
		this.manager = manager;
		this.attacker = attacker;
	}
	
	public void passiveUpdate(float timePassed) {
		for(Consumable spawn : attackSpawns)
			spawn.update(timePassed);
		if(attacking) {
			attackWait += timePassed;
			if(attackWait >= attackSpeed) {
				attacking = false;
				attackWait = 0;
			}
		}
	}
	
	public void passiveRender(Renderer renderer) {
		for(Consumable spawn : attackSpawns)
			spawn.render(renderer);
	}
	
	public void activeUpdate(float timePassed) {
		passiveUpdate(timePassed);
		if(Input.isKeyPressed(KeyEvent.VK_RIGHT) && !attacking) {
			attacking = true;
			if(animations.length > 0) {
				animations[0].play();
				currentAnim = 0;
				if(animations.length > 1)
					animations[1].stop();
			}
			onAttack(true);
		} else if(Input.isKeyPressed(KeyEvent.VK_LEFT) && !attacking) {
			attacking = true;
			if(animations.length > 0) {
				animations[0].stop();
				if(animations.length > 1) {
					animations[1].play();
					currentAnim = 1;
				}
			}
			onAttack(false);
		}
		if(attacking) {
			attackWait += timePassed;
			if(attackWait >= attackSpeed) {
				attacking = false;
				attackWait = 0;
				currentAnim = -1;
			}
			for(Animation animation : animations)
				animation.update(timePassed);
		}
	}
	
	public void activeRender(Renderer renderer) {
		passiveRender(renderer);
		animations[currentAnim].render(renderer, (int)attacker.getX(), (int)attacker.getY());
	}
	
	public void onAttack(boolean toRight) {
		switch(type) {
			case SHOOTING:
				if(toRight)
					attackSpawns.add(new Bullet(attacker.getX() + attacker.getWidth() + 10, attacker.getY() + animations[0].getHeight()/4.0f, 25, true, manager));
				else
					attackSpawns.add(new Bullet(attacker.getX() - 10 - Bullet.IMAGE.width, attacker.getY() + animations[0].getHeight()/4.0f, 25, false, manager));
				break;
			case KNIFING:
				
		}
	}
}
