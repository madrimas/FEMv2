package model;

import java.io.IOException;

public class Node {

    private double x, y;//współrzędne
    private boolean status;//1-na krawędzi siatki, 0-wewnątrz siatki
    private double temp;

    Node(double x, double y) throws IOException {
        GlobalData globalData = GlobalData.getInstance();

        this.x = x;
        this.y = y;
        this.temp = globalData.getTempStart();

        this.status = this.x == 0.0 || this.x == globalData.getWidth();
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    boolean getStatus() {
        return status;
    }
}
