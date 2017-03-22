package game.interactable;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.retrochicken.engine.Input;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Image;
import com.retrochicken.engine.fx.SpriteSheet;
import com.retrochicken.engine.physics.Collider;
import com.retrochicken.engine.physics.GameObject;

public class Noose extends GameObject {

	private static SpriteSheet tile1 = new SpriteSheet("/interactable/noosetile1.png");
	private static Image tile2 = new Image("/interactable/noosetile2.png");
	
	private float x, y;
	private int tiles;
	private boolean dragging = true;
	
	private Character character;
	
	private Collider trigger;
	
	public Noose(float x, float y, Character character) {
		super(x, y, false);
		this.x = x;
		this.y = y;
		this.character = character;
	}
	
	public void update(float timePassed) {
		if(dragging && Input.getMouseY() >= y + tile1.getHeight(0) * (tiles + 2) - 1)
			tiles++;
		if(Input.isButtonReleased(MouseEvent.BUTTON3)) {
			dragging = false;
			trigger = new Collider((int)x - 8, (int)(y + tile1.getHeight(0)*(tiles + 1) - 1) - 4, tile2.width + 8, tile2.height + 8, true);
		}
		
		if(Input.isKeyPressed(KeyEvent.VK_F) && !dragging && !trigger.fullyLeftRight(character.getCollider()) && !trigger.fullyAboveBelow(character.getCollider()))
			character.recieveDamage(1000);
	}
	
	public void render(Renderer renderer) {
		tile1.drawSprite(renderer, 0, false, (int)x, (int)y - 1);
		float currY = y + tile1.getHeight(0) - 1;
		for(int i = 0; i < tiles; i++) {
			tile1.drawSprite(renderer, 1, false, (int)x + 2, (int)currY);
			currY += tile1.getHeight(0);
		}
		if(!dragging)
			renderer.drawImage(tile2, (int)x - 4, (int)currY);
	}

	@Override
	public float getWidth() {
		return 0;
	}

	@Override
	public float getHeight() {
		return 0;
	}
	
}
