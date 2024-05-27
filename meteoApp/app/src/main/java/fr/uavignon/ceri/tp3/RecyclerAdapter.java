package fr.uavignon.ceri.tp3;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import fr.uavignon.ceri.tp3.data.City;

public class RecyclerAdapter extends RecyclerView.Adapter<fr.uavignon.ceri.tp3.RecyclerAdapter.ViewHolder> {

    private static final String TAG = fr.uavignon.ceri.tp3.RecyclerAdapter.class.getSimpleName();

    private List<City> cityList;
    private ListViewModel listViewModel;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.itemTitle.setText(cityList.get(i).getName());
        viewHolder.itemDetail.setText(cityList.get(i).getCountry());
        if (cityList.get(i).getTemperature() == null)
            viewHolder.itemTemp.setText("");
        else
            viewHolder.itemTemp.setText(Math.round(cityList.get(i).getTemperature())+" °C");
        if (cityList.get(i).getSmallIconUri() == null)
            viewHolder.itemIcon.setImageResource(0);
        else
            viewHolder.itemIcon.setImageDrawable(viewHolder.itemIcon.getResources().getDrawable(viewHolder.itemIcon.getResources().getIdentifier(cityList.get(i).getSmallIconUri(),
                    null, viewHolder.itemIcon.getContext().getPackageName())));
    }

    @Override
    public int getItemCount() {
        //return Book.books.length;
        return cityList == null ? 0 : cityList.size();
    }

    public void setCityList(List<City> cities) {
        cityList = cities;
        notifyDataSetChanged();
    }
    public void setListViewModel(ListViewModel viewModel) {
        listViewModel = viewModel;
    }
    private void deleteItem(long id) {
        if (listViewModel != null)
            listViewModel.deleteCity(id);
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle;
        TextView itemDetail;
        TextView itemTemp;
        ImageView itemIcon;

         ActionMode actionMode;
        long idSelectedLongClick;

        ViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemDetail = itemView.findViewById(R.id.item_detail);
            itemTemp = itemView.findViewById(R.id.item_temp);
            itemIcon = itemView.findViewById(R.id.item_image);

            ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

                // Called when the action mode is created; startActionMode() was called
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    // Inflate a menu resource providing context menu items
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.context_menu, menu);
                    return true;
                }

                // Called each time the action mode is shown. Always called after onCreateActionMode, but
                // may be called multiple times if the mode is invalidated.
                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false; // Return false if nothing is done
                }

                // Called when the user selects a contextual menu item
                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.menu_delete) {
                        RecyclerAdapter.this.deleteItem(idSelectedLongClick);
                        Snackbar.make(itemView, "Ville supprimée !", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    } else if (itemId == R.id.menu_update) {
                        ListFragmentDirections.ActionListFragmentToNewCityFragment action = ListFragmentDirections.actionListFragmentToNewCityFragment();
                        action.setCityNum(idSelectedLongClick);
                        Navigation.findNavController(itemView).navigate(action);
                        return true;
                    }
                    return false;
                }


                // Called when the user exits the action mode
                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    actionMode = null;
                }
            };

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Log.d(TAG,"position="+getAdapterPosition());
                    long id = RecyclerAdapter.this.cityList.get((int)getAdapterPosition()).getId();
                    Log.d(TAG,"id="+id);

                    ListFragmentDirections.ActionListFragmentToDetailFragment action = ListFragmentDirections.actionListFragmentToDetailFragment();
                    action.setCityNum(id);
                    Navigation.findNavController(v).navigate(action);

                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    idSelectedLongClick = RecyclerAdapter.this.cityList.get((int)getAdapterPosition()).getId();
                    if (actionMode != null) {
                        return false;
                    }
                    Context context = v.getContext();
                    // Start the CAB using the ActionMode.Callback defined above
                    actionMode = ((Activity)context).startActionMode(actionModeCallback);
                    v.setSelected(true);
                    return true;
                }
            });
        }




     }

}