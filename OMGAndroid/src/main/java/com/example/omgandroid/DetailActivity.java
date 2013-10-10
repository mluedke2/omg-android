package com.example.omgandroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We have to tell the activity which XML layout is the right one
        setContentView(R.layout.activity_detail);

        // Access the imageview from XML
        ImageView imageView = (ImageView) findViewById(R.id.img_cover);

        // unpack the coverID from its trip inside our Intent
        String coverID = this.getIntent().getExtras().getString("coverID");

        // See if there is a valid coverID
        if (coverID.length() > 0) {

        // Use the ID to construct an image URL over at Open Library
        String imageURL = "http://covers.openlibrary.org/b/id/"
                + coverID
                + "-L.jpg";

        // Use Picasso to load the image
        Picasso.with(this)
                .load(imageURL)
                .placeholder(R.drawable.img_books_large)
                .into(imageView);

        } else {

            // If the coverID is invalid, just use a placeholder
            imageView.setImageResource(R.drawable.img_books_large);
        }
    }


}
