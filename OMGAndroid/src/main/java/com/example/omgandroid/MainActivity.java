package com.example.omgandroid;

import android.app.Activity;
import android.content.Intent;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    TextView mainTextView;
    Button mainButton;
    EditText mainEditText;
    ListView mainListView;
    ArrayAdapter<String> mArrayAdapter;
    ArrayList<String> mCountryList = new ArrayList<String>();
    ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Access the TextView defined in layout XML and set its text
        mainTextView = (TextView) findViewById(R.id.main_textview);
        //mainTextView.setText("Set in Java");

        // Access the Button defined in layout XML and listen for it here
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        // Access the EditText defined in layout XML
        mainEditText = (EditText) findViewById(R.id.main_edittext);

        // Access the ListView
        mainListView = (ListView) findViewById(R.id.main_listview);

        // Create an ArrayAdapter for the ListView
        mArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                mCountryList);

        // Set the ListView to use the ArrayAdapter
        mainListView.setAdapter(mArrayAdapter);

        // Set this activity to react to list items being pressed
        mainListView.setOnItemClickListener(this);

        // Create a client to perform networking for us
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://open.undp.org/api/donor-country-index.json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonArray) {

                // Display a "Toast" message to announce our success
                Toast.makeText(getApplicationContext(),
                        "Success!",
                        Toast.LENGTH_LONG)
                        .show();

                // Parse the JSONArray returned and add names to the dataset
                for (int i = 0; i < jsonArray.length(); i++) {
                    mCountryList.add(jsonArray.optJSONObject(i).optString("name"));
                }

                // Update the ListView
                // this is the quick and dirty way-- better to subclass the adapter to accept JSON
                mArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable throwable, String s) {

                // Display a "Toast" message to announce the failure
                Toast.makeText(getApplicationContext(),
                        "Error: " + throwable.getMessage() + " " + s,
                        Toast.LENGTH_LONG)
                        .show();

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
        // 1. Test the Button
        //  mainTextView.setText("Button pressed!");

        // 2. Take what was typed into the EditText and use in TextView
        mainTextView.setText(mainEditText.getText().toString()
                + " is an Android developer!");

        // 3. Also add that value to the list shown in the ListView
        mCountryList.add(mainEditText.getText().toString());
        mArrayAdapter.notifyDataSetChanged();

        // 4. The text we'd like to share has changed, and we need to update
        setShareIntent();
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Log the item's position and contents to the console in Debug
        Log.d("omg android", position + ": " + mCountryList.get(position));
    }
}
