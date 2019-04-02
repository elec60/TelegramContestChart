package com.hm60.telegramcontestchart;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.hm60.telegramcontestchart.ui.component.TelegramChart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    boolean isDark = false;

    TelegramChart chart;
    LinearLayout checkBoxesContainer;

    private MenuItem mSpinnerItem1 = null;
    CharSequence[] sequences = new CharSequence[5];

    private ValueAnimator colorAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(new ColorDrawable(-986896));

        if (getActionBar() != null) {
            getActionBar().setBackgroundDrawable(new ColorDrawable(-11436638));
            getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Statistics</font>"));
        }

        setContentView(R.layout.activity_main);

        checkBoxesContainer = findViewById(R.id.checkbox_container);

        if (getActionBar() != null) {
            getActionBar().setTitle("Statistics");
        }

        chart = findViewById(R.id.chart);

        for (int i = 0; i < sequences.length; i++) {
            sequences[i] = "Chart #" + (i + 1);
        }

        setDataByIndex(0);

    }

    private void setDataByIndex(int index) {
        JSONArray jsonArray = readJsonDataFromAssets("chart_data.json");

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            JSONArray columnsJSONArray = jsonObject.getJSONArray("columns");
            JSONObject namesJSONObject = jsonObject.getJSONObject("names");
            JSONObject colorsJSONObject = jsonObject.getJSONObject("colors");
            JSONObject typesJSONObject = jsonObject.getJSONObject("types");

            int columnsCount = columnsJSONArray.length();

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

            checkBoxesContainer.removeAllViews();
            for (int i1 = 0; i1 < yDataList.size(); i1++) {
                String name = names[i1];
                String color = colors[i1];

                checkBoxesContainer.addView(createCheckBox(i1, name, color));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("RestrictedApi")
    private CheckBox createCheckBox(int i1, String name, String color) {
        final CheckBox cb = new CheckBox(this);
        cb.setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(4), 0, AndroidUtilities.dp(4));
        cb.setChecked(true);
        cb.setText(name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cb.setButtonTintList(ColorStateList.valueOf(Color.parseColor(color)));
        }
        cb.setTag(i1);

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chart.setActiveChart((int) buttonView.getTag(), isChecked);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem toggleDarkMenuItem = menu.findItem(R.id.nightMode);
        toggleDarkMenuItem.setIcon(new BitmapDrawable(getResources(), moonBitmap()));

        mSpinnerItem1 = menu.findItem(R.id.menu_spinner1);
        View view1 = mSpinnerItem1.getActionView();
        if (view1 instanceof Spinner) {
            final Spinner spinner = (Spinner) view1;


            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item,
                    sequences);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    setDataByIndex(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.nightMode) {
            return super.onOptionsItemSelected(item);
        }

        toggleDarkMode();

        return true;

    }

    private void toggleDarkMode() {
        this.isDark = !this.isDark;

        float[] fArr = new float[2];
        float f = 1.0f;
        fArr[0] = isDark ? 1.0f : 0.0f;
        if (isDark) {
            f = 0.0f;
        }
        fArr[1] = f;

        colorAnimator = ValueAnimator.ofFloat(fArr);
        colorAnimator.setDuration(150);
        colorAnimator.setInterpolator(new DecelerateInterpolator(2.0f));
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                chart.applyTheme((Float) animation.getAnimatedValue());
            }
        });
        colorAnimator.start();

        getWindow().setBackgroundDrawable(new ColorDrawable(this.isDark ? -15393241 : -986896));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(this.isDark ? -15064018 : -12426366);
        }

    }

    private Bitmap moonBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(AndroidUtilities.dp(32), AndroidUtilities.dp(32), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(bitmap.getWidth() * 0.5f, bitmap.getHeight() * 0.5f, bitmap.getWidth() * 0.28f, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        paint.setColor(Color.TRANSPARENT);
        canvas.drawCircle(bitmap.getWidth() * 0.7f, bitmap.getHeight() * 0.34f, bitmap.getWidth() * 0.25f, paint);

        return bitmap;
    }
}