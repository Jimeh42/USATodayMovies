package challenge.gannett.usatodaymovies;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * A fragment representing a single Movie detail screen. This fragment is either
 * contained in a {@link MovieListActivity} in two-pane mode (on tablets) or a
 * {@link MovieDetailActivity} on handsets.
 */
public class MovieDetailFragment extends Fragment {
	
	/*
	 * In a real app, these values would be in external configuration
	 * to keep the code independent of the specific JSON API details,
	 * but for simplicity, they're defined here.
	 */
	public static final String ARG_MOVIENAME = "MovieName";
	public static final String ARG_ACTORNAME = "ActorName";
	public static final String ARG_RELEASEDATE = "ReleaseDate";
	public static final String ARG_RATING = "Rating";
	public static final String ARG_DIRECTOR = "Director";
	public static final String ARG_DISTRIBUTOR = "Distributor";
	public static final String ARG_MPAARATING = "MPAARating";
	public static final String ARG_REVIEWER = "Reviewer";
	public static final String ARG_BRIEF = "Brief";
	public static final String ARG_REVIEW = "Review";
	public static final String ARG_REVIEWDATE = "ReviewDate";
	public static final String ARG_WEBURL = "WebUrl";
	public static final String ARG_ID = "Id";

	private Bundle item = null;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public MovieDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		item = savedInstanceState;
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			item = new Bundle();
			item.putAll(getArguments());
		}
	}

	private static final String review_format = "<body style='margin: 0; padding: 0'>%s<p>--&nbsp;%s</p><p><a href='%s'>Full Review</a></p>";
	private static final String review_format_short = "<body style='margin: 0; padding: 0'>%s";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_movie_detail,
				container, false);

		if (item != null) {
			((TextView) rootView.findViewById(R.id.title))
					.setText(item.getString(ARG_MOVIENAME));
			((TextView) rootView.findViewById(R.id.director))
			.setText(item.getString(ARG_DIRECTOR));
			((TextView) rootView.findViewById(R.id.actor))
			.setText(item.getString(ARG_ACTORNAME));
			((TextView) rootView.findViewById(R.id.mpaa_rating))
			.setText(item.getString(ARG_MPAARATING));
			String ratingString = item.getString(ARG_RATING);
			try {
				float rating = Float.parseFloat(ratingString);
				((RatingBar) rootView.findViewById(R.id.rating)).setRating(rating);
			} catch (NumberFormatException nfe) {;}

			String review = item.getString(ARG_REVIEW);
			String reviewer = item.getString(ARG_REVIEWER);
			String url = "";
			try {
				url = URLEncoder.encode(item.getString(ARG_WEBURL), "utf-8");
			} catch (UnsupportedEncodingException e) {
				// Should never happen, but just in case
				url = item.getString(ARG_WEBURL);
			}
			
			String webContent;
			if (url != null && url.length() > 0 && reviewer != null & reviewer.length() > 0)
				webContent = String.format(review_format, review, reviewer, url);
			else
				webContent = String.format(review_format_short, review);
			WebView webView = ((WebView) rootView.findViewById(R.id.review));
			webView.setBackgroundColor(0);
			webView.loadData(webContent, "text/html", "utf-8");
			((TextView) rootView.findViewById(R.id.reviewer))
			.setText(item.getString(ARG_REVIEWER));
			((TextView) rootView.findViewById(R.id.url))
			.setText(item.getString(ARG_WEBURL));
		}

		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (item != null)
			outState.putAll(item);
	}

}
