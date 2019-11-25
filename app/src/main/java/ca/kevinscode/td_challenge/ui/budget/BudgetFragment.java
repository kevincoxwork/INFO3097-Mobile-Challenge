package ca.kevinscode.td_challenge.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import ca.kevinscode.td_challenge.MainActivity;
import ca.kevinscode.td_challenge.R;
import ca.kevinscode.td_challenge.ui.database.Location;

public class BudgetFragment extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_budget, container, false);

        Spinner business_spinner = (Spinner) root.findViewById(R.id.business_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.business_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        business_spinner.setAdapter(adapter);


        Spinner distance_spinner = (Spinner) root.findViewById(R.id.distance_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_distance = ArrayAdapter.createFromResource(this.getContext(),
                R.array.distance_values, android.R.layout.simple_spinner_item);
        adapter_distance.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distance_spinner.setAdapter(adapter_distance);

        List<Location> initialLocationScreen = new ArrayList<>();
        initialLocationScreen.add(new Location("", 0.0, "", 0, "", "", "Please Enter Search Criteria", 0.0, "", "", "", "", "", "", "", 0.0, "", "", "", ""));

        ViewPager vpPager = (ViewPager) root.findViewById(R.id.viewPager);
        FragmentStatePagerAdapter adapterViewPager = new MainActivity.MyPagerAdapter(getFragmentManager(), initialLocationScreen, 0.0);
        vpPager.setAdapter(adapterViewPager);


        return root;
    }

}