package com.softvision.jattack;

import com.softvision.jattack.Mods.Menu;
import com.softvision.jattack.coordinates.CoordinatesCache;
import com.softvision.jattack.coordinates.FixedCoordinates;
import com.softvision.jattack.elements.Defender;
import com.softvision.jattack.elements.bullets.Bullet;
import com.softvision.jattack.elements.bullets.DefenderBullet;
import com.softvision.jattack.elements.bullets.PlaneBullet;
import com.softvision.jattack.elements.bullets.TankBullet;
import com.softvision.jattack.elements.invaders.Invader;
import com.softvision.jattack.elements.invaders.InvaderFactory;
import com.softvision.jattack.elements.invaders.ImageType;
import com.softvision.jattack.elements.bullets.HelicopterBullet;
import com.softvision.jattack.images.ImageLoader;
import com.softvision.jattack.util.Constants;
import com.softvision.jattack.util.Util;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.softvision.jattack.util.Util.*;

public class JAttack extends Application implements Runnable {

    private final Thread gameThread;

    Text text = new Text();
    Text mode1=new Text();
    Text mode2=new Text();
    Scene scene;
    private Canvas canvas;
    private List<Invader> invaders;
    private GraphicsContext graphicsContext;
    private Defender defender;
    private boolean appearedBoss = false;
    private int teleport = 0;
    private double lastY = Constants.HEIGHT / 2;
    private double lastX = Constants.WIDTH / 2;
    private int score = 0;
    private boolean ismenu = true;
    private int selected = 0;
    private Rectangle rectangle = new Rectangle();
    Group root = new Group();

    public JAttack() {
        gameThread = new Thread(this);
    }

    @Override
    public void init() {
        this.invaders = new ArrayList<>();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("J Attack 1.0 Alpha");

        this.canvas = new Canvas(Constants.WIDTH, Constants.HEIGHT);
        graphicsContext = this.canvas.getGraphicsContext2D();
        generateElements();
//        invaders.forEach(this::drawImage);
        defender = new Defender(new FixedCoordinates((Constants.WIDTH / 2) - 50, Constants.HEIGHT - 150));
        StackPane holder = new StackPane();
        holder.getChildren().add(this.canvas);
        root.getChildren().add(holder);
        text.setX(Constants.WIDTH / 2);
        text.setY(50);
        text.setFont(Font.font("Verdana", 35));
        text.setFill(Color.RED);
        root.getChildren().add(text);
        root.getChildren().add(mode1);
        root.getChildren().add(mode2);
        root.getChildren().add(rectangle);
        mode1.setText("Normal Mode");
        mode2.setText("Time Atack");
        mode1.setFont(Font.font("Verdana",50));
        mode2.setFont(Font.font("Verdana",50));
        mode2.setY(Constants.HEIGHT/2-100);
        mode1.setY(Constants.HEIGHT/2+100);
        mode1.setX(Constants.WIDTH/2-200);
        mode2.setX(Constants.WIDTH/2-200);

        ImagePattern backgroundImage = new ImagePattern(ImageLoader.getImage(ImageType.BACKGROUND));
        holder.setBackground(new Background(new BackgroundFill(backgroundImage, CornerRadii.EMPTY, Insets.EMPTY)));

        scene = new Scene(root);
        scene.setCursor(Cursor.NONE);


        EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                int y = 0;
                int x = 0;
                boolean shoot = false;
                if (mouseEvent.getY() < lastY) {
                    y = 1;
                    lastY = mouseEvent.getY();
                } else if (mouseEvent.getY() > lastY) {
                    y = -1;
                    lastY = mouseEvent.getY();
                }
                if (mouseEvent.getX() < lastX) {
                    x = 1;
                    lastX = mouseEvent.getX();
                } else if (mouseEvent.getX() > lastX) {
                    x = -1;
                    lastX = mouseEvent.getX();
                }
                if (mouseEvent.getEventType().toString() == "MOUSE_CLICKED") shoot = true;
                defender.moveAndShoot(graphicsContext, y, x, shoot);
            }
        };

        scene.setOnMouseClicked(mouseHandler);
        scene.setOnMouseDragged(mouseHandler);
        scene.setOnMouseMoved(mouseHandler);
        scene.setOnMousePressed(mouseHandler);


        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(t->{
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
        gameThread.start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("Inside stop() method! Destroy resources. Perform Cleanup.");
    }

    @Override
    public void run() {

        while (ismenu) {
            scene.setOnKeyPressed(e->{
                                      if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.UP) {
                                          selected = (selected + 1) % 2;
                                      }
                                      if (e.getCode() == KeyCode.ENTER) {
                                          ismenu = false;
                                      }
                                  }
            );
            if(selected==0) {mode1.setFill(Color.RED);mode2.setFill(Color.BLACK);}
            if(selected==1){mode1.setFill(Color.BLACK);mode2.setFill(Color.RED);}
            try {
                Thread.sleep(Util.getTick());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mode2.setText("");
        mode1.setText("");
        boolean gameEnded = false;
        int run_ = 0;
        final double targetDelta = 0.0166;
        long previousTime = System.nanoTime();
        while (!gameEnded) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - previousTime) / 1_000_000_000.0;

            if (this.invaders.isEmpty()) {
                if (!appearedBoss) {
                    if (teleport < Util.MAX_TELEPORT) {
                        appearBoss();
                        teleport++;
                    } else appearedBoss = true;
                } else {
                    CoordinatesCache.getInstance().getEnemyBullets().clear();
                    CoordinatesCache.getInstance().getDefenderBullets().clear();
                    redraw();
                    Image youWon = ImageLoader.getImage(ImageType.YOU_WON);
                    graphicsContext.drawImage(youWon,
                                              Constants.WIDTH / 2 - 100,
                                              Constants.HEIGHT / 2 - 100,
                                              youWon.getWidth(),
                                              youWon.getHeight());
                    gameEnded = true;
                }
            } else if (this.defender.isDead()) {
                CoordinatesCache.getInstance().getEnemyBullets().clear();
                CoordinatesCache.getInstance().getDefenderBullets().clear();
                invaders.clear();
                redraw();
                Image youLost = ImageLoader.getImage(ImageType.YOU_LOST);
                graphicsContext.drawImage(youLost,
                                          Constants.WIDTH / 2 - 100,
                                          Constants.HEIGHT / 2 - 100,
                                          youLost.getWidth(),
                                          youLost.getHeight());
                gameEnded = true;
            }

            for (int i = 0; i < invaders.size(); i++) {
                synchronized (Util.lockOn()) {
                    Invader invader = invaders.get(i);
                    if (invader.wasHit()) {
                        invader.decrementLife();
                        if (invader.isDead()) {
                            invaders.remove(invader);
                            score += 5;
                            text.setText(Integer.toString(score));
                            CoordinatesCache.getInstance().getCoordinatesInUse().remove(invader.getCoordinates());
                        }
                    } else {
                        //either move or shoot
//                        boolean shouldShoot = random.nextBoolean();
                        boolean shouldShoot = Math.random() < 0.02;
                        if (shouldShoot) {
                            invader.shoot(graphicsContext);
                        } else {
                            invader.move();
                        }
                    }
                }
            }


            synchronized (Util.lockOn()) {
                if (this.defender.wasHit()) {
                    this.defender.decreaseLife();
                }
            }

            if (!gameEnded) {
                redraw();
            }

            previousTime = currentTime;
            double frameTime = (System.nanoTime() - currentTime) / 1_000_000_000.0;

            if (frameTime < targetDelta) {
                try {
                    Thread.sleep((long) ((targetDelta - frameTime) * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void generateElements() {
        for (int i = 0; i < Constants.NUMBER_OF_PLANES; i++) {
            invaders.add(InvaderFactory.generateElement(ImageType.PLANE));
        }

        for (int i = 0; i < Constants.NUMBER_OF_TANKS; i++) {
            invaders.add(InvaderFactory.generateElement(ImageType.TANK));
        }

        for (int i = 0; i < Constants.NUMBER_OF_HELICOPTERS; i++) {
            invaders.add(InvaderFactory.generateElement(ImageType.HELICOPTER));
        }

    }

    private void appearBoss() {
        invaders.add(InvaderFactory.generateElement(ImageType.ALIEN));
    }

    private void drawImage(Invader invader) {
        graphicsContext.drawImage(invader.getImage(),
                                  invader.getCoordinates().getX(),
                                  invader.getCoordinates().getY(),
                                  invader.getImage().getWidth(),
                                  invader.getImage().getHeight());
    }

    private void drawDefender(Defender defender) {
        if (!defender.isDead()) {
            graphicsContext.drawImage(defender.getImage(),
                                      defender.getCoordinates().getX(),
                                      defender.getCoordinates().getY(),
                                      defender.getImage().getWidth(),
                                      defender.getImage().getHeight());
            for (int life = 0; life < defender.getLife(); life++) {
                graphicsContext.drawImage(ImageLoader.getImage(ImageType.HEART),
                                          Constants.WIDTH - 100 - life * 40,
                                          10,
                                          ImageLoader.getImage(ImageType.HEART).getWidth(),
                                          ImageLoader.getImage(ImageType.HEART).getHeight()
                );
            }
        }


//        root.getChildren().add(text);
//        graphicsContext.drawIma
    }


    private void drawnBullet(DefenderBullet bullet) {
        //RECTANGULAR
        bullet.getCoordinates().setY(bullet.getCoordinates().getY() + bullet.getVelocity());
        graphicsContext.setFill(bullet.getColor());
        graphicsContext.fillRect(bullet.getCoordinates().getX(), bullet.getCoordinates().getY(), bullet.getWidth(), bullet.getHeight());
    }

    private void drawnBullet(TankBullet bullet) {
        bullet.getCoordinates().setY(bullet.getCoordinates().getY() + bullet.getVelocity());
        graphicsContext.setFill(bullet.getColor());
        graphicsContext.fillOval(bullet.getCoordinates().getX(), bullet.getCoordinates().getY(), bullet.getBulletDiameter(), bullet.getBulletDiameter());
    }

    private void drawnBullet(HelicopterBullet bullet) {
        bullet.getCoordinates().setY(bullet.getCoordinates().getY() + bullet.getVelocity());
        graphicsContext.setFill(bullet.getColor());
        graphicsContext.setFont(new Font("Arial Bold", bullet.getBulletSize()));
        graphicsContext.fillText(bullet.getBulletShape(), bullet.getCoordinates().getX(), bullet.getCoordinates().getY());
    }

    private void drawnBullet(PlaneBullet bullet) {
        bullet.getCoordinates().setY(bullet.getCoordinates().getY() + bullet.getVelocity());
        graphicsContext.setFill(bullet.getColor());
        graphicsContext.fillOval(bullet.getCoordinates().getX(), bullet.getCoordinates().getY(), bullet.getWidth(), bullet.getHeight());
    }

    private void drawBullet(Bullet bullet) {
        switch (bullet.getShape()) {
            case RECTANGULAR:
                drawnBullet((DefenderBullet) bullet);
                break;
            case OVAL:
                drawnBullet((PlaneBullet) bullet);
                break;
            case CHAR:
                drawnBullet((HelicopterBullet) bullet);
                break;
            case CIRCLE:
                drawnBullet((TankBullet) bullet);
                break;
            default:
                throw new IllegalArgumentException("Illegal bullet shape");
        }
    }


    private void redraw() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        invaders.forEach(this::drawImage);
        drawDefender(defender);

//		CoordinatesCache.getInstance().getEnemyBullets().stream().map(h->h.getClass().)

        CoordinatesCache.getInstance().getEnemyBullets().forEach(this::drawBullet);
        CoordinatesCache.getInstance().getDefenderBullets().forEach(this::drawBullet);


    }
}
