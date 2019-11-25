package ca.kevinscode.td_challenge.ui.budget;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;

import ca.kevinscode.td_challenge.R;
import ca.kevinscode.td_challenge.ui.database.DBAdapter;
import ca.kevinscode.td_challenge.ui.database.Location;

public class BudgetItemFragment extends Fragment {
    // Store instance variables
    private String merchantName;
    private String street;
    private String city;
    private double averageCost;
    private int page;
    private double distance;
    private double budgetAmount;
    private int totalPageSize;

    // newInstance constructor for creating fragment with arguments
    public static BudgetItemFragment newInstance(int page, Location passedLocation, double distance, double budgetAmount, int totalPageSize) {
        BudgetItemFragment fragmentFirst = new BudgetItemFragment();
        Bundle args = new Bundle();
        args.putInt("pageNumber", page);
        args.putString("merchantname", passedLocation.getMerchantName());
        args.putDouble("cost", passedLocation.getCurrencyAmount());
        args.putString("street", passedLocation.getLocationStreet());
        args.putString("city", passedLocation.getLocationCity());
        args.putDouble("distance", distance);
        args.putDouble("budgetAmount", budgetAmount);
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
        averageCost = getArguments().getDouble("cost");
        street = getArguments().getString("street");
        city = getArguments().getString("city");
        distance = getArguments().getDouble("distance");
        budgetAmount = getArguments().getDouble("budgetAmount");
        totalPageSize = getArguments().getInt("totalPageSize");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.budgetitem, container, false);

        DecimalFormat df = new DecimalFormat("0.00");

        TextView txtName = (TextView) view.findViewById(R.id.txtStore);
        txtName.setText(merchantName);

        if (!(merchantName.equals("No Results Matched Criteria") || merchantName.equals("Please Enter Search Criteria"))) {

            TextView txtCost = (TextView) view.findViewById(R.id.txtCost);
            txtCost.setText(getString(R.string.average_cost_of) + df.format(averageCost));

            TextView txtDistance = (TextView) view.findViewById(R.id.txtDate);
            txtDistance.setText(df.format(distance) + getString(R.string.km_from_you));

            TextView txtStreet = (TextView) view.findViewById(R.id.txtStreet);
            txtStreet.setText(street);

            TextView txtCity = (TextView) view.findViewById(R.id.txtCity);
            txtCity.setText(city);

            TextView txtPageNumber = (TextView) view.findViewById(R.id.txtPageNumber);
            txtPageNumber.setText("Page " + (page + 1) + " of " + (totalPageSize));


            TextView txtPercentageUsed = (TextView) view.findViewById(R.id.txtPercentageUsed);
            ProgressBar bugetUsedBar = (ProgressBar) view.findViewById(R.id.progressBarBudget);


            int budgetPercentUsed = (int) ((averageCost / budgetAmount) * 100);
            Log.d("TD_Challenge_Kevin_Cox", budgetPercentUsed + "");

            if (budgetPercentUsed > 80) {
                txtPercentageUsed.setText("Warning: " + budgetPercentUsed + " % of budget for this location.");
                bugetUsedBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
            } else {
                txtPercentageUsed.setText(budgetPercentUsed + " % of budget for this location.");
                bugetUsedBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
            }
            bugetUsedBar.setProgress(budgetPercentUsed, true);
        }else{
            TextView txtCost = (TextView) view.findViewById(R.id.txtCost);
            txtCost.setText("");

            TextView txtDistance = (TextView) view.findViewById(R.id.txtDate);
            txtDistance.setText("");

            TextView txtStreet = (TextView) view.findViewById(R.id.txtStreet);
            txtStreet.setText("");

            TextView txtCity = (TextView) view.findViewById(R.id.txtCity);
            txtCity.setText("");

            TextView txtPageNumber = (TextView) view.findViewById(R.id.txtPageNumber);
            txtPageNumber.setText("Page " + (page + 1) + " of " + (totalPageSize));

            TextView txtPercentageUsed = (TextView) view.findViewById(R.id.txtPercentageUsed);

            txtPercentageUsed.setText("");

        }

        return view;
    }
}