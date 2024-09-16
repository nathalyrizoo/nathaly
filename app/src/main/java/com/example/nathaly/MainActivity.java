package com.example.nathaly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configuración de Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://randomuser.me/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Llamada a la API
        Call<UserResponse> call = apiService.getUsers();
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> userList = response.body().getResults();
                    if (userList != null && !userList.isEmpty()) {
                        userAdapter = new UserAdapter(userList);
                        recyclerView.setAdapter(userAdapter);
                    } else {
                        Log.e("MainActivity", "No se encontraron usuarios en la respuesta");
                    }
                } else {
                    Log.e("MainActivity", "Error en la respuesta: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("MainActivity", "Error al realizar la solicitud", t);
            }
        });
    }

    // Adaptador para el RecyclerView
    public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

        private List<User> userList;

        public UserAdapter(List<User> userList) {
            this.userList = userList;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = userList.get(position);
            holder.textViewName.setText(user.getName());
            holder.textViewEmail.setText(user.getEmail());
            holder.textViewCountry.setText(user.getCountry());
            Picasso.get().load(user.getPhotoUrl()).into(holder.imageViewUser);

            // Acción al hacer clic en un usuario
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, UserDetailsActivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("email", user.getEmail());
                intent.putExtra("address", user.getAddress()); // Verifica que este dato esté disponible y válido
                intent.putExtra("phone", user.getPhone());
                intent.putExtra("cell", user.getCell());
                intent.putExtra("country", user.getCountry());
                intent.putExtra("photoUrl", user.getPhotoUrl());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        class UserViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName, textViewEmail, textViewCountry;
            ImageView imageViewUser;

            UserViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textViewName);
                textViewEmail = itemView.findViewById(R.id.textViewEmail);
                textViewCountry = itemView.findViewById(R.id.textViewCountry);
                imageViewUser = itemView.findViewById(R.id.imageViewUser);
            }
        }
    }

    interface ApiService {
        @GET("api/?results=20")
        Call<UserResponse> getUsers();
    }

    public class UserResponse {
        private List<User> results;

        public List<User> getResults() {
            return results;
        }
    }

    public class User {
        private Name name;
        private String email;
        private Location location;
        private Picture picture;
        private String address;
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

        // Métodos adicionales para obtener dirección, teléfono y celular
        public String getAddress() {
            return address != null ? address : "No disponible";
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
            String country;
        }

        class Picture {
            String large;
        }
    }
}
