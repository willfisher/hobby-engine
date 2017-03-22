package game.crafting;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.retrochicken.engine.physics.Collidable;
import com.retrochicken.engine.physics.CollidableImage;
import com.retrochicken.engine.physics.Collider;

import game.scenes.MainScene;

public class CraftingManager {
	private MainScene scene;
	private ArrayList<CraftableItem> craftableItems = new ArrayList<>();
	
	public CraftingManager(MainScene scene) {
		this.scene = scene;
		craftableItems.add(new CraftableItem(new String[]{"wood", "metal"}, new int[]{3, 2}) {
			@Override
			public void onCraft(Rectangle select) {
				scene.addGameObject((new CollidableImage("/props/barrel.png", select.x, select.y, true, scene.getCollisionManager())).getObject());
			}
		});
		craftableItems.add(new CraftableItem(new String[]{"wood"}, new int[]{4}){
			@Override
			public void onCraft(Rectangle select) {
				scene.addGameObject((new CollidableImage("/props/table.png", select.x, select.y, true, scene.getCollisionManager())).getObject());
			}
		});
	}
	
	public void processSelect(Rectangle select) {
		for(CraftableItem item : craftableItems)
			item.tryCraft(select);
	}
	
	//Craftable Items
	private class CraftableItem {
		private List<String> keywords;
		private int[] amounts;
		private int itemsNeeded;
		
		public CraftableItem(String[] keywords, int[] amounts) {
			this.keywords = Arrays.asList(keywords);
			this.amounts = amounts;
			for(int i : amounts)
				itemsNeeded += i;
		}
		
		public boolean tryCraft(Rectangle select) {
			ArrayList<Craftable> items = new ArrayList<>();
			for(Collidable col : scene.getCollisionManager().collidesWith(new Collider(select.x, select.y, select.width, select.height)))
				if(col instanceof Craftable)
					items.add((Craftable)col);
			
			if(items.size() != itemsNeeded)
				return false;
			int[] amounts = new int[this.amounts.length];
			for(Craftable item : items) {
				int i = keywords.indexOf(item.getTag().toLowerCase());
				if(i == -1)
					return false;
				amounts[i] = amounts[i] + 1;
			}
			for(int i = 0; i < amounts.length; i++)
				if(amounts[i] != this.amounts[i])
					return false;
			for(Craftable item : items)
				item.setUsed(true);
			onCraft(select);
			return true;
		}
		
		public void onCraft(Rectangle select) {
			
		}
	}
}
