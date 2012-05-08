package de.redlion.gamejam.prot1;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Body;

public class NextGenCam extends OrthographicCamera {
	private float vx=0;
	private float vy=0;
	private float r=0;
	private float rv=0;
		
	public NextGenCam(float viewportWidth, float viewportHeight) {
		super(viewportWidth,viewportHeight);

	}
	
	public void update(Body cartBody) {
		
		vx -= (position.x - cartBody.getPosition().x) * 0.01 + 0. * (Math.random() - 0.5);
		vy -= (position.y - cartBody.getPosition().y) * 0.005 + 0. * (Math.random() - 0.5);
		vx *= 0.90;
		vy *= 0.90;
		
		position.x += vx;
		position.y += vy;
		
		rv += (cartBody.getLinearVelocity().x) * 0.0003;
		this.rotate(-r, 0, 0, 1);
		r += rv;
		rv *= 0.95;
		rv -= 0.01 * r;
		
		this.rotate(r, 0, 0, 1);
		
		this.zoom = this.zoom *0.95f + 0.05f*(0.5f+Math.abs(0.02f*(cartBody.getLinearVelocity().x+cartBody.getLinearVelocity().y)));
	}
}
