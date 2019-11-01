import pages.BaseLinkedIn;
import ui.SimpleUI;
import utils.Tools;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by odiachuk on 8/29/19.
 */
public class App {
    public static void main(String[] args) {

//        BaseLinkedIn linkedIn = new BaseLinkedIn();

//        String login = "olex.dyachuk@gmail.com";
//        String password = "WtkmLink1!";


        String login = "bohdan.hrynchuk@fortegrp.com";
        String password = "123QWEasd";

        String title = "VP OR CTO OR president OR Director OR head OR owner" ; //"\"VP\" OR \"President\" OR \"Director\" OR \"CTO\" OR \"Owner\" OR \"Founder\"";
        //String title = "\"VP\" OR \"Vice President\" OR \"Director\" OR \"CTO\" OR \"Chief Technology officer\" OR \"President\" OR \"Owner\" OR \"Founder\"";
        String listOfCompanies = "Fitch Group, Inc.;" +
                "Fitch Ratings;" +
                "Fitch Solutions;" +
                "Cresset;" +
                "Cresset Partners;" +
                "BMO Capital Markets;" +
                "BMO Financial Group;" +
                "BMO Harris Bank;" +
                "BMO Nesbitt Burns;" +
                "BMO Private Banking;" +
                "BMO Learning Institute;" +
                "BMO Commercial Bank;" +
                "BMO Family Office;" +
                "CURO Financial Technologies Corp;" +
                "Vista Equity Partners;" +
                "Vista Consulting Group;" +
                "Equity Residential;" +
                "Nuveen;" +
                "Here International B.V.;" +
                "Morningstar;" +
                "Ibbotson Associates;" +
                "Ventas, Inc.;" +
                "JLL;" +
                "JLL Technology Solutions (formerly BRG);" +
                "JLL's Hotels & Hospitality Group;" +
                "LaSalle Investment Management;" +
                "Discover Financial Services;" +
                "Discover Global Network;" +
                "Diners Club International;" +
                "PULSE, A Discover Company;" +
                "RJO Futures;" +
                "R.J. O'Brien;" +
                "CME Group;" +
                "GCM Grosvenor;" +
                "Allston Trading;" +
                "Enova International;" +
                "TransUnion;" +
                "Citadel;" +
                "R1 RCM;" +
                "Northern Trust Corporation;" +
                "Northern Trust Wealth Management;" +
                "Northern Trust Asset Servicing;" +
                "Northern Trust Asset Management;" +
                "Wells Fargo;" +
                "Wells Fargo Advisors;" +
                "Wells Fargo Home Mortgage;" +
                "Wells Fargo Securities;" +
                "Wells Fargo Capital Finance;" +
                "Abbot Downing;" +
                "Cboe Global Markets;" +
                "TIAA;" +
                "TIAA Bank;" +
                "Mesirow Financial;" +
                "OCC;" +
                "Harbor Capital Advisors, Inc;" +
                "E*TRADE;" +
                "E*TRADE Advisor Services;" +
                "American Credit Acceptance;" +
                "Synchrony;" +
                "H.I.G. Capital";


//        String reportFile = Tools.getCurDateTime() + ".html";
//        String sessionFile = Tools.getCurDateTime() + ".txt"; //"Session.txt" ; //


        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                showUI();
            }
        });

//        String reportFile = "20190906_083425.html";
//        String sessionFile = "20190906_083425.txt"; //"Session.txt" ; //
//
//        System.out.println("Started: " + Tools.getCurDateTime());
//        while(true) {
//            try {
////            String reportFile = "20190904_205912.html";; //Tools.getCurDateTime() + ".html";
////            String sessionFile = "20190904_205912.txt";// Tools.getCurDateTime() + ".txt"; //"Session.txt" ; //
//                if (linkedIn.readAllConnectionsWithTitle(reportFile, login, password, title, listOfCompanies, sessionFile))
//                    break;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        System.out.println("Finished: " + Tools.getCurDateTime());

    }

    private static void showUI() {
        SimpleUI ui = new SimpleUI();
        ui.createPanel();
    }
}
