package com.myapplication.maps;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class SearchLocationAdapter  extends RecyclerView.Adapter<SearchLocationAdapter.SearchLocationHolder> {
    private List<SearchLocation> locations;
    private SearchLocationListener locationListener;

    public SearchLocationAdapter(SearchLocationListener locationListener) {
        this.locationListener = locationListener;
        this.locations = new ArrayList<>();
    }

    /**
     * Function to add list of searched location result
     * @param locations - locations to add
     */
    public void addLocations(List<SearchLocation> locations) {
        this.locations.addAll(locations);
        notifyDataSetChanged();
    }

    /**
     * Function to clear list of searched locations
     */
    public void clearLocations() {
        this.locations.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchLocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_location, parent, false);
        return new SearchLocationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchLocationHolder holder, int position) {
        holder.bind(locations.get(position), locationListener);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    class SearchLocationHolder extends RecyclerView.ViewHolder {
        private LinearLayout root;
        private TextView description, details;

        public SearchLocationHolder(@NonNull View itemView) {
            super(itemView);
            this.root = itemView.findViewById(R.id.search_location);
            this.description = itemView.findViewById(R.id.location_description);
            this.details = itemView.findViewById(R.id.location_details);
        }

        public void bind(SearchLocation location, SearchLocationListener listener) {
            description.setText(location.getDisplayName());
            details.setText(location.getAddress().getSummary());

            double lat = Double.valueOf(location.getLat());
            double lon = Double.valueOf(location.getLon());
            root.setOnClickListener(view -> listener.onSearchLocationSelected(lat, lon));
        }
    }

}
