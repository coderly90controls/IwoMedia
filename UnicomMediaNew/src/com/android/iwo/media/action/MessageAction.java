package com.android.iwo.media.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MessageAction {

	/**
	 * 消息时间戳
	 */
	public String timestamp;

	/**
	 * 消息类型
	 */
	public String category;

	/**
	 * 图片和语音加密流
	 */
	public String richbody;

	/**
	 * 音频时长
	 */
	public String duartion;
	public String head;
	public String nick;

	public void doc(Document doc) {

		// 取到xml中所有元素
		NodeList nl = doc.getElementsByTagName("custom");
		// 取得xml中元素个数
		int len = nl.getLength();
		for (int i = 0; i < len; i++) {
			// 遍历每一个元素
			Element elt = (Element) nl.item(i);
			// 取得元素节点
			Node timestamp = elt.getElementsByTagName("timestamp").item(0);
			Node richbody = elt.getElementsByTagName("richbody").item(0);
			Node duartion = elt.getElementsByTagName("duartion").item(0);
			Node category = elt.getElementsByTagName("category").item(0);
			Node head = elt.getElementsByTagName("head").item(0);
			Node nick = elt.getElementsByTagName("nick").item(0);
			// 取得节点中的内容
			try {
				if (timestamp != null)
					this.timestamp = timestamp.getFirstChild().getNodeValue();
			} catch (Exception e) {
				this.timestamp = "0";
			}

			try {
				if (richbody != null)
					this.richbody = richbody.getFirstChild().getNodeValue();
			} catch (Exception e) {
				this.richbody = "0";
			}

			try {
				if (duartion != null)
					this.duartion = duartion.getFirstChild().getNodeValue();
			} catch (Exception e) {
				this.duartion = "0";
			}

			try {
				if (category != null)
					this.category = category.getFirstChild().getNodeValue();
			} catch (Exception e) {
				this.category = "0";
			}
			
			try {
				if (head != null)
					this.head = head.getFirstChild().getNodeValue();
			} catch (Exception e) {
				this.head = "0";
			}
			
			try {
				if (nick != null)
					this.nick = nick.getFirstChild().getNodeValue();
			} catch (Exception e) {
				this.nick = "0";
			}
		}
	}

	public void getMessage(String str) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;

		factory.setValidating(false);
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
		}

		InputStream is = null;

		try {
			is = new ByteArrayInputStream(str.getBytes());
			document = builder.parse(is);
		} catch (SAXException e) {
		} catch (IOException e) {
		}
		doc(document);
	}
}
