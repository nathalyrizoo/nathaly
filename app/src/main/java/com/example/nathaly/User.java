package com.example.nathaly;
public class User {
    private Name name;
    private String email;
    private Location location;
    private Picture picture;
    private String address; // Asegúrate de que esta variable exista
    private String phone;
    private String cell;

    public String getName() {
        return name.first + " " + name.last;
    }

    public String getEmail() {
        return email;
    }

    public String getCountry() {
        return location.country;
    }

    public String getPhotoUrl() {
        return picture.large;
    }

    // Método para obtener la dirección
    public String getAddress() {
        return location.street.name + " " + location.street.number + ", " + location.city + ", " + location.state + ", " + location.postcode;
    }

    public String getPhone() {
        return phone != null ? phone : "No disponible";
    }

    public String getCell() {
        return cell != null ? cell : "No disponible";
    }

    class Name {
        String first;
        String last;
    }

    class Location {
        Street street;
        String city;
        String state;
        String country;
        String postcode;

        class Street {
            String name;
            String number;
        }
    }

    class Picture {
        String large;
    }
}
