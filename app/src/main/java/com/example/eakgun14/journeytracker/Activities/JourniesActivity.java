package com.example.eakgun14.journeytracker.Activities;

import android.arch.persistence.room.Room;
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
import com.example.eakgun14.journeytracker.DataTypes.Journey;
import com.example.eakgun14.journeytracker.LocalDatabase.AppDatabase;
import com.example.eakgun14.journeytracker.R;

import java.util.ArrayList;
import java.util.List;

public class JourniesActivity extends AppCompatActivity {

    AppDatabase db;

    List<Journey> journies;
    int journalID;
    String journalName;

    private RecyclerView recyclerView;
    private JourniesAdapter adapter;
    private RecyclerView.LayoutManager layoutMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journies);

        Intent intent = getIntent();

        if (intent.getIntExtra("Special", 0) == -1) {
            journalName = "All Journeys";
            journalID = 0;
        }
        else {
            journalID = intent.getIntExtra("Journal", 0);
            journalName = intent.getStringExtra("Name");
        }

        android.support.v7.widget.Toolbar bar = findViewById(R.id.journey_toolbar);
        setSupportActionBar(bar);
        ActionBar actBar = getSupportActionBar();
        actBar.setDisplayHomeAsUpEnabled(true);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()
                .build();

        if (journalID == 0)
            journies = db.journeyDao().getAllJourneys();
        else
            journies = db.journeyDao().getAllJourneysInJournal(journalID);

        recyclerView = findViewById(R.id.journies_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutMgr = new LinearLayoutManager(this);
        adapter = new JourniesAdapter(journies, this.getApplicationContext(), db);
        recyclerView.setLayoutManager(layoutMgr);
        recyclerView.setAdapter(adapter);

        ImageButton deleteButton = findViewById(R.id.journey_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.removeSelected();
            }
        });

        ImageButton viewButton = findViewById(R.id.journey_view_button);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.add(adapter.getItemCount(), new Journey("perkele", "vwelbvle", journalID));
            }
        });

        TextView title = findViewById(R.id.journey_title);
        title.setText(journalName);
    }
}
