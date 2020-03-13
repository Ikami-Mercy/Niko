package com.myapplication.data;

import com.google.gson.annotations.SerializedName;

    public class Address {
        @SerializedName("bus_station")
        private String busStation;

        @SerializedName("road")
        private String road;

        @SerializedName("suburb")
        private String suburb;

        @SerializedName("town")
        private String town;

        @SerializedName("state")
        private String state;

        @SerializedName("country")
        private String country;

        @SerializedName("country_code")
        private String countryCode;

        @SerializedName("address29")
        private String address29;

        @SerializedName("house_number")
        private String houseNumber;

        @SerializedName("commercial")
        private String commercial;

        public Address(String busStation, String road, String suburb, String town, String state, String country, String countryCode, String address29, String houseNumber, String commercial) {
            this.busStation = busStation;
            this.road = road;
            this.suburb = suburb;
            this.town = town;
            this.state = state;
            this.country = country;
            this.countryCode = countryCode;
            this.address29 = address29;
            this.houseNumber = houseNumber;
            this.commercial = commercial;
        }

        /*public boolean isValidCountry() {
            return getCountry().toLowerCase().replaceAll(" ", "").equals(Constants.COUNTRY.toLowerCase());
        }*/

        public String getSummary() {
            String summary;

            if (getRoad() == null || getRoad().isEmpty()) {
                summary = (getSuburb() == null || getSuburb().isEmpty()) ? getState() : getSuburb() + ", " + getState();
            } else {
                summary = getRoad() + ", " + getSuburb() + ", " + getState();
            }

            return summary;
        }

        public String getAddress29() {
            return address29;
        }

        public void setAddress29(String address29) {
            this.address29 = address29;
        }

        public String getHouseNumber() {
            return houseNumber;
        }

        public void setHouseNumber(String houseNumber) {
            this.houseNumber = houseNumber;
        }

        public String getCommercial() {
            return commercial;
        }

        public void setCommercial(String commercial) {
            this.commercial = commercial;
        }

        public String getBusStation() {
            return busStation;
        }

        public void setBusStation(String busStation) {
            this.busStation = busStation;
        }

        public String getRoad() {
            return road;
        }

        public void setRoad(String road) {
            this.road = road;
        }

        public String getSuburb() {
            return suburb;
        }

        public void setSuburb(String suburb) {
            this.suburb = suburb;
        }

        public String getTown() {
            return town;
        }

        public void setTown(String town) {
            this.town = town;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }
    }

