package com.qvc.selenium.plugin;


import com.gargoylesoftware.htmlunit.StorageHolder.Type;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qvc.selenium.data.PageObjectManager;
import com.qvc.selenium.data.TestDataManager;
import com.qvc.selenium.data.TestManager;
import com.qvc.selenium.drivers.QVCAndroidDriver;
import com.qvc.selenium.model.CucumberCase;
import com.qvc.selenium.model.CucumberReport;
import com.qvc.selenium.model.CucumberStep;
import com.qvc.selenium.model.CucumberSuite;
import com.qvc.selenium.reporting.HtmlTestReporter;
import com.qvc.selenium.reporting.ReportNGTestLayout;
import com.qvc.selenium.reporting.SummaryReporter;
import com.sun.mail.imap.protocol.Status;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xerces.internal.impl.xpath.XPath.Step;
import com.thoughtworks.xstream.io.path.Path;

import io.appium.java_client.AppiumDriver;
import net.sf.json.JSONObject;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TestCase extends TestModule implements org.testng.ITest {

    private static final Logger logger = Logger.getLogger(TestCase.class.getName());
    private SummaryReporter summary;
    private static int counter = 0;
    private static final int RESTART_EMULATOR_AFTER = 2;
    
   

    @Test(timeOut = 1800000)
    public void runTestCase() throws Exception {

        String retriesNumber = System.getProperty("retries");
        int maxRetries = 0;
        if (retriesNumber != null) {
            try{
                maxRetries = Integer.parseInt(retriesNumber);
            } catch (Exception e){
                logger.error("Incorrect value for \"Number of retries\": \"" + retriesNumber +
                        "\". Please specify integer value (>= 0).");
                e.printStackTrace();
                throw e;
            }

        }
        Exception err = null;

        try {
            testSetup();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        for (int i = 0; i < maxRetries + 1; i++) {
            err = null;
            if (i > 0) {
                logger.info("Retrying the test ... ");
                testSetup();
            }
            try {
                runTestFlow(true);
                super.teardown();
                break;
            }catch (InterruptedException e) {
                err = e;
                super.passed=false;
                super.teardown();
                break;
            }catch (Exception e) {
                err = e;
                super.passed=false;
                super.teardown();
            }
        }
        getDriver().quit();
        if (err != null) {
            err.printStackTrace();
            throw err;
        }

        if (!passed)
            throw new Exception(String.format("Test \"%s\" was failed.", getTestName()));
    }

    private void testSetup() throws Exception {
        TestManager.clearCache();
        TestDataManager.clearCache();
        PageObjectManager.clearCache();
        // indicate test name for current thread (for logging)
        Appender testNGAppender = Logger.getRootLogger().getAppender("TestNG");
        ReportNGTestLayout logLayout = (ReportNGTestLayout) testNGAppender.getLayout();
        logLayout.threadTestName.put(Long.toString(Thread.currentThread().getId()), getTestName());

        //get the data sheet and replace the productNum with actual value
        if (testData == null){
            logger.info("No test data for \""+getTestName()+"\"");
        } else {
            // Append data to singleton of current thread
            for (Map.Entry<String, Object> entry : testData.entrySet()) {
                data.setStoredData(entry.getKey().toLowerCase(), entry.getValue());
            }
            data.setStoredData("testCaseData", testData.clone());
            testData = null;
        }
        data.setStoredData("testPackage", this.getTestPackage());
        data.setStoredData("env", this.getEnv());
        data.setStoredData("ui", this.getUi());
        data.setStoredData("client", this.getBrowserType());
        data.setStoredData("deviceName", this.getDeviceName());
        data.setStoredData("app", this.getAppPath());

        //New a reporter
        setReporter(new HtmlTestReporter(this.getTestName(), getTestFlow().size(), env, ui, this.getBrowserType(), logLayout.executionDate, (logLayout.consoleLevel == Level.DEBUG_INT)));

        getReporter().appendHeader((HashMap<String, Object>) data.getStoredData("testCaseData"));

        HashMap<String, String> driverOptions = new HashMap<String, String>();
        driverOptions.put("app", this.getAppPath());
        driverOptions.put("client", this.getBrowserType());
        driverOptions.put("ui", this.getUi());
        driverOptions.put("env", this.getEnv());
        driverOptions.put("deviceName", this.getDeviceName());
		
//        if (counter == RESTART_EMULATOR_AFTER && this.getBrowserType().equalsIgnoreCase("android")){
//            QVCAndroidDriver.restartEmulator(this.getDeviceName());
//            counter = 0;
//        }else{
//            ++counter;
//        }

        setDriver(WebDriverManager.getSingletonManager().buildDriverInstance(getBrowserType(), getPlatform(), false, getTargetTestServer(), driverOptions));
    
        
    //check feature is new or not
        CucumberSuite cucumberSuite; 
        synchronized (this) {
        	 if(!CucumberReport.suiteMap.containsKey(getSuiteName())){
             	cucumberSuite = new CucumberSuite();
             	cucumberSuite.setName(getSuiteName());
             	CucumberReport.suiteMap.put(cucumberSuite.getName(), cucumberSuite);
             }else{
             	cucumberSuite=CucumberReport.suiteMap.get(getSuiteName());
             }
		}
        //init cucumbercase
        boolean isRerunCase=false;
        for(CucumberCase ele:cucumberSuite.getElements()){
        	if(ele.getName().equals(getTestName())){
        		isRerunCase=true;
        		cucumberCase=ele;
        		break;
        	}
        }
        //if this case is rerun, this case info will replace the older one, if not, new a cucumbercase for it
        if(!isRerunCase){
        	cucumberCase=new CucumberCase();
        	cucumberSuite.getElements().add(cucumberCase);
        	cucumberCase.setName(getTestName());
        } 
        startTime=System.currentTimeMillis();
    
    }

    private void restartApp() {
        if (getDriver() instanceof AppiumDriver)
            ((AppiumDriver) getDriver()).resetApp();
    }

    @Override
    public String getTestName() {
        return testName;
    }

    @BeforeSuite
    public void setupSummaryReport() {
        ReportNGTestLayout mobileLayout = (ReportNGTestLayout) Logger.getRootLogger().getAppender("TestNG").getLayout();
        String executionDate = mobileLayout.executionDate;
        summary = new SummaryReporter(executionDate, System.getProperty("executeSet"));
//        summary = new SummaryReporter(executionDate, suiteName);//because the executeSet, may continue multi suiteName(e.g. Checkout,ProductDetail, Navigation...), changed to use executeSet
   
       
        
    }

    @AfterSuite
    public void createSummaryReport() throws URISyntaxException, IOException {
        summary.createReport();
        //create cucumberReport
        Collection<CucumberSuite> suites = new ArrayList<>();
        for(Entry<String, CucumberSuite> integer : CucumberReport.suiteMap.entrySet()){
        	suites.add(integer.getValue());
        }
        //count the number of cases' passed,failed and total in every suites
        for(CucumberSuite suite : suites){
        	int passedCount=0;
    		int failedCount=0;
    		int totalCount=0;
    		//reorder the sequence, failed case is in front of success case
    		Collection<CucumberCase> passedCases = new ArrayList<>();
    		Collection<CucumberCase> failedCases = new ArrayList<>();
    		
        	for(CucumberCase cucumberCase : suite.getElements()){
        		String status = cucumberCase.getSteps().get(0).getResult().getStatus();
        		if("passed".equals(status)){
        			passedCount++;
        			passedCases.add(cucumberCase);
        		}else{
        			failedCount++;
        			failedCases.add(cucumberCase);
        		}
        		totalCount++;
        	}
        	ArrayList<CucumberCase> elements = new ArrayList<>();
        	elements.addAll(failedCases);
        	elements.addAll(passedCases);
        	suite.setElements(elements);
        	suite.setPassedCount(passedCount);
        	suite.setFailedCount(failedCount);
        	suite.setTotalCount(totalCount);
        }              
        //result to cucumber.json format
        Gson gson = new Gson();   
        String jsonArray = gson.toJson(suites);
        System.out.println("old:"+jsonArray);   
    	jsonArray = toPrettyFormat(jsonArray);  
    	System.out.println(jsonArray);   
    	//output the json file
        createJsonFile(jsonArray);
    }
    
    
    public static String toPrettyFormat(String jsonString) 
    {
    	if(jsonString==null||jsonString==""){
    		return "";
    	}
    	try {
    		JsonParser parser = new JsonParser();
    		JsonElement jsonElement;
    		if(jsonString.startsWith("{")){//jsonobject
    			jsonElement = parser.parse(jsonString).getAsJsonObject();
            	
        	}else{//jsonarray
        		jsonElement = parser.parse(jsonString).getAsJsonArray();
        	}
    		Gson gson = new GsonBuilder().setPrettyPrinting().create();
        	return gson.toJson(jsonElement);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
    }
    
    /**
     * output json file
     */
    public static void createJsonFile(String jsonString) {
        String path = Paths.get(System.getProperty("reportRootPath"),"jsonreport","cucumber.json").toString();

        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) { 
            	file.getParentFile().mkdirs();
            }
            if (file.exists()) { 
                file.delete();
            }
            file.createNewFile();
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(jsonString);
            write.flush();
            write.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
    	String compactJson = "[{\"keyword\":\"Feature\",\"name\":\"AEM_Shop_Live_TV_UK\",\"passedCount\":1,\"failedCount\":1,\"totalCount\":2,\"elements\":[{\"keyword\":\"Tasecase\",\"name\":\"AEM_SLTV_BuyBox_Shop_Now_AddToCart_All_Geos_uk_chrome\",\"steps\":[{\"line\":1,\"keyword\":\"Result\",\"name\":\"failed\",\"result\":{\"duration\":170622,\"status\":\"failed\"}}]},{\"keyword\":\"Tasecase\",\"name\":\"AEM_SLTV_BuyBox_Shop_Now_Speedbuy_All_Geos_uk_chrome\",\"steps\":[{\"line\":1,\"keyword\":\"Result\",\"name\":\"passed\",\"result\":{\"duration\":102122,\"status\":\"passed\"}}]}]},{\"keyword\":\"Feature\",\"name\":\"AEM_PDP_UK\",\"passedCount\":1,\"failedCount\":0,\"totalCount\":1,\"elements\":[{\"keyword\":\"Tasecase\",\"name\":\"AEM_SizeColorOptions_Swatch Not Available_uk_chrome\",\"steps\":[{\"line\":1,\"keyword\":\"Result\",\"name\":\"passed\",\"result\":{\"duration\":22543,\"status\":\"passed\"}}]}]}]";

        String prettyJson = toPrettyFormat(compactJson);

        System.out.println("Compact:\n" + compactJson);
        System.out.println("Pretty:\n" + prettyJson);
	}
}
