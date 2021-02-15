package com.gb.carspot.models;

import java.io.Serializable;

public class LicensePlate implements Serializable {
    private String plateNumber;

    public LicensePlate() {
    }

    public LicensePlate(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
}
