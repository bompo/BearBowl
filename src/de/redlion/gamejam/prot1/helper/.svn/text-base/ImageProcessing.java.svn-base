package de.redlion.gamejam.prot1.helper;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;

public class ImageProcessing {

	
	public static Pixmap loadImage (String imagePath){
		Pixmap pm = new Pixmap(Gdx.files.internal(imagePath));
		
		/*InputStream in = Gdx.files.internal(imagePath).read();

		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(in);
		} catch (IOException e) {
			e.printStackTrace();
		}*/

		return pm;
	}
	
	public static Vector2[] detectPath (Pixmap img, int stepSize){
		
		ArrayList<Vector2> vertices = new ArrayList<Vector2>();
		for (int x=0; x< img.getWidth(); x+=stepSize){
			Gdx.app.log("Path Detection ", "started" );
			int oldRGB = 0;
			for (int y=0; y<img.getHeight(); y+=1){
				if(oldRGB != 0 && oldRGB != img.getPixel(x, y)){
					vertices.add(new Vector2(x,img.getHeight()- y));
					Gdx.app.log("Path Detection", "x: "+ x + ", y: " +y);
				}
				oldRGB = img.getPixel(x,y);
			}
		}
		Vector2[] vec = new Vector2[vertices.size()];
		for (int z=0; z<vertices.size(); z++){
			vec[z]=vertices.get(z);
		}
		return vec;
	}
	
	public Vector2[] removeTrackOffset (Vector2[] vertices){
		float offset = vertices[0].y;
		for (int i=1; i<vertices.length; i++){
			 vertices[i].y = vertices[i].y - offset;
		}
		return vertices;
	}
	
	
}
