package com.macisdev.apppedidospizzeria;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ConfirmationScreenActivity extends AppCompatActivity {
    private static final String ORDER_STATUS = "orderStatus";
    private static final String ORDER_WAITING_TIME = "orderWaitingTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_confirmation_screen);

        Intent intent = getIntent();
        boolean orderSuccessfullyPlaced = intent.getBooleanExtra(ORDER_STATUS, false);
        int waitingTime = intent.getIntExtra(ORDER_WAITING_TIME, -1);

        ImageView imgConfirmation = findViewById(R.id.img_confirmation);
        TextView tvMainMessage = findViewById(R.id.tv_main_message);
        TextView tvSecondaryMessage = findViewById(R.id.tv_secondary_message);

        if (orderSuccessfullyPlaced) {
            imgConfirmation.setImageResource(R.drawable.ic_ok_order);
            tvMainMessage.setText(getString(R.string.order_placed_OK));
            tvSecondaryMessage.setText(String.format(Locale.getDefault(), getString(R.string.order_waiting_time), waitingTime));
        } else {
            imgConfirmation.setImageResource(R.drawable.ic_fail_order);
            tvMainMessage.setText(getString(R.string.order_placed_fail));
            tvSecondaryMessage.setText(getString(R.string.try_again_message));
        }

    }

    public static Intent getConfirmationScreenIntent(Context context, boolean orderSuccessfullyPlaced, int waitingTime) {
        Intent intent = new Intent(context, ConfirmationScreenActivity.class);
        intent.putExtra(ORDER_STATUS, orderSuccessfullyPlaced);
        intent.putExtra(ORDER_WAITING_TIME, waitingTime);
        return intent;
    }

    @Override
    public void onBackPressed() {
        //closes the app
        this.finishAffinity();
    }
}