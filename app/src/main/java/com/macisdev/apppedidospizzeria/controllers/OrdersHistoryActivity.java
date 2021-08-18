package com.macisdev.apppedidospizzeria.controllers;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.macisdev.apppedidospizzeria.R;
import com.macisdev.apppedidospizzeria.util.DBHelper;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class OrdersHistoryActivity extends AppCompatActivity {
	SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orders_history);

		//create and configure the list
		ListView ordersHistoryList = findViewById(R.id.orders_history_list);

		db = new DBHelper(this).getReadableDatabase();
		Cursor ordersHistoryCursor = db.rawQuery("SELECT _id, order_id, time FROM orders_history", null);

		SimpleCursorAdapter adapterOrderHistory = new SimpleCursorAdapter(this,
				R.layout.layout_listview_two_items,
				ordersHistoryCursor,
				new String[]{"order_id", "time"},
				new int[]{R.id.tv_1, R.id.tv_2},
				0);

		ordersHistoryList.setAdapter(adapterOrderHistory);

		ordersHistoryList.setOnItemClickListener((adapterView, view, i, l) -> {
			//Cursor to get the orderId of the selected item in the list
			Cursor selectedOrderCursor = db.rawQuery("SELECT order_id FROM orders_history WHERE _id = ?",
					new String[]{String.valueOf(l)});

			selectedOrderCursor.moveToFirst();
			String orderId = selectedOrderCursor.getString(0);
			selectedOrderCursor.close();

			new ServerConnectionBackground(orderId).execute();
		});
	}

	private class ServerConnectionBackground extends AsyncTask<Void, Void, String> {
		//private Context context;
		private final String orderId;

		ServerConnectionBackground(String orderId) {
			this.orderId = orderId;
		}

		@Override
		protected String doInBackground(Void... objects) {
			String responseFromWebService;
			try {
				//Variables for the SOAP service
				String NAMESPACE = "http://pizzashopwebservice.macisdev.com/";
				String URL = "http://83.47.223.157:8080/PizzaShopWebService/PizzaShopWebService";
				String METHOD_NAME = "getStoredOrder";
				String SOAP_ACTION = "";

				//SOAP handling logic
				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
				request.addProperty("arg0", orderId);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE transport = new HttpTransportSE(URL);
				transport.call(SOAP_ACTION, envelope);
				SoapPrimitive serverAnswer = (SoapPrimitive) envelope.getResponse();
				responseFromWebService = serverAnswer.toString();

			} catch (Exception e) {
				responseFromWebService = null;
				e.printStackTrace();
			} catch (Error e) {
				responseFromWebService = null;
			}

			return responseFromWebService;
		}

		@Override
		protected void onPostExecute(String serverResponse) {
			if (serverResponse != null) {
				//Launches the activity that shows the details recovered from server
				startActivity(FiledOrderDetails.getFiledOrderIntent(getApplicationContext(), serverResponse));
			}
		}
	}
}