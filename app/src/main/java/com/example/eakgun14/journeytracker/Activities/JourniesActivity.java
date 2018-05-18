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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.eakgun14.journeytracker.Adapters.JournableAdapter;
import com.example.eakgun14.journeytracker.Adapters.LightManagerAdapter;
import com.example.eakgun14.journeytracker.DataTypes.Journey;
import com.example.eakgun14.journeytracker.Adapters.ViewAdapterListener;
import com.example.eakgun14.journeytracker.Dialogs.NoticeDialogListener2;
import com.example.eakgun14.journeytracker.Dialogs.ViewJourneyDialogFragment;
import com.example.eakgun14.journeytracker.LocalDatabase.AppDatabase;
import com.example.eakgun14.journeytracker.R;

import java.util.List;

public class JourniesActivity extends AppCompatActivity implements ViewAdapterListener<Journey>,
        SensorEventListener, NoticeDialogListener2 {

    AppDatabase db;

    private LightManagerAdapter lightManager;

    List<Journey> journies;
    String journalName;

    private JournableAdapter<Journey> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journies);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()
                .build();

        ViewGroup thisLayout = findViewById(R.id.manage_journeys_constraint_layout);
        lightManager = new LightManagerAdapter(thisLayout, this);

        Intent intent = getIntent();

        // Special case requires to display all journies, regardless of journal
        if (intent.getIntExtra("Special", 0) == -1) {
            journalName = "All Journeys";
            journies = db.journeyDao().getAllJourneys();
        }
        else {
            int journalID = intent.getIntExtra("Journal", 0);
            journalName = intent.getStringExtra("Name");
            db.journeyDao().getAllJourneysInJournal(journalID);
        }

        android.support.v7.widget.Toolbar bar = findViewById(R.id.journey_toolbar);
        setSupportActionBar(bar);
        ActionBar actBar = getSupportActionBar();
        assert actBar != null;
        actBar.setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.journies_recycler_view);
        recyclerView.setHasFixedSize(true);

        adapter = new JournableAdapter<>(journies, this);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutMgr = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutMgr);


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
                List<Journey> selected = adapter.getSelectedJournables();
                startViewJournesActivity(extractRouteArray(selected));
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
    public void onViewItemClicked(Journey j) {
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
        args.putString("photos", j.getCoordinate_photos());
        frag.setArguments(args);

        frag.show(fm, "fragment_view_journey_info");
    }

    public void updateJourniesDatabase() {
        List<Journey> toAdd = adapter.getJournablesToAdd();
        db.journeyDao().insertAll(toAdd);

        List<Journey> toDelete = adapter.getJournablesToAdd();
        db.journeyDao().insertAll(toDelete);
    }

    private void startViewJournesActivity(String ...routes) {
        // routes is an array of JSON objects
        // each JSON object represents a list of coordiantes when deserialized
        Intent intent = new Intent(JourniesActivity.this, ViewJourniesActivity.class);
        intent.putExtra("routes", routes);

        startActivity(intent);
    }

    private String[] extractRouteArray(List<Journey> journies) {
        // Only need the list of coordinates that represent a route,
        // Can't pass Journey object via intents anyways, they aren't parcelable
        String[] routes = new String[journies.size()];

        for (int i = 0; i < routes.length; i++)
            routes[i] = journies.get(i).getRoute();

        return routes;
    }

    @Override
    public void onDialogClick(DialogFragment dialog) {
        // NoticeDialogListener callback,
        // display the route that is stored in the dialogFragment
        startViewJournesActivity( ((ViewJourneyDialogFragment) dialog).getRoute() );
    }

    @Override
    public void onSecondaryDialogClick(DialogFragment dialog) {
        ViewJourneyDialogFragment dial = (ViewJourneyDialogFragment) dialog;
        Intent intent = new Intent(JourniesActivity.this, PhotoViewActivity.class);
        intent.putExtra("Journey Name", dial.getName());
        intent.putExtra("URI JSON", dial.getPhotos());
        startActivity(intent);
    }
}
