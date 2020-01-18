package com.macisdev.apppedidospizzeria;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class PlaceOrderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
    }

    //When the button to send the order is pressed
    public void makeOrder(View v) {
        //Get information about the order
        EditText etCustomerName = findViewById(R.id.et_customer_name);
        EditText etCustomerPhone = findViewById(R.id.et_customer_phone);
        EditText etCustomerAddress = findViewById(R.id.et_customer_address);
        RadioButton rbCard = findViewById(R.id.rb_card);
        RadioButton rbCash = findViewById(R.id.rb_cash);
        RadioButton rbHomeDelivery = findViewById(R.id.rb_home_delivery);
        RadioButton rbPickRestaurant = findViewById(R.id.rb_pick_restaurant);

        String customerName = etCustomerName.getText().toString();
        String customerPhone = etCustomerPhone.getText().toString();
        String customerAddress = etCustomerAddress.getText().toString();
        String orderId = UUID.randomUUID().toString();
        String deliveryMethod = "";
        String paymentMethod = "";

        if(rbCard.isChecked()) {
            paymentMethod = getString(R.string.card);
        }
        if(rbCash.isChecked()) {
            paymentMethod = getString(R.string.cash);
        }
        if(rbHomeDelivery.isChecked()) {
            deliveryMethod = getString(R.string.home_delivery);
        }
        if ((rbPickRestaurant.isChecked())) {
            deliveryMethod = getString(R.string.pick_at_restaurant);
        }

        try {
            //Checking that all the information has been given
            if(customerName.length() < 1) {
                throw new InputMismatchException();
            }
            if(customerPhone.length() < 1) {
                throw new InputMismatchException();
            }
            if(customerAddress.length() < 1 && rbHomeDelivery.isChecked()) {
                throw new IllegalArgumentException();
            }


            /*
             *  Building the XML file
             */
            //Creates the document
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            //Creates the root element
            Element root = document.createElement("order");
            document.appendChild(root);

            //Creates the order_info node
            Element orderInfo = document.createElement("order_info");
            root.appendChild(orderInfo);

            //Create the elements for the actual information about the order
            Element orderIdElement = document.createElement("order_id");
            orderIdElement.appendChild(document.createTextNode(orderId));
            orderInfo.appendChild(orderIdElement);

            Element name = document.createElement("customer_name");
            name.appendChild(document.createTextNode(customerName));
            orderInfo.appendChild(name);

            Element phone = document.createElement("customer_phone");
            phone.appendChild(document.createTextNode(customerPhone));
            orderInfo.appendChild(phone);

            Element address = document.createElement("customer_address");
            address.appendChild(document.createTextNode(customerAddress));
            orderInfo.appendChild(address);

            Element payment = document.createElement("payment_method");
            payment.appendChild(document.createTextNode(paymentMethod));
            orderInfo.appendChild(payment);

            Element delivery = document.createElement("delivery_method");
            delivery.appendChild(document.createTextNode(deliveryMethod));
            orderInfo.appendChild(delivery);

            //Add the ordered stuffs to the xml (suppose everything is a pizza for simplicity)
            for (OrderElement currentElement : MainActivity.ORDER_ELEMENTS) {
                Element pizza = document.createElement("pizza");
                root.appendChild(pizza);

                Element pizzaCode = document.createElement("code");
                pizzaCode.appendChild(document.createTextNode(String.valueOf(currentElement.getCode())));
                pizza.appendChild(pizzaCode);

                Element pizzaName = document.createElement("name");
                pizzaName.appendChild(document.createTextNode(currentElement.getName()));
                pizza.appendChild(pizzaName);

                Element pizzaSize = document.createElement("size");
                pizzaSize.appendChild(document.createTextNode(currentElement.getSize()));
                pizza.appendChild(pizzaSize);

                Element pizzaExtras = document.createElement("extras");
                pizzaExtras.appendChild(document.createTextNode(currentElement.getExtras()));
                pizza.appendChild(pizzaExtras);

                Element pizzaPrice = document.createElement("price");
                pizzaPrice.appendChild(document.createTextNode(String.valueOf(currentElement.getPrice())));
                pizza.appendChild(pizzaPrice);
            }

            //creates the actual xml
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource domSource = new DOMSource(document);
            File outputDirectory = getFilesDir();
            File outputFile = new File(outputDirectory, orderId + ".xml");
            StreamResult streamResult = new StreamResult(outputFile);
            transformer.transform(domSource, streamResult);

            //gets an String representation of the xml content
            String xmlContent = getXmlAsString(outputFile);

            //sends the order to the server
            new ServerConectionBackground(xmlContent).execute();
        } catch (InputMismatchException e) {
            Toast.makeText(this, R.string.incomplete_info, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.random_error, Toast.LENGTH_SHORT).show();
        }


    }

    //Converts an xml file to a string
    private String getXmlAsString(File xmlFile) throws IOException{
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(xmlFile));

        while(reader.ready()) {
            builder.append(reader.readLine());
        }

        return builder.toString();
    }

    //Inner class that manages the background proccess that uses the network
    @SuppressLint("StaticFieldLeak")
    private class ServerConectionBackground extends AsyncTask<Void, Void, Boolean>{
        //private Context context;
        private String xmlContent;

        ServerConectionBackground(String xmlContent) {
            this.xmlContent = xmlContent;
        }


        @Override
        protected Boolean doInBackground(Void... objects) {
            Socket socket = null;
            BufferedWriter writeToServer = null;
            boolean orderPlacedSucessfully;
            try {
                socket = new Socket("83.59.44.170", 8080);
                writeToServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writeToServer.write("1");
                writeToServer.flush();
                writeToServer.write(xmlContent);
                writeToServer.flush();
                orderPlacedSucessfully = true;

            } catch (IOException e) {
                orderPlacedSucessfully = false;
                e.printStackTrace();

            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                    if (writeToServer != null) {
                        writeToServer.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
            return orderPlacedSucessfully;
        }

        @Override
        protected void onPostExecute(Boolean orderPlacedSucessfully) {
            String message;
            if (orderPlacedSucessfully) {
                message = getString(R.string.order_placed_OK);
            } else {
                message = getString(R.string.order_placed_fail);
            }

            Toast.makeText(PlaceOrderActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void deliveryMethodChanged(View v) {
        EditText etCustomerAddress = findViewById(R.id.et_customer_address);
        if (v.getId() == R.id.rb_home_delivery) {
            etCustomerAddress.setEnabled(true);
        } else {
            etCustomerAddress.setEnabled(false);
        }
    }
}
