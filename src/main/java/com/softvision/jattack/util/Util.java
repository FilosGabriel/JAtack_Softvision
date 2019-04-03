package com.softvision.jattack.util;

import com.softvision.jattack.coordinates.Coordinates;
import com.softvision.jattack.coordinates.CoordinatesCache;
import com.softvision.jattack.coordinates.RandomCoordinates;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Util {

    private static final Object lockObject = new Object();
    private static int tick = 50;
    public  static  final int MAX_FRAME=7;
    public static final  int MAX_TELEPORT=3;
    private static final Random random = new Random();
    public static final BooleanProperty spacePressed = new SimpleBooleanProperty(false);
    public static final BooleanProperty rightPressed = new SimpleBooleanProperty(false);
    public static final BooleanProperty leftPressed = new SimpleBooleanProperty(false);
    public static final BooleanProperty upPressed = new SimpleBooleanProperty(false);
    public static final BooleanProperty downPressed = new SimpleBooleanProperty(false);
    public static final BooleanBinding spaceAndLeftPressed = spacePressed.and(leftPressed);
    public static final BooleanBinding spaceAndRightPressed = spacePressed.and(rightPressed);
    public static final BooleanBinding spaceAndUpPressed = spacePressed.and(upPressed);
    public static final BooleanBinding spaceAndDownPressed = spacePressed.and(downPressed);
    public static final BooleanBinding spaceAndLeftUpPressed = spacePressed.and(leftPressed).and(upPressed);
    public static final BooleanBinding spaceAndLeftDownPressed = spacePressed.and(leftPressed).and(downPressed);
    public static final BooleanBinding spaceAndRightUpPressed = spacePressed.and(rightPressed).and(upPressed);
    public static final BooleanBinding spaceAndRightDownPressed = spacePressed.and(rightPressed).and(downPressed);
    public static final  BooleanBinding leftAndUpPressed=leftPressed.and(upPressed);
    public static final BooleanBinding leftAndDownPressed=leftPressed.and(downPressed);
    public static final BooleanBinding rightAndUpPressed=rightPressed.and(upPressed);
    public  static  final  BooleanBinding rightAndDownPressed=rightPressed.and(downPressed);

//    KeyCombination keyCombinationMac = new KeyCodeCombination(KeyCode.W, KeyCombination.
//            );



    public static void reset(){
        spacePressed.set(false);
        rightPressed.set(false);
        leftPressed.set(false);
        upPressed.set(false);
        downPressed.set(false);
    }


    public static Object lockOn() {
        return lockObject;
    }

    public static int getTick() {
        return tick;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public static Coordinates computeCoordinates() {
        Coordinates coordinates;
        do {
            coordinates = new RandomCoordinates(Constants.WIDTH, Constants.HEIGHT);
        } while (!coordinatesAreWithinBounds(coordinates) || coordinatesOverlapAnotherImage(coordinates, CoordinatesCache.getInstance().getCoordinatesInUse()));

        CoordinatesCache.getInstance().getCoordinatesInUse().add(coordinates);

        return coordinates;
    }

    public static boolean coordinatesOverlapAnotherImage(Coordinates coordinates, List<Coordinates> coordinatesInUse) {
        Optional<Coordinates> existingImageAtTheSpecifiedCoordinates = coordinatesInUse.stream().filter(c -> {
            int xAxisDifference = c.getX() - coordinates.getX();
            int yAxisDifference = c.getY() - coordinates.getY();

            return !((xAxisDifference == 0 && Math.abs(yAxisDifference) >= 100) ||
                    (yAxisDifference == 0 && Math.abs(xAxisDifference) >= 100) ||
                    (xAxisDifference >= 100 && Math.abs(yAxisDifference) >= 100) ||
                    (yAxisDifference >= 100 && Math.abs(xAxisDifference) >= 100) ||
                    (Math.abs(xAxisDifference) >= 100 && Math.abs(yAxisDifference) >= 100));
        }).findFirst();

        return existingImageAtTheSpecifiedCoordinates.isPresent();
    }

    public static boolean coordinatesAreWithinBounds(Coordinates coordinates) {
        //returns if the coordinates for the generated invader are inside the bounds determined by the points A(50,50), B(50, WIDTH - 150), C(50, HEIGHT - 450), D(WIDTH - 150, HEIGHT - 450)
        return coordinates.getX() >= 50 && coordinates.getX() <= Constants.WIDTH - 150
                && coordinates.getY() >= 50 && coordinates.getY() <= Constants.HEIGHT - 450;
    }
}
