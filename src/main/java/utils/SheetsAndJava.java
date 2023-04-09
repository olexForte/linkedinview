package utils;

import java.io.IOException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class SheetsAndJava {
    private static Sheets sheetsService;
    private static String SPREADSHEET_ID;
    private static List<List<Object>> sheetValues;
    private static String SERVICE_NAME = "GS Example";
    private static String SPREADSHEET_ID_TEST = "1RMYw31bO7M6GX9GCkwHRi4_AmPZ0h_ZEyu8cQllp8f4";

    private static Credential authorize() throws IOException, GeneralSecurityException {
        InputStream in = SheetsAndJava.class.getResourceAsStream("/credential.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                GsonFactory.getDefaultInstance(), new InputStreamReader(in)
        );
        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(),
                clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver())
                .authorize("user");

        return credential;
    }

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException{
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(), credential)
                .setApplicationName(SERVICE_NAME)
                .build();
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException{
        sheetsService = getSheetsService();
        String range = "testSh!A:B";

        sheetValues = getData_Param_ID_Range(SPREADSHEET_ID_TEST, range);


        if(sheetValues == null || sheetValues.isEmpty()){
            System.out.println("No data found. ");
        } else {
            for(List row : sheetValues){
                System.out.printf("%s value 1 %s value 2 \n", row.get(0), row.get(1));
            }
        }
    }

    public static void setData(String[] args) throws IOException, GeneralSecurityException{
        sheetsService = getSheetsService();
        String range = "testSh!A:B";

        sheetValues = getData_Param_ID_Range(SPREADSHEET_ID_TEST, range);


        if(sheetValues == null || sheetValues.isEmpty()){
            System.out.println("No data found. ");
        } else {
            for(List row : sheetValues){
                System.out.printf("%s value 1 %s value 2 \n", row.get(0), row.get(1));
            }
        }
    }


    public static List<List<Object>> getData_Param_ID_Range(String spreadSheetId, String range) throws IOException, GeneralSecurityException{
        sheetsService = getSheetsService();
        SPREADSHEET_ID = spreadSheetId;

        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();

        return response.getValues();

    }

}

