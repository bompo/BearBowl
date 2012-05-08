package de.redlion.gamejam.prot1;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.MathUtils;

import de.redlion.gamejam.prot1.helper.ImageProcessing;

public class GameJam implements ApplicationListener, InputProcessor {

	/** the camera **/
	private NextGenCam camera;

	/** the immediate mode renderer to output our debug drawings **/
	private ImmediateModeRenderer renderer;

	/**
	 * a spritebatch and a font for text rendering and a Texture to draw our
	 * boxes
	 **/
	private SpriteBatch batch;
	private BitmapFont font;

	/** our box2D world **/
	private World world;

	/** our track box **/
	private Body trackBody;
	PolygonShape track;

	/** our mouse joint **/
	private MouseJoint mouseJoint = null;

	/** a hit body **/
	Body hitBody = null;

	Body hitBox1;
	Body hitBox2;

	float accelerometerX = 0;
	float accelerometerY = 0;

	float timer = 0;

	private Body cartBody;
	private Body wheel1;
	private Body wheel2;
	private Body axle1;
	private Body axle2;
	private RevoluteJoint motor1;
	private RevoluteJoint motor2;
	private PrismaticJoint spring1;
	private PrismaticJoint spring2;

	private TextureRegion textureWheels;
	private TextureRegion textureCar;

	private TextureRegion textureBackground1;
	private TextureRegion textureBackground2;

	private TextureRegion textureBorder;
	private TextureRegion textureFill;

	private TextureRegion textureButton;
	private TextureRegion textureBear;

	private ArrayList<PolygonShape> trackShapes;
	private Vector2[] trackVertices;

	boolean up, down, left, right = false;

	private ArrayList<Body> bears = new ArrayList<Body>();

	ParticleEffect effect;
	int emitterIndex;
	ArrayList<ParticleEmitter> emitters;
	int particleCount = 10;
	float fpsCounter;

	@Override
	public void create() {
		ImageProcessing imageProc = new ImageProcessing();
		Pixmap levelImage = ImageProcessing.loadImage("data/level_border_25.png");
		trackVertices = ImageProcessing.detectPath(levelImage, 10);
		imageProc.removeTrackOffset(trackVertices);
		levelImage.dispose();

		// 48 meters in width and 32 meters in height.
		camera = new NextGenCam(48, 32);
		camera.position.set(0, 16, 0);

		// next we setup the immediate mode renderer
		renderer = new ImmediateModeRenderer();

		// next we create a SpriteBatch and a font
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.RED);

		textureWheels = new TextureRegion(new Texture(Gdx.files.internal("data/wheel.png")));
		textureCar = new TextureRegion(new Texture(Gdx.files.internal("data/car.png")));

		textureBackground1 = new TextureRegion(new Texture(Gdx.files.internal("data/background_1.png")));
		textureBackground2 = new TextureRegion(new Texture(Gdx.files.internal("data/background_2.png")));

		textureBorder = new TextureRegion(new Texture(Gdx.files.internal("data/gras2.png")));
		textureFill = new TextureRegion(new Texture(Gdx.files.internal("data/erde.png")));

		textureBear = new TextureRegion(new Texture(Gdx.files.internal("data/bear.png")));

		textureButton = new TextureRegion(new Texture(Gdx.files.internal("data/onscreen_control_knob.png")));

		// effect = new ParticleEffect();
		// effect.load(Gdx.files.internal("data/test.p"),
		// Gdx.files.internal("data"));
		// effect.setPosition(Gdx.graphics.getWidth() / 2,
		// Gdx.graphics.getHeight() / 2);
		// // Of course, a ParticleEffect is normally just used, without messing
		// around with its emitters.
		//
		// emitters = new ArrayList<ParticleEmitter>(effect.getEmitters());
		// effect.getEmitters().clear();
		// effect.getEmitters().add(emitters.get(0));

		// next we create out physics world.
		createPhysicsWorld();
		createTrack();
		createCar();
		createBears();

		// register ourselfs as an InputProcessor
		Gdx.input.setInputProcessor(this);
	}

	private void createPhysicsWorld() {
		// we instantiate a new World with a proper gravity vector
		// and tell it to sleep when possible.
		world = new World(new Vector2(0, -10), false);
		world.setContinuousPhysics(true);

	}

	private void createTrack() {
		PolygonShape trackPoly = new PolygonShape();
		{

			trackShapes = new ArrayList<PolygonShape>();
			/*
			 * float oldX = -30; float oldY = -4;
			 */
			float offsetX = -20;
			float offsetY = -4;
			float blockSize = 10;

			for (int i = 0; i < trackVertices.length - 1; i++) {
				trackPoly = new PolygonShape();
				Vector2[] vertices = new Vector2[4];
				vertices[0] = new Vector2(trackVertices[i].x + offsetX, trackVertices[i].y + offsetY);
				vertices[1] = new Vector2(trackVertices[i].x + offsetX, trackVertices[i].y - blockSize + offsetY);
				/*
				 * float newX = oldX + (float) (Math.random() * 3)+2; float newY
				 * ; if(oldY < 4){ if (oldY < -4) newY = oldY + ((float)
				 * ((Math.random() * 0.5))); else newY = oldY + ((float)
				 * (((Math.random()-0.4)))) ; } else newY = oldY + ((float)
				 * (Math.random() * -0.5));
				 */
				vertices[2] = new Vector2(trackVertices[i + 1].x + offsetX, trackVertices[i + 1].y - blockSize + offsetY);
				vertices[3] = new Vector2(trackVertices[i + 1].x + offsetX, trackVertices[i + 1].y + offsetY);

				trackPoly.set(vertices);

				BodyDef trackBodyDef = new BodyDef();
				trackBodyDef.type = BodyType.StaticBody;
				trackBodyDef.position.x = 0;
				trackBodyDef.position.y = 0;
				trackBody = world.createBody(trackBodyDef);

				// oldX = newX;
				// oldY = newY ;

				trackBody.createFixture(trackPoly, 1);

				// add the track to our list
				trackShapes.add(trackPoly);
			}
		}

		trackPoly.dispose();
	}

	public void createCar() {
		// temporary variables for adding new bodies //
		RevoluteJointDef revoluteJointDef;

		// cart
		{
			BodyDef cartDef = new BodyDef();
			FixtureDef fixDef = new FixtureDef();
			PolygonShape polShape = new PolygonShape();

			cartDef.position.set(0f, 3.5f);
			cartDef.type = BodyType.DynamicBody;
			cartDef.allowSleep = false;

			polShape.setAsBox(1.5f, 0.3f);

			fixDef.shape = polShape;
			fixDef.density = 2;
			fixDef.friction = 0.5f;
			fixDef.restitution = 0.2f;
			fixDef.filter.groupIndex = -1;

			// cart body
			cartBody = world.createBody(cartDef);
			cartBody.createFixture(fixDef);

			// small cart elements
			polShape.setAsBox(0.4f, 0.15f, new Vector2(-1, -0.3f), (float) (Math.PI / 3.0f));
			cartBody.createFixture(fixDef);

			polShape.setAsBox(0.4f, 0.15f, new Vector2(1, -0.3f), (float) (-Math.PI / 3.0f));
			cartBody.createFixture(fixDef);

			fixDef.density = 1;

		}

		// axles
		{
			BodyDef axleDef = new BodyDef();
			FixtureDef fixDef = new FixtureDef();
			PolygonShape polShape = new PolygonShape();

			polShape.setAsBox(1.5f, 0.3f);

			axleDef.position.set(0, 3.5f);
			axleDef.type = BodyType.DynamicBody;
			axleDef.allowSleep = false;

			fixDef.shape = polShape;
			fixDef.density = 1;
			fixDef.friction = 0.5f;
			fixDef.restitution = 0.2f;
			fixDef.filter.groupIndex = -1;

			// create axle1
			axle1 = world.createBody(axleDef);

			polShape.setAsBox(0.4f, 0.1f, new Vector2((float) (-1 - 0.6f * Math.cos(Math.PI / 3.0f)), (float) (-0.3f - 0.6f * Math.sin(Math.PI / 3.0f))), (float) (Math.PI / 3.0f));
			axle1.createFixture(fixDef);

			// create axle2
			axle2 = world.createBody(axleDef);

			polShape.setAsBox(0.4f, 0.1f, new Vector2((float) (1 + 0.6f * Math.cos(-Math.PI / 3.0f)), (float) (-0.3f + 0.6f * Math.sin(-Math.PI / 3.0f))), (float) (-Math.PI / 3.0f));
			axle2.createFixture(fixDef);

			// create joint spring1 for axle1
			PrismaticJointDef prismaticJointDef = new PrismaticJointDef();

			prismaticJointDef.initialize(cartBody, axle1, axle1.getWorldCenter(), new Vector2((float) (Math.cos(Math.PI / 3.0f)), (float) (Math.sin(Math.PI / 3.0f))));
			prismaticJointDef.lowerTranslation = -0.3f;
			prismaticJointDef.upperTranslation = 0.5f;
			prismaticJointDef.enableLimit = true;
			prismaticJointDef.enableMotor = true;

			spring1 = (PrismaticJoint) world.createJoint(prismaticJointDef);

			// create joint spring2 for axle1
			prismaticJointDef.initialize(cartBody, axle2, axle2.getWorldCenter(), new Vector2((float) (-Math.cos(Math.PI / 3.0f)), (float) (Math.sin(Math.PI / 3.0f))));
			prismaticJointDef.lowerTranslation = -0.3f;
			prismaticJointDef.upperTranslation = 0.5f;
			prismaticJointDef.enableLimit = true;
			prismaticJointDef.enableMotor = true;

			spring2 = (PrismaticJoint) world.createJoint(prismaticJointDef);
		}

		// wheels
		{
			BodyDef wheelDef = new BodyDef();
			FixtureDef fixDef = new FixtureDef();
			CircleShape circleShape = new CircleShape();

			circleShape.setRadius(0.7f);
			fixDef.shape = circleShape;
			fixDef.density = 0.1f;
			fixDef.friction = 5;
			fixDef.restitution = 0.2f;
			fixDef.filter.groupIndex = -1;

			for (int i = 0; i < 2; i++) {
				if (i == 0)
					wheelDef.position.set((float) (axle1.getWorldCenter().x - 1f * Math.cos(Math.PI / 3.0f)), (float) (axle1.getWorldCenter().y - 0.5f * Math.sin(Math.PI / 3.0f)));
				else
					wheelDef.position.set((float) (axle2.getWorldCenter().x + 0.8f * Math.cos(-Math.PI / 3.0f)), (float) (axle2.getWorldCenter().y + 0.5f * Math.sin(-Math.PI / 3.0f)));

				wheelDef.type = BodyType.DynamicBody;
				wheelDef.allowSleep = false;

				if (i == 0)
					wheel1 = world.createBody(wheelDef);
				else
					wheel2 = world.createBody(wheelDef);

				if (i == 0) {
					wheel1.createFixture(fixDef);
				} else {
					wheel2.createFixture(fixDef);
				}

			}

			// connect wheels with axle
			revoluteJointDef = new RevoluteJointDef();
			revoluteJointDef.enableMotor = true;

			revoluteJointDef.initialize(axle1, wheel1, wheel1.getWorldCenter());
			motor1 = (RevoluteJoint) world.createJoint(revoluteJointDef);

			revoluteJointDef.initialize(axle2, wheel2, wheel2.getWorldCenter());
			motor2 = (RevoluteJoint) world.createJoint(revoluteJointDef);
		}
	}

	private void createBears() {

		CircleShape circlePoly = new CircleShape();
		circlePoly.setRadius(1);

		for (int i = 0; i < 20; i++) {
			BodyDef circleBodyDef = new BodyDef();
			circleBodyDef.type = BodyType.DynamicBody;
			circleBodyDef.position.x = -24 + (float) (Math.random() * 480);
			circleBodyDef.position.y = 10 + (float) (Math.random() * 100);
			Body boxBody = world.createBody(circleBodyDef);
			boxBody.createFixture(circlePoly, 0.001f);

			bears.add(boxBody);
		}

		circlePoly.dispose();
	}

	@Override
	public void render() {

		long start = System.nanoTime();
		world.step(Gdx.graphics.getDeltaTime(), 20, 50);
		float updateTime = (System.nanoTime() - start) / 100000.0f;

		// next we clear the color buffer and set the camera
		// matrices
		GL10 gl = Gdx.graphics.getGL10();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();
		camera.apply(gl);

		if (up) {
			motor1.setMotorSpeed((float) (10 * Math.PI));
			motor1.setMaxMotorTorque(17);
			motor2.setMotorSpeed((float) (10 * Math.PI));
			motor2.setMaxMotorTorque(12);
		} else if (down) {
			motor1.setMotorSpeed((float) (-10 * Math.PI));
			motor1.setMaxMotorTorque(12);
			motor2.setMotorSpeed((float) (-10 * Math.PI));
			motor2.setMaxMotorTorque(17);
		} else {
			motor1.setMotorSpeed((float) 0);
			motor2.setMotorSpeed((float) 0);
		}

		spring1.setMaxMotorForce((float) (40 + Math.abs(800 * Math.pow(spring1.getJointTranslation(), 2))));
		spring1.setMotorSpeed((float) ((spring1.getMotorSpeed() - 10 * spring1.getJointTranslation()) * 0.4));

		spring2.setMaxMotorForce((float) (30 + Math.abs(800 * Math.pow(spring2.getJointTranslation(), 2))));
		spring2.setMotorSpeed((float) (-4 * Math.pow(spring2.getJointTranslation(), 1)));

		if (left) {
			cartBody.applyTorque(10);
		} else if (right) {
			cartBody.applyTorque(-10);
		} else {
			cartBody.applyTorque(0);
		}

		// next we render each box via the SpriteBatch.
		// for this we have to set the projection matrix of the
		// spritebatch to the camera's combined matrix. This will
		// make the spritebatch work in world coordinates
		batch.getProjectionMatrix().set(camera.combined);
		batch.begin();
		// background
		{
			batch.draw(textureBackground1, camera.position.x, 5f, 1f, 1f, 2, 2, 32, 16, 0);
			for (int i = 0; i < 1000; i = i + 70) {
				batch.draw(textureBackground2, camera.position.x / 2 + i, 5f, 1f, 1f, 2, 2, 32, 16, 0);
			}
		}

		{
			Vector2 position = cartBody.getPosition();
			float angle = MathUtils.radiansToDegrees * cartBody.getAngle();
			batch.draw(textureCar, position.x - 1, position.y - 0.8f, 1f, 1f, 2, 2, 6, 1.5f, angle);
		}
		{
			Vector2 position = wheel1.getPosition();
			float angle = MathUtils.radiansToDegrees * wheel1.getAngle();
			batch.draw(textureWheels, position.x - 1, position.y - 1, 1f, 1f, 2, 2, 0.7f, 0.7f, angle);
		}
		{
			Vector2 position = wheel2.getPosition();
			float angle = MathUtils.radiansToDegrees * wheel2.getAngle();
			batch.draw(textureWheels, position.x - 1, position.y - 1, 1f, 1f, 2, 2, 0.7f, 0.7f, angle);
		}
		for (int i = 0; i < bears.size(); i++) {
			Body box = bears.get(i);
			Vector2 position = box.getPosition(); 
			float angle = MathUtils.radiansToDegrees * box.getAngle(); 
			batch.draw(textureBear, position.x - 1, position.y - 1, 1f, 1f, 2, 2, 1, 1, angle);
		}
		batch.end();

		renderTrack(gl);
		// renderContactPoints(gl);

		// finally we render the time it took to update the world
		// for this we have to set the projection matrix again, so
		// we work in pixel coordinates
		batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.begin();
		{
			// buttons
			batch.draw(textureButton, 70f, 50f, 1f, 1f, 2, 2, 20, 20, 0);
			batch.draw(textureButton, 780, 50, 1f, 1f, 2, 2, 20, 20, 0);

			font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond() + " update time: " + updateTime, 0, 20);
		}
		batch.end();

		camera.update(cartBody);

	}

	private void renderContactPoints(GL10 gl) {
		// finally we render all contact points
		gl.glPointSize(4);
		renderer.begin(GL10.GL_POINTS);
		for (int i = 0; i < world.getContactCount(); i++) {
			Contact contact = world.getContactList().get(i);
			// we only render the contact if it actually touches
			if (contact.isTouching()) {
				// get the world manifold from which we get the
				// contact points. A manifold can have 0, 1 or 2
				// contact points.
				WorldManifold manifold = contact.GetWorldManifold();
				int numContactPoints = manifold.getNumberOfContactPoints();
				for (int j = 0; j < numContactPoints; j++) {
					Vector2 point = manifold.getPoints()[j];
					renderer.color(0, 1, 0, 1);
					renderer.vertex(point.x, point.y, 0);
				}
			}
		}
		renderer.end();
		gl.glPointSize(1);
	}

	private void renderBox(GL10 gl, Body body, float halfWidth, float halfHeight, float initAngle) {
		// push the current matrix and
		// get the bodies center and angle in world coordinates
		gl.glPushMatrix();
		Vector2 pos = body.getPosition();
		float angle = body.getAngle();

		// set the translation and rotation matrix
		gl.glTranslatef(pos.x, pos.y, 0);
		gl.glRotatef((float) Math.toDegrees(initAngle), 0, 0, 1);
		gl.glRotatef((float) Math.toDegrees(angle), 0, 0, 1);

		// render the box
		renderer.begin(GL10.GL_LINE_STRIP);
		renderer.color(1, 1, 1, 1);
		renderer.vertex(-halfWidth, -halfHeight, 0);
		renderer.color(1, 1, 1, 1);
		renderer.vertex(-halfWidth, halfHeight, 0);
		renderer.color(1, 1, 1, 1);
		renderer.vertex(halfWidth, halfHeight, 0);
		renderer.color(1, 1, 1, 1);
		renderer.vertex(halfWidth, -halfHeight, 0);
		renderer.color(1, 1, 1, 1);
		renderer.vertex(-halfWidth, -halfHeight, 0);
		renderer.end();

		// pop the matrix
		gl.glPopMatrix();
	}

	private void renderTrack(GL10 gl) {


		batch.getProjectionMatrix().set(camera.combined);
		batch.begin();

		Vector2 vertex = new Vector2(0, 0);
		Vector2 startVertex = new Vector2(0, 0);
		Vector2 endVertex = new Vector2(0, 0);
		for (PolygonShape pShape : trackShapes) {
			// renderer.begin(GL10.GL_LINE_STRIP);
			// renderer.color(1, 1, 1, 1);

			pShape.getVertex(0, vertex);
			// renderer.vertex(vertex.x, vertex.y, 0);
			startVertex.set(vertex);

			// pShape.getVertex(1, vertex);
			// renderer.vertex(vertex.x, vertex.y, 0);

			// pShape.getVertex(2, vertex);
			// renderer.vertex(vertex.x, vertex.y, 0);

			pShape.getVertex(3, vertex);
			// renderer.vertex(vertex.x, vertex.y, 0);
			endVertex.set(vertex);

			// pShape.getVertex(0, vertex);
			// renderer.vertex(vertex.x, vertex.y, 0);

			if(startVertex.x>camera.position.x-35 && startVertex.x<camera.position.x+25) {
			for (float i = startVertex.x; i < endVertex.x; i++) {
				for (float n = startVertex.y + (i - startVertex.x) * (endVertex.y - startVertex.y) / (endVertex.x - startVertex.x) - 2; n > startVertex.y + (i - startVertex.x) * (endVertex.y - startVertex.y) / (endVertex.x - startVertex.x) - 17; n = n - 2) {
					batch.draw(textureFill, i, n, 1f, 1f, 2, 2, 1, 1, 0);
				}
				batch.draw(textureBorder, i, startVertex.y + (i - startVertex.x) * (endVertex.y - startVertex.y) / (endVertex.x - startVertex.x) - 1f, 1f, 1f, 2, 2, 1, 1, 0);
			}
			}

			// renderer.end();
		}
		batch.end();

	}

	/**
	 * we instantiate this vector and the callback here so we don't irritate the
	 * GC
	 **/
	Vector3 testPoint = new Vector3();
	QueryCallback callback = new QueryCallback() {
		@Override
		public boolean reportFixture(Fixture fixture) {
			// if the hit fixture's body is the ground body
			// we ignore it
			if (fixture.getBody() == trackBody)
				return true;

			// if the hit point is inside the fixture of the body
			// we report it
			if (fixture.testPoint(testPoint.x, testPoint.y)) {
				hitBody = fixture.getBody();
				return false;
			} else
				return true;
		}
	};

	@Override
	public boolean touchDown(int x, int y, int pointer, int newParam) {
		if (x > 0 && x < 150 && y < 480 && y > 350)
			up = true;
		if (x > 704 && x < 854 && y < 480 && y > 350)
			down = true;

		// translate the mouse coordinates to world coordinates
		testPoint.set(x, y, 0);
		camera.unproject(testPoint);

		// ask the world which bodies are within the given
		// bounding box around the mouse pointer
		hitBody = null;
		world.QueryAABB(callback, testPoint.x - 0.1f, testPoint.y - 0.1f, testPoint.x + 0.1f, testPoint.y + 0.1f);

		// if we hit something we create a new mouse joint
		// and attach it to the hit body.
		if (hitBody != null) {
			MouseJointDef def = new MouseJointDef();
			def.bodyA = trackBody;
			def.bodyB = hitBody;
			def.collideConnected = true;
			def.target.set(testPoint.x, testPoint.y);
			def.maxForce = 1000.0f * hitBody.getMass();

			mouseJoint = (MouseJoint) world.createJoint(def);
			hitBody.setAwake(true);
		} else {

		}

		return false;
	}

	/** another temporary vector **/
	Vector2 target = new Vector2();

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		// if a mouse joint exists we simply update
		// the target of the joint based on the new
		// mouse coordinates
		if (mouseJoint != null) {
			camera.unproject(testPoint.set(x, y, 0));
			mouseJoint.setTarget(target.set(testPoint.x, testPoint.y));
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		// if a mouse joint exists we simply destroy it
		if (mouseJoint != null) {
			world.destroyJoint(mouseJoint);
			mouseJoint = null;
		}

		up = false;
		down = false;
		left = false;
		right = false;
		return false;
	}

	@Override
	public void dispose() {
		world.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.KEYCODE_W) {
			up = true;
		}
		if (keycode == Keys.KEYCODE_S) {
			down = true;
		}
		if (keycode == Keys.KEYCODE_A) {
			left = true;
		}
		if (keycode == Keys.KEYCODE_D) {
			right = true;
		}
		if (keycode == Keys.KEYCODE_DPAD_LEFT) {
			camera.translate(-1, 0, 0);
		}
		if (keycode == Keys.KEYCODE_DPAD_RIGHT) {
			camera.translate(1, 0, 0);
		}
		if (keycode == Keys.KEYCODE_DPAD_UP) {
			camera.translate(0, 1, 0);
		}
		if (keycode == Keys.KEYCODE_DPAD_DOWN) {
			camera.translate(0, -1, 0);
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		up = false;
		down = false;
		left = false;
		right = false;
		return false;
	}

	@Override
	public boolean touchMoved(int x, int y) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

}
