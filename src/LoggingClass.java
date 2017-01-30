/*import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

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

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;


public class LoggingClass {

	final static Logger logger = Logger.getLogger(LoggingClass.class);
	
	
	static int  periodId = 0 ;
	
	static  String emplName = "";
	
	static int emplNumber = 77757 ;
	
	static String discountGroupCode = "" ;
	
	static String periodName = "" ;
	
	static String discountEntlments = "";
	
	static String retailLimit = "";
	
	static String currentPeriodPurchases = ""; 
	
	static int  entlId = 0 ;
	
	static int emplGroupId = 0;
	
	
	static String maxSpendEntl = "";
	
	static String discountPercent = ""; 
	static String getEepEmailDtls = "select * from ct_eep_emp_eml";
	static String getEepItemDtls = "select * from ct_eep_item where empl_number = ? and rownum <2";
	
   static String getPeriodDtlsQuery = "SELECT * FROM ct_eep_period WHERE sysdate BETWEEN period_start_date AND period_end_date+1" ;
   
   static String getPeriodDtlsQuery1 = 
		   "SELECT * FROM ct_eep_period WHERE sysdate BETWEEN period_start_date AND period_end_date  or  period_end_date = to_date(sysdate,'DD-MON-YY') or period_start_date = to_date(sysdate,'DD-MON-YY')";
   static String getPeriodIdQuery = 	
   "SELECT period_id FROM ct_eep_period WHERE TO_DATE(sysdate, ''DD-MON-YY'') >=  " +
   "to_date(period_start_date,''DD-MON-YY'') " +
   "AND TO_DATE(sysdate, ''DD-MON-YY'') <=to_date(period_end_date,''DD-MON-YY'')";
   
   static String merchandiseGroupIdQuery = "select id_mrhrc_gp , de_itm from as_itm where id_itm = ?"; 
   
   static String divisionQuery = "select divdesc from ct_merch_hierarchy where id_mrhrc_gp = ?"; 
   
   
   
  static String str= 
"SELECT period_id FROM ct_eep_period WHERE TO_DATE(sysdate, ''DD-MON-YY'') >=  to_date(period_start_date,''DD-MON-YY'') " +
"AND TO_DATE(sysdate, ''DD-MON-YY'') <=to_date(period_end_date,''DD-MON-YY'')";
   static String getEmplMstrDtlsQuery = "select * from ct_eep_empl_master where empl_number = ?";
   static  Connection connection = null;
	
   static String getEntlIdQuery = "select entitlement_id from ct_eep_item where empl_number = ? and rownum <2";
   
   static String getEntlDtlsQuery = "select * from ct_eep_entitlement where ENTITLEMENT_ID = ? and EMPL_GROUP_ID = ? ";
   
	static String getEmplgrp = "select * from ct_eep_group where empl_disc_group_code = = ? ";

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
   
	static Double totalRetailPrice = new Double(0);
	static Double totalNetPrice = new Double(0);
    
	static BigDecimal totalRetailsPriceBD = new BigDecimal(totalRetailPrice).setScale(2,RoundingMode.HALF_UP);
	static BigDecimal totalNetPriceDB = new BigDecimal(totalNetPrice).setScale(2,RoundingMode.HALF_UP);
	
	static BigDecimal totalRetailsPriceBD = new BigDecimal(0);
	static BigDecimal totalNetPriceDB = new BigDecimal(0);
	
	
	
	static BigDecimal maxSpendAmtBD = new BigDecimal(0);
	 
	static String url = "";
	static String userName = "";
	static String passWord = "";
	
    private static Connection getConnection(){
		Connection connection = null;
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			 connection =  DriverManager.getConnection("jdbc:oracle:thin:@sdodah91.corp.gdglobal.ca:1521/stotst.corp.gdglobal.ca", "RCO_SCHEMA", "Rco_1120#");
		}catch(Exception e){
			e.printStackTrace();
		}
		return connection;
	}
	
    private static Connection getConnection(String url, String userName, String passWord){
		Connection connection = null;
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			 connection =  DriverManager.getConnection("jdbc:oracle:thin:@sdodah91.corp.gdglobal.ca:1521/stotst.corp.gdglobal.ca", "RCO_SCHEMA", "Rco_1120#");
		}catch(Exception e){
			e.printStackTrace();
		}
		return connection;
	}
	
    
	static List<String> getMerchandiseGroupId(String idItem){
		List<String> list =  new ArrayList<>();
		try{
			PreparedStatement preparedStatement = connection.prepareStatement(merchandiseGroupIdQuery);
			preparedStatement.setString(1, idItem);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()){
				
				list.add(resultSet.getString("id_mrhrc_gp"));
				list.add(resultSet.getString("de_itm"));
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		return list;
	}
	
	
	static String geMerchandiseGrpId(String idItem){
		String merchandiseGroupId = "";
		try{
			PreparedStatement preparedStatement = connection.prepareStatement(merchandiseGroupIdQuery);
			preparedStatement.setString(1, idItem);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()){
				
				merchandiseGroupId = 	resultSet.getString("id_mrhrc_gp");
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		return merchandiseGroupId;
	}
	
	static String geItemDivision(String merchandiseGrpId){
		String divisionName = "";
		try{
			PreparedStatement preparedStatement = connection.prepareStatement(divisionQuery);
			preparedStatement.setString(1, merchandiseGrpId);
			ResultSet resultSet = preparedStatement.executeQuery();
		
			while(resultSet.next()){
				divisionName = 	resultSet.getString("divdesc");
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		return divisionName;
	}
	
	static String  getTransaDetails(int emplId,Integer periodId){
		String transactionDetails = "Store Id\t WorkStattion Id\tBusiness Date\ttrans Id\tLN_ITM\tEMPL Id\tPeriod\tItem Id<br><br>";
		//String transactionDetails = "";
		//System.out.println("emplid "+emplId + " : period Id "+periodId);
		try{
			
		//	Connection connection =  getConnection();
			String queryString = "select * from ct_eep_item where empl_number  = ? and  period_id = ?";
			
			PreparedStatement preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setString(1, ""+emplId+"");
			preparedStatement.setInt(2, 2);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()){
					
				System.out.println(resultSet.getString(1)+"   " +
						String.format("%10s", resultSet.getString(2)).replace(' ','\t')	+"\t\t"+
					 resultSet.getString(3)+"\t\t\t"+ resultSet.getInt(4)+"\t\t"+ resultSet.getString(5)+"\t" + 
					 resultSet.getString(6)+"\t"+ resultSet.getInt(7)+"\t"+ resultSet.getString(8));
				
				String ID_ITM = resultSet.getString("ID_ITM");
				
				List<String> list = getMerchandiseGroupId(ID_ITM);
		        String	merchandiseGrpId = list.get(0);
		        String	itemDesc = list.get(1);
				String divisionName = geItemDivision(merchandiseGrpId);
				purchaseDateList.add(resultSet.getString("DC_DY_BSN"));
				itemIdList.add(resultSet.getString("ID_ITM"));
				retailPriceList.add(resultSet.getString("MO_EXTN_LN_ITM_RTN"));
				discountPriceList.add(resultSet.getString("MO_EXTN_DSC_LN_ITM"));
				if(divisionName!=null && !"".equalsIgnoreCase(divisionName)){
				divisionList.add(divisionName);
				}else{
					divisionName = "no Division";
					divisionList.add(divisionName);
				}
				itemDescList.add(itemDesc);
				
				System.out.println(resultSet.getString("DC_DY_BSN")+" : "+resultSet.getString("ID_ITM")
						+" : "+resultSet.getString("MO_EXTN_LN_ITM_RTN")+" : "+resultSet.getString("MO_EXTN_DSC_LN_ITM")
						+" : "+merchandiseGrpId+" : "+divisionName+ " : "+itemDesc);
				
				
			}
		 
			
			
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return transactionDetails;
	}
	
	
	
	public static void getEepDtls(){
		
		try{
				
		PreparedStatement preparedStatement = connection.prepareStatement(getEepItemDtls);
		preparedStatement.setInt(1, emplNumber);
		ResultSet resultSet = preparedStatement.executeQuery();
		
		while(resultSet.next()){
			System.out.println(resultSet.getString(1)+ " : "+resultSet.getString(2) 
					+ " : "+resultSet.getString(3)+" : "+resultSet.getString(4)
					+" : "+resultSet.getString(5)+" : "+resultSet.getString(6)
					+" : "+resultSet.getString(7)+" : "+resultSet.getString(8));
			emplGroupId = 	resultSet.getInt("EMPL_GROUP_ID");
		}

		}catch(Exception e){
			
		}
		
	}
	
public static void getCtEepEmailDtls(){
		
		try{
				
		PreparedStatement preparedStatement = connection.prepareStatement(getEepEmailDtls);
		//preparedStatement.setInt(1, emplNumber);
		ResultSet resultSet = preparedStatement.executeQuery();
		
		while(resultSet.next()){
			System.out.println(resultSet.getString(1)+ " : "+resultSet.getString(2) 
					+ " : "+resultSet.getString(3)+" : "+resultSet.getString(4));
			//emplGroupId = 	resultSet.getInt("EMPL_GROUP_ID");
		}

		}catch(Exception e){
			
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
				//System.out.println("111Employee Name " + emplName);
			}

		} catch (SQLException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}

	}

	public static void  getEntitlementId(){
	
		try {
			PreparedStatement pstmt = connection
					.prepareStatement(getEntlIdQuery);
			pstmt.setInt(1, emplNumber);
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				entlId = resultSet.getInt("ENTITLEMENT_ID"); 
				}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public static void  getEntitlementDtls(){
		
		try {
			PreparedStatement pstmt = connection
					.prepareStatement(getEntlDtlsQuery);
			pstmt.setInt(1, entlId);
			pstmt.setInt(2, emplGroupId);
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				maxSpendEntl = resultSet.getString("MAX_SPEND_ENTITLED");
				maxSpendAmtBD = new BigDecimal(Double.parseDouble(maxSpendEntl)).setScale(2,RoundingMode.HALF_UP);
				discountPercent = resultSet.getString("DISCOUNT_PERCENT");
				}

		} catch (SQLException e) {
			e.printStackTrace();
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

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return itemSize;
	}


	
	public static void getEmplGroupDtls() {

		try {
			PreparedStatement pstmt = connection
					.prepareStatement(getEntlDtlsQuery);
			pstmt.setInt(1, entlId);
			pstmt.setInt(2, emplGroupId);
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				maxSpendEntl = resultSet.getString("MAX_SPEND_ENTITLED");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
 


	
	   public static void main(String[] args)throws IOException,SQLException{
		   
		   BasicConfigurator.configure();
		   if(logger.isDebugEnabled())
			   logger.debug("Hello this is a debug message");
		   logger.info("Hello this is an info message");
		   
		   try (FileReader reader = new FileReader("parameters.properties")) {
				Properties properties = new Properties();
				properties.load(reader);
				
				url = properties.getProperty("url");
				userName = properties.getProperty("userName");
				passWord = properties.getProperty("passWord");
				connection = getConnection(url, userName, passWord); 
				//System.out.println("connection==null  "+connection==null);
				getCtEepEmailDtls();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				 
				
				getEepDtls();
				getEmplMstrDtls();
				getEmplPeriodDtls();
				getEntitlementId();
				getEntitlementDtls();
				System.out.println("Employee Name "+emplName+ " : "+periodId+ " : "+periodName + " : "+entlId+ " : "+emplGroupId);
				System.out.println(maxSpendEntl + " : "+discountPercent);
				getTransaDetails(emplNumber, periodId);
				
				for(String s: retailPriceList){
					//totalRetailPrice += Double.valueOf(s);
					
					
					totalRetailsPriceBD = totalRetailsPriceBD.add(new BigDecimal(s)).setScale(2,RoundingMode.HALF_UP);
				}
				
				for(String s: discountPriceList){
					//totalNetPrice += Double.valueOf(s);
					totalNetPriceDB = totalNetPriceDB.add(new BigDecimal(s)).setScale(2,RoundingMode.HALF_UP);
				}
				
				totalNetPriceDB = new BigDecimal(totalNetPrice).setScale(2,RoundingMode.HALF_UP);
				totalRetailsPriceBD = new BigDecimal(totalRetailPrice).setScale(2,RoundingMode.HALF_UP);
				
				//System.out.println("totalRetailPrice "+totalRetailsPriceBD+" : "+  totalNetPriceDB);
			}catch(Exception e){
				e.printStackTrace();
			}
				
			
			
			StringBuilder  str = new StringBuilder ();
			str.append("<html><title></title>");
	        str.append("<body style='font-size:12px;font-family:Trebuchet MS;'>");
	        str.append("<table width='600px' align='left' border='1' cellpadding='0' cellspacing='0' style='border-top:5px solid white;'");
	        str.append("<tr border='1' ><th>Employee Name<th><th>Employee Number</th><th>Discount Group</th><th>Reporting Period</th></tr>");

	        str.append("<tr border='1' ><td>"+emplName+"<td><td>"+emplNumber+"</td>"+"<td></td> "+"<td>"+periodName+"</td></tr></table><br><br>");
	        str.append("                                                                                            ");
	        str.append("<table width='600px' align='left' border='1' cellpadding='0' cellspacing='0' style='border-top:5px solid white;'");
	        str.append("<tr border='1' ><th>Discount Entitlement(s)<th><th>Retail $ Limit(s)" +
	        		"</th><th>Current Period Purchases</th><th>Solde Remaining</th></tr>");
	        
	         str.append("<tr border='1' ><td>"+"Store "+discountPercent+"%"+"<td><td>"+"$"+maxSpendEntl+"</td>$"+totalRetailsPriceBD+"<td>$"+ maxSpendAmtBD.subtract(totalRetailsPriceBD) +"</td></tr></table><br><br>");

	         str.append("");
	        
	         str.append("<table width='300px' align='left' border='1' cellpadding='0' cellspacing='0' style='border-top:5px solid white;'");
	         str.append("<tr border='1' ><th>TOTAL Employee Purchases With Employee Discount applied<th>" +
	         		"</tr></table></br></br></br></br>");
	         
	         str.append("<table width='800px' align='left' border='1' cellpadding='0' cellspacing='0' style='border-top:5px solid white;'");
	         str.append("<tr border='1'><th width='200px'>TOTAL Employee Purchases With Employee Discount applied<th>" +
	         		"<th></th><th></th><th></th><th></th><th></th><th></th></tr>");
	         str.append("<tr border='1' ><th>Purchase Date</th><th>Division</th><th>Article Item</th><th>Description</th>" +
	           		"<th>Size</th><th>Retail Price</th><th>Discount %</th><th>Net Price</th></tr>");
	         for(int i = 0 ; i<purchaseDateList.size();i++){
	        	BigDecimal discPercent =  (new BigDecimal(retailPriceList.get(i)).divide(new BigDecimal(discountPriceList.get(i)))).multiply(new BigDecimal(100));
	        	discPercent = new BigDecimal(100).subtract(discPercent).setScale(2,RoundingMode.HALF_UP);
	        	
	        	 
	        	 BigDecimal retailPriceBD = new BigDecimal(retailPriceList.get(i));
	        	 BigDecimal netPriceBD = new BigDecimal(discountPriceList.get(i));
	        	 
	        	 BigDecimal discPercent = (netPriceBD.divide(retailPriceBD)).multiply(new BigDecimal(100));
	        	 discPercent =  new BigDecimal(100).subtract(discPercent);
	        	 discPercent.setScale(0,RoundingMode.HALF_UP);
	        	 System.out.println("retailPriceBD "+retailPriceBD+"  netPriceBD "+netPriceBD + " : "+discPercent);
	         	
	        	 String itemId = itemIdList.get(i);
	        	 str.append("<tr border='1' ><td>"+purchaseDateList.get(i)+"</td><td>"+divisionList.get(i)+"</td><td>"+itemIdList.get(i)+"</td><td>"+itemDescList.get(i)+"</td>" +
	              		"<td>"+ getItemSize(itemId)+"</td><td>"+retailPriceList.get(i)+"</td><td>"+discPercent+"%"+"</td><td>"+discountPriceList.get(i)+"</td></tr>"); 
	         }
	         
	         str.append("<tr border='1' ><th>Purchase Date</th><th>Division</th><th>Article Item</th><th>Description</th>" +
	         		"<th>Size</th><th>Retail Price</th><th>Discount %</th><th>Net Price</th></tr>");
	         str.append("<tr border='1' ><th>Purchase Date</th><th>Division</th><th>Article Item</th><th>Description</th>" +
	          		"<th>Size</th><th>Retail Price</th><th>Discount %</th><th>Net Price</th></tr>");
	         str.append("<tr border='1' ><th>Purchase Date</th><th>Division</th><th>Article Item</th><th>Description</th>" +
	          		"<th>Size</th><th>Retail Price</th><th>Discount %</th><th>Net Price</th></tr>");
	          		new BigDecimal(totalNetPrice).setScale(2,RoundingMode.HALF_UP);
				new BigDecimal(totalRetailPrice).setScale(2,RoundingMode.HALF_UP);
	          		
	         str.append("<tr border='1' ><th>Totals</th><th></th><th></th><th></th>" +
	            		"<th></th><th>$"+totalRetailsPriceBD+"</th><th></th><th>$"+totalNetPriceDB+"</th></tr>");
	         str.append("</table>");
	         
	          
	         str.append("<table width='800px' align='left' border='1' cellpadding='0' cellspacing='0' style='border-top:5px solid white;'");
	         str.append("<tr border='1'><th width='200px'>TOTAL Employee Purchases WITH NO  Employee Discount<th>" +
	         		"<th></th><th></th><th></th><th></th><th></th><th></th></tr>");
	         str.append("<tr border='1' ><th>Purchase Date</th><th>Division</th><th>Article Item</th><th>Description</th>" +
	           		"<th>Size</th><th>Retail Price</th><th>Discount %</th><th>Net Price</th></tr></table>");
	         
	         str.append("<table width='300px' align='left' ");
	         str.append("<tr border='0'><th><th></tr>");
	         str.append("<tr border='0' ><th></th></tr></table>");
	         str.append("<table width='300px' align='left' ");
	         str.append("<tr border='0'><th><th></tr>");
	         str.append("<tr border='0' ><th></th></tr></table>");
	         str.append("<table width='300px' align='left' ");
	         str.append("<tr border='0' ><th  align='left'>Yours Sincerely<th></tr>");
	         str.append("<tr border='0' ><th align='left'>Groupe Dynamite</th></tr></table>");
	         
	         try{

				//  String emplId = "4001";
				  //Integer periodId = 9877;
				  		  
				//String mailContent =  getTransaDetails(emplId,periodId);
				  
				String mailContent =  str.toString();
				  String user = null ;
				  String userName = null ;
				  String pass =null; 
				  String msgTo = null ;
				  String msgFrom = null ;
				  String msgCc = null ;
				  String amnt = null ;
				  
				  String subject = null ;
				  
				  try (FileReader reader = new FileReader("parameters.properties")) {
						Properties properties = new Properties();
						properties.load(reader);
						
						user = properties.getProperty("userKey");
						pass = properties.getProperty("userPass");
						msgTo = properties.getProperty("messageTo");
						msgFrom = properties.getProperty("messageFrom");
						msgCc = properties.getProperty("messageCc");
						amnt = properties.getProperty("amount");
						subject = properties.getProperty("sub");
						
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				  
				SendGrid sendgrid = new SendGrid(user,pass);

			    SendGrid.Email email = new SendGrid.Email();
			    
			    userName = user.toUpperCase();

			    email.addTo(msgTo);
			    email.addBcc(msgCc);
			    email.setFrom(msgFrom);
			    email.setSubject(subject);
			   
			    email.setHtml(mailContent);
			    
			    //+"<br>Thanks & Regards<br>Groupe Dynamite<br><br></body</html>"
			   
			    try {
					SendGrid.Response response = sendgrid.send(email);
					//System.out.println(response.getMessage());
				} catch (SendGridException e) {
					//System.err.println(e);
				}
			  
				
			}catch(Exception e){
				
			}
			

		   			
			
		  

	   }

}
*/