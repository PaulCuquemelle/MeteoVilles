package com.example.meteovilles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import java.util.List;

public class CityAdapter extends ArrayAdapter<City> {
    private final LayoutInflater inflater;

    public CityAdapter(@NonNull Context context, @NonNull List<City> cities) {
        super(context, 0, cities);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_city, parent, false);
        }

        City city = getItem(position);

        TextView textViewCityName = convertView.findViewById(R.id.textViewCityName);
        TextView textViewWeatherInfo = convertView.findViewById(R.id.textViewWeatherInfo);
        ImageView imageViewWeatherIcon = convertView.findViewById(R.id.imageViewWeatherIcon);

        textViewCityName.setText(city.getName());
        textViewWeatherInfo.setText(city.getWeatherInfo());

        // Associer l'icône correspondante
        int iconResId = getWeatherIcon(city.getWeatherCode());
        imageViewWeatherIcon.setImageResource(iconResId);

        return convertView;
    }

    private int getWeatherIcon(String weatherCode) {
        switch (weatherCode) {
            case "01d":
                return R.drawable.sun; // Icône pour ciel dégagé
            case "02d":
                return R.drawable.default_icon; // Icône pour peu nuageux
            case "03d":
                return R.drawable.cloudy; // Icône pour pluie
            case "04d":
                return R.drawable.cloudy; // Icône pour pluie
            case "09d":
                return R.drawable.rain; // Icône pour pluie
            case "10d":
                return R.drawable.rain; // Icône pour pluie
            case "11d":
                return R.drawable.thunderstorm; // Icône pour pluie
            case "13d":
                return R.drawable.snow; // Icône pour pluie
            case "50d":
                return R.drawable.mist; // Icône pour pluie
            default:
                return R.drawable.default_icon; // Icône par défaut
        }
    }
}
