package com.iyzico.challenge.model.request;

public class BuyerRequest {
    private String id;
    private String name;
    private String surname;
    private String gsmNumber;
    private String email;
    private String identityNumber;
    private String lastLoginDate;
    private String registrationDate;
    private String registrationAddress;
    private String ip;
    private String city;
    private String country;
    private String zipCode;

    public BuyerRequest() {
    }

    public BuyerRequest(String id, String name, String surname, String gsmNumber,
                        String email, String identityNumber, String lastLoginDate,
                        String registrationDate, String registrationAddress,
                        String ip, String city, String country, String zipCode) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.gsmNumber = gsmNumber;
        this.email = email;
        this.identityNumber = identityNumber;
        this.lastLoginDate = lastLoginDate;
        this.registrationDate = registrationDate;
        this.registrationAddress = registrationAddress;
        this.ip = ip;
        this.city = city;
        this.country = country;
        this.zipCode = zipCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGsmNumber() {
        return gsmNumber;
    }

    public void setGsmNumber(String gsmNumber) {
        this.gsmNumber = gsmNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getRegistrationAddress() {
        return this.registrationAddress;
    }

    public void setRegistrationAddress(String registrationAddress) {
        this.registrationAddress = registrationAddress;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
