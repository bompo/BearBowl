package de.redlion.gamejam.prot1;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player {
	
	public Body body;
	
	public Player(World world) {
		PolygonShape boxPoly = new PolygonShape();
		boxPoly.setAsBox(0.5f, 0.5f);
		
		BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = BodyType.DynamicBody;
		boxBodyDef.position.x = 5;
		boxBodyDef.position.y = 5;
		boxBodyDef.angularDamping = 1f;
		boxBodyDef.linearDamping = 1.0f;
		Body boxBody = world.createBody(boxBodyDef);

		boxBody.createFixture(boxPoly, 1);
		// add the box to our list of boxes
		body = boxBody;
		

		// we are done, all that's left is disposing the boxPoly
		boxPoly.dispose();
	}

}
