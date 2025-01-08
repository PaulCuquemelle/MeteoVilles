package com.example.meteovilles;

public class City {
    private String name;
    private String weatherInfo;
    private String weatherCode;

    // Constructeur
    public City(String name, String weatherInfo, String weatherCode) {
        this.name = name;
        this.weatherInfo = weatherInfo;
        this.weatherCode = weatherCode;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getWeatherInfo() {
        return weatherInfo;
    }

    public String getWeatherCode() {
        return weatherCode;
    }

    // Setters
    public void setWeatherInfo(String weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    public void setWeatherCode(String weatherCode) {
        this.weatherCode = weatherCode;
    }
}
