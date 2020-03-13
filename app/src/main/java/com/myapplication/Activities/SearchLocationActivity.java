package com.myapplication.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myapplication.Presenters.SearchLocationPresenter;
import com.myapplication.R;
import com.myapplication.callbacks.SearchLocationListener;
import com.myapplication.maps.SearchLocation;
import com.myapplication.maps.SearchLocationAdapter;
import com.myapplication.maps.SearchLocationContract;
import com.myapplication.utils.Constants;

import java.util.List;

public class SearchLocationActivity extends AppCompatActivity implements SearchLocationContract.View, SearchLocationListener {
    private ImageButton back, clear;
    private EditText searchView;
    private ProgressBar progressBar;
    private TextView emptyState;
    private RecyclerView locationsRv;
    private SearchLocationPresenter searchLocationPresenter;
    private SearchLocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        back = findViewById(R.id.back);
        clear = findViewById(R.id.clear);
        searchView = findViewById(R.id.search);
        progressBar = findViewById(R.id.searching);
        emptyState = findViewById(R.id.locations_empty);
        locationsRv = findViewById(R.id.locations);

        // Initialize presenter
        searchLocationPresenter = new SearchLocationPresenter(this);
        searchView.requestFocus();

        locationsRv.setHasFixedSize(true);
        locationsRv.setLayoutManager(new LinearLayoutManager(this));
        locationsRv.setItemAnimator(new DefaultItemAnimator());
        adapter = new SearchLocationAdapter(this);
        locationsRv.setAdapter(adapter);


        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchView.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                String text = searchView.getText().toString();
                if (!text.isEmpty()) search(text);
                return true;
            }
            return false;
        });

        back.setOnClickListener(view -> super.onBackPressed());
        clear.setOnClickListener(view -> searchView.setText(""));
    }

    private void search(String location) {
        location = location.toLowerCase().contains(Constants.COUNTRY.toLowerCase()) ? location : location + " " + Constants.COUNTRY;
        searchLocationPresenter.search(location);
    }

    @Override
    public void showSearching() {
        setHidden(emptyState);
        if (adapter.getItemCount() == 0) {
            setVisible(progressBar);
        }
    }

    @Override
    public void onSearchResult(List<SearchLocation> locations) {
        setHidden(progressBar);

        if (locations.size() > 0) {
            adapter.clearLocations();
            setHidden(emptyState);
            adapter.addLocations(locations);
        } else {
            if (adapter.getItemCount() < 1) setVisible(emptyState);
        }
    }

    @Override
    public void onError() {
        adapter.clearLocations();
        setHidden(progressBar);
        setVisible(emptyState);
    }

    @Override
    public void onSearchLocationSelected(double lat, double lon) {
        Intent intent = new Intent();
        intent.putExtra(Constants.LOCATION_LAT, lat);
        intent.putExtra(Constants.LOCATION_LON, lon);
        intent.putExtra(Constants.LOCATION_SEARCH, searchView.getText().toString());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void setVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    private void setHidden(View view) {
        view.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchLocationPresenter.onDestroy();
    }
}
