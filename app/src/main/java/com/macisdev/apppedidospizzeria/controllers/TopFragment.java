package com.macisdev.apppedidospizzeria.controllers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.macisdev.apppedidospizzeria.R;

public class TopFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top, container, false);

        LinearLayout whatsappFrame = view.findViewById(R.id.whatsapp_frame);
        whatsappFrame.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://api.whatsapp.com/send/?phone=34649425570"))));

        return view;
    }
}