package com.example.nathaly;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String RESTCOUNTRIES_API_BASE_URL = "https://restcountries.com/v3.1/";
    private static final String GEOCODING_API_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String API_KEY = "AIzaSyDEaPJ0Pr1XzIi6fHfTiDdYGhdSit7FM9c"; // Clave API de Google Maps

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // Inicializar vistas
        ImageView imageViewUser = findViewById(R.id.imageViewUser);
        ImageView imageViewFlag = findViewById(R.id.imageViewFlag);
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewEmail = findViewById(R.id.textViewEmail);
        TextView textViewAddress = findViewById(R.id.textViewAddress);
        TextView textViewPhone = findViewById(R.id.textViewPhone);
        TextView textViewCell = findViewById(R.id.textViewCell);
        TextView textViewCountry = findViewById(R.id.textViewCountry);
        mapView = findViewById(R.id.mapView);

        // Manejar posibles excepciones al obtener datos del Intent
        try {
            String name = getIntent().getStringExtra("name");
            String email = getIntent().getStringExtra("email");
            String address = getIntent().getStringExtra("address");
            String phone = getIntent().getStringExtra("phone");
            String cell = getIntent().getStringExtra("cell");
            String country = getIntent().getStringExtra("country");
            String photoUrl = getIntent().getStringExtra("photoUrl");

            // Configurar textos con formato HTML para negrita
            textViewName.setText(name != null ? name : "Nombre no disponible");

            Spanned emailText = Html.fromHtml("<b>Correo electrónico:</b> " + (email != null ? email : "No disponible"));
            textViewEmail.setText(emailText);

            Spanned addressText = Html.fromHtml("<b>Dirección:</b> " + (address != null ? address : "No disponible"));
            textViewAddress.setText(addressText);

            Spanned phoneText = Html.fromHtml("<b>Teléfono:</b> " + (phone != null ? phone : "No disponible"));
            textViewPhone.setText(phoneText);

            Spanned cellText = Html.fromHtml("<b>Celular:</b> " + (cell != null ? cell : "No disponible"));
            textViewCell.setText(cellText);

            Spanned countryText = Html.fromHtml("<b>País:</b> " + (country != null ? country : "No disponible"));
            textViewCountry.setText(countryText);

            // Cargar imagen del usuario
            if (photoUrl != null && !photoUrl.isEmpty()) {
                Picasso.get().load(photoUrl).into(imageViewUser);
            }

            // Obtener la bandera del país
            if (country != null && !country.isEmpty()) {
                fetchCountryFlag(country, imageViewFlag);
            } else {
                Toast.makeText(this, "El país no está disponible o es inválido", Toast.LENGTH_SHORT).show();
            }

            // Configurar el MapView
            Bundle mapViewBundle = null;
            if (savedInstanceState != null) {
                mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
            }
            mapView.onCreate(mapViewBundle);
            mapView.getMapAsync(this);

            // Obtener las coordenadas de la dirección
            if (address != null && !address.isEmpty() && !address.equals("No disponible")) {
                fetchCoordinates(address);
            } else {
                Toast.makeText(this, "La dirección no está disponible o es inválida", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("UserDetailsActivity", "Error al obtener datos del Intent", e);
            Toast.makeText(this, "Error al cargar los detalles del usuario", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    private void fetchCountryFlag(String country, ImageView imageViewFlag) {
        try {
            String encodedCountry = URLEncoder.encode(country.trim(), StandardCharsets.UTF_8.toString());

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RESTCOUNTRIES_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            CountryService countryService = retrofit.create(CountryService.class);

            retrofit2.Call<List<Country>> call = countryService.getCountryByName(encodedCountry);
            call.enqueue(new retrofit2.Callback<List<Country>>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<List<Country>> call, @NonNull retrofit2.Response<List<Country>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        String flagUrl = response.body().get(0).getFlagUrl();
                        if (flagUrl != null) {
                            Picasso.get().load(flagUrl).into(imageViewFlag);
                        } else {
                            Toast.makeText(UserDetailsActivity.this, "No se pudo obtener la bandera", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("UserDetailsActivity", "Error en la respuesta de la API de bandera");
                        Toast.makeText(UserDetailsActivity.this, "No se pudo obtener la bandera", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<List<Country>> call, @NonNull Throwable t) {
                    Log.e("UserDetailsActivity", "Error al realizar la solicitud", t);
                    Toast.makeText(UserDetailsActivity.this, "Error al realizar la solicitud", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("UserDetailsActivity", "Error al codificar la URL", e);
            Toast.makeText(this, "Error al procesar la solicitud", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchCoordinates(String address) {
        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
            String url = GEOCODING_API_BASE_URL + "?address=" + encodedAddress + "&key=" + API_KEY;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("UserDetailsActivity", "Error al obtener coordenadas", e);
                    runOnUiThread(() -> Toast.makeText(UserDetailsActivity.this, "Error de red al obtener la ubicación", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();
                        Log.d("UserDetailsActivity", "Respuesta de geocodificación: " + responseData);
                        try {
                            JsonObject jsonObject = new Gson().fromJson(responseData, JsonObject.class);

                            if (jsonObject.has("results") && jsonObject.getAsJsonArray("results").size() > 0) {
                                JsonObject location = jsonObject.getAsJsonArray("results")
                                        .get(0).getAsJsonObject()
                                        .getAsJsonObject("geometry")
                                        .getAsJsonObject("location");
                                double lat = location.get("lat").getAsDouble();
                                double lng = location.get("lng").getAsDouble();
                                runOnUiThread(() -> updateMap(lat, lng));
                            } else {
                                Log.e("UserDetailsActivity", "No se encontraron resultados de geocodificación");
                                runOnUiThread(() -> Toast.makeText(UserDetailsActivity.this, "No se encontraron resultados para la dirección proporcionada", Toast.LENGTH_SHORT).show());
                            }
                        } catch (Exception e) {
                            Log.e("UserDetailsActivity", "Error al analizar el JSON", e);
                            runOnUiThread(() -> Toast.makeText(UserDetailsActivity.this, "Error al procesar la ubicación: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Log.e("UserDetailsActivity", "Error en la respuesta de geocodificación: " + response.message());
                        runOnUiThread(() -> Toast.makeText(UserDetailsActivity.this, "No se pudo obtener la ubicación: " + response.message(), Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (Exception e) {
            Log.e("UserDetailsActivity", "Error al obtener coordenadas", e);
        }
    }

    private void updateMap(double lat, double lng) {
        if (googleMap != null) {
            LatLng userLocation = new LatLng(lat, lng);
            googleMap.addMarker(new MarkerOptions().position(userLocation).title("Ubicación del Usuario"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
        }
    }
}
