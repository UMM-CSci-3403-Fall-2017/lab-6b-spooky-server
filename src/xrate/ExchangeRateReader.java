package xrate;

import java.io.*;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Provide access to basic currency exchange rate services.
 * 
 * //Group members: Sam Miller, Travis Warling
 * @author Spooky Server
 */
public class ExchangeRateReader {

	//initialize variables
	DocumentBuilderFactory dbf;
	DocumentBuilder builder;
	URL url;
	InputStream xmlStream;
	Document xml;
    Element elem;
    NodeList nodes;
	
    /**
     * Construct an exchange rate reader using the given base URL. All requests
     * will then be relative to that URL. If, for example, your source is Xavier
     * Finance, the base URL is http://api.finance.xaviermedia.com/api/ Rates
     * for specific days will be constructed from that URL by appending the
     * year, month, and day; the URL for 25 June 2010, for example, would be
     * http://api.finance.xaviermedia.com/api/2010/06/25.xml
     * 
     * @param baseURL
     *            the base URL for requests
     * @throws IOException 
     * @throws ParserConfigurationException 
     */
    public ExchangeRateReader(String baseURL) throws IOException, ParserConfigurationException {

    	//initialize exchange rate reader using base URL
    	dbf = DocumentBuilderFactory.newInstance();
    	url = new URL(baseURL);
    	builder = dbf.newDocumentBuilder();
    }
    /**
     * Get the exchange rate for the specified currency against the base
     * currency (the Euro) on the specified date.
     * 
     * @param currencyCode
     *            the currency code for the desired currency
     * @param year
     *            the year as a four digit integer
     * @param month
     *            the month as an integer (1=Jan, 12=Dec)
     * @param day
     *            the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public float getExchangeRate(String currencyCode, int year, int month, int day) throws IOException, ParserConfigurationException, SAXException  {
    	
    	float rate = 0;  
    	
    	//convert one digit dates to two digit
    	String zeroMonth = Integer.toString(month);
    	String zeroDay = Integer.toString(day); 
    	
    	if (month < 10){
    		zeroMonth = "0" + Integer.toString(month);
    	}
    	
    	if (day < 10){
    		zeroDay = "0" + Integer.toString(day);
    	}
    	
    	//assemble new URL
    	String relativePath = Integer.toString(year) + "/" + zeroMonth + "/" + zeroDay + ".xml";	
        URL newUrl = new URL(url.toExternalForm() + relativePath);  
        
        //open stream at new url and parse xml
        xmlStream = newUrl.openStream();
    	builder = dbf.newDocumentBuilder();
    	xml = builder.parse(xmlStream);
        elem = xml.getDocumentElement();
        elem.normalize();
        NodeList tagged = elem.getElementsByTagName("*");
        
    	// find requested node depending on currency code input
        for (int i = 0; i < tagged.getLength(); i++) {
                Node node = tagged.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if(node.getNodeName()=="currency_code" && node.getTextContent().equals(currencyCode)) {
                    	rate = Float.parseFloat(node.getNextSibling().getNextSibling().getTextContent());        
                    }
                }
            }
        //return float rate
        return rate;
    }

    /**
     * Get the exchange rate of the first specified currency against the second
     * on the specified date.
     * 
     * @param currencyCode
     *            the currency code for the desired currency
     * @param year
     *            the year as a four digit integer
     * @param month
     *            the month as an integer (1=Jan, 12=Dec)
     * @param day
     *            the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public float getExchangeRate(
    		String fromCurrency, String toCurrency,
            int year, int month, int day) throws IOException, ParserConfigurationException, SAXException  {		
        
    	//use exchange rate method to obtain both rate floats
       	float rate1 = getExchangeRate(fromCurrency,year,month,day);    	
       	float rate2 = getExchangeRate(toCurrency,year,month,day);
       	
       	//return new rate
       	return (rate1/rate2);
    }
}