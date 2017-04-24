package com.akitektuo.clujtransport.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.akitektuo.clujtransport.R;

public class TicketsActivity extends Activity implements View.OnClickListener {
    private AutoCompleteTextView completeTextViewSearch;
    private Button buttonBack;
    private Button buttonSearch;
    private Button buttonSms;
    private Button buttonCounter;
    private Button buttonMachine;
    private ImageView imageSms;
    private ImageView imageCounter;
    private ImageView imageMachine;
    private LinearLayout layoutSms;
    private LinearLayout layoutCounter;
    private LinearLayout layoutMachine;
    private boolean expandedSms = false;
    private boolean expandedCounter = false;
    private boolean expandedMachine = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        completeTextViewSearch = (AutoCompleteTextView) findViewById(R.id.edit_text_tickets_search);
        buttonBack = (Button) findViewById(R.id.button_tickets_back);
        buttonSearch = (Button) findViewById(R.id.button_tickets_search);
        buttonSms = (Button) findViewById(R.id.button_tickets_sms);
        buttonCounter = (Button) findViewById(R.id.button_tickets_counter);
        buttonMachine = (Button) findViewById(R.id.button_tickets_machine);
        imageSms = (ImageView) findViewById(R.id.image_more_sms);
        imageCounter = (ImageView) findViewById(R.id.image_more_counter);
        imageMachine = (ImageView) findViewById(R.id.image_more_machine);
        layoutSms = (LinearLayout) findViewById(R.id.layout_sms_info);
        layoutCounter = (LinearLayout) findViewById(R.id.layout_counter_info);
        layoutMachine = (LinearLayout) findViewById(R.id.layout_machine_info);

        buttonBack.setOnClickListener(this);
        buttonSearch.setOnClickListener(this);
        buttonSms.setOnClickListener(this);
        buttonCounter.setOnClickListener(this);
        buttonMachine.setOnClickListener(this);

        SettingsActivity.refreshList(this, completeTextViewSearch);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_tickets_back:
                super.onBackPressed();
                break;
            case R.id.button_tickets_search:
                if (!completeTextViewSearch.getText().toString().isEmpty()) {
                    Intent stationSearch = new Intent(this, StationsListActivity.class);
                    stationSearch.putExtra(MapActivity.SEARCH, completeTextViewSearch.getText().toString());
                    startActivity(stationSearch);
                    completeTextViewSearch.setText("");
                }
                break;
            case R.id.button_tickets_sms:
                if (!expandedSms) {
                    expandedSms = true;
                    imageSms.setBackgroundResource(R.drawable.collapse_blue);
                    layoutSms.setVisibility(View.VISIBLE);
                } else {
                    expandedSms = false;
                    imageSms.setBackgroundResource(R.drawable.expand_blue);
                    layoutSms.setVisibility(View.GONE);
                }
                break;
            case R.id.button_tickets_counter:
                if (!expandedCounter) {
                    expandedCounter = true;
                    imageCounter.setBackgroundResource(R.drawable.collapse_blue);
                    layoutCounter.setVisibility(View.VISIBLE);
                } else {
                    expandedCounter = false;
                    imageCounter.setBackgroundResource(R.drawable.expand_blue);
                    layoutCounter.setVisibility(View.GONE);
                }
                break;
            case R.id.button_tickets_machine:
                if (!expandedMachine) {
                    expandedMachine = true;
                    imageMachine.setBackgroundResource(R.drawable.collapse_blue);
                    layoutMachine.setVisibility(View.VISIBLE);
                } else {
                    expandedMachine = false;
                    imageMachine.setBackgroundResource(R.drawable.expand_blue);
                    layoutMachine.setVisibility(View.GONE);
                }
                break;
        }
    }
}
