package com.gb.carspot.models;

import java.io.Serializable;
import java.util.UUID;

public class Location implements Serializable
{
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Double lat;
    private Double lon;
    private String streetAddress;
    private String city;
    private String country;
    private boolean isCurrentLocation;

    public Location(UUID id, Double lat, Double lon, String streetAddress, String city, String country, boolean isCurrentLocation)
    {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.streetAddress = streetAddress;
        this.city = city;
        this.country = country;
        this.isCurrentLocation = isCurrentLocation;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public Double getLat()
    {
        return lat;
    }

    public void setLat(Double lat)
    {
        this.lat = lat;
    }

    public Double getLon()
    {
        return lon;
    }

    public void setLon(Double lon)
    {
        this.lon = lon;
    }

    public String getStreetAddress()
    {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress)
    {
        this.streetAddress = streetAddress;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public boolean isCurrentLocation()
    {
        return isCurrentLocation;
    }

    public void setCurrentLocation(boolean currentLocation)
    {
        isCurrentLocation = currentLocation;
    }

    @Override
    public String toString()
    {
        return
                id + "," +
                        lat + "," +
                        lon + "," +
                        streetAddress + "," +
                        city + "," +
                        country + "," +
                        isCurrentLocation + "," +
                        "";
    }
}
