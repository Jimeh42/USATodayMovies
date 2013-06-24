package challenge.gannett.usatodaymovies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A list fragment representing a list of Movies. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link MovieDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class MovieListFragment extends ListFragment {
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * list content.
	 */
	private static final String STATE_JSON_CONTENT = "json_content";

	/*
	 * Cached string for endpoint URL retrieved from resources
	 */
    private String MOVIE_API_ENDPOINT = null;

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 * @param item content of selected list item
		 */
		public void onItemSelected(Map<String, Object> item);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(Map<String, Object> item) {
		}
	};

	/*
	 * Retain list adapter for content update callbacks
	 */
	private MovieListAdapter adapter = null;

	/*
	 * Retain retrieved JSON content to use in serialized instance state
	 * In a more general application, I would probably use instance state
	 * handling that better separates the input format from the implementation,
	 * but since this content arrives in a convenient string format, it
	 * makes a handy serialization format for a simple app.
	 */
	private String jsonContent = null;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public MovieListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MOVIE_API_ENDPOINT = getResources().getString(R.string.movie_api_endpoint);
		
		adapter = new MovieListAdapter(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, new ArrayList<Map<String, Object>>());

		setListAdapter(adapter);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.custom_list_view, container);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(STATE_JSON_CONTENT)) {
				updateContent(savedInstanceState.getString(STATE_JSON_CONTENT));

				// Restore the previously serialized activated item position.
				// In a two-panel display, this also restores the detail panel
				if (savedInstanceState != null
						&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
					setActivatedPosition(savedInstanceState
							.getInt(STATE_ACTIVATED_POSITION));
				}
			}
		}
		if (jsonContent == null)
			new QueryTask().execute(MOVIE_API_ENDPOINT);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(adapter.getItem(position));

		mActivatedPosition = position;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
		if (jsonContent != null) {
			outState.putString(STATE_JSON_CONTENT, jsonContent);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
			mCallbacks.onItemSelected(adapter.getItem(position));
		}

		mActivatedPosition = position;
	}

	private void updateContent(String content) {
		if (content != null) {
			try {
				adapter.updateList(content);
				jsonContent  = content;
				if (getResources().getBoolean(R.bool.has_two_panes) && 
						mActivatedPosition == ListView.INVALID_POSITION) {
					setActivatedPosition(0);
				}
			} catch (Exception e) {
				message(R.string.content_error);
			}
		}
	}

	private void message(int resId) {
		Toast.makeText(this.getActivity().getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
	}
	
	private class QueryTask extends AsyncTask<String, String, String> {
		
		public QueryTask() {
		}

		@Override
		protected String doInBackground(String... params) {
			String result = null;
			try {
				result = JSONHelper.getUrlContent(params[0]);
			} catch (IOException e) {
				message(R.string.comm_error);
			}

			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if (result != null)
				updateContent(result);
		}

	}

}

