package com.retrochicken.engine.physics;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.ShadowType;

public class CollisionManager {
	private ArrayList<Collidable> activeObjects = new ArrayList<>();
	
	public void update() {
		Rectangle rect;
		for(int i = 0; i < activeObjects.size() - 1; i++) {
			if(activeObjects.get(i).getCollider().isDispose()) {
				activeObjects.remove(i);
				i--;
				continue;
			}
			for(int j = i + 1; j < activeObjects.size(); j++) {
				rect = activeObjects.get(i).getCollider().collides(activeObjects.get(j).getCollider());
				if(rect.width > 0 && rect.height > 0) {
					activeObjects.get(i).onCollide(activeObjects.get(j).getCollider(), rect, activeObjects.get(j));
					activeObjects.get(j).onCollide(activeObjects.get(i).getCollider(), rect, activeObjects.get(i));
				}
			}
		}
		if(activeObjects.get(activeObjects.size() - 1).getCollider().isDispose())
			activeObjects.remove(activeObjects.size() - 1);
	}
	
	public void removeCollider(int index) {
		if(index < 0 || index >= activeObjects.size())
			return;
		activeObjects.remove(index);
	}
	
	public int colliderCount() {
		return activeObjects.size();
	}
	
	public void debug(Renderer renderer) {
		for(Collidable col : activeObjects) {
			Rectangle rect = col.getCollider().getRect();
			for(int i = 0; i < rect.width; i++) {
				renderer.setPixel((int)col.getCollider().getX() + i, (int)col.getCollider().getY(), 0xff7fff00, ShadowType.NONE);
				renderer.setPixel((int)col.getCollider().getX() + i, (int)(col.getCollider().getY() + col.getCollider().getHeight()) - 1, 0xff7fff00, ShadowType.NONE);
			}
			for(int i = 0; i < rect.height; i++) {
				renderer.setPixel((int)col.getCollider().getX(), (int)col.getCollider().getY() + i, 0xff7fff00, ShadowType.NONE);
				renderer.setPixel((int)(col.getCollider().getX() + col.getCollider().getWidth()), (int)col.getCollider().getY() + i, 0xff7fff00, ShadowType.NONE);
			}
		}
	}
	
	public boolean collidesAny(Collider collider) {
		boolean result = false;
		Rectangle rect;
		for(Collidable col : activeObjects) {
			rect = col.getCollider().collides(collider);
			if(rect.width > 0 || rect.height > 0) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	public boolean collidesAnyWithoutTag(Collider collider, String safeTag) {
		boolean result = false;
		Rectangle rect;
		for(Collidable col : activeObjects) {
			rect = col.getCollider().collides(collider);
			if(rect.width > 0 && rect.height > 0 && !col.getCollider().getTag().equals(safeTag)) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	public boolean collidesAnyWithoutTags(Collider collider, String[] safeTags) {
		boolean result = false;
		Rectangle rect;
		List<String> tags = Arrays.asList(safeTags);
		for(Collidable col : activeObjects) {
			rect = col.getCollider().collides(collider);
			if((rect.width > 0 || rect.height > 0) && !tags.contains(col.getCollider().getTag())) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	public void addCollidable(Collidable collidable) {
		activeObjects.add(collidable);
	}
	
	public ArrayList<Collidable> collidesWith(Collider collider) {
		ArrayList<Collidable> result = new ArrayList<>();
		Rectangle rect;
		for(Collidable col : activeObjects) {
			rect = col.getCollider().collides(collider);
			if(rect.width > 0 && rect.height > 0)
				result.add(col);
		}
		return result;
	}
}
