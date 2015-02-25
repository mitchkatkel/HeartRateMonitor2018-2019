package edu.und.cs.com.heart_monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

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
        public android.app.Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            switch(position){
                case 0:
                    return new HmFragment();
                case 1:
                    return new PFragment();
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
       public FragmentTransaction ft;

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container,Bundle savedInstanceState){
            final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
            final String macAddress = myPrefs.getString("macAddress",null );
            rootView.findViewById(R.id.btnECG).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (macAddress != null) {
                        Intent intent = new Intent(getActivity(), ECG.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getActivity(), "Please assign a macaddress first", Toast.LENGTH_LONG).show();
                    }
                }
            });

                   return rootView;
        }
    }

    public static class PFragment extends PreferenceFragment{

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            ArrayAdapter<String> list_device = new ArrayAdapter<String>(this.getActivity() , android.R.layout.simple_list_item_1);

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            bluetoothAdapter.startDiscovery();
            Set<BluetoothDevice> pairdDevices = bluetoothAdapter.getBondedDevices();
            ListPreference listPreference = (ListPreference)findPreference("blue_tooth");
            CharSequence[] entries = new String[pairdDevices.size()];
            CharSequence[] entryValues =  new String[pairdDevices.size()];

            int i = 0;

            for(BluetoothDevice device : pairdDevices){
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                entries[i] = deviceName;
                entryValues[i] = deviceAddress;
                list_device.add(deviceName);
                i++;
            }

            listPreference.setEntries(entries);
            listPreference.setEntryValues(entryValues);

            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
;
                    EditTextPreference mac_address = (EditTextPreference)findPreference("macAddress");

                    mac_address.setSummary((String) newValue.toString());
                    mac_address.setText((String) newValue.toString());

                    return false;
                }
            });
       }
    }

    public static class RFragment extends Fragment implements  AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
        ListView reportsList;                                                                //reference to listview that contains recorded ECG sessions
        File fileDir;                                                                        //reference to internal file directory
        File[] filesList;                                                                    //used to store list of internal files
        ArrayList<String> fileNames = new ArrayList();                                       //stores names of current files in internal directory
        ArrayAdapter myArrayAdapter;                                                         //allows ListView object to me updated using information gathered

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_report,container, false);

            fileNames.clear();
            myArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,android.R.id.text1, fileNames);
            reportsList = (ListView) rootView.findViewById(R.id.lstvECGFiles);
            reportsList.setOnItemClickListener(this);
            reportsList.setOnItemLongClickListener(this);
            fileDir = getActivity().getApplicationContext().getFilesDir();                  //retrieve reference internal file directory
            filesList = fileDir.listFiles();                                                //retrieve list of existing files
            if(filesList != null){
                for(File file : filesList){                                                 //add file names to ArrayList
                    String name = file.getName();
                    fileNames.add(name);
                }
            }
            reportsList.setAdapter(myArrayAdapter);                                         //assign an ArrayAdapter to UI ListView
            return rootView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Bundle myBundle = new Bundle();                                                 //Bundle fileName to send to ViewRecording Activity
            myBundle.putString("fileName",fileNames.get(position));
            Intent newIntent = new Intent(getActivity(),ViewRecording.class);
            newIntent.putExtras(myBundle);
            startActivity(newIntent);
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            getActivity().getApplicationContext().deleteFile(fileNames.get(position));      //delete file from internal file directory
            ((BaseAdapter)reportsList.getAdapter()).notifyDataSetChanged();                 //MAY NOT BE NEEDED CHECK LATER
            fileNames.remove(position);                                                     //delete unwanted file
            myArrayAdapter.notifyDataSetChanged();                                          //update UI ListView
            return false;
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
