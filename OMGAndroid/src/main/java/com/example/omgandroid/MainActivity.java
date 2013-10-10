package com.example.omgandroid;

import android.app.Activity;
import android.content.Intent;
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

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String QUERY_URL = "http://openlibrary.org/search.json?q=";
    TextView mainTextView;
    Button mainButton;
    EditText mainEditText;
    ListView mainListView;
    ShareActionProvider mShareActionProvider;
    JSONAdapter mJSONAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We want to add a spinning progress bar (and make sure it's off)
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);

        // We have to tell the activity which XML layout is the right one
        setContentView(R.layout.activity_main);

        // Access the TextView defined in layout XML and set its text
        mainTextView = (TextView) findViewById(R.id.main_textview);

        // Access the Button defined in layout XML and listen for it with this activity
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        // Access the EditText defined in layout XML
        mainEditText = (EditText) findViewById(R.id.main_edittext);

        // Access the ListView
        mainListView = (ListView) findViewById(R.id.main_listview);

        // Create a JSONAdapter for the ListView
        mJSONAdapter = new JSONAdapter(this);

        // Set the ListView to use the ArrayAdapter
        mainListView.setAdapter(mJSONAdapter);

        // Set this activity to react to list items being pressed
        mainListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Access the Share Item defined in menu XML
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        // Access the object responsible for putting together the sharing submenu
        if (shareItem != null) {
            mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
        }

        // Create an Intent to share our content
        // It is in a separate method so we can call it from elsewhere too
        setShareIntent();

        return true;
    }

    private void setShareIntent() {

        // create an Intent with the contents of the TextView
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Development");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mainTextView.getText());

        // Make sure the provider knows it should work with that Intent
        mShareActionProvider.setShareIntent(shareIntent);
    }

    @Override public void onClick(View v) {

        // Take what was typed into the EditText and use in search
        queryBooks(mainEditText.getText().toString());
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Now that the user's chosen a book, let's grab the cover data
        String coverID = mJSONAdapter.getItem(position).optString("cover_i","");

        // create an Intent to take us over to a new DetailActivity
        Intent detailIntent = new Intent(this, DetailActivity.class);

        // pack away the data about the cover into our Intent before we head out
        detailIntent.putExtra("coverID", coverID);

        // TODO: add any other data we'd like as Extras

        // start the next Activity using our prepared Intent
        startActivity(detailIntent);
    }

    private void queryBooks(String searchString) {

        // Prepare our search string to be put in a URL
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

        // Create a client to perform networking for us
        AsyncHttpClient client = new AsyncHttpClient();

        setProgressBarIndeterminateVisibility(true);
        //mProgressBar.setVisibility(View.VISIBLE);

        // Have the client get a JSONArray of data, and define how to respond
        client.get(QUERY_URL + urlString,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(JSONObject jsonObject) {

                        setProgressBarIndeterminateVisibility(false);

                        // Display a "Toast" message to announce our success
                        Toast.makeText(getApplicationContext(),
                                "Success!",
                                Toast.LENGTH_LONG)
                                .show();

                        // Now we are being wise and have created the JSONAdapter subclass
                        // update the data in our custom method.
                        mJSONAdapter.updateData(jsonObject);
                    }

                    @Override
                    public void onFailure(Throwable throwable, String s) {

                        setProgressBarIndeterminateVisibility(false);

                        // Display a "Toast" message to announce the failure
                        Toast.makeText(getApplicationContext(),
                                "Error: " + throwable.getMessage() + " " + s,
                                Toast.LENGTH_LONG)
                                .show();

                        // Log error message to help solve any problems
                        Log.e("omg android", throwable.getMessage() + " " + s);
                    }
                });
    }
}