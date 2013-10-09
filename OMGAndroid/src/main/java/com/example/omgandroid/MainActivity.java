package com.example.omgandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    TextView mainTextView;
    Button mainButton;
    EditText mainEditText;
    ListView mainListView;
    ShareActionProvider mShareActionProvider;
    JSONAdapter mJSONAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Create a client to perform networking for us
        AsyncHttpClient client = new AsyncHttpClient();

        // Have the client get a JSONArray of data, and define how to respond
        client.get("http://openlibrary.org/search.json?q=the+lord+of+the+rings", new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject jsonObject) {

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

        // Take what was typed into the EditText and use in TextView
        mainTextView.setText(mainEditText.getText().toString()
                + " is learning Android development!");

        // The text we'd like to share has changed, and we need to update
        setShareIntent();
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Log the item's position and contents to the console in Debug
        Log.d("omg android", position
                + ": "
                + mJSONAdapter.getItem(position).optString("name")
                + " ("
                + mJSONAdapter.getItem(position).optString("id")
                + ")");
    }
}