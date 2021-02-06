package com.gb.carspot.models;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ParkingTicket implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String buildingCode;  // exactly 5 alphanumeric
    private int noOfHours;  // 1-hour or less, 4-hour, 12-hour, 24-hour
    private String licensePlate;  // min 2, max 8 alphanumeric
    private String hostSuite;  // min 2, max 5 alphanumeric
    private Location location;  // street address, lat and lng
    private Date date;  // system date
    private String imageUrl;

    public ParkingTicket(String buildingCode, int noOfHours, String licensePlate, String hostSuite, Location location, Date date, String imageUrl)
    {
        this.buildingCode = buildingCode;
        this.noOfHours = noOfHours;
        this.licensePlate = licensePlate;
        this.hostSuite = hostSuite;
        this.location = location;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    public static long getSerialVersionUID()
    {
        return serialVersionUID;
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

    public String getLength()
    {
        String length = "";
        switch (noOfHours)
        {
            case 1:
                length = "1 Hr.";
                break;
            case 4:
                length = "4 Hrs.";
                break;
            case 12:
                length = "12 Hrs.";
                break;
            case 24:
                length = "24 Hrs.";
                break;
        }
        return length;
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

    public String getDateString()
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
        return dateFormat.format(date).toString();
    }

    public String getStartString()
    {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        return dateFormat.format(date).toString();
    }

    public String getEndString()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, noOfHours);

        DateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString()
    {
        return
                buildingCode + "," +
                        noOfHours + "," +
                        licensePlate + "," +
                        hostSuite + "," +
                        location + "," +
                        date + "," +
                        imageUrl + "," +
                        "";
    }
}
