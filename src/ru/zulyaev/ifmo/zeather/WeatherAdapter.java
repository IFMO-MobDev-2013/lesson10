package ru.zulyaev.ifmo.zeather;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.zulyaev.ifmo.zeather.weather.Forecast;
import ru.zulyaev.ifmo.zeather.weather.Weather;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author seidhe
 */
class WeatherAdapter extends BaseAdapter {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM");

    private final Context context;
    private final IconCache cache;
    private final WeatherTable table;
    private final String degrees;
    private final String kmph;
    private List<WeatherForecast> forecasts;

    WeatherAdapter(Context context, WeatherTable table) {
        this.context = context;
        this.cache = new IconCache(context);
        this.table = table;
        this.degrees = context.getString(R.string.degrees);
        this.kmph = context.getString(R.string.kmph);
        this.forecasts = table.getAll();
    }

    public void refresh() {
        forecasts = table.getAll();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return forecasts.size();
    }

    @Override
    public WeatherForecast getItem(int position) {
        return forecasts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return forecasts.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
            resetViews(holder);
        } else {
            view = inflater.inflate(R.layout.weather_row, parent, false);
            holder = new ViewHolder(
                    (ImageView) view.findViewById(R.id.icon),
                    (TextView) view.findViewById(R.id.location),
                    (TextView) view.findViewById(R.id.temperature),
                    (TextView) view.findViewById(R.id.wind),
                    (LinearLayout) view.findViewById(R.id.forecast)
            );
            view.setTag(holder);
        }
        WeatherForecast weatherForecast = forecasts.get(position);
        Forecast forecast = weatherForecast.getForecast();

        holder.location.setText(weatherForecast.getLocation());
        if (forecast == null) {
            holder.temperature.setText("No data to display");
        } else {
            Weather today = forecast.getToday();
            new IconDownloader(holder.icon, cache).execute(today.getIconUrl());
            holder.icon.setContentDescription(today.getDescription());
            holder.temperature.setText(today.getMinTemp() + degrees);
            holder.wind.setText(today.getWindSpeed() + kmph + " " + today.getWindDirection());

            for (int i = 0; i < forecast.getDays(); ++i) {
                Weather weather = forecast.getDay(i);
                ChildHolder child;
                if (i >= holder.children.size()) {
                    child = inflateChildView(inflater, holder.forecast);
                    holder.forecast.addView(child.root);
                    holder.children.add(child);
                } else {
                    child = holder.children.get(i);
                }
                child.date.setText(DATE_FORMAT.format(weather.getDate()));
                new IconDownloader(child.icon, cache).execute(weather.getIconUrl());
                child.icon.setContentDescription(weather.getDescription());
                child.temperature.setText((weather.getMinTemp() + weather.getMaxTemp()) / 2 + degrees);
            }
        }

        return view;
    }

    private ChildHolder inflateChildView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.forecast, parent, false);
        return new ChildHolder(
                view,
                (TextView) view.findViewById(R.id.date),
                (ImageView) view.findViewById(R.id.icon),
                (TextView) view.findViewById(R.id.temperature)
        );
    }

    private void resetViews(ViewHolder holder) {
        holder.icon.setImageBitmap(null);
        holder.location.setText("");
        holder.temperature.setText("");
        holder.wind.setText("");
        for (ChildHolder child : holder.children) {
            child.icon.setImageBitmap(null);
            child.date.setText("");
            child.temperature.setText("");
        }
    }

    private static class ViewHolder {
        final ImageView icon;
        final TextView location;
        final TextView temperature;
        final TextView wind;
        final LinearLayout forecast;
        final List<ChildHolder> children = new ArrayList<ChildHolder>();

        ViewHolder(ImageView icon, TextView location, TextView temperature, TextView wind, LinearLayout forecast) {
            this.icon = icon;
            this.location = location;
            this.temperature = temperature;
            this.wind = wind;
            this.forecast = forecast;
        }
    }

    private static class ChildHolder {
        final View root;
        final TextView date;
        final ImageView icon;
        final TextView temperature;

        ChildHolder(View root, TextView date, ImageView icon, TextView temperature) {
            this.root = root;
            this.date = date;
            this.icon = icon;
            this.temperature = temperature;
        }
    }

    private static class IconDownloader extends AsyncTask<String, Void, Bitmap> {
        private final ImageView view;
        private final IconCache cache;

        private IconDownloader(ImageView view, IconCache cache) {
            this.view = view;
            this.cache = cache;
        }

        @Override
        protected void onPreExecute() {
            view.setTag(this);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                return cache.getBitmap(params[0]);
            } catch (IOException e) {
                Log.w(WeatherAdapter.class.toString(), "Unable to download icon " + params[0], e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (view.getTag() == this) {
                view.setImageBitmap(bitmap);
            }
        }
    }
}
