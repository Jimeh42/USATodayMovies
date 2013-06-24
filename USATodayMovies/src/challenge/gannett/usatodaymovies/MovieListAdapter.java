package challenge.gannett.usatodaymovies;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

/*
 * Custom list adapter to display rows based on maps derived from JSON content
 */
public class MovieListAdapter extends ArrayAdapter<Map<String, Object>> {
	
	public MovieListAdapter(Context context, int resource,
			int textViewResourceId, List<Map<String, Object>> objects) {
		super(context, resource, textViewResourceId, objects);
		
	}

	/*
	 * Replace list content with new data
	 */
    public void updateList(List<Map<String, Object>> newList) {
    	clear();
    	addAll(newList);
        notifyDataSetChanged();
     }

    /*
     * Parse JSON input and replace the content of the list with the results
     * 
     * throws JSON exceptions or IOException if processing fails. List is not updated
     * in that case.
     */
    @SuppressWarnings("unchecked")
	public void updateList(String jsonContent) 
			throws JsonParseException, JsonMappingException, IOException {
    	ObjectMapper mapper = new ObjectMapper();
    	Map<String, Object> content = (Map<String, Object>)mapper.readValue(jsonContent, Map.class);
    	List<Map<String, Object>> result = (List<Map<String, Object>>)content.get("MovieReviews");
    	updateList(result);
     }

    /*
     * Generate custom row view
     */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
			View rowView = null;

			if (getItemViewType(position) == 0) {
				if (convertView != null)
					rowView = convertView;
				else
					rowView = inflater.inflate(R.layout.custom_row_view, parent, false);
				TextView titleView = (TextView) rowView.findViewById(R.id.title);
				TextView teaserView = (TextView) rowView.findViewById(R.id.teaser);
				TextView mpaaRatingView = (TextView) rowView.findViewById(R.id.mpaa_rating);
				RatingBar ratingBar = (RatingBar) rowView.findViewById(R.id.rating);
				Map<String, Object> item = getItem(position);
				titleView.setText(Html.fromHtml((String)item.get("MovieName")));
				teaserView.setText(Html.fromHtml((String)item.get("Review")));
				mpaaRatingView.setText((String)item.get("MPAARating"));
				String ratingString = (String)item.get("Rating");
				try {
					float rating = Float.parseFloat(ratingString);
					ratingBar.setRating(rating);
				} catch (NumberFormatException nfe) {;}
				
				return rowView;
			}
			
			return super.getView(position, convertView, parent);
	}

}
