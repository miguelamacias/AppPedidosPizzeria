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
	public static final String ORDER_ID_KEY = "orderIdKey";
	public static final String ORDER_DATETIME_KEY = "orderDateTimeKey";
	public static final String CUSTOMER_NAME_KEY = "customerNameKey";
	public static final String CUSTOMER_PHONE_KEY = "customerPhoneKey";
	public static final String DELIVERY_METHOD_KEY = "deliveryMethodKey";
	public static final String CUSTOMER_ADDRESS_KEY = "customerAddressKey";
	public static final String PAYMENT_METHOD_KEY = "paymentMethodKey";
	public static final String ORDER_STATUS_KEY = "orderStatusKey";
	public static final String TOTAL_PRICE_KEY = "totalPriceKey";
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

		orderId = getIntent().getStringExtra(ORDER_ID_KEY);
		tvFiledOrderId.setText(orderId);
		tvFiledOrderDate.setText(getIntent().getStringExtra(ORDER_DATETIME_KEY));
		tvFiledOrderCustomerName.setText(getIntent().getStringExtra(CUSTOMER_NAME_KEY));
		tvFiledOrderCustomerPhone.setText(getIntent().getStringExtra(CUSTOMER_PHONE_KEY));
		tvFiledOrderAddress.setText(getIntent().getStringExtra(CUSTOMER_ADDRESS_KEY));
		tvFiledOrderPaymentMethod.setText(getIntent().getStringExtra(PAYMENT_METHOD_KEY));
		double totalPrice = getIntent().getDoubleExtra(TOTAL_PRICE_KEY, 0);
		tvFiledTotalPrice.setText(String.format(Locale.getDefault(), "%.2f€", totalPrice));

		AndroidParserXML parserXML = new AndroidParserXML(getIntent().getStringExtra(FULL_ORDER_KEY));

		ArrayAdapter<OrderElement> filedOrderAdapter = new ArrayAdapter<>(this,
				android.R.layout.simple_list_item_1, parserXML.getOrderElements());
		filedOrderContentList.setAdapter(filedOrderAdapter);

	}

	public static Intent getFiledOrderIntent(Context context, String orderId, String orderDateTime, String customerName,
											 String customerPhone, String deliveryMethod, String customerAddress,
											 String paymentMethod, int orderStatus, double totalPrice,
											 String fullOrder) {

		Intent intent = new Intent(context, FiledOrderDetails.class);
		intent.putExtra(ORDER_ID_KEY, orderId);
		intent.putExtra(ORDER_DATETIME_KEY, orderDateTime);
		intent.putExtra(CUSTOMER_NAME_KEY, customerName);
		intent.putExtra(CUSTOMER_PHONE_KEY, customerPhone);
		intent.putExtra(DELIVERY_METHOD_KEY, deliveryMethod);
		intent.putExtra(CUSTOMER_ADDRESS_KEY, customerAddress);
		intent.putExtra(PAYMENT_METHOD_KEY, paymentMethod);
		intent.putExtra(ORDER_STATUS_KEY, orderStatus);
		intent.putExtra(TOTAL_PRICE_KEY, totalPrice);
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