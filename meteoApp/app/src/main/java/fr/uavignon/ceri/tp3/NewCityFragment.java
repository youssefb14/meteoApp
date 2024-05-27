package fr.uavignon.ceri.tp3;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import fr.uavignon.ceri.tp3.data.City;

public class NewCityFragment extends Fragment {


    public static final String TAG = NewCityFragment.class.getSimpleName();

    private NewCityViewModel viewModel;
    private EditText editNewName, editNewCountry;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(NewCityViewModel.class);

        // Get selected city
        NewCityFragmentArgs args = NewCityFragmentArgs.fromBundle(getArguments());
        long cityID = args.getCityNum();
        Log.d(TAG, "selected id=" + cityID);
        viewModel.setCity(cityID);

        listenerSetup();
        observerSetup();

    }


    private void listenerSetup() {
        editNewName = getView().findViewById(R.id.editNewName);
        editNewCountry = getView().findViewById(R.id.editNewCountry);

        getView().findViewById(R.id.buttonInsert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editNewName.getText().toString().isEmpty() || editNewCountry.getText().toString().isEmpty())
                    Snackbar.make(view, "Les champs de la ville et du pays doivent être remplis",
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                else
                    if (viewModel.insertOrUpdateCity(new City(editNewName.getText().toString(), editNewCountry.getText().toString())) == -1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Une ville avec le même nom et le même pays existe déjà dans la base de données")
                                .setTitle("Erreur à l'ajout");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Snackbar.make(view, "La ville a été ajoutée à la base de données",
                                Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
            }
        });

        getView().findViewById(R.id.buttonBack2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(fr.uavignon.ceri.tp3.NewCityFragment.this)
                        .navigate(R.id.action_NewCityFragment_to_ListFragment);
            }
        });
    }

    private void observerSetup() {
        viewModel.getCity().observe(getViewLifecycleOwner(),
                city -> {
                    if (city != null) {
                        Log.d(TAG, "observing city view");
                        editNewName.setText(city.getName());
                        editNewCountry.setText(city.getCountry());
                    }
                });

    }
}
