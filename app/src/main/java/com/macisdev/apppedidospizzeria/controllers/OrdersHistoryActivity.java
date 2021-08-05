package com.macisdev.apppedidospizzeria.controllers;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.macisdev.apppedidospizzeria.R;
import com.macisdev.apppedidospizzeria.util.DBHelper;

import java.util.Locale;

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

			String url = String.format(Locale.getDefault(), "http://83.47.223.157:8080/invoices/invoice?id=%s",
					orderId);
			Intent browser= new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browser);
		});
	}
}