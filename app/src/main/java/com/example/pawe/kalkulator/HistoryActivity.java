package com.example.pawe.kalkulator;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class HistoryActivity extends ListActivity {
    private ListView listView;
    private ArrayAdapter<StringBuilder> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Intent intent = getIntent();
        ArrayList<StringBuilder> history = (ArrayList) intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE);
        Log.d("myTag", history.toString());
        listView = getListView();
        arrayAdapter = new ArrayAdapter<StringBuilder>(
                this,
                R.layout.listview_layout,
                history);
        listView.setAdapter(arrayAdapter);
    }

    public void backClick(View view) {

        finish();
    }

    public void deleteHistoryClick(View view) {
        arrayAdapter.clear();
        arrayAdapter.notifyDataSetChanged();
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
    }
}
