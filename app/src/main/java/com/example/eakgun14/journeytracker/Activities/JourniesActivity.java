package com.example.eakgun14.journeytracker.Activities;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.eakgun14.journeytracker.Adapters.JournableAdapter;
import com.example.eakgun14.journeytracker.Adapters.LightManagerAdapter;
import com.example.eakgun14.journeytracker.DataTypes.Journable;
import com.example.eakgun14.journeytracker.DataTypes.Journey;
import com.example.eakgun14.journeytracker.Adapters.JournableAdapterListener;
import com.example.eakgun14.journeytracker.Dialogs.NoticeDialogListener;
import com.example.eakgun14.journeytracker.Dialogs.ViewJourneyDialogFragment;
import com.example.eakgun14.journeytracker.LocalDatabase.AppDatabase;
import com.example.eakgun14.journeytracker.R;

import java.util.Arrays;
import java.util.List;

public class JourniesActivity extends AppCompatActivity implements JournableAdapterListener, SensorEventListener, NoticeDialogListener {

    AppDatabase db;

    private LightManagerAdapter lightManager;

    Journable[] journies;
    int journalID;
    String journalName;

    private RecyclerView recyclerView;
    private JournableAdapter adapter;
    private RecyclerView.LayoutManager layoutMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journies);

        ViewGroup thisLayout = findViewById(R.id.manage_journeys_constraint_layout);
        lightManager = new LightManagerAdapter(thisLayout, this);

        Intent intent = getIntent();

        // Special case requires to display all journies, regardless of journal
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

        // Special case, get all journeys to display
        if (journalID == 0) {
            Object[] temp = db.journeyDao().getAllJourneys().toArray();
            journies = Arrays.copyOf(temp, temp.length, Journey[].class);
        }
        // get only the journies that are under the specified journal by the intent.
        else {
            Object[] temp = db.journeyDao().getAllJourneysInJournal(journalID).toArray();
            journies = Arrays.copyOf(temp, temp.length, Journey[].class);
        }


        recyclerView = findViewById(R.id.journies_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutMgr = new LinearLayoutManager(this);
        adapter = new JournableAdapter(journies, this);
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
                Object[] temp = adapter.getSelectedJournables().toArray();
                Journey[] selectedJournies = Arrays.copyOf(temp, temp.length, Journey[].class);

                startViewJournesActivity(extractRouteArray(selectedJournies));
            }
        });

        TextView title = findViewById(R.id.journey_title);
        title.setText(journalName);
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
        // Store changes to database only when the activity stops.
        updateJourniesDatabase();
    }

    @Override
    public void onViewItemClicked(Object o) {
        Journey j = (Journey) o;

        // Set up a dialog box to show a specific journeys details.
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment frag =  new ViewJourneyDialogFragment();

        // The dialog fragment needs to display the name and the description
        // It also stores the route of the journey since the route will be viewed
        // by pressing a button from the dialog fragment
        Bundle args = new Bundle();
        args.putString("name", j.getName());
        args.putString("description", j.getDescription());
        args.putString("route", j.getRoute());
        frag.setArguments(args);

        frag.show(fm, "fragment_view_journey_info");
    }

    public void updateJourniesDatabase() {
        // Using arrays rather than collections because they are simpler to cast
        Object[] temp = adapter.getJournablesToAdd().toArray();
        db.journeyDao().insertAll(Arrays.copyOf(temp, temp.length, Journey[].class));

        temp = adapter.getJournablesToDelete().toArray();
        db.journeyDao().deleteAll(Arrays.copyOf(temp, temp.length, Journey[].class));
    }

    private void startViewJournesActivity(String ...routes) {
        // routes is an array of JSON objects
        // each JSON object represents a list of coordiantes when deserialized
        Intent intent = new Intent(JourniesActivity.this, ViewJourniesActivity.class);
        intent.putExtra("routes", routes);

        startActivity(intent);
    }

    private String[] extractRouteArray(Journey ...journies) {
        // Only need the list of coordinates that represent a route,
        // Can't pass Journey object via intents anyways, they aren't parcelable
        String[] routes = new String[journies.length];

        for (int i = 0; i < routes.length; i++)
            routes[i] = journies[i].getRoute();

        return routes;
    }

    @Override
    public void onDialogClick(DialogFragment dialog) {
        // NoticeDialogListener callback,
        // display the route that is stored in the dialogFragment
        startViewJournesActivity( ((ViewJourneyDialogFragment) dialog).getRoute() );
    }
}
