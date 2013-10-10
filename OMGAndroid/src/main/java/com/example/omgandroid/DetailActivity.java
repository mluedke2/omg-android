package com.example.omgandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

import com.squareup.picasso.Picasso;

public class DetailActivity extends Activity {

    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";
    ShareActionProvider mShareActionProvider;
    String mImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We have to tell the activity which XML layout is the right one
        setContentView(R.layout.activity_detail);

        // Enable the "Up" button for more navigation options
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Access the imageview from XML
        ImageView imageView = (ImageView) findViewById(R.id.img_cover);

        // unpack the coverID from its trip inside our Intent
        String coverID = this.getIntent().getExtras().getString("coverID");

        // See if there is a valid coverID
        if (coverID.length() > 0) {

        // Use the ID to construct an image URL over at Open Library
        mImageURL = IMAGE_URL_BASE + coverID + "-L.jpg";

        // Use Picasso to load the image
        Picasso.with(this)
                .load(mImageURL)
                .placeholder(R.drawable.img_books_large)
                .into(imageView);

        } else {

            // If the coverID is invalid, just use a placeholder
            imageView.setImageResource(R.drawable.img_books_large);
        }
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

        setShareIntent(mImageURL);

        return true;
    }

    private void setShareIntent(String imageURL) {

        // create an Intent with the contents of the TextView
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Book Recommendation!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, imageURL);

        // Make sure the provider knows it should work with that Intent
        mShareActionProvider.setShareIntent(shareIntent);
    }

}
