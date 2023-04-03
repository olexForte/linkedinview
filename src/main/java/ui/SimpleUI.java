package ui;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BaseLinkedIn;
import utils.DriverProvider;
import utils.FileIO;
import utils.PropertiesList;
import utils.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.TimerTask;
import java.util.Timer;

/**
 * Created by odiachuk on 9/6/19.
 */
public class SimpleUI implements ActionListener {

    String VERSION_LABEL = "(2023/03/28)";

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SimpleUI.class);

    static String LOG_FILE_LOCATION = "log.txt";
    static String CURRENT_LOG_FILE_LOCATION = "log" + Tools.getCurDateTime() + ".txt";

    BaseLinkedIn linkedIn;

    WebDriver driver;

    Thread mainThread;

    String START_COMMAND = "start";
    String RESUME_COMMAND = "resume";

    String loginFromConfig = PropertiesList.getConfigProperty("LOGIN");
    String passwordFromConfig= PropertiesList.getConfigProperty("PASS");
    String titleFromConfig1= PropertiesList.getConfigProperty("TITLE1");
    String contactsFromConfig= PropertiesList.getConfigProperty("CONTACTS");
    String companiesFromConfig1= PropertiesList.getConfigProperty("COMPANIES1");
    String titleFromConfig2= PropertiesList.getConfigProperty("TITLE2");

    String prevhtml = PropertiesList.getConfigProperty("PREV_HTML");
    String prevtxt  = PropertiesList.getConfigProperty("PREV_TXT");

    JTextField login = new JTextField(loginFromConfig,45);
    JPasswordField password = new JPasswordField(passwordFromConfig, 20);

    JTextField title1 = new JTextField(titleFromConfig1);
    JTextField title2 = new JTextField(titleFromConfig2);

    JTextField contacts = new JTextField(contactsFromConfig);
    JTextArea listOfCompanies = new JTextArea(companiesFromConfig1, 5, 5);

    JTextField previousHTMLreport = new JTextField(prevhtml);
    JTextField previousProcessedContacts = new JTextField(prevtxt);

    JLabel labelpreviousHTMLreport = new JLabel("Previously created report:");
    JLabel labelpreviousProcessedContacts = new JLabel("Previously processes clients:");

    JPanel mainPanel = new JPanel();
    JPanel loginPanel = new JPanel();
    JPanel searchCriteria1Panel = new JPanel();
    JPanel searchCriteria2Panel = new JPanel();
    JPanel resumePanelPanel = new JPanel();
    JPanel searchPanel = new JPanel();
    JPanel logPanel = new JPanel();

    JButton searchButton = new JButton("Start Search");
    JButton resumeSearchButton = new JButton("Resume Search");

    JTextArea textlProcessingLog = new JTextArea();
    JScrollPane logAreaScrollPane = new JScrollPane(textlProcessingLog);
    JLabel labelExecutionTime = new JLabel();

    JCheckBox headlessCheckbox  = new JCheckBox("Headless browser");

    JLabel blankJLabel = new JLabel("<html>&nbsp;</html>");
    JLabel orJLabel = new JLabel("-- OR --", 0);

    //text
    //1st circle search criteria
    String searchCriteriaBorder_1st_Text = "1st Search criteria";
    String searchContactTitles_1st_Text = "1st Level Contact Job Titles: ";
    String searchContactPerson_1st_Text = "1st Level Contact (single person only): ";

    //2nd circle search criteria
    String searchCriteriaBorder_2nd_Text = "2nd Search criteria";
    String searchCriteriaNote_2nd_Text = "<html><p>Parameters should be separated by \";\"<br>Example: QA; SDET<br>&nbsp;</p></html>";

    //fonts
    Font note_Font_11 = new Font("Lucida Grande", Font.PLAIN, 11);

    private JLabel setJLabelWithFont(String content, Font font) {
        JLabel jLabel = new JLabel(content);
        jLabel.setHorizontalAlignment(JLabel.LEFT);
        if (font != null) {
            jLabel.setFont(font);
        }
        return jLabel;
    }

    //JLabel searchCriteriaNote_2nd = new JLabel("<html>Parameters should be separated by \";\"<br>Example: QA; SDET</html>").setFont(new Font("FontN", Font.PLAIN, 18));
    //searchCriteriaNote_2nd.
    //JLabel searchCriteriaNote_2nd_Formatted = searchCriteriaNote_2nd.setFont(new Font("Verdana", Font.PLAIN, 18));

    Timer refreshTimer = new Timer();

    int numberOfProcessedContacts = 0;
    int exectuionTime = 0;

    public void createPanel(){

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        //Create and set up the window.
        JFrame frame = new JFrame("LinkedIn Viewer " + VERSION_LABEL);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        //frame.setSize(1200,1000);
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                PropertiesList.setConfigProperty("LOGIN", login.getText());
                PropertiesList.setConfigProperty("PASS", password.getText());
                PropertiesList.setConfigProperty("TITLE1", title1.getText());
                PropertiesList.setConfigProperty("CONTACTS", contacts.getText());
                PropertiesList.setConfigProperty("COMPANIES", listOfCompanies.getText());
                PropertiesList.setConfigProperty("TITLE2", title2.getText());
                PropertiesList.setConfigProperty("PREV_HTML", previousHTMLreport.getText());
                PropertiesList.setConfigProperty("PREV_TXT", previousProcessedContacts.getText());

                PropertiesList.saveCurrentState();

                stopApp();

                System.exit(0);

            }
        });


        JScrollPane companiesAreaScrollPane = new JScrollPane(listOfCompanies);
        companiesAreaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        companiesAreaScrollPane.setPreferredSize(new Dimension(5, 50));

        listOfCompanies.setLineWrap(true);
        listOfCompanies.setWrapStyleWord(true);


        searchButton.setVerticalTextPosition(AbstractButton.BOTTOM);
        searchButton.setMinimumSize(new Dimension(120, 50));
        searchButton.setMaximumSize(new Dimension(180, 75));


        resumeSearchButton.setVerticalTextPosition(AbstractButton.BOTTOM);
        resumeSearchButton.setMinimumSize(new Dimension(120, 50));
        resumeSearchButton.setMaximumSize(new Dimension(180, 75));
        resumeSearchButton.setHorizontalTextPosition(AbstractButton.CENTER);

        searchButton.setActionCommand(START_COMMAND);
        resumeSearchButton.setActionCommand(RESUME_COMMAND);

        searchButton.addActionListener(this);
        resumeSearchButton.addActionListener(this);

        loginPanel.add(new JLabel("Account email:       "));
        loginPanel.add(login);
//        loginPanel.add(new JLabel("Password: "));
//        loginPanel.add(password);
        loginPanel.setBorder(BorderFactory.createTitledBorder("Login"));

        //1st circle
        searchCriteria1Panel.setLayout(new GridLayout(4,2));
        searchCriteria1Panel.add(new JLabel("<html>&nbsp;</html>"));
        searchCriteria1Panel.add(new JLabel("<html>&nbsp;</html>"));
        searchCriteria1Panel.setBorder(BorderFactory.createTitledBorder(searchCriteriaBorder_1st_Text));
        searchCriteria1Panel.add(new JLabel(searchContactTitles_1st_Text));
        searchCriteria1Panel.add(title1);
        searchCriteria1Panel.add(new JLabel("<html>&nbsp;</html>"));
        searchCriteria1Panel.add(orJLabel);
        searchCriteria1Panel.add(new JLabel(searchContactPerson_1st_Text));
        searchCriteria1Panel.add(contacts);

        //2nd circle
        searchCriteria2Panel.setLayout(new GridLayout(3,2));
        searchCriteria2Panel.setBorder(BorderFactory.createTitledBorder(searchCriteriaBorder_2nd_Text));
        searchCriteria2Panel.add(setJLabelWithFont(searchCriteriaNote_2nd_Text, note_Font_11));
        searchCriteria2Panel.add(new JLabel("<html>&nbsp;</html>"));
        searchCriteria2Panel.add(new JLabel("2nd Level Contact Titles:"));
        searchCriteria2Panel.add(title2);
        searchCriteria2Panel.add(new JLabel("List of companies:"));
        searchCriteria2Panel.add(companiesAreaScrollPane);

        resumePanelPanel.setLayout(new BoxLayout(resumePanelPanel, BoxLayout.PAGE_AXIS));
        resumePanelPanel.setBorder(BorderFactory.createTitledBorder("Resume previous run"));
        resumePanelPanel.add(labelpreviousHTMLreport);
        resumePanelPanel.add(previousHTMLreport);
        resumePanelPanel.add(labelpreviousProcessedContacts);
        resumePanelPanel.add(previousProcessedContacts);
        resumePanelPanel.add(resumeSearchButton);

        showResumeDialog();

        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.setLayout(new GridLayout(0, 1));
        //searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.PAGE_AXIS));
        searchPanel.add(headlessCheckbox);
        searchPanel.add(searchButton);

        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.PAGE_AXIS));
        logPanel.add(labelExecutionTime);
        logAreaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        logPanel.add(logAreaScrollPane);

        labelExecutionTime.setVisible(false);
        textlProcessingLog.setVisible(false);


        mainPanel.setBorder(BorderFactory.createTitledBorder("Linkedin"));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setOpaque(true); //content panes must be opaque
        mainPanel.setEnabled(true);
        frame.setContentPane(mainPanel);

        mainPanel.add(loginPanel);
        mainPanel.add(searchCriteria1Panel);
        mainPanel.add(searchCriteria2Panel);
        mainPanel.add(resumePanelPanel);
        mainPanel.add(searchPanel);
        mainPanel.add(logPanel);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private void showResumeDialog() {
        if(previousHTMLreport.getText().trim().equals("")) {
            previousHTMLreport.setVisible(false);
            previousProcessedContacts.setVisible(false);
            resumeSearchButton.setVisible(false);
            labelpreviousHTMLreport.setVisible(false);
            labelpreviousProcessedContacts.setVisible(false);
            //check: this double needed?
            resumeSearchButton.setVisible(false);
        };
    }


    public void startTimer(){
        textlProcessingLog.setVisible(true);
        labelExecutionTime.setVisible(true);
        logAreaScrollPane.setVisible(true);

        TimerTask timerTask = new TimerTask() {
            boolean running = true;
            @Override
            public void run() {
                while(running) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String latestFromLogFile = FileIO.getFileContent(LOG_FILE_LOCATION);

                    textlProcessingLog.setText(latestFromLogFile);
                    labelExecutionTime.setText(String.valueOf(exectuionTime) + " seconds");
                    exectuionTime++;

                    if (linkedIn != null && !linkedIn.running) {
                        textlProcessingLog.append("FINISHED");
                        try {
                            DriverProvider.closeDriver();
                            driver.quit();
                        }catch (Exception e){
                            //may not be started
                            try {
                                FileIO.appendLineToFile(LOG_FILE_LOCATION, "ERROR: " +  e.getMessage());
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        terminate();
                    }
                }
            };

            void terminate(){
                running = false;
            }

        };
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(timerTask, 500, 1000);

    }

    public void actionPerformed(ActionEvent evt) {
        if(evt.getActionCommand().equals(START_COMMAND)) {

            mainPanel.setEnabled(false);

            String reportFile = Tools.getCurDateTime() + ".csv"; // ".html";
            String sessionFile = Tools.getCurDateTime() + ".txt";

            previousHTMLreport.setText(reportFile);
            previousProcessedContacts.setText(sessionFile);

            runApp(reportFile, sessionFile);
        }

        if(evt.getActionCommand().equals(RESUME_COMMAND)) {

            mainPanel.setEnabled(false);

            String reportFile = previousHTMLreport.getText();
            String sessionFile = previousProcessedContacts.getText();

            runApp(reportFile, sessionFile);
        }
    }

    private void runApp(String reportFile, String sessionFile) {
        startTimer();
        LOGGER.info("Started: " + Tools.getCurDateTime());
        driver = DriverProvider.getDriver(headlessCheckbox.isSelected());
        LOGGER.info("Driver created");
        linkedIn = new BaseLinkedIn(
                driver,
                reportFile,
                login.getText(),
                password.getText(),
                title1.getText(),
                listOfCompanies.getText(),
                title2.getText(),
                contacts.getText(),
                sessionFile);
        LOGGER.info("linkedIn(BaseLinkedIn) object created");
        mainThread = new Thread(linkedIn);
        LOGGER.info("mainThread object created");
        mainThread.start();
        LOGGER.info("mainThread started");
    }

    private void stopApp(){
        try {
            FileIO.copyFile(LOG_FILE_LOCATION, CURRENT_LOG_FILE_LOCATION);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("Application stopped");
        if(linkedIn != null) {
            linkedIn.terminate();
            try{
                DriverProvider.closeDriver();
                driver.quit();
            }catch (Exception e){
                //trying to close dead driver
            }
        }
    }
}
