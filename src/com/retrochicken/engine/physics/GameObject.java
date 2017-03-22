package com.retrochicken.engine.physics;

import com.retrochicken.engine.Renderer;

public abstract class GameObject {
	public abstract float getWidth();
	public abstract float getHeight();
	
	private float[] x;
	private float[] y;
	
	private boolean isKinematic = false;
	
	public boolean grounded = false;
	
	public float verticalSpeed = 0;
	
	private Collider lastHit;
	
	public GameObject(float x, float y) {
		this.x = new float[]{x, x};
		this.y = new float[]{y, y};
	}
	
	public GameObject(float x, float y, boolean isKinematic) {
		this(x, y);
		this.isKinematic = isKinematic;
	}
	
	public void hitGround(Collider collider) {
		grounded = true;
		verticalSpeed = 0;
		lastHit = collider;
	}
	
	public void update(float timePassed) {
		if((grounded && (Math.abs(getY() + getHeight() - lastHit.getY()) > 1 || (x[0] + getWidth() <= lastHit.getX() + 1 || x[0] + 1 >= lastHit.getX() + lastHit.getWidth()) || Math.abs(getY() - previousY()) > 1))
			|| (lastHit != null && lastHit.isDispose()))
			grounded = false;
		
		if(isKinematic) {
			if(!grounded) {
				verticalSpeed -= Physics.GRAVITY * timePassed;
				if(Math.abs(verticalSpeed) > Physics.TERMINAL_VELOCITY)
					verticalSpeed = Math.signum(verticalSpeed) * Physics.TERMINAL_VELOCITY;
			}
			
			setY(y[0] - verticalSpeed * timePassed);
		}
	}
	
	public void render(Renderer renderer) {
		
	}
	
	public void setPos(float x, float y) {
		setX(x);
		setY(y);
	}
	
	public float getX() {
		return x[0];
	}

	public void setX(float x) {
		this.x[1] = this.x[0];
		this.x[0] = x;
	}

	public float getY() {
		return y[0];
	}

	public void setY(float y) {
		this.y[1] = this.y[0];
		this.y[0] = y;
	}
	
	public float previousX() {
		return x[1];
	}
	
	public float previousY() {
		return y[1];
	}
	
	public void setKinematic(boolean isKinematic) {
		this.isKinematic = isKinematic;
	}
}
