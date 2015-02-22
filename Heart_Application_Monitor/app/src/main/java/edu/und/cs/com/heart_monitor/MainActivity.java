package edu.und.cs.com.heart_monitor;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        actionBar.addTab(
                actionBar.newTab()
                .setText("Home")
                .setTabListener(this));
        actionBar.addTab(
                actionBar.newTab()
                .setText("User Settings")
                .setTabListener(this));
        actionBar.addTab(
                actionBar.newTab()
                .setText("Reports")
                .setTabListener(this));
        actionBar.addTab(
                actionBar.newTab()
                .setText("Help")
                .setTabListener(this));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
//        mViewPager.setCurrentItem(tab.getPosition());

            mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            //return PlaceholderFragment.newInstance(position + 1);
            switch(position){
                case 0:
                    return new HmFragment();
                case 1:
                    return new UFragment();
                case 2:
                    return new RFragment();
                case 3:
                    return new HFragment();

            }
            return null;

        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }
    }



    public static class HmFragment extends Fragment {
ViewPager mViewPager;
        Button btnUser;
       public FragmentTransaction ft;
        UFragment user = new UFragment();



        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState){

            final View rootView = inflater.inflate(R.layout.fragment_home, container, false);

            rootView.findViewById(R.id.btnECG)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), ECG.class);
                            startActivity(intent);
                        }
                    });


                   return rootView;
        }


    }

    public static class UFragment extends Fragment{
        Button btnSubmit;
        EditText name;
        EditText phone;
        EditText email;
        EditText emergencyName;
        EditText emergencyPhone;
        EditText emergencyEmail;

        TextView txtResults;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_user, container,false);

            name = (EditText)rootView.findViewById(R.id.edtName);
            phone = (EditText)rootView.findViewById(R.id.edtPhone);
            email = (EditText)rootView.findViewById(R.id.edtEmail);
            emergencyName = (EditText)rootView.findViewById(R.id.edtEmergencyName);
            emergencyPhone = (EditText)rootView.findViewById(R.id.edtEmergencyPhone);
            emergencyEmail = (EditText)rootView.findViewById(R.id.edtEmergencyemail);
            btnSubmit = (Button)rootView.findViewById(R.id.btnUserSubmit);
            txtResults = (TextView)rootView.findViewById(R.id.txtResults);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String n,e,p, emerg_n,emerg_p,emerg_e;
                    n = name.getText().toString().trim();
                    e = email.getText().toString().trim();
                    p = phone.getText().toString().trim();

                    emerg_n = emergencyName.getText().toString().trim();
                    emerg_e = emergencyEmail.getText().toString().trim();
                    emerg_p = emergencyPhone.getText().toString().trim();

                    Intent myIntent = new Intent(getActivity(),UserData.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name",n);
                    bundle.putString("email",e);
                    bundle.putString("phone",p);
                    bundle.putString("Emerg_name",emerg_n);
                    bundle.putString("Emerg_email",emerg_e);
                    bundle.putString("Emerg_phone",emerg_p);
                    myIntent.putExtras(bundle);
                    startActivityForResult(myIntent,1);

                }

            });


            return rootView;

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode,data);
            if((requestCode == 1)&&(resultCode== Activity.RESULT_OK)){
                Bundle dataBundle = data.getExtras();
                String name = dataBundle.getString("name");
                String email = dataBundle.getString("email");
                String phone = dataBundle.getString("phone");
                String emergName = dataBundle.getString("Emerg_name");
                String emergPhone = dataBundle.getString("Emerg_phone");
                String emergEmail = dataBundle.getString("Emerg_email");
                txtResults.setText("Name: " + name + "\nEmail: " + email + ":\nPhone: "+phone +"\n\nEmergency Contact" + "\nName: " +emergName +"\nPhone: " + emergPhone +"\nEmail" + emergEmail);
            }else{
                Toast toast = Toast.makeText(getActivity(), "Something went wrong. Please try again.",Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }

    public static class RFragment extends Fragment{

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_report,container, false);

            return rootView;
        }
    }

    public static class HFragment extends Fragment{

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_help,container,false);

            return rootView;
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
