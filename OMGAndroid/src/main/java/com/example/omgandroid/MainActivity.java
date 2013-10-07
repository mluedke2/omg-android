package com.example.omgandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    TextView mainTextView;
    Button mainButton;
    EditText mainEditText;
    ListView mainListView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> nameList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Access the TextView defined in layout XML and set its text
        mainTextView = (TextView) findViewById(R.id.main_textview);
        mainTextView.setText("Set in Java");

        // Access the Button defined in layout XML and listen for it here
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        // Access the EditText defined in layout XML
        mainEditText = (EditText) findViewById(R.id.main_edittext);

        // Access the ListView
        mainListView = (ListView) findViewById(R.id.main_listview);

        // Create an ArrayAdapter for the ListView
        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                nameList);

        // Set the ListView to use the ArrayAdapter
        mainListView.setAdapter(arrayAdapter);

        // Set this activity to react to list items being pressed
        mainListView.setOnItemClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override public void onClick(View v) {
        // 1. Test the Button
        //  mainTextView.setText("Button pressed!");

        // 2. Take what was typed into the EditText and use in TextView
        mainTextView.setText(mainEditText.getText().toString()
              + " is an Android developer!");

        // 3. Also add that value to the list shown in the ListView
        nameList.add(mainEditText.getText().toString());
        arrayAdapter.notifyDataSetChanged();

    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Log the item's position and contents to the console in Debug
        Log.d("omg android", position + ": " + nameList.get(position));

    }
}
