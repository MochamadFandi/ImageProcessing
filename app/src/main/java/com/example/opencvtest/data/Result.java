package com.example.opencvtest.data;

public class Result {
    private String distance;
    private String angle;
    private String entropy;
    private String energy;
    private String contrast;
    private String correlation;
    private String Homogeneity;

    public Result(String distance, String angle, String entropy, String energy, String contrast, String correlation, String homogeneity) {
        this.distance = distance;
        this.angle = angle;
        this.entropy = entropy;
        this.energy = energy;
        this.contrast = contrast;
        this.correlation = correlation;
        Homogeneity = homogeneity;
    }

    public String getDistance() {
        return distance;
    }

    public String getAngle() {
        return angle;
    }

    public String getEntropy() {
        return entropy;
    }

    public String getEnergy() {
        return energy;
    }

    public String getContrast() {
        return contrast;
    }

    public String getCorrelation() {
        return correlation;
    }

    public String getHomogeneity() {
        return Homogeneity;
    }
}
