package com.konnect.jpms.util;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class CallSmscApi {

    public CallSmscApi() {
    }

    CallSmscApi(String mobilenumber, String message) {
    }
    /*
     * 1	Create a URL. 2	Retrieve the URLConnection object. 3	Set output
     * capability on the URLConnection. 4	Open a connection to the resource. 5
     * Get an output stream from the connection. 6	Write to the output stream. 7
     * Close the output stream.
     */
//    String User = "itfact";
//    String passwd = "itfact123";
    String User = "vclsal";
    String passwd = "vclsal";
    String mobilenumber = "";
    String message = "";
    String sid = "VCLSAL";
    String mtype = "N";


    public void sendSMSToUsers(String mobile, String sms) throws Exception {

        String postData = "";
        String retval = "";

        mobilenumber = mobile;
        message = sms;
        String DND_TEMPLATE_ID = "4076";

        System.out.println("in Send SMS function --------- " + mobile);
        //http://admin.bulksmslogin.com/api/sendhttp.php?authkey=301832AUQNGj0jUt0D5dbd1764&mobiles=7020174556&message=hi&sender=VCLSAL&route=4
//        postData += "uid=" + URLEncoder.encode(User, "UTF-8") + "&pin=" + URLEncoder.encode(passwd, "UTF-8") + "&sender=" + URLEncoder.encode(sid, "UTF-8") + "&route=5&tempid=" + DND_TEMPLATE_ID + "&mobile=" + mobile + "&message=" + URLEncoder.encode(message, "UTF-8");
        postData += "authkey=" + URLEncoder.encode("301832AUQNGj0jUt0D5dbd1764", "UTF-8") + "&sender=" + URLEncoder.encode(sid, "UTF-8") + "&route=4&mobiles=" + mobile + "&message=" + URLEncoder.encode(message, "UTF-8");
        URL url = new URL("http://admin.bulksmslogin.com/api/sendhttp.php");
        HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();

        urlconnection.setRequestMethod("POST");
        urlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlconnection.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
        out.write(postData);
        out.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
        String decodedString;
        while ((decodedString = in.readLine()) != null) {
            retval += decodedString;

        }
        in.close();

        System.out.println(retval);
    }


    public static void main(String[] args) throws Exception {
        String details2 = "";
        
        details2 = "CAB DETAILS:\nDATE : #VAL#\nTIME : #VAL#\n"
                + "GUEST : #VAL#\nREPORTING ADD : #VAL#\n"
                + "DUTY : #VAL#\nCAR TYPE : #VAL#\n"
                + "CAR NO : #VAL#\nCHAUFFEUR : #VAL#\n"
                + "HELPLINE : #VAL#\nRegards.";
        CallSmscApi csa = new CallSmscApi();
        csa.sendSMSToUsers("7020174556", details2);
    }
}