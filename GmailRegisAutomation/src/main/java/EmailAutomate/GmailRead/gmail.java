package EmailAutomate.GmailRead;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.io.InputStream;

import io.restassured.path.json.JsonPath;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Thread;

public class gmail {

	 private static final String APPLICATION_NAME = "GmailAuto";
	  /**
	   * Global instance of the JSON factory.
	   */
	// private static final JsonFactory JSON_FACTORY = JsonFactory.get
	 private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	    private static final String USER_ID = "me";
	    
	    /**
	     * Global instance of the scopes required by this quickstart.
	     * If modifying these scopes, delete your previously saved tokens/ folder.
	     */
	    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);
	    private static final String CREDENTIALS_FILE_PATH =  
	    		System.getProperty("user.dir") +
	             File.separator + "src" +
	             File.separator + "main" +
	             File.separator + "resources" +
	             File.separator + "credentials" +
	             File.separator + "credentials.json";
	    
	    private static final String TOKENS_DIRECTORY_PATH = System.getProperty("user.dir") +
	            File.separator + "src" +
	            File.separator + "main" +
	            File.separator + "resources" +
	            File.separator + "credentials";
	    /**
	     * Creates an authorized Credential object.
	     * @param HTTP_TRANSPORT The network HTTP Transport.
	     * @return An authorized Credential object.
	     * @throws IOException If the credentials.json file cannot be found.
	     */
	    @SuppressWarnings("unused")
		private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
	        // Load client secrets.
	        InputStream in = new FileInputStream(new File(CREDENTIALS_FILE_PATH));
	        if (in == null) {
	            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
	        }
	        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
	        // Build flow and trigger user authorization request.
	        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
	                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
	                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
	                .setAccessType("offline")
	                .build();
	        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(9999).build();
	        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	    }
	    
	    
	    public static Gmail getService() throws IOException, GeneralSecurityException {
	        // Build a new authorized API client service.
	        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
	               .setApplicationName(APPLICATION_NAME)
	               .build();
	        //Gmail service = 
	        return service;
	    }
	    public static List<Message> listMessagesMatchingQuery(Gmail service, String userId,
	                                                          String query) throws IOException {
	        ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();
	        List<Message> messages = new ArrayList<Message>();
	        while (response.getMessages() != null) {
	            messages.addAll(response.getMessages());
	            if (response.getNextPageToken() != null) {
	                String pageToken = response.getNextPageToken();
	                response = service.users().messages().list(userId).setQ(query)
	                        .setPageToken(pageToken).execute();
	            } else {
	                break;
	            }
	        }
	        return messages;
	    }
	    public static Message getMessage(Gmail service, String userId, List<Message> messages, int index)
	            throws IOException {
	        Message message = service.users().messages().get(userId, messages.get(index).getId()).execute();
	        return message;
	    }
	    public static HashMap<String, String> getGmailData(String query) {
	        try {
	            Gmail service = getService();
	            List<Message> messages = listMessagesMatchingQuery(service, USER_ID, query);
	            Message message = getMessage(service, USER_ID, messages, 0);
	            JsonPath jp = new JsonPath(message.toString());
	            String subject = jp.getString("payload.headers.find { it.name == 'Subject' }.value");
	            
	            String bodyEncoded = jp.getString("payload.body.data");
	            String body = new String(Base64.getUrlDecoder().decode(bodyEncoded));
	           
	            String link = null;
	            	 List<String> list
	                 = new ArrayList<>();
	       
	             // Regular Expression to extract
	             // URL from the string
	             String regex
	                 = "\\b((?:https?|ftp|file):"
	                   + "//[-a-zA-Z0-9+&@#/%?="
	                   + "~_|!:, .;]*[-a-zA-Z0-9+"
	                   + "&@#/%=~_|])";
	       
	             // Compile the Regular Expression
	             Pattern p = Pattern.compile(
	                 regex,
	                 Pattern.CASE_INSENSITIVE);
	       
	             // Find the match between string
	             // and the regular expression
	             Matcher m = p.matcher(body);
	       
	             // Find the next subsequence of
	             // the input subsequence that
	             // find the pattern
	             while (m.find()) {
	       
	                 // Find the substring from the
	                 // first index of match result
	                 // to the last index of match
	                 // result and add in the list
	                 list.add(body.substring(
	                     m.start(0), m.end(0)));
	             }
	       
	             // IF there no URL present
	             if (list.size() == 0) {
	                 System.out.println("-1");
	                 //return;
	             }
	       
	             // Print all the URLs stored
	             for (String url : list) {
	                 System.out.println(url);
	                 link=url;
	                 
	             }
	            
	            HashMap<String, String> hm = new HashMap<String, String>();
	            hm.put("subject", subject);
	            hm.put("body", body);
	            hm.put("link", link);
	            return hm;
	        } catch (Exception e) {
	        		System.out.println("email not found....");
	            throw new RuntimeException(e);
	        }
	    }
	    
	    public static int getTotalCountOfMails() {
	        int size;
	        try {
	            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
	                    .setApplicationName(APPLICATION_NAME)
	                    .build();
	            /*List<Thread> threads = service.
	            		users().
	                    threads().
	                    list("me").
	                    execute().
	                    getThreads();*/
	            List<Thread> threads = service.
	            		users().
	                    threads().
	                    list("me").
	                    execute().
	                    getThreads();
	             size = threads.size();
	        } catch (Exception e) {
	            System.out.println("Exception log " + e);
	            size = -1;
	        }
	        return size;
	    }
	    
	    public static boolean isMailExist(String messageTitle) {
	        try {
	            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
	                    .setApplicationName(APPLICATION_NAME)
	                    .build();
	            ListMessagesResponse response = service.
	                    users().
	                    messages().
	                    list("me").
	                    setQ("subject:" + messageTitle).
	                    execute();
	            List<Message> messages = getMessages(response);
	            return messages.size() != 0;
	        } catch (Exception e) {
	            System.out.println("Exception log" + e);
	            return false;
	        }
	    }
	        
	        private static List<Message> getMessages(ListMessagesResponse response) {
	        	//List<Message> messages =new arrayl
	           List<Message> messages = new ArrayList<Message>();
	            try {
	                final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	                Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
	                        .setApplicationName(APPLICATION_NAME)
	                        .build();
	                while (response.getMessages() != null) {
	                    messages.addAll(response.getMessages());
	                    if (response.getNextPageToken() != null) {
	                        String pageToken = response.getNextPageToken();
	                        response = service.users().messages().list(USER_ID)
	                                .setPageToken(pageToken).execute();
	                    } else {
	                        break;
	                    }
	                }
	                return messages;
	            } catch (Exception e) {
	                System.out.println("Exception log " + e);
	                return messages;
	            }
	        }
	    
	    
	    public static void main(String[] args) throws IOException, GeneralSecurityException {
	    	
	    	
	    	//Registration.signUp();
	    	
	    	
	        HashMap<String, String> hm = getGmailData("subject:Confirmation instructions");
	        System.out.println(hm.get("subject"));
	        System.out.println("=================");
	        System.out.println(hm.get("body"));
	        System.out.println("=================");
	        String link=hm.get("link");
	        System.out.println(link);
	 
	        System.out.println("=================");
	        System.out.println("Total count of emails is :"+getTotalCountOfMails());
	        
	        System.out.println("=================");
	        boolean exist = isMailExist("Confirmation instructions");
	        System.out.println("title exist or not: " + exist);
	        
	        //opening of link in google
	        String exePath = "D:\\Training\\ChromeDriver\\chromedriver.exe";
	        System.setProperty("webdriver.chrome.driver", exePath);
	        WebDriver driver = new ChromeDriver();
	        driver.manage().window().maximize();
	        driver.manage().deleteAllCookies();
			driver.get(link);
			


	    }
}
