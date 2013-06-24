package challenge.gannett.usatodaymovies;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * An activity representing a single Movie detail screen. This activity is 
 * used on handset devices and tablets in portrait mode. On landscape tablets
 * item details are presented side-by-side with a list of items in a 
 * {@link MovieListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link MovieDetailFragment}.
 */
public class MovieDetailActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// If we're now in a two-pane configuration, end the detail activity
		if (getResources().getBoolean(R.bool.has_two_panes)) {
			finish();
			return;
		}
		setContentView(R.layout.activity_movie_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putAll(getIntent().getExtras());
			MovieDetailFragment fragment = new MovieDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.movie_detail_container, fragment).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
