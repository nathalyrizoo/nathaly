package com.example.nathaly;
public class User {
    private Name name;
    private String email;
    private Location location;
    private Picture picture;
    private String phone;
    private String cell;

    // Métodos para obtener los datos de nombre, email, país y foto
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

    public String getPhone() {
        return phone != null ? phone : "No disponible";
    }

    public String getCell() {
        return cell != null ? cell : "No disponible";
    }

    // Método para obtener la dirección completa
    public String getFullAddress() {
        if (location != null && location.street != null) {
            String address = location.street.number + " " + location.street.name + ", " +
                    location.city + ", " + location.state + ", " + location.country;
            return address;
        }
        return "No disponible";
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

        class Street {
            int number;
            String name;
        }
    }

    class Picture {
        String large;
    }
}
