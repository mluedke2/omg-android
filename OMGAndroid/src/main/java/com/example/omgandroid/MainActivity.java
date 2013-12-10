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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends Activity
        implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    TextView mainTextView; // 1
    Button mainButton; // 2
    EditText mainEditText; // 3
    ListView mainListView; // 4
    JSONAdapter mJSONAdapter; // 10
    ArrayList<String> mNameList = new ArrayList<String>();
    ShareActionProvider mShareActionProvider; // 6
    private static final String PREFS = "prefs"; // 7
    private static final String PREF_NAME = "name"; // 7
    SharedPreferences mSharedPreferences; // 7
    private static final String QUERY_URL
            = "http://openlibrary.org/search.json?q="; // 8

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 11. Add a spinning progress bar (and make sure it's off)
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);

        setContentView(R.layout.activity_main);

        // 1. Access the TextView defined in layout XML
        // and then set its text
        mainTextView = (TextView) findViewById(R.id.main_textview);

        // 2. Access the Button defined in layout XML
        // and listen for it here
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        // 3. Access the EditText defined in layout XML
        mainEditText = (EditText) findViewById(R.id.main_edittext);

        // 4. Access the ListView
        mainListView = (ListView) findViewById(R.id.main_listview);

        // 5. Set this activity to react to list items being pressed
        mainListView.setOnItemClickListener(this);

        // 7. Greet the user, or ask for their name if new
        displayWelcome();

        // 10. Create a JSONAdapter for the ListView
        mJSONAdapter = new JSONAdapter(this, getLayoutInflater());

        // Set the ListView to use the ArrayAdapter
        mainListView.setAdapter(mJSONAdapter);
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

        // 9. Take what was typed into the EditText and use in search
        queryBooks(mainEditText.getText().toString());
    }

    @Override public void onItemClick(AdapterView<?> parent,
                                      View view, int position, long id) {

        // 12. Now that the user's chosen a book, grab the cover data
        String coverID =
                mJSONAdapter.getItem(position).optString("cover_i","");

        // create an Intent to take you over to a new DetailActivity
        Intent detailIntent = new Intent(this, DetailActivity.class);

        // pack away the data about the cover
        // into your Intent before you head out
        detailIntent.putExtra("coverID", coverID);

        // TODO: add any other data you'd like as Extras

        // start the next Activity using your prepared Intent
        startActivity(detailIntent);
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

    private void queryBooks(String searchString) {

        // Prepare your search string to be put in a URL
        // It might have reserved characters or something
        String urlString = "";
        try {
            urlString = URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException e) {

            // if this fails for some reason, let the user know why
            e.printStackTrace();
            Toast.makeText(this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }

        // Create a client to perform networking
        AsyncHttpClient client = new AsyncHttpClient();

        // 11. start progress bar
        setProgressBarIndeterminateVisibility(true);

        // Have the client get a JSONArray of data
        // and define how to respond
        client.get(QUERY_URL + urlString,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(JSONObject jsonObject) {

                        // 11. stop progress bar
                        setProgressBarIndeterminateVisibility(false);

                        // Display a "Toast" message
                        // to announce your success
                        Toast.makeText(getApplicationContext(),
                                "Success!",
                                Toast.LENGTH_LONG)
                                .show();

                        // update the data in your custom method.
                        mJSONAdapter.updateData(jsonObject.optJSONArray("docs"));
                    }

                    @Override
                    public void onFailure(int statusCode,
                                          Throwable throwable,
                                          JSONObject error) {

                        // 11. stop progress bar
                        setProgressBarIndeterminateVisibility(false);

                        // Display a "Toast" message
                        // to announce the failure
                        Toast.makeText(getApplicationContext(),
                                "Error: "
                                        + statusCode
                                        + " "
                                        + throwable.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();

                        // Log error message
                        // to help solve any problems
                        Log.e("omg android",
                                statusCode
                                        + " "
                                        + throwable.getMessage());
                    }
                });
    }
}
