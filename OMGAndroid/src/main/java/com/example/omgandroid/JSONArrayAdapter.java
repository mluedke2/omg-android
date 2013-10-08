package com.example.omgandroid;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONArrayAdapter extends BaseAdapter {

    Activity mActivity;
    JSONArray mJsonArray;

    public JSONArrayAdapter(Activity activity, JSONArray jsonArray) {
        mActivity = activity;
        mJsonArray = jsonArray;
    }

    public void updateData(JSONArray jsonArray) {

        // update the adapter's dataset
        mJsonArray = jsonArray;
        notifyDataSetChanged();
    }

    @Override public int getCount() {
        return mJsonArray.length();
    }

    @Override public JSONObject getItem(int position) {
        return mJsonArray.optJSONObject(position);
    }

    @Override public long getItemId(int position) {
        return mJsonArray.optJSONObject(position).optLong("id");
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        // if the view already exists, no need to inflate again!
        if (convertView == null) {

            // There's a standard list item in Android XML. We're going to inflate that.
            convertView = mActivity.getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
        }

        // Parse the current dataset. It is a JSONArray of JSONObjects
        JSONObject jsonObject = mJsonArray.optJSONObject(position);

        // The JSONObject has key-value pairs. One key is "name"
        String name = jsonObject.optString("name");

        // Tell our TextView to display the name.
        ((TextView) convertView).setText(name);

        return convertView;
    }
}