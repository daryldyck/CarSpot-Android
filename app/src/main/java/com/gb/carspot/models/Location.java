package com.gb.carspot.models;

import java.io.Serializable;
import java.util.UUID;

public class Location implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Double lat;
    private Double lon;
    private String streetAddress;
    private String city;
    private String country;
    private boolean isCurrentLocation;

    public Location()
    {
        this.lat = 0.0;
        this.lon = 0.0;
        this.streetAddress = "";
        this.city = "";
        this.country = "";
        this.isCurrentLocation = false;
    }

    public Location(Double lat, Double lon, String streetAddress, String city, String country, boolean isCurrentLocation)
    {
        this.lat = lat;
        this.lon = lon;
        setStreetAddress(streetAddress);
        setCity(city);
        setCountry(country);
        this.isCurrentLocation = isCurrentLocation;
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
        if (streetAddress == null || streetAddress.contains("null"))
        {
            this.streetAddress = "";
        }
        else
        {
            this.streetAddress = streetAddress;
        }
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        if (city == null || city.contains("null"))
        {
            this.city = "";
        }
        else
        {
            this.city = city;
        }
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        if (country == null || country.contains("null"))
        {
            this.country = "";
        }
        else
        {
            this.country = country;
        }
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
                lat + "," +
                        lon + "," +
                        streetAddress + "," +
                        city + "," +
                        country + "," +
                        isCurrentLocation + "," +
                        "";
    }
}
