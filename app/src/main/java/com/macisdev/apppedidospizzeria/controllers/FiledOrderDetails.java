package com.macisdev.apppedidospizzeria.controllers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.macisdev.apppedidospizzeria.R;
import com.macisdev.apppedidospizzeria.model.OrderElement;
import com.macisdev.apppedidospizzeria.util.AndroidParserXML;

import java.util.Locale;

public class FiledOrderDetails extends AppCompatActivity {
	public static final String FULL_ORDER_KEY = "fullOrderKey";

	private String orderId;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filed_order_details);

		TextView tvFiledOrderId = findViewById(R.id.tv_filed_order_id);
		TextView tvFiledOrderDate = findViewById(R.id.tv_filed_order_date);
		TextView tvFiledOrderCustomerName = findViewById(R.id.tv_filed_order_customer_name);
		TextView tvFiledOrderCustomerPhone = findViewById(R.id.tv_filed_order_customer_phone);
		TextView tvFiledOrderAddress = findViewById(R.id.tv_filed_order_address);
		TextView tvFiledOrderPaymentMethod = findViewById(R.id.tv_filed_order_payment_method);
		TextView tvFiledTotalPrice = findViewById(R.id.tv_filed_total_price);
		ListView filedOrderContentList = findViewById(R.id.filed_order_content_list);

		AndroidParserXML parserXML = new AndroidParserXML(getIntent().getStringExtra(FULL_ORDER_KEY));
		orderId = parserXML.getOrderId();
		tvFiledOrderId.setText(orderId);
		tvFiledOrderDate.setText(parserXML.getOrderDateTime());
		tvFiledOrderCustomerName.setText(parserXML.getCustomerName());
		tvFiledOrderCustomerPhone.setText(parserXML.getCustomerPhone());
		tvFiledOrderAddress.setText(parserXML.getCustomerAddress());
		tvFiledOrderPaymentMethod.setText(parserXML.getPaymentMethod());
		tvFiledTotalPrice.setText(String.format(Locale.getDefault(), "%.2f€", parserXML.getTotalPrice()));



		ArrayAdapter<OrderElement> filedOrderAdapter = new ArrayAdapter<>(this,
				android.R.layout.simple_list_item_1, parserXML.getOrderElements());
		filedOrderContentList.setAdapter(filedOrderAdapter);

	}

	public static Intent getFiledOrderIntent(Context context, String fullOrder) {

		Intent intent = new Intent(context, FiledOrderDetails.class);
		intent.putExtra(FULL_ORDER_KEY, fullOrder);

		return intent;
	}

	//called from the android:onclick of the button
	public void getInvoice(View v) {
		String url = String.format(Locale.getDefault(), "http://83.47.223.157:8080/invoices/invoice?id=%s",
				orderId);
		Intent browser= new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browser);
	}
}