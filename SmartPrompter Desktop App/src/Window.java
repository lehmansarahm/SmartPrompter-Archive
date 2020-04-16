package src;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;

public class Window {
    
    private static final String PLATFORM_TOOLS_MAC = "platform-tools-mac/";
    private static final String PLATFORM_TOOLS_WINDOWS = "platform-tools-windows/";
    
    private static final String ADMIN_APK = "apk/sp_admin.apk";
    private static final String ADMIN_PACKAGE_NAME = "edu.temple.smartprompter_v3.admin";
    
    private static final String PATIENT_APK = "apk/sp_patient.apk";
    private static final String PATIENT_PACKAGE_NAME = "edu.temple.smartprompter_v3";

    /*
    private static final String ANDROID_DATA_DIR = "storage/self/primary/Android/data/";
    private static final String ADMIN_FILES_DIR = ANDROID_DATA_DIR + ADMIN_PACKAGE_NAME + "/files";
    private static final String PATIENT_FILES_DIR = ANDROID_DATA_DIR + PATIENT_PACKAGE_NAME + "/files";
    */

    private static final String DOCS_DIRECTORY = "storage/self/primary/Documents";
    private static final String ALARMS_DIRECTORY = DOCS_DIRECTORY + "/sp_alarms";
    private static final String ARCHIVE_DIRECTORY = DOCS_DIRECTORY + "/sp_archive";
    private static final String AUDIO_DIRECTORY = DOCS_DIRECTORY + "/sp_audio";
    private static final String PHOTOS_DIRECTORY = DOCS_DIRECTORY + "/sp_photos";
    private static final String LOGS_DIRECTORY = DOCS_DIRECTORY + "/sp_logs";
    
    
    // ---------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------
    
    
    boolean usingWindows = false;
    String ADB_PATH = (usingWindows ? PLATFORM_TOOLS_WINDOWS + "adb.exe" : PLATFORM_TOOLS_MAC + "adb");
    
    JFrame frame;
    JTextPane feedbackText;
    JButton installButton, extractButton;
    
    CliRunner cliRunner = new CliRunner();
    
    ActionListener installAL = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            boolean success = true;
            success &= cliRunner.run("Install Admin App", new String[] {ADB_PATH, "install", ADMIN_APK});
            success &= cliRunner.run("Install Patient App", new String[] {ADB_PATH, "install", PATIENT_APK});
    
            // ignore the success result of this guy ... if the directory already exists, this command will fail, and we don't
            // want that to influence the rest of the output
            cliRunner.run("Create Documents directory", new String[] {ADB_PATH, "shell", "mkdir", DOCS_DIRECTORY});
 
            success &= cliRunner.run("Create sp_alarms directory", new String[] {ADB_PATH, "shell", "mkdir", ALARMS_DIRECTORY});
            success &= cliRunner.run("Create sp_archive directory", new String[] {ADB_PATH, "shell", "mkdir", ARCHIVE_DIRECTORY});
            success &= cliRunner.run("Create sp_audio directory", new String[] {ADB_PATH, "shell", "mkdir", AUDIO_DIRECTORY});
            success &= cliRunner.run("Create sp_photos directory", new String[] {ADB_PATH, "shell", "mkdir", PHOTOS_DIRECTORY});
            success &= cliRunner.run("Create sp_logs directory", new String[] {ADB_PATH, "shell", "mkdir", LOGS_DIRECTORY});
    
            if (success)
                feedbackText.setText("Installation procedure complete! \n\n Please open the "
                                     + "apps on the target device and accept the requested "
                                     + "permissions.");
            else
                feedbackText.setText("Ahh crap ... something went wrong with the installation ...");
        }
    };
    
    ActionListener extractAL = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            boolean success = true;
            success &= cliRunner.run("Uninstall Admin App", new String[] {ADB_PATH, "uninstall", ADMIN_PACKAGE_NAME});
            success &= cliRunner.run("Uninstall Patient App", new String[] {ADB_PATH, "uninstall", PATIENT_PACKAGE_NAME});

/*
            success &= cliRunner.run("Pull Admin files directory", new String[] {ADB_PATH, "pull", ADMIN_FILES_DIR});
            success &= cliRunner.run("Remove Admin files directory", new String[] {ADB_PATH, "shell", "rm", "-r", ADMIN_FILES_DIR});

            success &= cliRunner.run("Pull Patient files directory", new String[] {ADB_PATH, "pull", PATIENT_FILES_DIR});
            success &= cliRunner.run("Remove Patient files directory", new String[] {ADB_PATH, "shell", "rm", "-r", PATIENT_FILES_DIR});
*/

            success &= cliRunner.run("Pull sp_alarm directory", new String[] {ADB_PATH, "pull", ALARMS_DIRECTORY});
            success &= cliRunner.run("Remove sp_alarm directory", new String[] {ADB_PATH, "shell", "rm", "-r", ALARMS_DIRECTORY});

            success &= cliRunner.run("Pull sp_archive directory", new String[] {ADB_PATH, "pull", ARCHIVE_DIRECTORY});
            success &= cliRunner.run("Remove sp_archive directory", new String[] {ADB_PATH, "shell", "rm", "-r", ARCHIVE_DIRECTORY});

            success &= cliRunner.run("Pull sp_audio directory", new String[] {ADB_PATH, "pull", AUDIO_DIRECTORY});
            success &= cliRunner.run("Remove sp_audio directory", new String[] {ADB_PATH, "shell", "rm", "-r", AUDIO_DIRECTORY});

            success &= cliRunner.run("Pull sp_photos directory", new String[] {ADB_PATH, "pull", PHOTOS_DIRECTORY});
            success &= cliRunner.run("Remove sp_photos directory", new String[] {ADB_PATH, "shell", "rm", "-r", PHOTOS_DIRECTORY});

            success &= cliRunner.run("Pull sp_logs directory", new String[] {ADB_PATH, "pull", LOGS_DIRECTORY});
            success &= cliRunner.run("Remove sp_logs directory", new String[] {ADB_PATH, "shell", "rm", "-r", LOGS_DIRECTORY});
            
            if (success)
                feedbackText.setText("Extraction procedure complete! \n\n Please disable developer "
                                    + "mode on the device, and return it to the tester.");
            else
                feedbackText.setText("Ahh crap ... something went wrong with the extraction ...");
        }
    };
    
    public Window() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        feedbackText = new JTextPane();
        feedbackText.setBounds(50, 50, 375, 120);
        feedbackText.setEditable(false);

        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
        feedbackText.setParagraphAttributes(attribs, true);

        String osName = System.getProperty("os.name");
        usingWindows = osName.toLowerCase().contains("windows");
        System.out.println("Running OS: " + osName + " \t with platform tools: " + (usingWindows ? "windows" : "mac"));

        feedbackText.setText("Welcome to the SmartPrompter Desktop App! \n\n"
            + "Please ensure that the test device is plugged in, Developer Mode is enabled, "
            + "and a Firebase account had been created for this tester. \n\n"
            + "Then, click one of the buttons below to continue.");
        
        installButton = new JButton("Install");
        installButton.setBounds(125, 180, 100, 40);
        installButton.addActionListener(installAL);
        
        extractButton = new JButton("Extract");
        extractButton.setBounds(250, 180, 100, 40);
        extractButton.addActionListener(extractAL);
    }
    
    public void show() {
        frame.add(feedbackText);
        frame.add(installButton);
        frame.add(extractButton);
        
        frame.setSize(475, 290);
        frame.setLayout(null);
        frame.setVisible(true);
    }
    
}
