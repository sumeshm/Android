package planetjup.com.quickexpense.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import planetjup.com.quickexpense.R;
import planetjup.com.quickexpense.activity.TripTabsActivity;
import planetjup.com.quickexpense.pojo.TripDetails;

/**
 * This is an custom ListView adapter for Trip Details
 * Created by Sumesh Mani on 2/16/18.
 */

public class TripArrayAdapter extends ArrayAdapter<TripDetails> implements View.OnClickListener {

    private static final String TAG = TripArrayAdapter.class.getSimpleName();

    private final Context context;
    private final ArrayList<TripDetails> tripList;


    public TripArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<TripDetails> list) {
        super(context, resource, list);

        this.context = context;
        this.tripList = new ArrayList<>(list);
    }

    public ArrayList<TripDetails> getTripList() {
        return tripList;
    }

    @SuppressLint("Range")
    @Override
    public void onClick(View view) {
        Log.v(TAG, "onClick(): id=" + view.getId());

        if (view.getTag() == null || view.getTag().getClass() != ListItemManager.class) {
            Log.v(TAG, "onClick() : no valid POJO in tag");
            return;
        }

        ListItemManager listItemManager = (ListItemManager) view.getTag();
        TripDetails tripDetails = listItemManager.tripDetails;

        switch (view.getId()) {
            case R.id.button_Delete:
                Log.v(TAG, "onClick() : Delete : Trip=" + tripDetails.getTripName() + ", index=" + getPosition(tripDetails));
                remove(tripDetails);
                notifyDataSetChanged();
                break;
            case R.id.list_view:
                Log.v(TAG, "onClick() : List : Trip=" + tripDetails.getTripName() + ", index=" + getPosition(tripDetails));
                Intent tripTabIntent = new Intent(context, TripTabsActivity.class);
                tripTabIntent.putExtra("tripName", tripDetails.getTripName());
                context.startActivity(tripTabIntent);
                break;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_view_trip, null);
            convertView.setOnClickListener(this);
        }

        new ListItemManager(tripList.get(position), convertView, this);

        return convertView;
    }

    @Override
    public void add(@Nullable TripDetails tripDetails) {
        Log.v(TAG, "add()");
        tripList.add(tripDetails);
        super.add(tripDetails);
    }

    @Override
    public void remove(@Nullable TripDetails tripDetails) {
        Log.v(TAG, "remove()");
        tripList.remove(getPosition(tripDetails));
        super.remove(tripDetails);
    }


    private class ListItemManager {
        private final TripDetails tripDetails;
        private final View view;
        private final TextView textView;
        private final ImageButton deleteButton;

        private ListItemManager(@NonNull TripDetails tripDetails, @NonNull View view, @NonNull View.OnClickListener listener) {
            this.tripDetails = tripDetails;

            this.view = view;
            this.view.setTag(this);

            textView = view.findViewById(R.id.text_rowName);
            textView.setText(tripDetails.getTripName());

            deleteButton = view.findViewById(R.id.button_Delete);
            deleteButton.setTag(this);
            deleteButton.setOnClickListener(listener);
        }
    }
}
