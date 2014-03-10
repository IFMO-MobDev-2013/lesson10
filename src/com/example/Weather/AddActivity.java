package com.example.Weather;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class AddActivity extends Activity {
	ListView listView;
	EditText editText;
	Button button;
	ArrayList<String> latitude = new ArrayList<>(),longtitude = new ArrayList<>();
	ArrayList<String> addresses = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add);
		editText = (EditText) findViewById(R.id.editText);
		listView = (ListView) findViewById(R.id.listView);
		button = (Button) findViewById(R.id.button);
		editText.setHint("SPB");
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					new DownloadASyncTask().execute(new URL("https://maps.googleapis.com/maps/api/geocode/xml?address=" +
							editText.getText().toString().replaceAll(" ","+") +
							"&sensor=true"));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				WeatherSQLiteOpenHelper openHelper = new WeatherSQLiteOpenHelper(AddActivity.this);
				SQLiteDatabase database = openHelper.getWritableDatabase();
				ContentValues contentValues = new ContentValues();
				contentValues.put(WeatherSQLiteOpenHelper.CITY,addresses.get(position));
				contentValues.put(WeatherSQLiteOpenHelper.LATITUDE,latitude.get(position));
				contentValues.put(WeatherSQLiteOpenHelper.LONGITUDE,longtitude.get(position));
				database.insert(WeatherSQLiteOpenHelper.TABLE_NAME,null,contentValues);
				openHelper.close();
				database.close();
				onBackPressed();
				Toast.makeText(AddActivity.this,"Город добавлен",Toast.LENGTH_SHORT).show();
			}
		});

	}

	class DownloadASyncTask extends AsyncTask<URL, Void, NodeList> {

		@Override
		protected NodeList doInBackground(URL... params) {
			InputStream IN;
			NodeList nodeList = null;
			try {
				IN = params[0]
						.openConnection()
						.getInputStream();
				Element element = DocumentBuilderFactory
						.newInstance()
						.newDocumentBuilder()
						.parse(IN)
						.getDocumentElement();
				nodeList = element.getElementsByTagName("result");
			} catch (Exception e) {

			}
			return nodeList;
		}

		@Override
		protected void onPostExecute(NodeList strings) {
			super.onPostExecute(strings);

			for (int i = 0; i < strings.getLength(); i++) {
				Element element = (Element) strings.item(i);
				NodeList type = element.getElementsByTagName("type");
				for (int j = 0; j < type.getLength(); j++) {
					if (type.item(i).getFirstChild().getNodeValue().equals("locality")) {
						addresses.add(addNewData(element, "formatted_address"));
						addCoordination(element);
						break;
					}
				}

			}
			if (strings.equals(null)) {
				Toast.makeText(AddActivity.this,"Не найдено",Toast.LENGTH_SHORT).show();
			}
			ArrayAdapter adapter = new ArrayAdapter(AddActivity.this,android.R.layout.simple_list_item_1,addresses);

			listView.setAdapter(adapter);
		}

		private String addNewData(Element e, String n) {
			return e.getElementsByTagName(n).item(0)
					.getFirstChild()
					.getNodeValue();
		}
		private void addCoordination(Element e){
			Node node = e.getElementsByTagName("location").item(0);
			NodeList nodeList = node.getChildNodes();
			latitude.add(nodeList.item(1).getFirstChild().getNodeValue());
			longtitude.add(nodeList.item(3).getFirstChild().getNodeValue());
		}
	}

	private boolean checkLink(String city) {
		WeatherSQLiteOpenHelper openHelper = new WeatherSQLiteOpenHelper(this);
		SQLiteDatabase database = openHelper.getWritableDatabase();
		boolean result = false;
		assert database != null;
		Cursor cursor = database.query(WeatherSQLiteOpenHelper.TABLE_NAME, null,
				null,
				null,
				null,
				null,
				null
		);
		while (cursor.moveToNext()) {
			if (city.equals(cursor.getString(cursor.getColumnIndex(WeatherSQLiteOpenHelper.CITY)))) {
				result = true;
				break;
			}
		}
		cursor.close();
		database.close();
		openHelper.close();
		return result;
	}

}
