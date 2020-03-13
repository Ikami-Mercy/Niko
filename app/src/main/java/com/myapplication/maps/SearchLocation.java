package com.myapplication.maps;

import com.google.gson.annotations.SerializedName;
import com.myapplication.data.Address;

public class SearchLocation {
    @SerializedName("place_id")
    private long placeId;

    @SerializedName("licence")
    private String licence;

    @SerializedName("osm_type")
    private String osmType;

    @SerializedName("osm_id")
    private long osId;

    @SerializedName("lat")
    private String lat;

    @SerializedName("lon")
    private String lon;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("class")
    private String locationClass;

    @SerializedName("type")
    private String type;

    @SerializedName("importance")
    private double importance;

    @SerializedName("icon")
    private String icon;

    @SerializedName("address")
    private Address address;

    public SearchLocation(long placeId, String licence, String osmType, long osId, String lat, String lon, String displayName, String locationClass, String type, double importance, String icon, Address address) {
        this.placeId = placeId;
        this.licence = licence;
        this.osmType = osmType;
        this.osId = osId;
        this.lat = lat;
        this.lon = lon;
        this.displayName = displayName;
        this.locationClass = locationClass;
        this.type = type;
        this.importance = importance;
        this.icon = icon;
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Address getAddress() {
        return address;
    }
}
