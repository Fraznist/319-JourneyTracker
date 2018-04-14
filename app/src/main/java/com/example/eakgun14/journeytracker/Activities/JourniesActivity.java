package com.example.eakgun14.journeytracker.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.eakgun14.journeytracker.Adapters.JournalAdapter;
import com.example.eakgun14.journeytracker.Adapters.JourniesAdapter;
import com.example.eakgun14.journeytracker.DataTypes.Journal;
import com.example.eakgun14.journeytracker.R;

import java.util.ArrayList;

public class JourniesActivity extends AppCompatActivity {

    private Journal journies;

    private RecyclerView recyclerView;
    private JourniesAdapter adapter;
    private RecyclerView.LayoutManager layoutMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journies);

        Intent intent = getIntent();
        journies = intent.getParcelableExtra("Journal");

        android.support.v7.widget.Toolbar bar = findViewById(R.id.journey_toolbar);
        setSupportActionBar(bar);
        ActionBar actBar = getSupportActionBar();
        actBar.setDisplayHomeAsUpEnabled(true);

        Log.d("debug", journies.toString());

        recyclerView = findViewById(R.id.journies_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutMgr = new LinearLayoutManager(this);
        adapter = new JourniesAdapter(journies.getJourneyList(), this.getApplicationContext());
        recyclerView.setLayoutManager(layoutMgr);
        recyclerView.setAdapter(adapter);

        ImageButton deleteButton = findViewById(R.id.journey_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.removeSelected();
            }
        });

        TextView title = findViewById(R.id.journey_title);
        title.setText(journies.getName());
    }
}
