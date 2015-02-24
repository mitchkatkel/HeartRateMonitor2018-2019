package edu.und.cs.com.heart_monitor;

import android.content.Intent;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothClass;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import edu.und.cs.com.heart_monitor.R;

public class PrefUser extends PreferenceActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.preferences);
        ArrayAdapter<String>list_device = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();
        Toast toast=Toast.makeText(getApplicationContext(),"Please ensure that the BITalino board is discoverable",Toast.LENGTH_LONG);
        toast.show();

        Set<BluetoothDevice> pairdDevices = bluetoothAdapter.getBondedDevices();

        ListPreference listPreference = (ListPreference)findPreference("blue_tooth");
        CharSequence[] entries = new String[pairdDevices.size()];
        CharSequence[] entryValues =  new String[pairdDevices.size()];

        int i =0;

            for(BluetoothDevice device : pairdDevices){
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                entries[i] = deviceName;
                entryValues[i] = deviceAddress;
                list_device.add(deviceName);
                i++;
            }

        setListAdapter(list_device);

        listPreference.setEntries(entries);
        listPreference.setEntryValues(entryValues);

        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                EditTextPreference mac_address = (EditTextPreference)findPreference("macAddress");
                mac_address.setSummary((String)newValue.toString());
                mac_address.setText((String) newValue.toString());
                return false;
            }
        });

        Preference submit = (Preference)findPreference("buttnsubmit");

        submit.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(PrefUser.this, MainActivity.class);
                startActivity(i);
                finish();
                return true;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pref_user, menu);
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

}
