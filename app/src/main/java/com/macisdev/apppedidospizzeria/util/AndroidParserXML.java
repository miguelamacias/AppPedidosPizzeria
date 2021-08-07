/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.macisdev.apppedidospizzeria.util;

import com.macisdev.apppedidospizzeria.model.OrderElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

public class AndroidParserXML {

	private final String customerName;
	private final String customerPhone;
	private final String deliveryMethod;
	private final String customerAddress;
	private final String paymentMethod;
	private final int orderStatus;
	private final String orderId;
	private final String orderDateTime;
	private final double totalPrice;

	private final ArrayList<OrderElement> orderElements = new ArrayList<>();

	public AndroidParserXML(String xml) {

		Document document;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
					new InputSource(new StringReader(xml)));
		} catch (Exception e) {
			e.printStackTrace();
			document = null;
		}

		//Get order header
		assert document != null;
		NodeList orderInfo = document.getElementsByTagName("order_info");

		Node nodeOrderInfo = orderInfo.item(0);

		Element elementOrderInfo = (Element) nodeOrderInfo;


		orderId = elementOrderInfo.getElementsByTagName("order_id").item(0).getTextContent();
		orderDateTime = elementOrderInfo.getElementsByTagName("order_datetime").item(0).getTextContent();

		customerName = elementOrderInfo.getElementsByTagName("customer_name").item(0).getTextContent();
		customerPhone = elementOrderInfo.getElementsByTagName("customer_phone").item(0).getTextContent();
		deliveryMethod = elementOrderInfo.getElementsByTagName("delivery_method").item(0).getTextContent();
		customerAddress = elementOrderInfo.getElementsByTagName("customer_address").item(0).getTextContent();
		paymentMethod = elementOrderInfo.getElementsByTagName("payment_method").item(0).getTextContent();
		orderStatus = Integer.parseInt(
				elementOrderInfo.getElementsByTagName("order_status").item(0).getTextContent());
		totalPrice = Double.parseDouble(elementOrderInfo.getElementsByTagName("total_price").
				item(0).getTextContent());

		//Parse order elements		
		NodeList orderElementsList = document.getElementsByTagName("pizza");

		for (int i = 0; i < orderElementsList.getLength(); i++) {
			Node currentNode = orderElementsList.item(i);

			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				//Creates the orderElement object to add its details after being parsed from the xml
				OrderElement orderElement = new OrderElement();

				Element currentElement = (Element) currentNode;
				orderElement.setCode(Integer.parseInt(currentElement.getElementsByTagName("code").item(0).getTextContent()));
				orderElement.setName(currentElement.getElementsByTagName("name").item(0).getTextContent());
				orderElement.setSize(currentElement.getElementsByTagName("size").item(0).getTextContent());
				orderElement.setExtras(currentElement.getElementsByTagName("extras").item(0).getTextContent());
				double price = Double.parseDouble(currentElement.getElementsByTagName("price").
						item(0).getTextContent());
				orderElement.setPrice(price);

				//Adds the element parsed to the order
				orderElements.add(orderElement);
			}
		}
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public String getDeliveryMethod() {
		return deliveryMethod;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public int getOrderStatus() {
		return orderStatus;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getOrderDateTime() {
		return orderDateTime;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public ArrayList<OrderElement> getOrderElements() {
		return orderElements;
	}
}
