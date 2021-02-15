package com.macisdev.apppedidospizzeria;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class PlaceOrderActivity extends AppCompatActivity {
    private static final String CUSTOMER_SAVED_NAME = "customerSavedName";
    private static final String CUSTOMER_SAVED_PHONE = "customerSavedPhone";
    private static final String CUSTOMER_SAVED_ADDRESS = "customerSavedAddress";

    //Declares the views needed
    EditText etCustomerName;
    EditText etCustomerPhone;
    EditText etCustomerAddress;
    RadioButton rbCard;
    RadioButton rbCash;
    RadioButton rbHomeDelivery;
    RadioButton rbPickRestaurant;

    //Preferences object used to store the last entered customer information
    SharedPreferences savedCustomerPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        //Initializes the views
        etCustomerName = findViewById(R.id.et_customer_name);
        etCustomerPhone = findViewById(R.id.et_customer_phone);
        etCustomerAddress = findViewById(R.id.et_customer_address);
        rbCard = findViewById(R.id.rb_card);
        rbCash = findViewById(R.id.rb_cash);
        rbHomeDelivery = findViewById(R.id.rb_home_delivery);
        rbPickRestaurant = findViewById(R.id.rb_pick_restaurant);

        savedCustomerPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        etCustomerName.setText(savedCustomerPreferences.getString(CUSTOMER_SAVED_NAME, ""));
        etCustomerPhone.setText(savedCustomerPreferences.getString(CUSTOMER_SAVED_PHONE, ""));
    }

    //Sends the order when the button is pressed
    public void placeOrder(View v) {
        //Gets the order information
        String customerName = etCustomerName.getText().toString();
        String customerPhone = etCustomerPhone.getText().toString();
        String customerAddress = etCustomerAddress.getText().toString();

        String deliveryMethod = "";
        String paymentMethod = "";

        if (rbCard.isChecked()) {
            paymentMethod = getString(R.string.card);
        }
        if (rbCash.isChecked()) {
            paymentMethod = getString(R.string.cash);
        }
        if (rbHomeDelivery.isChecked()) {
            deliveryMethod = getString(R.string.home_delivery);
        }
        if ((rbPickRestaurant.isChecked())) {
            deliveryMethod = getString(R.string.pick_at_restaurant);
        }

        try {
            //Checks that the information is correct before proceding, throws an exception otherwise
            if (!checkCorrectValues(customerName, customerPhone, customerAddress)) {
                throw new InputMismatchException();
            }

            //creates the xml document content
            Document xmlContent = createXMLContent(customerName, customerPhone,
                    customerAddress, deliveryMethod, paymentMethod);

            //creates the XML file
            File xmlFile = createXMLFile(xmlContent);

            //gets an String representation of the xml content
            String xmlAsString = getXmlAsString(xmlFile);

            //sends the order to the server using the AsyncTask
            new ServerConectionBackground(xmlAsString).execute();

            //Stores the customer information into the shared preferences
            Editor savedCustomerPreferencesEditor = savedCustomerPreferences.edit();
            savedCustomerPreferencesEditor.putString(CUSTOMER_SAVED_NAME, customerName);
            savedCustomerPreferencesEditor.putString(CUSTOMER_SAVED_PHONE, customerPhone);
            if (rbHomeDelivery.isChecked()) {
                savedCustomerPreferencesEditor.putString(CUSTOMER_SAVED_ADDRESS, customerAddress);
            }
            savedCustomerPreferencesEditor.apply();


        } catch (InputMismatchException e){
            //Do nothing, this exception has already been controlled, its only purpose is to stop the method
        } catch (ParserConfigurationException | TransformerException | IOException e) {
            Toast.makeText(this, R.string.random_error, Toast.LENGTH_SHORT).show();
        }
    }

    //Creates the xml content representing the order
    public Document createXMLContent(String customerName, String customerPhone,
                                  String customerAddress, String deliveryMethod, String paymentMethod)
            throws ParserConfigurationException {
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
        Element orderDateTime = document.createElement("order_datetime");
        orderDateTime.appendChild(document.createTextNode(String.valueOf(System.currentTimeMillis())));
        orderInfo.appendChild(orderDateTime);

        Element name = document.createElement("customer_name");
        name.appendChild(document.createTextNode(customerName));
        orderInfo.appendChild(name);

        Element phone = document.createElement("customer_phone");
        phone.appendChild(document.createTextNode(customerPhone));
        orderInfo.appendChild(phone);

        Element delivery = document.createElement("delivery_method");
        delivery.appendChild(document.createTextNode(deliveryMethod));
        orderInfo.appendChild(delivery);

        Element address = document.createElement("customer_address");
        address.appendChild(document.createTextNode(customerAddress));
        orderInfo.appendChild(address);

        Element payment = document.createElement("payment_method");
        payment.appendChild(document.createTextNode(paymentMethod));
        orderInfo.appendChild(payment);

        //Adds the total price to the xml file
        double totalPrice = 0;
        for (OrderElement element : OrderSingleton.getInstance().getOrderElementsList()) {
            totalPrice += element.getPrice();
        }
        Element price = document.createElement("total_price");
        price.appendChild(document.createTextNode(String.format(Locale.ENGLISH, "%.2f", totalPrice)));
        orderInfo.appendChild(price);

        //Add the ordered stuffs to the xml (suppose everything is a pizza for simplicity)
        for (OrderElement currentElement : OrderSingleton.getInstance().getOrderElementsList()) {
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

        return document;
    }

    public File createXMLFile(Document document) throws TransformerException {
        //creates the physical xml
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource domSource = new DOMSource(document);
        File outputDirectory = getFilesDir();
        File outputFile = new File(outputDirectory, UUID.randomUUID() + ".xml");
        StreamResult streamResult = new StreamResult(outputFile);
        transformer.transform(domSource, streamResult);

        return outputFile;
    }

    //Converts an xml file to string
    private String getXmlAsString(File xmlFile) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(xmlFile));

        while (reader.ready()) {
            builder.append(reader.readLine());
        }

        return builder.toString();
    }

    //Enables or disables the address editText
    public void deliveryMethodChanged(View v) {
        EditText etCustomerAddress = findViewById(R.id.et_customer_address);

        if (v.getId() == R.id.rb_home_delivery) {
            etCustomerAddress.setEnabled(true);
            etCustomerAddress.setText(savedCustomerPreferences.getString(CUSTOMER_SAVED_ADDRESS, ""));

        } else {
            etCustomerAddress.setEnabled(false);
            etCustomerAddress.setText(R.string.pick_at_restaurant);
            etCustomerAddress.setError(null);
        }
    }

    //Checks that the info provided by the customer meets the requeriments
    private boolean checkCorrectValues(String customerName, String customerPhone, String customerAddress) {
        boolean validInfo = false;
        try {

            //Checks that all the information has been given
            if (customerName.length() < 1) {
                throw new InputMismatchException();
            }
            if (customerPhone.length() < 1) {
                throw new InputMismatchException();
            }
            if (customerAddress.length() < 1 && rbHomeDelivery.isChecked()) {
                throw new InputMismatchException();
            }

            //checks that the phone has a valid format using 'libphonenumber' external library
            String number = etCustomerPhone.getText().toString();
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            PhoneNumber phoneNumber = phoneNumberUtil.parse(number, "ES");
            boolean isValidPhone = phoneNumberUtil.isValidNumberForRegion(phoneNumber, "ES");

            if (!isValidPhone) {
                throw new NumberFormatException("invalid");
            }

            if (phoneNumberUtil.getNumberType(phoneNumber) != PhoneNumberUtil.PhoneNumberType.MOBILE) {
                throw new NumberFormatException("notMobile");
            }
            //If no exceptions have been thrown the values are all right
            validInfo = true;
        } catch (InputMismatchException e) {//Empty fields control
            //Checks the empty fields to show an error message in them
            if (customerName.length() < 1) {
                etCustomerName.setError(getString(R.string.error_empty_name));
            }
            if (customerPhone.length() < 1) {
                etCustomerPhone.setError(getString(R.string.error_empty_phone));
            }
            if (customerAddress.length() < 1 && rbHomeDelivery.isChecked()) {
                etCustomerAddress.setError(getString(R.string.error_empty_address));
            }
        } catch (NumberParseException e) {//Valid phone number control (library exception)
            etCustomerPhone.setError(getString(R.string.invalid_number));
        } catch (NumberFormatException e) {//Valid phone number control (meets the requeriments of this project)

            if (e.getMessage().equalsIgnoreCase("invalid")) {
                etCustomerPhone.setError(getString(R.string.invalid_number));
            } else {
                etCustomerPhone.setError(getString(R.string.not_mobile_number));
            }

        } catch (Exception e) {//Control others exceptions
            Toast.makeText(this, R.string.random_error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return validInfo;
    }

    //Inner class that manages the background proccess that uses the network
    @SuppressLint("StaticFieldLeak")
    private class ServerConectionBackground extends AsyncTask<Void, Void, String> {
        //private Context context;
        private String xmlContent;

        ServerConectionBackground(String xmlContent) {
            this.xmlContent = xmlContent;
        }


        @Override
        protected String doInBackground(Void... objects) {
            String responseFromWebService;
            try {
                //Variables for the SOAP service
                String NAMESPACE = "http://pizzashopwebservice.macisdev.com/";
                String URL = "http://88.6.163.82:8080/PizzaShopWebService/PizzaShopWebService";
                String METHOD_NAME = "sendOrder";
                String SOAP_ACTION = "";

                //SOAP handling logic
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("arg0", xmlContent);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.call(SOAP_ACTION, envelope);
                SoapPrimitive serverAnswer = (SoapPrimitive) envelope.getResponse();
                responseFromWebService = serverAnswer.toString();

            } catch (Exception e) {
                responseFromWebService = null;
                e.printStackTrace();
            } catch (Error e) {
                responseFromWebService = null;
            }

            return responseFromWebService;
        }

        @Override
        protected void onPostExecute(String serverResponse) {

            if (serverResponse != null) {
                //Gets the orderId and the waiting time from the server response
                String orderId = serverResponse.substring(serverResponse.indexOf("_") + 1);
                int waitingTime = Integer.parseInt(serverResponse.substring(0, serverResponse.indexOf("_")));

                //Start the confirmation activity with the right info
                startActivity(ConfirmationScreenActivity.getConfirmationScreenIntent(
                        PlaceOrderActivity.this, true, waitingTime, orderId));
            } else {
                startActivity(ConfirmationScreenActivity.getConfirmationScreenIntent(
                        PlaceOrderActivity.this, false, -1, "null"));
            }
        }
    }
}
