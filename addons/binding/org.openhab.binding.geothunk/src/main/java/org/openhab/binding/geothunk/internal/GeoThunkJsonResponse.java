package org.openhab.binding.geothunk.internal;

public class GeoThunkJsonResponse {
    private int pm2; // 2.5 particle count
    private int pm1; // 1.0 particle count
    private int pm10; // 10.0 particle count
    private float l; // latitude
    private float n; // longitude
    private short t; // temperature, in celsius. Yes integer :(
    private short h; // humidity, percentage, 1-100
    private short u; // last reading, in minutes since sensor has been on
    private short a; // "a": 0

    public int getPm2_5() { return pm2; }
    public int getPm10() { return pm10; }
    public int getPm1() { return pm1; }
    public short getTemperatureCelsius() { return t; }
    public short getHumidityPercent() { return h; }
    public int getLastUpdate() { return u; }
}
