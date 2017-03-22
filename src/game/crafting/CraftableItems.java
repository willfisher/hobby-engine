package game.crafting;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import com.retrochicken.engine.Input;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Image;
import com.retrochicken.engine.interactable.TriggerEvent;
import com.retrochicken.engine.physics.Collidable;
import com.retrochicken.engine.physics.Collider;
import com.retrochicken.engine.physics.CollisionManager;
import com.retrochicken.engine.physics.GameObject;

import game.Icon;
import game.scenes.MainScene;

public class CraftableItems {
	private static final Image WOOD_IMAGE = new Image("/icons/woodicon.png");
	private static final Image METAL_IMAGE = new Image("/icons/metalicon.png");
	
	private MainScene mainScene;
	
	public CraftableItems(MainScene mainScene) {
		this.mainScene = mainScene;
	}
	
	public class Wood extends GenericItem {
		public Wood(float x, float y, CollisionManager manager) {
			super(x, y, manager, WOOD_IMAGE, true, "wood", new Icon(new Image("/icons/woodicon.png"), "wood", true));
		}
	}
	
	public class Metal extends GenericItem {
		public Metal(float x, float y, CollisionManager manager) {
			super(x, y, manager, METAL_IMAGE, true, "metal", new Icon(new Image("/icons/metalicon.png"), "metal", true));
		}
	}
	
	public class GenericItem extends GameObject implements Craftable, Collidable {
		private Collider collider;
		private boolean used = false;
		private String tag;
		private Image image;
		
		private Icon icon;
		private TriggerEvent event;
		
		public GenericItem(float x, float y, CollisionManager manager, Image image, boolean isKinematic, String tag, Icon icon) {
			super(x, y, true);
			this.collider = new Collider(x, y, image.width, image.height);
			this.tag = tag;
			this.image = image;
			manager.addCollidable(this);
			this.icon = icon;
			event = new TriggerEvent(new Collider(collider.getX() - 10, collider.getY() - 10, collider.getWidth() + 20, collider.getHeight() + 20, true), "player", mainScene.getCollisionManager()) {
				@Override
				public void onTagCollide() {
					if(Input.isKeyPressed(KeyEvent.VK_F)) {
						mainScene.getCharacter().getInventory().addIcon(icon);
						deactivate();
						setUsed(true);
					}
				}
			};
		}
		
		public void update(float timePassed) {
			super.update(timePassed);
			collider.setPos(super.getX(), super.getY());
			event.setPos(super.getX() - 10, super.getY() - 10);
		}
		
		public void render(Renderer renderer) {
			renderer.drawImage(image, (int)super.getX(), (int)super.getY());
		}
		
		@Override
		public String getTag() {
			return tag;
		}

		@Override
		public boolean isUsed() {
			return used;
		}

		@Override
		public void setUsed(boolean used) {
			this.used = used;
			if(used)
				collider.setDispose(true);
		}

		@Override
		public float getWidth() {
			return collider.getWidth();
		}

		@Override
		public float getHeight() {
			return collider.getHeight();
		}

		@Override
		public void onCollide(Collider collider, Rectangle collision, Collidable collidable) {
			if(!collider.isTrigger()) {
				if(Math.abs(super.getY() - super.previousY()) > 0 && collider.fullyAboveBelow(super.previousY(), this.collider.getHeight())) {
					super.setY(super.getY() + (this.collider.toAbove(collider) ? -1 : 1) * collision.height);
					if(this.collider.toAbove(collider)) super.hitGround(collider);
				}
				if(Math.abs(super.getX() - super.previousX()) > 0 && collider.fullyLeftRight(super.previousX(), this.collider.getWidth()))
					super.setX(super.getX() + (this.collider.toLeft(collider) ? -1 : 1) * collision.width);
			}
			
			this.collider.setPos(super.getX(), super.getY());
		}

		@Override
		public Collider getCollider() {
			return collider;
		}
		
		public Icon getIcon() {
			return icon;
		}
	}
}
