package com.myapplication.data;

import android.location.Address;
import com.google.gson.annotations.SerializedName;

import java.util.List;


    public class LocationResponse {
        @SerializedName("place_id")
        private int id;

        @SerializedName("licence")
        private String licence;

        @SerializedName("osm_type")
        private String osmType;

        @SerializedName("osm_id")
        private String osmId;

        @SerializedName("lat")
        private String lat;

        @SerializedName("lon")
        private String lon;

        @SerializedName("display_name")
        private String displayName;

        @SerializedName("address")
        private Address address;

        @SerializedName("boundingbox")
        private List<String> boundingBox;
        private String error;

        public LocationResponse(int id, String licence, String osmType, String osmId, String lat, String lon, String displayName, Address address, List<String> boundingBox, String error) {
            this.id = id;
            this.licence = licence;
            this.osmType = osmType;
            this.osmId = osmId;
            this.lat = lat;
            this.lon = lon;
            this.displayName = displayName;
            this.address = address;
            this.boundingBox = boundingBox;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLicence() {
            return licence;
        }

        public void setLicence(String licence) {
            this.licence = licence;
        }

        public String getOsmType() {
            return osmType;
        }

        public void setOsmType(String osmType) {
            this.osmType = osmType;
        }

        public String getOsmId() {
            return osmId;
        }

        public void setOsmId(String osmId) {
            this.osmId = osmId;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public Address getAddress() {
            return address;
        }

        public String getError() {
            return error;}

        public void setAddress(Address address) {
            this.address = address;
        }

        public List<String> getBoundingBox() {
            return boundingBox;
        }

        public void setBoundingBox(List<String> boundingBox) {
            this.boundingBox = boundingBox;
        }
    }

