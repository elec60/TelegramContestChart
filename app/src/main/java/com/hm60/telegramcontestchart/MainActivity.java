package com.hm60.telegramcontestchart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hm60.telegramcontestchart.ui.component.TelegramChart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TelegramChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Statistics");
        }

        setTheme(0);

        chart = findViewById(R.id.chart);

        JSONArray jsonArray = readJsonDataFromAssets("chart_data.json");

        int chartsCount = jsonArray.length();
        chartsCount = 1;

        for (int i = 0; i < chartsCount; i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray columnsJSONArray = jsonObject.getJSONArray("columns");
                JSONObject namesJSONObject = jsonObject.getJSONObject("names");
                JSONObject colorsJSONObject = jsonObject.getJSONObject("colors");
                JSONObject typesJSONObject = jsonObject.getJSONObject("types");

                int columnsCount = columnsJSONArray.length();
                if (columnsCount == 0) {
                    continue;
                }

                JSONArray xArray = columnsJSONArray.getJSONArray(0);
                long[] xData = new long[xArray.length() - 1];
                for (int i1 = 0; i1 < xData.length; i1++) {
                    xData[i1] = xArray.getLong(i1 + 1);
                }

                List<Integer[]> yDataList = new ArrayList<>(columnsCount - 1);
                String[] names = new String[columnsCount - 1];
                String[] colors = new String[columnsCount - 1];
                String[] types = new String[columnsCount - 1];
                for (int i1 = 1; i1 < columnsCount; i1++) {
                    JSONArray yArray = columnsJSONArray.getJSONArray(i1);
                    String y = yArray.getString(0);
                    names[i1 - 1] = namesJSONObject.getString(y);
                    colors[i1 - 1] = colorsJSONObject.getString(y);
                    types[i1 - 1] = typesJSONObject.getString(y);

                    Integer[] yData = new Integer[yArray.length() - 1];

                    for (int i2 = 0; i2 < yData.length; i2++) {
                        yData[i2] = yArray.getInt(i2 + 1);
                    }
                    yDataList.add(yData);
                }

                chart.setData(yDataList, xData, names, colors, types);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public JSONArray readJsonDataFromAssets(String jsonFileName) {
        JSONArray jsonArray = null;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(jsonFileName)));

            String mLine;
            StringBuilder json = new StringBuilder();
            while ((mLine = reader.readLine()) != null) {
                json.append(mLine);
            }

            jsonArray = new JSONArray(json.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        return jsonArray;
    }
}
