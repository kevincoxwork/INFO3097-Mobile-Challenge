package ca.kevinscode.td_challenge;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.kevinscode.td_challenge.ui.budget.BudgetItemFragment;
import ca.kevinscode.td_challenge.ui.database.DBAdapter;
import ca.kevinscode.td_challenge.ui.database.DBTrackItemAdapter;
import ca.kevinscode.td_challenge.ui.database.Location;
import ca.kevinscode.td_challenge.ui.database.TrackItem;
import ca.kevinscode.td_challenge.ui.track.TrackItemFragment;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DBAdapter db;
    private DBTrackItemAdapter dba;


    private final int CAMERA_REQUEST = 1888;
    private final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final double TORONTO_LAT = 43.647380, TORONTO_LONG = -79.380980;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.track_purchase, R.id.find_in_my_budget,
                R.id.explore, R.id.about)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        db = new DBAdapter(this);
        dba = new DBTrackItemAdapter(this);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void onSearchInBudgetClicked(View view) {
        EditText budgetedAmount = findViewById(R.id.budgeted_amount_txt);
        Spinner businessSpinner = findViewById(R.id.business_spinner);
        Spinner distance_spinner = findViewById(R.id.distance_spinner);

        if (budgetedAmount.getText().toString().equals("")) {
            budgetedAmount.setError("Budget Amount Required");
            return;
        }
        //remove the KM unit
        double amount = Double.parseDouble(budgetedAmount.getText().toString());

        int distance = Integer.parseInt(distance_spinner.getSelectedItem().toString().replace("km", "").trim());

        final double TORONTO_LAT = 43.647380, TORONTO_LONG = -79.380980;
        LatLng loc = new LatLng(TORONTO_LAT, TORONTO_LONG);

        db.open();
        List<Location> foundLocations = db.getLocationsInRange(distance, loc, "");
        db.close();
        List<Location> parsedLocations = new ArrayList<>();
        for (int i = 0; i < foundLocations.size(); i++) {
            if (foundLocations.get(i).getCategoryTags().contains(businessSpinner.getSelectedItem().toString()) && foundLocations.get(i).getCurrencyAmount() <= amount) {
                parsedLocations.add(foundLocations.get(i));
            }
        }

        if (parsedLocations.size() == 0){
            parsedLocations.add(new Location("", 0.0, "", 0, "", "", "No Results Matched Criteria", 0.0, "", "", "", "", "", "", "", 0.0, "", "", "", ""));
        }

        ViewPager vpPager = (ViewPager) findViewById(R.id.viewPager);
        FragmentStatePagerAdapter adapterViewPager = new MainActivity.MyPagerAdapter(getSupportFragmentManager(), parsedLocations, amount);
        vpPager.setAdapter(adapterViewPager);
    }

    public void onTrackPurchaseClicked(View v) {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //camera gave image back

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            getImageAmount(photo);
        }
    }

    private void openOCRDialogVerifcation(double amount){
        LinearLayout alertBoxLayout = new LinearLayout(getApplicationContext());
        alertBoxLayout.setOrientation(LinearLayout.VERTICAL);

        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.verify_value);

        final TextView txtNearby = new TextView(this);
        txtNearby.setText(getString(R.string.nearby_businesses));
        alertBoxLayout.addView(txtNearby);

        //get locations near me
        db.open();
        final List<Location> locationsInRange = db.getLocationsInRange(0.2, new LatLng(TORONTO_LAT, TORONTO_LONG), "");
        db.close();

        String[] locationNames = new String[locationsInRange.size()];
        for(int i =0; i < locationsInRange.size(); i++){
            locationNames[i] = locationsInRange.get(i).getMerchantName();
        }

        final Spinner locationsNearMe = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locationNames);
        //set the spinners adapter to the previously created one.
        locationsNearMe.setAdapter(adapter);
        alertBoxLayout.addView(locationsNearMe);

        final TextView txtamount = new TextView(this);
        txtamount.setText(getString(R.string.amount));
        alertBoxLayout.addView(txtamount);

        final EditText inputAmount = new EditText(this);
        inputAmount.setWidth(800);
        inputAmount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);


        if (amount != -1){
            inputAmount.setText(amount + "");
        }
        alertBoxLayout.addView(inputAmount);

        builder.setView(alertBoxLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //add some data

                if (inputAmount.getText().toString().equals("")) {
                    inputAmount.setError("Amount Required");
                    return;
                }

                String selectedItem = (String) locationsNearMe.getSelectedItem();
                int selectedIndex = -1;
                for (int i =0; i < locationsInRange.size(); i++){
                    if (locationsInRange.get(i).getMerchantName().equals(selectedItem)){
                        selectedIndex = i;
                        break;
                    }

                }
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm z");
                dba.open();
                dba.insertTrackItem(new TrackItem(locationsInRange.get(selectedIndex).getMerchantName(), locationsInRange.get(selectedIndex).getLocationStreet(), locationsInRange.get(selectedIndex).getLocationCity(), formatter.format(new Date(System.currentTimeMillis())), Double.parseDouble(inputAmount.getText().toString()), locationsInRange.get(selectedIndex).get_id()));

                List<TrackItem> items = dba.getAllTrackingInfo();
                dba.close();

                if (items.size() == 0){
                    items.add(new TrackItem("No Purchases Tracked", "", "", "", 0, ""));
                }

                ViewPager vpPager = (ViewPager) findViewById(R.id.viewPagerTrack);
                FragmentStatePagerAdapter adapterViewPager = new MainActivity.MyBudgetTrackerAdapeter(getSupportFragmentManager(), items);
                vpPager.setAdapter(adapterViewPager);



            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.common_google_signin_btn_text_dark_focused));
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.common_google_signin_btn_text_dark_focused));
    }

    private void getImageAmount(Bitmap photo){


        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);
            // the on-device model for text recognition
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();

            Task<FirebaseVisionText> result =
                    detector.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                    String[] allNumberString = firebaseVisionText.getText().replaceAll("\\D+",",").split(",");
                                    double amount = 0;
                                    for (int i = 0; i < allNumberString.length; i++){
                                        try{
                                            if (Double.parseDouble(allNumberString[i].trim()) > amount){
                                                amount = Double.parseDouble(allNumberString[i].trim());
                                            }
                                        }catch (NumberFormatException ex){

                                        }
                                    }
                                    openOCRDialogVerifcation(amount);
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Log.d("Kevin Cox", "Task Failed");
                                        }
                                    });
    }




    public static class MyPagerAdapter extends FragmentStatePagerAdapter {

        private List<Location> data;
        private double budgetAmount;
        public MyPagerAdapter(FragmentManager fragmentManager, List<Location> data, double budgetAmount) {
            super(fragmentManager);
            this.data = data;
            this.budgetAmount = budgetAmount;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return this.data.size();
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            double distance = distanceBetweenPoints(new LatLng(TORONTO_LAT, TORONTO_LONG) , new LatLng(data.get(position).getLocationLatitude(), data.get(position).getLocationLongitude()));
            return BudgetItemFragment.newInstance(position, data.get(position), distance, budgetAmount, data.size());
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public double distanceBetweenPoints (LatLng point1, LatLng point2){


            final int RADIUS_OF_EARTH = 6371; // Radius of the earth

            double latDistance = Math.toRadians(point2.latitude - point1.latitude);
            double lonDistance = Math.toRadians(point2.longitude - point1.longitude);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(point1.latitude)) * Math.cos(Math.toRadians(point2.latitude))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return RADIUS_OF_EARTH * c;
        }
    }

    public static class MyBudgetTrackerAdapeter extends FragmentStatePagerAdapter {

        private List<TrackItem> data;
        private double budgetAmount;
        public MyBudgetTrackerAdapeter(FragmentManager fragmentManager, List<TrackItem> data) {
            super(fragmentManager);
            this.data = data;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return this.data.size();
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            return TrackItemFragment.newInstance(position, data.get(position), data.size());
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }
}
