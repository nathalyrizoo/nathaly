package com.example.nathaly;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CountryService {
    // Método para obtener la información del país por nombre
    @GET("name/{country}")
    Call<List<Country>> getCountryByName(@Path("country") String country);
}