package org.nagarro.com.phonebook.model;

import java.util.Objects;

public class City {

    private String name;
    private String zipcode;

    public City(){}

    public City(String name, String zipcode) {
        this.name = name;
        this.zipcode = zipcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Objects.equals(name, city.name) && Objects.equals(zipcode, city.zipcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, zipcode);
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", zipcode='" + zipcode + '\'' +
                '}';
    }
}
