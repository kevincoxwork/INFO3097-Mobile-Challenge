package ca.kevinscode.td_challenge.ui.track;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import ca.kevinscode.td_challenge.MainActivity;
import ca.kevinscode.td_challenge.R;
import ca.kevinscode.td_challenge.ui.database.DBAdapter;
import ca.kevinscode.td_challenge.ui.database.DBTrackItemAdapter;
import ca.kevinscode.td_challenge.ui.database.TrackItem;

public class TrackFragment extends Fragment {
    private DBTrackItemAdapter db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_track, container, false);


        //load the data here
        db = new DBTrackItemAdapter(getContext());
        db.open();
        List<TrackItem> items = db.getAllTrackingInfo();
        db.close();

        if (items.size() == 0){
            items.add(new TrackItem("No Purchases Tracked", "", "", "", 0, ""));
        }

        ViewPager vpPager = (ViewPager) root.findViewById(R.id.viewPagerTrack);
        FragmentStatePagerAdapter adapterViewPager = new MainActivity.MyBudgetTrackerAdapeter(getFragmentManager(), items);
        vpPager.setAdapter(adapterViewPager);

        return root;
    }
}