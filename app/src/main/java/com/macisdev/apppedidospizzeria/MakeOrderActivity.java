package com.macisdev.apppedidospizzeria;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
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
    }
}
