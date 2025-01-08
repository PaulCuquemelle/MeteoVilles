package com.example.meteovilles;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import java.io.*;
import java.util.ArrayList;
import okhttp3.*;

public class MainActivity extends AppCompatActivity {
    private ArrayList<City> cities = new ArrayList<>();
    private CityAdapter cityAdapter;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listViewCities = findViewById(R.id.listViewCities);
        EditText editTextNewCity = findViewById(R.id.editTextNewCity);
        Button buttonAddCity = findViewById(R.id.buttonAddCity);
        Button buttonRefresh = findViewById(R.id.buttonRefresh);

        file = new File(getFilesDir(), "cities.txt");

        // Charger les villes depuis le fichier
        loadCitiesFromFile();

        // Configurer l'adaptateur personnalisé
        cityAdapter = new CityAdapter(this, cities);
        listViewCities.setAdapter(cityAdapter);

        // Ajouter une ville
        buttonAddCity.setOnClickListener(v -> {
            String newCityName = editTextNewCity.getText().toString().trim();
            if (!newCityName.isEmpty() && !cityExists(newCityName)) {
                cities.add(new City(newCityName, "Chargement...", "default_icon"));
                cityAdapter.notifyDataSetChanged();
                saveCitiesToFile();
                editTextNewCity.setText("");
                fetchWeatherForCity(newCityName);
            } else {
                Toast.makeText(this, "Ville déjà existante ou nom invalide", Toast.LENGTH_SHORT).show();
            }
        });

        // Supprimer une ville via un clic long
        listViewCities.setOnItemLongClickListener((parent, view, position, id) -> {
            City cityToRemove = cities.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Supprimer la ville")
                    .setMessage("Voulez-vous vraiment supprimer " + cityToRemove.getName() + " ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        cities.remove(position);
                        cityAdapter.notifyDataSetChanged();
                        saveCitiesToFile();
                    })
                    .setNegativeButton("Non", null)
                    .show();
            return true;
        });

        // Raffraîchir la météo pour toutes les villes
        buttonRefresh.setOnClickListener(v -> refreshWeatherForCities());
    }

    private void loadCitiesFromFile() {
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    cities.add(new City(line, "Chargement...", "default_icon"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveCitiesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (City city : cities) {
                writer.write(city.getName());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean cityExists(String cityName) {
        for (City city : cities) {
            if (city.getName().equalsIgnoreCase(cityName)) {
                return true;
            }
        }
        return false;
    }

    private void fetchWeatherForCity(String cityName) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&units=metric&lang=fr&appid=7f98d7017e8ba6adf60ecb72eeb21ab9";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HTTP_ERROR", e.getMessage());
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Erreur réseau pour " + cityName, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        double temp = json.getJSONObject("main").getDouble("temp");
                        String description = json.getJSONArray("weather").getJSONObject(0).getString("description");
                        String iconCode = json.getJSONArray("weather").getJSONObject(0).getString("icon");

                        // Mettre à jour la ville dans la liste
                        runOnUiThread(() -> {
                            for (City city : cities) {
                                if (city.getName().equalsIgnoreCase(cityName)) {
                                    city.setWeatherInfo(temp + "°C, " + description);
                                    city.setWeatherCode(iconCode);
                                    cityAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.e("JSON_ERROR", e.getMessage());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Erreur pour " + cityName, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void refreshWeatherForCities() {
        for (City city : cities) {
            fetchWeatherForCity(city.getName());
        }
    }

}
