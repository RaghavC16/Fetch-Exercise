package com.example.fetch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;

    private SectionedAdapter adapter;
    private List<ListItem> masterItemList = new ArrayList<>(); // all items from server
    private List<SectionRow> currentRows = new ArrayList<>();  // data displayed in the adapter

    // Data URL
    private static final String FETCH_URL =
            "https://fetch-hiring.s3.amazonaws.com/hiring.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search for item number");
        recyclerView = findViewById(R.id.recyclerView);

        adapter = new SectionedAdapter(currentRows);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Listen for search input
        setupSearch();

        // Fetch data from the server
        fetchData();
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAndGroup(newText);
                return true;
            }
        });
    }

    private void fetchData() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(FETCH_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    return;
                }
                String jsonResponse = response.body().string();
                parseData(jsonResponse);
            }
        });
    }

    private void parseData(String jsonResponse) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<ListItem>>(){}.getType();
        List<ListItem> allItems = gson.fromJson(jsonResponse, listType);

        List<ListItem> filtered = new ArrayList<>();
        for (ListItem item : allItems) {
            if (item.getName() != null && !item.getName().trim().isEmpty()) {
                filtered.add(item);
            }
        }

        Collections.sort(filtered, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                int c1 = Integer.compare(o1.getListId(), o2.getListId());
                if (c1 != 0) {
                    return c1;
                }
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        masterItemList.clear();
        masterItemList.addAll(filtered);

        runOnUiThread(() -> filterAndGroup(""));
    }

    /**
     * Groups the items by listId, applying an optional name filter.
     */
    private void filterAndGroup(String query) {
        String lowerQuery = query.toLowerCase().trim();

        List<ListItem> tempFiltered = new ArrayList<>();
        for (ListItem item : masterItemList) {

            String digitsOnly = item.getName().replaceAll("\\D+", "");

            if (digitsOnly.contains(lowerQuery)) {
                tempFiltered.add(item);
            }
        }


        List<SectionRow> newRows = new ArrayList<>();

        int currentListId = -1;
        for (ListItem item : tempFiltered) {
            if (item.getListId() != currentListId) {
                currentListId = item.getListId();

                newRows.add(new SectionRow("List ID: " + currentListId));
            }

            newRows.add(new SectionRow(item));
        }


        currentRows.clear();
        currentRows.addAll(newRows);
        adapter.notifyDataSetChanged();
    }
}