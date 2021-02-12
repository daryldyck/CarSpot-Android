//
//  Advanced Android - MADS4006
//  CarSpot
//
//  Group 7
//  Brian Domingo - 101330689
//  Daryl Dyck - 101338429
//

package com.gb.carspot.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private long phone;
    private String firstName;
    private String lastName;
    private List<String> licensePlates;

    public User()
    {
    }

    public User(String email, String password, long phone, String firstName, String lastName, List<String> licensePlates)
    {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.licensePlates = licensePlates;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public long getPhone()
    {
        return phone;
    }

    public void setPhone(long phone)
    {
        this.phone = phone;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public List<String> getLicensePlates()
    {
        if (licensePlates == null)
        {
            licensePlates = new ArrayList<String>();
        }
        return licensePlates;
    }

    public void setLicensePlates(List<String> licensePlates)
    {
        this.licensePlates = licensePlates;
    }

    @Override
    public String toString()
    {
        return
                email + "," +
                        password + "," +
                        phone + "," +
                        firstName + "," +
                        lastName + "," +
                        licensePlates + "," +
                        "";
    }
}
