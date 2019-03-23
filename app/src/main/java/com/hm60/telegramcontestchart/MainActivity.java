package com.hm60.telegramcontestchart;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.StaticLayout;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

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

        setTheme(R.style.AppTheme_Night);

        setContentView(R.layout.activity_main);

        LinearLayout container = findViewById(R.id.checkbox_container);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Statistics");
        }


        chart = findViewById(R.id.chart);

        JSONArray jsonArray = readJsonDataFromAssets("chart_data.json");

        int chartsCount = jsonArray.length();
        chartsCount = 5;

        for (int i = 4; i < chartsCount; i++) {
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

                chart.setData(yDataList, xData, names, colors, types, "Followers");

                for (int i1 = 0; i1 < yDataList.size(); i1++) {
                    String name = names[i1];
                    String color = colors[i1];

                    container.addView(createCheckBox(i1, name, color));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private CheckBox createCheckBox(int i1, String name, String color) {
        final CheckBox cb = new CheckBox(this);
        cb.setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(4), 0, AndroidUtilities.dp(4));
        cb.setChecked(true);
        cb.setText(name);
        cb.setHighlightColor(Color.parseColor(color));
        cb.setTag(i1);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chart.setActiveChart((int)buttonView.getTag(), isChecked);
            }
        });
        return cb;
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
