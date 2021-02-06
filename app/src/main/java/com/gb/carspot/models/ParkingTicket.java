package com.gb.carspot.models;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class ParkingTicket implements Serializable
{
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String email; 
    private String buildingCode;  // exactly 5 alphanumeric
    private int noOfHours;  // 1-hour or less, 4-hour, 12-hour, 24-hour
    private String licensePlate;  // min 2, max 8 alphanumeric
    private String hostSuite;  // min 2, max 5 alphanumeric
    private Location location;  // street address, lat and lng
    private Date date;  // system date

    public ParkingTicket(UUID id, String email, String buildingCode, int noOfHours, String licensePlate, String hostSuite, Location location, Date date)
    {
        this.id = id;
        this.email = email;
        this.buildingCode = buildingCode;
        this.noOfHours = noOfHours;
        this.licensePlate = licensePlate;
        this.hostSuite = hostSuite;
        this.location = location;
        this.date = date;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getBuildingCode()
    {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode)
    {
        this.buildingCode = buildingCode;
    }

    public int getNoOfHours()
    {
        return noOfHours;
    }

    public void setNoOfHours(int noOfHours)
    {
        this.noOfHours = noOfHours;
    }

    public String getLicensePlate()
    {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate)
    {
        this.licensePlate = licensePlate;
    }

    public String getHostSuite()
    {
        return hostSuite;
    }

    public void setHostSuite(String hostSuite)
    {
        this.hostSuite = hostSuite;
    }

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    @Override
    public String toString()
    {
        return
                id + "," +
                        email + "," +
                        buildingCode + "," +
                        noOfHours + "," +
                        licensePlate + "," +
                        hostSuite + "," +
                        location + "," +
                        date + "," +
                        "";
    }
}
