//package de.redlion.gamejam.prot1;
//
//import aurelienribon.levelloader.ImportHelper;
//import aurelienribon.levelloader.models.LevelModel;
//import aurelienribon.levelloader.models.ShapeModel;
//
//import com.badlogic.gdx.ApplicationListener;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input.Keys;
//import com.badlogic.gdx.InputProcessor;
//import com.badlogic.gdx.files.FileHandle;
//import com.badlogic.gdx.graphics.GL10;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.PerspectiveCamera;
//import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
//
//public class LevelTest implements ApplicationListener, InputProcessor{
//
//	/** the camera **/
//	private OrthographicCamera camera;
//	/** the immediate mode renderer to output our debug drawings **/
//	private ImmediateModeRenderer renderer;
//	
//	private LevelModel level;
//	
//	@Override
//	public void create() {
//		camera = new OrthographicCamera(48, 32);
//		camera.position.set(0, 16, 0);
//		
//		// First load the level binary file
//		FileHandle file = Gdx.files.internal("data/grasslevel.level");
//		level = new ImportHelper().loadLevel(file);
//
//		// Then rebuild it by giving it the path to the same assets folder
//		// you used in the editor.
//		FileHandle res = Gdx.files.internal("data/assets");
//		level.rebuild(res);
//	}
//
//	@Override
//	public void resume() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void render() {
//		 for (int i=0; i<level.getShapeCount(); i++) {
//		      ShapeModel shape = level.getShape(i);
//
//		      shape.fillTexDef.getTexture().bind();
//		      shape.getFillMesh().render(GL10.GL_TRIANGLES);
//		      shape.borderTexDef.getTexture().bind();
//		      shape.getBorderMesh().render(GL10.GL_TRIANGLES);
//		      //Gdx.app.log("shape", shape.center.toString());
//		   }
//	}
//
//	@Override
//	public void resize(int width, int height) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void pause() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void dispose() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public boolean keyDown(int keycode) {
//		if (keycode == Keys.KEYCODE_DPAD_LEFT) {
//			camera.translate(-1, 0, 0);
//		}
//		if (keycode == Keys.KEYCODE_DPAD_RIGHT) {
//			camera.translate(1, 0, 0);
//		}
//		if (keycode == Keys.KEYCODE_DPAD_UP) {
//			camera.translate(0, 1, 0);
//		}
//		if (keycode == Keys.KEYCODE_DPAD_DOWN) {
//			camera.translate(0, -1, 0);
//		}
//		return false;
//	}
//
//	@Override
//	public boolean keyUp(int keycode) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean keyTyped(char character) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean touchDown(int x, int y, int pointer, int button) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean touchUp(int x, int y, int pointer, int button) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean touchDragged(int x, int y, int pointer) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean touchMoved(int x, int y) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean scrolled(int amount) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//}
