package com.myapplication.maps;

import java.util.List;

public interface SearchLocationContract {
    interface View {
        void showSearching();

        void onSearchResult(List<SearchLocation> locations);

        void onError();
    }

    interface Presenter {
        void search(String location);

        void onDestroy();
    }

}

