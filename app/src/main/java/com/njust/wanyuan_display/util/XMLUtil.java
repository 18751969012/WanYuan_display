package com.njust.wanyuan_display.util;

import android.content.Context;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * XML类工具类
 */
public class XMLUtil {
	
	private final Logger log = Logger.getLogger(XMLUtil.class);

	
	/**
	 * 解析XML
	 */
	public Map<String, String> loadGoodsXml(Context context, String strxml, String updateTime) throws JDOMException, IOException {
		if (null == strxml || "".equals(strxml)) {
			return null;
		}
		log.info("strxml:" + strxml);
		
		List<String> goodsList = new ArrayList<String>();
		List<String> huodaoList = new ArrayList<String>();
		
		Map<String, String> m = new HashMap<String, String>();
		InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		Element root = doc.getRootElement();
		List<Element> list = root.getChildren();
		Iterator<Element> it = list.iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			
			List<Element> elements = e.getChildren();
			for (Iterator<Element> iterator = elements.iterator(); iterator.hasNext();) {
				Element element = (Element) iterator.next();

//				if ( e.getName()=="machine" ) {
//
//				} else if ( e.getName()=="cabinet" ) {
//					String feed_back = StringUtil.equals(element.getAttributeValue("feed_back"), "yes") ?"1" :"0";
//
//				} else if ( e.getName()=="huodao" ) {
//					String huodao = element.getAttributeValue("huodao");
//
//					DBManager.saveHuodaoActiveTime(context, huodao, HuodaoType.getActiveTime(type));
//				}

			}
		}
		in.close();
		return m;
	}
	
}
