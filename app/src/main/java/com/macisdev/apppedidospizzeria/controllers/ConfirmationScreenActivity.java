package com.macisdev.apppedidospizzeria.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.macisdev.apppedidospizzeria.R;
import com.macisdev.apppedidospizzeria.model.OrderElement;
import com.macisdev.apppedidospizzeria.model.OrderSingleton;
import com.macisdev.apppedidospizzeria.util.DBHelper;

import java.util.Calendar;
import java.util.Locale;

public class ConfirmationScreenActivity extends AppCompatActivity {
    private static final String ORDER_STATUS = "orderStatus";
    private static final String ORDER_WAITING_TIME = "orderWaitingTime";
    private static final String ORDER_ID = "orderId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_confirmation_screen);

        Intent intent = getIntent();
        boolean orderSuccessfullyPlaced = intent.getBooleanExtra(ORDER_STATUS, false);
        int waitingTime = intent.getIntExtra(ORDER_WAITING_TIME, -1);
        String orderId = intent.getStringExtra(ORDER_ID);

        ImageView imgConfirmation = findViewById(R.id.img_confirmation);
        TextView tvMainMessage = findViewById(R.id.tv_main_message);
        TextView tvSecondaryMessage = findViewById(R.id.tv_secondary_message);
        TextView tvOrderId = findViewById(R.id.tv_order_id);
        TextView tvDownloadInvoice = findViewById(R.id.tv_download_invoice);

        if (orderSuccessfullyPlaced) {
            //Adds the order to the local history
            addOrderToHistory(orderId);

            //Configures the visible screen to show that the order has been placed successfully
            imgConfirmation.setImageResource(R.drawable.ic_ok_order);
            tvMainMessage.setText(getString(R.string.order_placed_OK));
            tvSecondaryMessage.setText(String.format(Locale.getDefault(),
                    getString(R.string.order_waiting_time), waitingTime));
            tvOrderId.setText(String.format(Locale.getDefault(),
                    getString(R.string.your_order_id), orderId));

            //Clears the current order so the user can place another one when needed
            OrderSingleton.getInstance().getOrderElementsList().clear();

            //Configures the link to obtain the invoice
            tvDownloadInvoice.setOnClickListener(view -> {
                String url = String.format(Locale.getDefault(), "http://83.47.223.157:8080/invoices/invoice?id=%s",
                        orderId);
                Intent browser= new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browser);
            });
        } else {
            //Configures the visible screen to show that the order hasn't been placed
            imgConfirmation.setImageResource(R.drawable.ic_fail_order);
            tvMainMessage.setText(getString(R.string.order_placed_fail));
            tvSecondaryMessage.setText(getString(R.string.try_again_message));
            tvDownloadInvoice.setVisibility(View.GONE);

            //Deletes the Delivery object from the order so it doesn't get added twice
            int deliveryIndex = -1;
            for (int i = 0; i < OrderSingleton.getInstance().getOrderElementsList().size(); i++) {
                OrderElement element = OrderSingleton.getInstance().getOrderElementsList().get(i);
                if (element.getCode() == DBHelper.DELIVERY_CODE) {
                    deliveryIndex = i;
                }
            }
            if (deliveryIndex >= 0) {
                OrderSingleton.getInstance().getOrderElementsList().remove(deliveryIndex);
            }
        }
    }

    public static Intent getConfirmationScreenIntent(Context context, boolean orderSuccessfullyPlaced,
                                                     int waitingTime, String orderId) {
        Intent intent = new Intent(context, ConfirmationScreenActivity.class);
        intent.putExtra(ORDER_STATUS, orderSuccessfullyPlaced);
        intent.putExtra(ORDER_WAITING_TIME, waitingTime);
        intent.putExtra(ORDER_ID, orderId);
        return intent;
    }

    private void addOrderToHistory(String orderId) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String date = String.format(Locale.getDefault(),"%02d/%02d/%d",
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.YEAR));

        ContentValues values = new ContentValues();
        values.put("order_id", orderId);
        values.put("time", date);

        db.insertOrThrow("orders_history", null, values);
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }
}