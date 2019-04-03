package com.softvision.jattack.coordinates;

public enum Direction {

    N(0,5),NE(5,5),E(5,0),SE(5,-5),S(0,-5),SW(-5,-5),W(-5,0),NW(-5,5);

    private int x;
    private int y;

    private Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
