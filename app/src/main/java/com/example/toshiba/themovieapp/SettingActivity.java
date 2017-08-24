package com.example.toshiba.themovieapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private String[] sortEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sortEntries = getResources().getStringArray(R.array.spinner_sort_by_entries);
        spinner = (Spinner) findViewById(R.id.spinner_sort);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, sortEntries);

        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(MainActivity.check);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        if (item.equals(sortEntries[0]))
            MainActivity.check = 0;
        else if (item.equals(sortEntries[1]))
            MainActivity.check = 1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
