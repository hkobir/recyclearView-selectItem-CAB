package com.example.recyclearview_selectitem_cab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private List<String> items;
    private AdapterItem adapterItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.itemRV);
        tvEmpty = findViewById(R.id.empty_tv);
        items = new ArrayList<>();

        //initialize value;
        for (int i = 1; i <= 15; i++) {
            items.add("Item " + i);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterItem = new AdapterItem(items, this, tvEmpty, this);
        recyclerView.setAdapter(adapterItem);

    }
}