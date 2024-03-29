package com.softvision.jattack.elements.invaders;

import com.softvision.jattack.coordinates.Coordinates;
import com.softvision.jattack.coordinates.CoordinatesCache;
import com.softvision.jattack.coordinates.FixedCoordinates;
import com.softvision.jattack.elements.bullets.HelicopterBullet;
import com.softvision.jattack.images.ImageLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.Font;

public class Alien extends  Invader {
	private final Image image = ImageLoader.getImage(ImageType.ALIEN);

	public Alien(Coordinates coordinates) {
		super(coordinates);
	}

	@Override
	public Image getImage() {
		return image;
	}

	public void shoot(GraphicsContext graphicsContext) {
		//the x coordinate of the bullet is computed based on the width of the image for the invader and also the bullet width
		HelicopterBullet bullet = new HelicopterBullet(new FixedCoordinates(getCoordinates().getX() + 35, getCoordinates().getY() + 100));
		graphicsContext.setFill(bullet.getColor());
		graphicsContext.setFont(new Font("Arial Bold", bullet.getBulletSize()));
		graphicsContext.fillText(bullet.getBulletShape(), bullet.getCoordinates().getX(), bullet.getCoordinates().getY());
		CoordinatesCache.getInstance().getEnemyBullets().add(bullet);
	}

}
