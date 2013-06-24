package challenge.gannett.usatodaymovies;


import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * An activity representing a list of Movies. This activity has different
 * presentations for handset and tablet-size devices. On handsets and 
 * portrait-mode tablets, the activity presents a list of items, which 
 * when touched, lead to a {@link MovieDetailActivity} representing item 
 * details. On tablets, the activity presents the list of items and item 
 * details side-by-side using two vertical panes.
 * <p>
 * This activity also implements the required
 * {@link MovieListFragment.Callbacks} interface to listen for item selections.
 */
public class MovieListActivity extends FragmentActivity implements
		MovieListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTwoPane = getResources().getBoolean(R.bool.has_two_panes);

		setContentView(R.layout.activity_movie_list);

		if (mTwoPane) {

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((MovieListFragment) getSupportFragmentManager().findFragmentById(
					R.id.movie_list)).setActivateOnItemClick(true);
		}

	}

	/**
	 * Callback method from {@link MovieListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(Map<String, Object> item) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			Set<String> keys = item.keySet();
			for (String key : keys) {
				Object value = item.get(key);
				if (value instanceof String)
					arguments.putString(key, (String)value);
			}
			MovieDetailFragment fragment = new MovieDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.movie_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, MovieDetailActivity.class);
			Set<String> keys = item.keySet();
			for (String key : keys) {
				Object value = item.get(key);
				if (value instanceof String)
					detailIntent.putExtra(key, (String)value);
			}

			startActivity(detailIntent);
		}
	}
}
