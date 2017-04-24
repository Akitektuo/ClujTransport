package com.akitektuo.clujtransport.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akitektuo.clujtransport.R;

public class MenuActivity extends Activity implements View.OnClickListener {
    private Button buttonBack;
    private Button buttonSearch;
    private Button buttonBus;
    private Button buttonTicket;
    private Button buttonSettings;
    private Button buttonHelp;
    private Button buttonOk;
    private ImageView imageTextError;
    private ImageView imageTextBar;
    private TextView textExplanation;
    private AutoCompleteTextView completeTextViewSearch;
    public static final String PASS = "pass";
    public static final String CHECK = "check";

    public static final String STARTING_NAVIGATION = "starting navigation";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        buttonBack = (Button) findViewById(R.id.button_menu_back);
        buttonSearch = (Button) findViewById(R.id.button_menu_search);
        buttonBus = (Button) findViewById(R.id.button_menu_bus);
        buttonTicket = (Button) findViewById(R.id.button_menu_ticket);
        buttonSettings = (Button) findViewById(R.id.button_settings);
        buttonHelp = (Button) findViewById(R.id.button_help);
        buttonOk = (Button) findViewById(R.id.button_menu_ok);
        imageTextBar = (ImageView) findViewById(R.id.image_after_explanation);
        textExplanation = (TextView) findViewById(R.id.text_menu_explanation);
        imageTextError = (ImageView) findViewById(R.id.image_menu_error);
        completeTextViewSearch = (AutoCompleteTextView) findViewById(R.id.edit_text_menu_search);

        SettingsActivity.refreshList(this, completeTextViewSearch);

        buttonBack.setOnClickListener(this);
        buttonSearch.setOnClickListener(this);
        buttonBus.setOnClickListener(this);
        buttonTicket.setOnClickListener(this);
        buttonSettings.setOnClickListener(this);
        buttonHelp.setOnClickListener(this);
        buttonOk.setOnClickListener(this);

        System.out.println("---------------" + getStatusBarHeight());
    }

    @Override
    public void onClick(View view) {
        Intent stationsListIntent = new Intent(this, StationsListActivity.class);
        Intent stationSearch = new Intent(this, StationsListActivity.class);
        Intent ticketIntent = new Intent(this, TicketsActivity.class);
        switch (view.getId()) {
            case R.id.button_menu_back:
                super.onBackPressed();
                break;
            case R.id.button_menu_search:
                if (!completeTextViewSearch.getText().toString().isEmpty()) {
                    stationSearch.putExtra(MapActivity.SEARCH, completeTextViewSearch.getText().toString());
                    startActivity(stationSearch);
                    completeTextViewSearch.setText("");
                }
                break;
//                imageTextError.setVisibility(View.VISIBLE);
//                textExplanation.setText(R.string.routes_explained);
//                imageTextBar.setVisibility(View.VISIBLE);
//                buttonOk.setVisibility(View.VISIBLE);
            case R.id.button_menu_bus:
                startActivity(stationsListIntent);
                break;
            case R.id.button_menu_ticket:
                startActivity(ticketIntent);
                break;
            case R.id.button_settings:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final View viewDialog = LayoutInflater.from(this).inflate(R.layout.dialog_password, null);
                builder.setView(viewDialog);
                builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editTextPassword = (EditText) viewDialog.findViewById(R.id.edit_text_password_dialog);
                        String password = editTextPassword.getText().toString();
                        if (password.equals("2412")) {
                            startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                            Toast.makeText(getBaseContext(), "Authentication successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Wrong password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
                break;
            case R.id.button_help:
                break;
            case R.id.button_menu_ok:
                imageTextError.setVisibility(View.GONE);
                textExplanation.setText("");
                imageTextBar.setVisibility(View.GONE);
                buttonOk.setVisibility(View.GONE);
                break;
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
