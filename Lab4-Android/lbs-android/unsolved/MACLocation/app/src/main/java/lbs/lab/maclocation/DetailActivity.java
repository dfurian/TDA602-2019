package lbs.lab.maclocation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

// *******************************************************************
// *** This file does not need to be read, and should not be edited **
// *******************************************************************

/**
 * Activity that presents a view of the the data stored in a particular item.
 */
public class DetailActivity extends AppCompatActivity {

    public static final String TITLE_DATA = "TITLE_DATA";
    public static final String INFO_DATA = "INFO_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView itemTitle = findViewById(R.id.titleDetail);
        TextView itemInfo = findViewById(R.id.infoDetail);

        if (getIntent().hasExtra(TITLE_DATA))
            itemTitle.setText(getIntent().getStringExtra(TITLE_DATA));
        if (getIntent().hasExtra(INFO_DATA))
            itemInfo.setText(getIntent().getStringExtra(INFO_DATA));
    }
}
