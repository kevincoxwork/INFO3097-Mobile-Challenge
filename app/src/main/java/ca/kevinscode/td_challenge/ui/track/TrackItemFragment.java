package ca.kevinscode.td_challenge.ui.track;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;

import ca.kevinscode.td_challenge.R;
import ca.kevinscode.td_challenge.ui.database.TrackItem;

public class TrackItemFragment extends Fragment {
    // Store instance variables
    private String merchantName;
    private String street;
    private String city;
    private String date;
    private double cost;
    private int page;
    private int totalPageSize;

    // newInstance constructor for creating fragment with arguments
    public static TrackItemFragment newInstance(int page, TrackItem item, int totalPageSize) {
        TrackItemFragment fragmentFirst = new TrackItemFragment();
        Bundle args = new Bundle();
        args.putInt("pageNumber", page);
        args.putString("merchantname", item.getMerchantName());
        args.putDouble("cost", item.getCost());
        args.putString("street", item.getStreet());
        args.putString("city", item.getCity());
        args.putString("date", item.getDate());
        args.putInt("totalPageSize", totalPageSize);

        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("pageNumber", 0);
        merchantName = getArguments().getString("merchantname");
        cost = getArguments().getDouble("cost");
        street = getArguments().getString("street");
        city = getArguments().getString("city");
        date = getArguments().getString("date");
        totalPageSize = getArguments().getInt("totalPageSize");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recorded_item, container, false);
        DecimalFormat df = new DecimalFormat("0.00");

        TextView txtName = (TextView) view.findViewById(R.id.txtStore);
        txtName.setText(merchantName);



        TextView txtCost = (TextView) view.findViewById(R.id.txtCost);
        if (merchantName.equals("No Purchases Tracked")){
            txtCost.setText("");
        }else{
            txtCost.setText("$" + df.format(cost) );
        }


        TextView txtDistance = (TextView) view.findViewById(R.id.txtDate);
        txtDistance.setText(date);

        TextView txtStreet = (TextView) view.findViewById(R.id.txtStreet);
        txtStreet.setText(street);

        TextView txtCity = (TextView) view.findViewById(R.id.txtCity);
        txtCity.setText(city);

        TextView txtPageNumber = (TextView) view.findViewById(R.id.txtPageNumber);
        txtPageNumber.setText("Page " + (page + 1) + " of " + (totalPageSize));

        return view;
    }
}