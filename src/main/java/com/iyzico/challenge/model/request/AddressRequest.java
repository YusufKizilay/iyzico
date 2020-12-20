package com.iyzico.challenge.model.request;

public class AddressRequest {
    private String contactName;
    private String city;
    private String country;
    private String address;
    private String zipCode;

    public AddressRequest() {
    }

    public AddressRequest(String contactName, String city, String country, String address, String zipCode) {
        this.contactName = contactName;
        this.city = city;
        this.country = country;
        this.address = address;
        this.zipCode = zipCode;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
