package com.macisdev.apppedidospizzeria.controllers;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.macisdev.apppedidospizzeria.R;
import com.macisdev.apppedidospizzeria.util.DBHelper;

public class OrdersHistoryActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orders_history);

		//create and configure the list
		ListView ordersHistoryList = findViewById(R.id.orders_history_list);

		SQLiteDatabase db = new DBHelper(this).getReadableDatabase();
		Cursor ordersHistoryCursor = db.rawQuery("SELECT _id, order_id, time FROM orders_history", null);

		SimpleCursorAdapter adapterOrderHistory = new SimpleCursorAdapter(this,
				R.layout.layout_listview_two_items,
				ordersHistoryCursor,
				new String[]{"order_id", "time"},
				new int[]{R.id.tv_1, R.id.tv_2},
				0);

		ordersHistoryList.setAdapter(adapterOrderHistory);

		//TODO: Implement click listener to the listview
	}
}