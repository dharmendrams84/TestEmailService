
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;






public class GetReportDetailsFromCO {

	static Logger logger = Logger.getLogger(GetReportDetailsFromCO.class);
	static int periodId = 0;

	static String emplName = "";

	static int emplNumber = 0;

	static String discountGroupCode = "";

	static String periodName = "";

	static String discountEntlments = "";

	static String retailLimit = "";

	static String currentPeriodPurchases = "";

	static int entlId = 0;

	static int emplGroupId = 0;

	static String maxSpendEntl = "";
	
	static String discGroupCode = "";

	static String discountPercent = "";
	static String getEepEmailDtls = "select * from ct_eep_emp_eml where  MSGTO is not null";
	static String getEepItemDtls = "select * from ct_eep_item where empl_number = ? and rownum <2";

	static String getPeriodDtlsQuery = "SELECT * FROM ct_eep_period WHERE sysdate BETWEEN period_start_date AND period_end_date+1";

	static String getPeriodIdQuery = "SELECT period_id FROM ct_eep_period WHERE TO_DATE(sysdate, ''DD-MON-YY'') >=  "
			+ "to_date(period_start_date,''DD-MON-YY'') "
			+ "AND TO_DATE(sysdate, ''DD-MON-YY'') <=to_date(period_end_date,''DD-MON-YY'')";

	static String merchandiseGroupIdQuery = "select id_mrhrc_gp , de_itm from as_itm where id_itm = ?";

	static String divisionQuery = "select divdesc from ct_merch_hierarchy where id_mrhrc_gp = ?";

	static String getEmplMstrDtlsQuery = "select * from ct_eep_empl_master where empl_number = ?";
	static Connection connection = null;

	static String getEntlIdQuery = "select entitlement_id from ct_eep_item where empl_number = ? and rownum <2";

	static String getEntlDtlsQuery = "select * from ct_eep_entitlement where  EMPL_GROUP_ID = ? ";

	static String getEmplGrpDescQuery = "select EMPL_GROUP_DESCR from ct_eep_group where EMPL_GROUP_ID = ?";

	static String getTransactionDtls = "select * from ct_eep_item where empl_number = ? and period_id = ?";

	static String getItemSizeQuery = "select ed_sz from as_itm_stk where id_itm = ?";

	static List<String> purchaseDateList = new ArrayList<String>();
	static List<String> divisionList = new ArrayList<String>();
	static List<String> itemIdList = new ArrayList<String>();
	static List<String> itemsDescriptionList = new ArrayList<String>();
	static List<String> itemsSizeList = new ArrayList<String>();
	static List<String> retailPriceList = new ArrayList<String>();
	static List<String> itemSizeList = new ArrayList<String>();
	static List<String> discountPriceList = new ArrayList<String>();
	static List<String> itemDescList = new ArrayList<String>();
	
	static List<String>  maxSpendEntlList = new ArrayList<String>();
	static  List<String> discountPercentList = new ArrayList<String>();
	static  List<String> discountDivList = new ArrayList<String>();
	static  List<String> entlDescList = new ArrayList<String>();
	
	

	static BigDecimal totalRetailsPriceBD = new BigDecimal(0);
	static BigDecimal totalNetPriceDB = new BigDecimal(0);

	static BigDecimal totalRetailsPriceBDWithDisc = new BigDecimal(0);
	static BigDecimal totalNetPriceDBWithDisc = new BigDecimal(0);
	
	static BigDecimal totalRetailsPriceBDWithoutDisc = new BigDecimal(0);
	static BigDecimal totalNetPriceDBWithoutDisc = new BigDecimal(0);
	
	static BigDecimal maxSpendAmtBD = new BigDecimal(0);

	static String driverClass = "";
	static String url = "";
	static String userName = "";
	static String passWord = "";

	static Boolean mailSendStatus = Boolean.TRUE;
	
	static Map<Integer,String> msgToMap = new HashMap<Integer,String>();
	static Map<Integer,String> msgCcMap = new HashMap<Integer,String>();
	
	static String user = null;
	static String pass = null;
	
	private static Connection getConnection(String driverClass, String url,
			String userName, String passWord) {
		Connection connection = null;
		try {
			
			Class.forName(driverClass);
			connection = DriverManager.getConnection(url, userName, passWord);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	static List<String> getMerchandiseGroupId(String idItem)
	{
		List<String> list = new ArrayList<String>();
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement(merchandiseGroupIdQuery);
			preparedStatement.setString(1, idItem);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				list.add(resultSet.getString("id_mrhrc_gp"));
				if(resultSet.getString("de_itm")!=null && !"".equalsIgnoreCase(resultSet.getString("de_itm"))){
				list.add(resultSet.getString("de_itm"));
				}else{
					list.add("No Item Desc");	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	static String geMerchandiseGrpId(String idItem) {
		String merchandiseGroupId = "";
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement(merchandiseGroupIdQuery);
			preparedStatement.setString(1, idItem);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				merchandiseGroupId = resultSet.getString("id_mrhrc_gp");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return merchandiseGroupId;
	}

	static String geItemDivision(String merchandiseGrpId) {
		String divisionName = "";
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement(divisionQuery);
			preparedStatement.setString(1, merchandiseGrpId);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				divisionName = resultSet.getString("divdesc");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return divisionName;
	}

	static String getTransaDetails(int emplId, Integer periodId) {
		String transactionDetails = "Store Id\t WorkStattion Id\tBusiness Date\ttrans Id\tLN_ITM\tEMPL Id\tPeriod\tItem Id<br><br>";
		try {

			String queryString = "select * from ct_eep_item where empl_number  = ? and  period_id = ?";

			PreparedStatement preparedStatement = connection
					.prepareStatement(queryString);
			preparedStatement.setString(1, "" + emplId + "");
			preparedStatement.setInt(2, periodId);

			ResultSet resultSet = preparedStatement.executeQuery();
			
			
			while (resultSet.next()) {

				String ID_ITM = resultSet.getString("ID_ITM");

				List<String> list = getMerchandiseGroupId(ID_ITM);
				String merchandiseGrpId = "";
				String itemDesc ="";
				if(list!=null&&list.size()!=0){
				 merchandiseGrpId = list.get(0);
				 itemDesc = list.get(1);
				}else{
					merchandiseGrpId="no Merchandise";
					itemDesc = "No ItemDesc";
				}
				String divisionName = geItemDivision(merchandiseGrpId);
				purchaseDateList.add(resultSet.getString("DC_DY_BSN"));
				itemIdList.add(resultSet.getString("ID_ITM"));
				retailPriceList.add(resultSet.getString("MO_EXTN_LN_ITM_RTN"));
				discountPriceList
						.add(resultSet.getString("MO_EXTN_DSC_LN_ITM"));
				if (divisionName != null && !"".equalsIgnoreCase(divisionName)) {
					divisionList.add(divisionName);
				} else {
					divisionName = "no Division";
					divisionList.add(divisionName);
				}
				itemDescList.add(itemDesc);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return transactionDetails;
	}

	public static void getEepDtls() {

		try {

			PreparedStatement preparedStatement = connection
					.prepareStatement(getEepItemDtls);
			preparedStatement.setInt(1, emplNumber);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				emplGroupId = resultSet.getInt("EMPL_GROUP_ID");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void getCtEepEmailDtls() {

		try {

			PreparedStatement preparedStatement = connection
					.prepareStatement(getEepEmailDtls);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				
				msgToMap.put(resultSet.getInt(1) , resultSet.getString(2));
				msgCcMap.put(resultSet.getInt(1) , resultSet.getString(3));
			}

		} catch (Exception e)
		{
			logger.error("Exception thrown while getting employee email details from ct_eep_emp_eml table" +  e);
		}

	}

	public static void getEmplMstrDtls() {
		try {
			PreparedStatement pstmt = connection
					.prepareStatement(getEmplMstrDtlsQuery);
			pstmt.setInt(1, emplNumber);
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				emplName = resultSet.getString("firstname") + " "
						+ resultSet.getString("lastname");
			}

		} catch (SQLException e) {
			logger.error("Exception thrown while getting employee name from ct_eep_empl_master table" +  e);
		}

	}

	public static void getEmplPeriodDtls() {
		try {
			PreparedStatement pstmt = connection
					.prepareStatement(getPeriodDtlsQuery);

			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				periodId = resultSet.getInt("PERIOD_ID");
				periodName = resultSet.getString("PERIOD_NAME");
			}

		} catch (SQLException e) {
			logger.error("Exception thrown while getting employee period details from ct_eep_period table" +  e);
		}

	}

	public static void getEntitlementId() {

		try {
			PreparedStatement pstmt = connection.prepareStatement(getEntlIdQuery);
			pstmt.setInt(1, emplNumber);
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				entlId = resultSet.getInt("ENTITLEMENT_ID");
			}

		} catch (SQLException e) {
			logger.error("Exception thrown while getting employee's entitlement ID from ct_eep_item table" +  e);
		}

	}

	public static void getEntitlementDtls() {

		try {
			PreparedStatement pstmt = connection.prepareStatement(getEntlDtlsQuery);
			//pstmt.setInt(1, entlId);
			pstmt.setInt(1, emplGroupId);
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				maxSpendEntlList.add(resultSet.getString("MAX_SPEND_ENTITLED"));
				maxSpendEntl = resultSet.getString("MAX_SPEND_ENTITLED");
				maxSpendAmtBD = new BigDecimal(Double.parseDouble(maxSpendEntl))
						.setScale(2, RoundingMode.HALF_UP);
				discountPercent = resultSet.getString("DISCOUNT_PERCENT");
				discountPercentList.add( resultSet.getString("DISCOUNT_PERCENT"));
				entlDescList.add(resultSet.getString("ENTITLEMENT_DESCR"));
				String divisionDiscount = resultSet.getString("DISCOUNT_DIVISION");
				if(divisionDiscount!=null&&!"".equalsIgnoreCase(divisionDiscount)){
					if(Integer.parseInt(divisionDiscount)==new Integer(50))
					discountDivList.add("DYN"+"  ");
					discountDivList.add("GRG"+"  ");
				}else{
					discountDivList.add("Store  ");
				}
			}

		} catch (SQLException e) {
			logger.error("Exception thrown while getting employee entitlement details from ct_eep_entitlement table" +  e);
		}

	}

	public static String getItemSize(String itemId) {

		String itemSize = "";

		try {
			PreparedStatement pstmt = connection
					.prepareStatement(getItemSizeQuery);
			pstmt.setString(1, itemId);

			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				itemSize = resultSet.getString("ed_sz");
			}

		} catch (SQLException e) 
		{
			logger.error("Exception thrown while getting item's size from as_itm_stk table" +  e);
		}
		return itemSize;
	}

	public static void getEmplGroupDtls() {

		try {
			PreparedStatement pstmt = connection
					.prepareStatement(getEmplGrpDescQuery);
			
			pstmt.setInt(1, emplGroupId);
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) 
			{
				discGroupCode = resultSet.getString("EMPL_GROUP_DESCR");
			}

		} catch (SQLException e) {
			logger.error("Exception thrown while getting employee's discount group description from ct_eep_group table" +  e);
		}

	}

	public static void configureHTMLContent(StringBuilder htmlMailContent) {

		htmlMailContent.append("<html><title></title>");
		htmlMailContent
				.append("<body style='font-size:12px;font-family:Trebuchet MS;'>");
		htmlMailContent
				.append("<table width='600px' align='left' border='1' cellpadding='0' cellspacing='0' style='border-top:5px solid white;'");
		htmlMailContent
				.append("<tr border='1' ><th>Employee Name</th><th>Employee Number</th><th>Discount Group</th><th>Reporting Period</th></tr>");

		htmlMailContent.append("<tr border='1' ><td>" + emplName + "</td><td>"
				+ emplNumber + "</td>" + "<td>"+discGroupCode+"</td> " + "<td>" + periodName
				+ "</td></tr></table><br><br>");
		htmlMailContent
				.append("                                                                                            ");
		htmlMailContent
				.append("<table width='600px' align='left' border='1' cellpadding='0' cellspacing='0' style='border-top:5px solid white;'");
		htmlMailContent
				.append("<tr border='1' ><th>Discount Entitlement(s)</th><th>Retail $ Limit(s)"
						+ "</th><th>Current Period Purchases</th><th>Solde Remaining</th></tr>");
		
		for(int i=0;i<maxSpendEntlList.size();i++){
			htmlMailContent.append("<tr border='1' ><td>" 
					//+ entlDescList.get(i)
					+discountDivList.get(i)+discountPercentList.get(i)+"%"
					+ "</td><td>" + "$" + maxSpendEntlList.get(i)
					+ "</td><td>$" + totalRetailsPriceBD + "</td><td>$"
					+ maxSpendAmtBD.subtract(totalRetailsPriceBD)
					+ "</td></tr>");

			htmlMailContent.append("");
		}
		
		/*htmlMailContent.append("<tr border='1' ><td>" + "Store "
				+ discountPercent + "%" + "<td><td>" + "$" + maxSpendEntl
				+ "</td>$" + totalRetailsPriceBD + "<td>$"
				+ maxSpendAmtBD.subtract(totalRetailsPriceBD)
				+ "</td></tr></table><br><br>");*/

		htmlMailContent.append("</table><br><br>");

		htmlMailContent
				.append("<table width='800px' align='left' border='1' cellpadding='0' cellspacing='0' style='border-top:5px solid white;'");
		htmlMailContent
				.append("<tr border='1'><th width='200px'>TOTAL Employee Purchases With Employee Discount applied<th>"
						+ "<th></th><th></th><th></th><th></th><th></th><th></th></tr>");
		htmlMailContent
				.append("<tr border='1' ><th>Purchase Date</th><th>Division</th><th>Article Item</th><th>Description</th>"
						+ "<th>Size</th><th>Retail Price</th><th>Discount %</th><th>Net Price</th></tr>");
		for (int i = 0; i < purchaseDateList.size(); i++) {

			BigDecimal retailPriceBD = new BigDecimal(retailPriceList.get(i));
			BigDecimal netPriceBD = new BigDecimal(discountPriceList.get(i));
			retailPriceBD.setScale(2,RoundingMode.HALF_UP);
			netPriceBD.setScale(2,RoundingMode.HALF_UP);
			
			BigDecimal discPercent = BigDecimal.ZERO;
			discPercent.setScale(2, RoundingMode.HALF_UP);
			discPercent = netPriceBD.divide(retailPriceBD,2, RoundingMode.HALF_UP);
			discPercent = discPercent.multiply(new BigDecimal(100));
			//discPercent = (netPriceBD.divide(retailPriceBD)).multiply(new BigDecimal(100));
			discPercent = new BigDecimal(100).subtract(discPercent);
			
			/*System.out.println("retailPriceBD " + retailPriceBD
					+ "  netPriceBD " + netPriceBD + " : " + discPercent);*/
			if(!discPercent.equals(BigDecimal.ZERO)){
			totalNetPriceDBWithDisc = totalNetPriceDBWithDisc.add(netPriceBD);
			totalRetailsPriceBDWithDisc = totalRetailsPriceBDWithDisc.add(ret);
			String itemId = itemIdList.get(i);
			htmlMailContent.append("<tr border='1' ><td>"
					+ purchaseDateList.get(i) + "</td><td>"
					+ divisionList.get(i) + "</td><td>" + itemIdList.get(i)
					+ "</td><td>" + itemDescList.get(i) + "</td>" + "<td>"
					+ getItemSize(itemId) + "</td><td>$"
					+ retailPriceList.get(i) + "</td><td>" + discPercent + "%"
					+ "</td><td>$" + discountPriceList.get(i) + "</td></tr>");
			}
		}

		htmlMailContent
				.append("<tr border='1' ><th>Totals</th><th></th><th></th><th></th>"
						+ "<th></th><th>$"
						+ totalRetailsPriceBDWithDisc
						+ "</th><th></th><th>$"
						+ totalNetPriceDBWithDisc
						+ "</th></tr>");
		htmlMailContent.append("</table>");

		htmlMailContent
				.append("<table width='800px' align='left' border='1' cellpadding='0' cellspacing='0' style='border-top:5px solid white;'");
		htmlMailContent
				.append("<tr border='1'><th width='200px'>TOTAL Employee Purchases WITH NO  Employee Discount<th>"
						+ "<th></th><th></th><th></th><th></th><th></th><th></th></tr>");
		htmlMailContent
				.append("<tr border='1' ><th>Purchase Date</th><th>Division</th><th>Article Item</th><th>Description</th>"
						+ "<th>Size</th><th>Retail Price</th><th>Discount %</th><th>Net Price</th></tr>");
		for (int i = 0; i < purchaseDateList.size(); i++) {

			BigDecimal retailPriceBD = new BigDecimal(retailPriceList.get(i));
			BigDecimal netPriceBD = new BigDecimal(discountPriceList.get(i));
			retailPriceBD.setScale(2,RoundingMode.HALF_UP);
			netPriceBD.setScale(2,RoundingMode.HALF_UP);
			BigDecimal discPercent = BigDecimal.ZERO;
			discPercent.setScale(2, RoundingMode.HALF_UP);
			discPercent = netPriceBD.divide(retailPriceBD,2, RoundingMode.HALF_UP);
			discPercent = discPercent.multiply(new BigDecimal(100));
			//discPercent = (netPriceBD.divide(retailPriceBD)).multiply(new BigDecimal(100));
			discPercent = new BigDecimal(100).subtract(discPercent);
			
			logger.info("Without Discount retailPriceBD " + retailPriceBD
					+ "  netPriceBD " + netPriceBD + " : " + discPercent);
			if(discPercent.equals(BigDecimal.ZERO)){
				totalNetPriceDBWithoutDisc = totalNetPriceDBWithoutDisc.add(netPriceBD);
				totalRetailsPriceBDWithoutDisc = totalRetailsPriceBDWithoutDisc.add(retailPriceBD);
			String itemId = itemIdList.get(i);
			htmlMailContent.append("<tr border='1' ><td>"
					+ purchaseDateList.get(i) + "</td><td>"
					+ divisionList.get(i) + "</td><td>" + itemIdList.get(i)
					+ "</td><td>" + itemDescList.get(i) + "</td>" + "<td>"
					+ getItemSize(itemId) + "</td><td>$"
					+ retailPriceList.get(i) + "</td><td>" + discPercent + "%"
					+ "</td><td>$" + discountPriceList.get(i) + "</td></tr>");
			}
		}
		htmlMailContent
		.append("<tr border='1' ><th>Totals</th><th></th><th></th><th></th>"
				+ "<th></th><th>$"
				+ totalRetailsPriceBDWithDisc
				+ "</th><th></th><th>$"
				+ totalNetPriceDBWithDisc
				+ "</th></tr>");
		htmlMailContent.append("</table>");
		htmlMailContent.append("<table width='300px' align='left' ");
		htmlMailContent.append("<tr border='0'><th><th></tr>");
		htmlMailContent.append("<tr border='0' ><th></th></tr></table>");
		htmlMailContent.append("<table width='300px' align='left' ");
		htmlMailContent.append("<tr border='0'><th><th></tr>");
		htmlMailContent.append("<tr border='0' ><th></th></tr></table>");
		htmlMailContent.append("<table width='300px' align='left' ");
		htmlMailContent
				.append("<tr border='0' ><th  align='left'>Yours Sincerely<th></tr>");
		htmlMailContent
				.append("<tr border='0' ><th align='left'>Groupe Dynamite</th></tr></table>");
		htmlMailContent.append("<br/>");

		

	}

	public static void sendMail(String mailContent,String messageTo,String messageCc) {
		try {
			
			String msgFrom = null;			
			String subject = null;
			String mailHost = null;
			String mailPort = null;
			Session session = null;
		
			// get current date time with Date()
			
			FileReader reader = new FileReader("parameters.properties");
			Properties properties = new Properties();
			properties.load(reader);
			user = properties.getProperty("userKey");
			pass = properties.getProperty("userPass");			
			msgFrom = properties.getProperty("messageFrom");			
			subject = properties.getProperty("sub");			
			

			mailHost = properties.getProperty("mHost");
			mailPort = properties.getProperty("mPort");

			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.host", mailHost);
			properties.put("mail.smtp.port", mailPort);

		session = Session.getInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, pass);
				}
			});
//Added by Monica to send email through SMTP server in HTML format		   
		    
			Message message = new MimeMessage(session);
			 MimeBodyPart wrap = new MimeBodyPart();
			    
	           MimeMultipart cover = new MimeMultipart("alternative");
	           MimeBodyPart html = new MimeBodyPart();
	           cover.addBodyPart(html);
	           
	           wrap.setContent(cover);
	    
	           MimeMultipart content = new MimeMultipart("related");
	           message.setContent(content);
	           content.addBodyPart(wrap);
	           
			    message.addHeader("Content-type", "text/HTML; charset=UTF-8");
			    message.addHeader("format", "flowed");
			    message.addHeader("Content-Transfer-Encoding", "8bit");
			
			message.setFrom(new InternetAddress(msgFrom));
			if(messageTo!=null)
			{
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("mambati@dynamite.ca"));
			}
			if(messageCc!=null)
			{
			message.setRecipients(Message.RecipientType.CC,
					InternetAddress.parse(messageCc));
			}
			message.setSubject(subject);				   
			message.setContent(mailContent, "text/html; charset=utf-8");
		    html.setContent(mailContent.getBytes(), "text/html");
			/*message.setText("Date: " + currentMailDateTime + "\n\n"
					+ "Subject:" + subject + "\n\n" + html);*/
			       
	          
			message.saveChanges();
		    
			Transport.send(message);
		
		//	System.out.println("response.getMessage() "+response.getMessage()+ " : response.getStatus() "+response.getStatus());
		} catch (Exception e) {
			logger.error("Exception thrown while sending an email to employee" +  e);
		}

	}

	private static void writeToLog(int emplNumber) {
		BasicConfigurator.configure();
		PropertyConfigurator.configure("log4j.properties");
		logger.debug("EEPurchase details sent for employee " + emplNumber);
	}
	
    private static void calculateTotalPrice(){
    	for (String s : retailPriceList) {
			totalRetailsPriceBD = totalRetailsPriceBD.add(
					new BigDecimal(s))
					.setScale(2, RoundingMode.HALF_UP);
		}
		for (String s : discountPriceList) {
			totalNetPriceDB = totalNetPriceDB.add(new BigDecimal(s))
					.setScale(2, RoundingMode.HALF_UP);
		}
    }
	
    public static void initialize(){
		purchaseDateList = new ArrayList<String>();
		divisionList = new ArrayList<String>();
		itemIdList = new ArrayList<String>();
		itemsDescriptionList = new ArrayList<String>();
		itemsSizeList = new ArrayList<String>();
		retailPriceList = new ArrayList<String>();
		itemSizeList = new ArrayList<String>();
		discountPriceList = new ArrayList<String>();
		itemDescList = new ArrayList<String>();
		maxSpendEntlList = new ArrayList<String>();
		discountPercentList = new ArrayList<String>();
		discountDivList = new ArrayList<String>();
		entlDescList = new ArrayList<String>();
		
		totalRetailsPriceBD = new BigDecimal(0);
		totalNetPriceDB = new BigDecimal(0);
		
		totalRetailsPriceBDWithDisc = new BigDecimal(0);
		totalNetPriceDBWithDisc = new BigDecimal(0);
		
		totalRetailsPriceBDWithoutDisc = new BigDecimal(0);
		totalNetPriceDBWithoutDisc = new BigDecimal(0);

		maxSpendAmtBD = new BigDecimal(0);
		mailSendStatus = Boolean.TRUE;    		
		periodId = 0;
    	 emplName = "";
		emplNumber = 0;
		discountGroupCode = "";
		periodName = "";
		discountEntlments = "";

		retailLimit = "";

		currentPeriodPurchases = "";
		entlId = 0;
		emplGroupId = 0;
		maxSpendEntl = "";
		discGroupCode = "";
		discountPercent = "";
		 
    }
    
	public static void main(String[] args) {

		try  {
			FileReader reader = new FileReader("parameters.properties");
			Properties properties = new Properties();
			properties.load(reader);

			driverClass = properties.getProperty("driverClass");
			url = properties.getProperty("url");
			userName = properties.getProperty("userName");
			passWord = properties.getProperty("passWord");
			connection = getConnection(driverClass, url, userName, passWord);

			
		} catch (IOException e)
		{
			logger.error("Exception thrown while reading the properties from file" +  e);
		}
		

		if (connection != null) 
		{
			try {
				getCtEepEmailDtls();
				for(Map.Entry<Integer,String> me :msgToMap.entrySet())
				{
				
				emplNumber = me.getKey();
				getEepDtls();
				getEmplMstrDtls();
				getEmplPeriodDtls();
				getEntitlementId();
				getEntitlementDtls();
				getEmplGroupDtls();
				getTransaDetails(emplNumber, periodId);
				calculateTotalPrice();
				StringBuilder htmlMailContent = new StringBuilder();
				configureHTMLContent(htmlMailContent);
				
				String mailContent = htmlMailContent.toString();
				sendMail(mailContent,me.getValue(),msgCcMap.get(me.getKey()));
				//sendMail(mailContent,"dharmensm@gmail.com","dharmensm@gmail.com"); 
				writeToLog(emplNumber);
				initialize();
				}
			} 
			catch (Exception e) 
			{
				logger.error("Exception thrown while getting purchase details of employee" +  e);
			}

			
		} 
		else 
		{
			logger.error("Database connection could not be obtained" );
		}
	}

}
