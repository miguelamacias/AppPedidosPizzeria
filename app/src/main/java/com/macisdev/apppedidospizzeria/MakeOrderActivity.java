package com.macisdev.apppedidospizzeria;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MakeOrderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order);
    }

    public void makeOrder(View v) throws Exception {
        //Get information about the order
        EditText etCustomerName = findViewById(R.id.et_customer_name);
        EditText etCustomerPhone = findViewById(R.id.et_customer_phone);
        EditText etCustomerAddress = findViewById(R.id.et_customer_address);

        String customerName = etCustomerName.getText().toString();
        String customerPhone = etCustomerPhone.getText().toString();
        String customerAddress = etCustomerAddress.getText().toString();
        String orderId = UUID.randomUUID().toString();

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


    }

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

            Toast.makeText(MakeOrderActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
