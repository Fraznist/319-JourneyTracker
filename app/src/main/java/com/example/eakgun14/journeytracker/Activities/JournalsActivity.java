package com.example.eakgun14.journeytracker.Activities;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Arrays;

import com.example.eakgun14.journeytracker.Adapters.JournableAdapter;
import com.example.eakgun14.journeytracker.Adapters.LightManagerAdapter;
import com.example.eakgun14.journeytracker.DataTypes.Journal;
import com.example.eakgun14.journeytracker.Adapters.JournableAdapterListener;
import com.example.eakgun14.journeytracker.Dialogs.CreateJournalDialogFragment;
import com.example.eakgun14.journeytracker.Dialogs.NoticeDialogListener;
import com.example.eakgun14.journeytracker.LocalDatabase.AppDatabase;
import com.example.eakgun14.journeytracker.R;

public class JournalsActivity extends AppCompatActivity implements JournableAdapterListener, NoticeDialogListener, SensorEventListener {

    AppDatabase db;

    private LightManagerAdapter lightManager;

    private Journal[] journals;

    private RecyclerView recyclerView;
    private JournableAdapter adapter;
    private RecyclerView.LayoutManager layoutMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journals);

        ViewGroup thisLayout = findViewById(R.id.manage_journal_constraint_layout);
        lightManager = new LightManagerAdapter(thisLayout, this);

        android.support.v7.widget.Toolbar bar = findViewById(R.id.journal_toolbar);
        setSupportActionBar(bar);
        ActionBar actBar = getSupportActionBar();
        actBar.setDisplayHomeAsUpEnabled(true);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries().fallbackToDestructiveMigration()
                .build();

        Object[] temp = db.journalDao().getAllJournals().toArray();
        journals = Arrays.copyOf(temp, temp.length, Journal[].class);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutMgr = new LinearLayoutManager(this);
        adapter = new JournableAdapter(journals, this);
        recyclerView.setLayoutManager(layoutMgr);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addJournalButton = findViewById(R.id.add_journal_button);
        addJournalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                DialogFragment frag =  new CreateJournalDialogFragment();

                frag.show(fm, "fragment_create_journal");
            }
        });

        TextView allJourneyField = findViewById(R.id.all_journals_text);
        allJourneyField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAllJourniesActivity();
            }
        });

        ImageButton deleteButton = findViewById(R.id.journal_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.removeSelected();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        lightManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lightManager.resume();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT)
            lightManager.illuminationChanged(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Database is updated only when the activity stops
        updateJournalDatabase();
    }

    public void updateJournalDatabase() {
        // Using arrays rather than collections because they are simpler to cast
        Object[] temp = adapter.getJournablesToAdd().toArray();
        db.journalDao().insertAll(Arrays.copyOf(temp, temp.length, Journal[].class));

        temp = adapter.getJournablesToDelete().toArray();
        Journal[] jays = Arrays.copyOf(temp, temp.length, Journal[].class);

        db.journalDao().deleteAll(jays);
    }

    // Start JourniesActivity with a special intent, in order to display every single journey
    public void startAllJourniesActivity() {
        Intent intent = new Intent(JournalsActivity.this, JourniesActivity.class);
        intent.putExtra("Special", -1);
        startActivity(intent);
    }

    @Override
    public void onViewItemClicked(Object o) {
        // JournableAdapterListener callback
        // Start a JourniesActivity to display the contents of the selcted journal.
        Journal j = (Journal)o;
        Integer journalID = j.getId();
        String journalName = j.getName();
        Intent intent = new Intent(JournalsActivity.this, JourniesActivity.class);
        intent.putExtra("Journal", journalID);
        intent.putExtra("Name", journalName);
        startActivity(intent);
    }

    @Override
    public void onDialogClick(DialogFragment dialog) {
        // NoticeDialogListener callback
        // Create a new journal with the specified details
        // For now it exists only in the JournableAdapter instance,
        // it will be stored in the db when this activity stops.
        try {
            CreateJournalDialogFragment dial = (CreateJournalDialogFragment) dialog;

            String name = dial.getNameText().getText().toString();

            adapter.add(adapter.getItemCount(), new Journal(name, ""));
        }
        catch (ClassCastException e) {
            throw new ClassCastException(dialog.toString()
                    + " must extend CreateJournalDialogFragment");
        }
    }
}
