package com.myapplication.maps;
import com.myapplication.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SearchLocationPresenter implements SearchLocationContract.Presenter {
    private SearchLocationContract.View searchLocationView;
    private ApiService apiService;

    public SearchLocationPresenter(SearchLocationContract.View searchLocationView) {
        this.searchLocationView = searchLocationView;
        this.apiService = ApiClient.getMapsClient();
    }

    @Override
    public void search(String location) {
        searchLocationView.showSearching();

        apiService.searchLocation(Constants.MAPS_REQUEST_FORMAT, location, Constants.MAPS_REQUST_ADDRESS_DETAILS)
                .enqueue(new Callback<List<SearchLocation>>() {
                    @Override
                    public void onResponse(Call<List<SearchLocation>> call, Response<List<SearchLocation>> response) {
                        if (searchLocationView != null) {
                            if (response.body() != null && response.body().size() > 0) {
                                // Filter locations to get only Kenya
                                List<SearchLocation> filteredLocations = new ArrayList<>();
                                for (SearchLocation l : response.body()) {
                                    if (l.getAddress().isValidCountry()) filteredLocations.add(l);
                                }

                                searchLocationView.onSearchResult(filteredLocations);
                            } else {
                                searchLocationView.onError();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SearchLocation>> call, Throwable t) {
                        Timber.e("Error searching for %s: %s", location, t.getLocalizedMessage());
                        if (searchLocationView != null) searchLocationView.onError();
                    }
                });
    }

    @Override
    public void onDestroy() {
        this.searchLocationView = null;
    }
}
