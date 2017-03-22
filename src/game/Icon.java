package game;

import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Image;
import com.retrochicken.engine.fx.ShadowType;

public class Icon {
	private Image icon;
	private String tag;
	private boolean isExpendable = false;
	private int amount = 1;
	
	public Icon(Image icon, String tag) {
		this.icon = icon;
		this.tag = tag;
		this.icon.shadowType = ShadowType.UNAFFECTED;
	}
	
	public Icon(Image icon, String tag, boolean isExpendable) {
		this(icon, tag);
		this.isExpendable = isExpendable;
	}
	
	public void render(Renderer renderer, int x, int y) {
		renderer.drawImage(icon, x, y);
	}
	
	public String getTag() {
		return tag;
	}

	public boolean isExpendable() {
		return isExpendable;
	}

	public void setExpendable(boolean isExpendable) {
		this.isExpendable = isExpendable;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
}
