package com.example.omgandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity
        implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    TextView mainTextView; // 1
    Button mainButton; // 2
    EditText mainEditText; // 3
    ListView mainListView; // 4
    ArrayAdapter<String> mArrayAdapter;
    ArrayList<String> mNameList = new ArrayList<String>();
    ShareActionProvider mShareActionProvider; // 6
    private static final String PREFS = "prefs"; // 7
    private static final String PREF_NAME = "name"; // 7
    SharedPreferences mSharedPreferences; // 7

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Access the TextView defined in layout XML
        // and then set its text
        mainTextView = (TextView) findViewById(R.id.main_textview);
        mainTextView.setText("Set in Java!");

        // 2. Access the Button defined in layout XML
        // and listen for it here
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        // 3. Access the EditText defined in layout XML
        mainEditText = (EditText) findViewById(R.id.main_edittext);

        // 4. Access the ListView
        mainListView = (ListView) findViewById(R.id.main_listview);

        // Create an ArrayAdapter for the ListView
        mArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                mNameList);

        // Set the ListView to use the ArrayAdapter
        mainListView.setAdapter(mArrayAdapter);

        // 5. Set this activity to react to list items being pressed
        mainListView.setOnItemClickListener(this);

        // 7. Greet the user, or ask for their name if new
        displayWelcome();
    }

    public void displayWelcome() {

        // Access the device's key-value storage
        mSharedPreferences
                = getSharedPreferences(PREFS, MODE_PRIVATE);

        // Read the user's name,
        // or an empty string if nothing found
        String name = mSharedPreferences.getString(PREF_NAME, "");

        if (name.length() > 0) {

            // If the name is valid, display a Toast welcoming them
            Toast.makeText(this,
                    "Welcome back, " + name + "!",
                    Toast.LENGTH_LONG)
                    .show();
        } else {

            // otherwise, show a dialog to ask for their name
            AlertDialog.Builder alert
                    = new AlertDialog.Builder(this);
            alert.setTitle("Hello!");
            alert.setMessage("What is your name?");

            // Create EditText for entry
            final EditText input = new EditText(this);
            alert.setView(input);

            // Make an "OK" button to save the name
            alert.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int whichButton) {

                            // Grab the EditText's input
                            String inputName = input.getText().toString();

                            // Put it into memory (don't forget to commit!)
                            SharedPreferences.Editor e =
                                    mSharedPreferences.edit();
                            e.putString(PREF_NAME, inputName);
                            e.commit();

                            // Welcome the new user
                            Toast.makeText(getApplicationContext(),
                                    "Welcome, " + inputName + "!",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

            // Make a "Cancel" button
            // that simply dismisses the alert
            alert.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int whichButton) {}
                    });

            alert.show();
        }
    }

    @Override public void onClick(View v) {

        // 3. Take what was typed into the EditText
        // and use in TextView
        mainTextView.setText(mainEditText.getText().toString()
                + " is learning Android development!");

        // 4. Also add that value to the list shown in the ListView
        mNameList.add(mainEditText.getText().toString());
        mArrayAdapter.notifyDataSetChanged();

        // 6. The text you'd like to share has changed,
        // and you need to update
        setShareIntent();
    }

    @Override public void onItemClick(AdapterView<?> parent,
                                      View view, int position, long id) {

        // 5. Log the item's position and contents
        // to the console in Debug
        Log.d("omg android", position
                + ": "
                + mNameList.get(position));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu.
        // Adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Access the Share Item defined in menu XML
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        // Access the object responsible for
        // putting together the sharing submenu
        if (shareItem != null) {
            mShareActionProvider = (ShareActionProvider)
                    shareItem.getActionProvider();
        }

        // Create an Intent to share your content
        setShareIntent();

        return true;
    }

    private void setShareIntent() {

        // create an Intent with the contents of the TextView
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                "Android Development");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mainTextView.getText());

        // Make sure the provider knows
        // it should work with that Intent
        mShareActionProvider.setShareIntent(shareIntent);
    }
}
