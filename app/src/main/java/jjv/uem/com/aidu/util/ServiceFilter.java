package jjv.uem.com.aidu.util;

/**
 * Created by javi_ on 07/06/2017.
 */

public class ServiceFilter {
    private int points, distance;
    private String category;

    public ServiceFilter(){}

    public ServiceFilter(int points, int distance, String category) {
        this.points = points;
        this.distance = distance;
        this.category = category;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
