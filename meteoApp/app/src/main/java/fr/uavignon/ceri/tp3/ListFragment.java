package fr.uavignon.ceri.tp3;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import fr.uavignon.ceri.tp3.data.City;
import fr.uavignon.ceri.tp3.data.WeatherRepository;

public class ListFragment extends Fragment {

    public static final String TAG = ListFragment.class.getSimpleName();

    private ListViewModel viewModel;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerAdapter adapter;
    private ProgressBar progress;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ListViewModel.class);
        listenerSetup();
        observerSetup();

    }

    private void listenerSetup() {
        recyclerView = getView().findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setListViewModel(viewModel);
        progress = getView().findViewById(R.id.progressList);

        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ListFragmentDirections.ActionListFragmentToNewCityFragment action = ListFragmentDirections.actionListFragmentToNewCityFragment();
                action.setCityNum(City.ADD_ID);
                Navigation.findNavController(view).navigate(action);

            }
        });
    }

    private void observerSetup() {
        viewModel.getAllCities().observe(getViewLifecycleOwner(),
                cities -> adapter.setCityList(cities));

    }

}