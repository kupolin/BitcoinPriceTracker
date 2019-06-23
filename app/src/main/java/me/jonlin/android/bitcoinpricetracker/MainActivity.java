package me.jonlin.android.bitcoinpricetracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    //Ticker Data (per Symbol)
    //https://apiv2.bitcoinaverage.com/indices/global/ticker/BTCUSD
    //GET https://apiv2.bitcoinaverage.com/indices/{symbol_set}/ticker/{symbol}
    private final String BASE_URL = "https://apiv2.bitcoinaverage.com/indices/global/ticker/BTC";

    TextView mPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getRequest("USD");
        this.mPriceTextView = findViewById(R.id.priceLabel);
        final Spinner spinner = findViewById(R.id.currency_spinner);

        // Create an ArrayAdapter using the String array and a spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.currency_array, R.layout.spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l)
            {
               // Toast.makeText(getBaseContext(), adapterView.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
                getRequest(adapterView.getItemAtPosition(pos).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            }
        });
    }

    private void log(String str)
    {
        Log.d(this.getClass().getSimpleName(), str);
    }

    private void updateUI(String str)
    {
        this.mPriceTextView.setText(str);
    }

    // TODO: complete the letsDoSomeNetworking() method
    // return String currency
    // all requests are made outside of main UI thread.
    // callback logic will be executed on the same thread as the c all back was created.
    // in other words call back is executed on main thread.
    private void getRequest(String country)
    {
        log("getRequestWeather() called");
        // uses a background thread to send requests. A request always is followed by a response
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(BASE_URL + country , new JsonHttpResponseHandler()
        {
            // ran in main thread
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                log("onSuccess() called");
                log("JSON: " + response.toString());

                String baseCur = "N/A";
                try
                {
                    baseCur = response.getString("last");
                    //Toast.makeText(getBaseContext(), baseCur, Toast.LENGTH_SHORT ).show();
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
                updateUI(baseCur);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse)
            {
                // inside anonymous class WeatherController.this
                Log.e(this.getClass().getSimpleName(), "Fail " + throwable.toString());
                log("Status code " + statusCode);

                Toast.makeText(getBaseContext(), "Request Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
