package fr.uavignon.ceri.tp3;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import fr.uavignon.ceri.tp3.data.City;

public class DetailFragment extends Fragment {
    public static final String TAG = DetailFragment.class.getSimpleName();

    private DetailViewModel viewModel;
    private TextView textCityName, textCountry, textTemperature, textHumidity, textCloudiness, textWind, textLastUpdate;
    private ImageView imgWeather;
    private ProgressBar progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        DetailFragmentArgs args = DetailFragmentArgs.fromBundle(getArguments());
        long cityID = args.getCityNum();
        viewModel.setCity(cityID);

        getView().findViewById(R.id.buttonUpdate).setOnClickListener(v -> {
            City currentCity = viewModel.getCity().getValue();
            if (currentCity != null) {
                viewModel.loadWeatherCity(currentCity);
            }
        });

        listenerSetup();
        observerSetup();
    }

    private void listenerSetup() {
        textCityName = getView().findViewById(R.id.nameCity);
        textCountry = getView().findViewById(R.id.country);
        textTemperature = getView().findViewById(R.id.editTemperature);
        textHumidity = getView().findViewById(R.id.editHumidity);
        textCloudiness = getView().findViewById(R.id.editCloudiness);
        textWind = getView().findViewById(R.id.editWind);
        textLastUpdate = getView().findViewById(R.id.editLastUpdate);
        imgWeather = getView().findViewById(R.id.iconeWeather);
        progress = getView().findViewById(R.id.progress);

        getView().findViewById(R.id.buttonUpdate).setOnClickListener(v -> {
            City currentCity = viewModel.getCity().getValue();
            if (currentCity != null) {
                viewModel.loadWeatherCity(currentCity);
            }
        });

        getView().findViewById(R.id.buttonUpdate).setOnClickListener(v -> {
            viewModel.refreshWeatherForCity();
        });

        getView().findViewById(R.id.buttonBack).setOnClickListener(view -> {
            NavHostFragment.findNavController(DetailFragment.this)
                    .navigate(R.id.action_DetailFragment_to_ListFragment);
        });
    }

    private void observerSetup() {
        viewModel.getCity().observe(getViewLifecycleOwner(), this::updateUIWithCity);
        viewModel.getCity().observe(getViewLifecycleOwner(), city -> {
            if (city != null) {
                updateUIWithCity(city);
            }
        });

        viewModel.getCity().observe(getViewLifecycleOwner(), this::updateUIWithCity);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getWebServiceThrowable().observe(getViewLifecycleOwner(), throwable -> {
            if (throwable != null) {
                Snackbar.make(getView(), "Erreur: " + throwable.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void updateUIWithCity(City city) {
        if (city != null) {
            textCityName.setText(city.getName() != null ? city.getName() : "N/A");
            textCountry.setText(city.getCountry() != null ? city.getCountry() : "N/A");
            textTemperature.setText(city.getTemperature() != null ? Math.round(city.getTemperature()) + " °C" : "N/A");
            textHumidity.setText(city.getHumidity() != null ? city.getHumidity() + "%" : "N/A");
            textCloudiness.setText(city.getCloudiness() != null ? city.getCloudiness() + "%" : "N/A");
            textWind.setText(city.getWindSpeed() >= 0 ? city.getWindSpeed() + " km/h" : "N/A");
            textLastUpdate.setText(city.getLastUpdate() > 0 ? city.getStrLastUpdate() : "N/A");

            // Icon handling
            String iconUri = city.getIconUri();
            if (iconUri != null && !iconUri.isEmpty()) {
                int iconResourceId = getResources().getIdentifier(iconUri, "drawable", getContext().getPackageName());
                imgWeather.setImageResource(iconResourceId);
            } else {
                //imgWeather.setImageResource(R.drawable.ic_weather_placeholder)
            }
        }
    }
}
