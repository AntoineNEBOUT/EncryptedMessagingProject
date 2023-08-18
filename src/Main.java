import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.Animator;
import utilPackage.ConfigFileProcessorClass;
import utilPackage.EncryptionProcessorClass;
import utilPackage.FileProcessorClass;
import utilPackage.LogProcessorClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        //UIManager.setLookAndFeel(new FlatLightLaf()); //If you want to use FlatLaf
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //If you don't want to use FlatLaf
        UIManager.put("Component.focusWidth", 1);
        UIManager.put( "TextComponent.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put( "Button.arc", 10);
        System.setProperty("flatlaf.useWindowDecorations", "true");
        System.setProperty("flatlaf.menuBarEmbedded", "true");
        System.setProperty( "flatlaf.animation", "true" );
        gui = new GUI(null, null);
        GUI.main(gui);

        ServerSocket ss = null;

        String knownUsers = FileProcessorClass.openFile(System.getProperty("user.dir").concat("\\knownUsers.txt"));

        if(!knownUsers.equals(""))
        {
            StringTokenizer st = new StringTokenizer(knownUsers, "-");

            while(st.hasMoreTokens())
            {
                String userTemp = st.nextToken().trim();

                if(!userTemp.equals("") && userTemp != null)
                {
                    StringTokenizer st2 = new StringTokenizer(userTemp, "=");
                    String ipAdressTemp = st2.nextToken();
                    String userNameTemp = st2.nextToken();
                    knownUsersList.put(userNameTemp, ipAdressTemp);
                    contactsIP.add(ipAdressTemp);
                    contactsName.add(userNameTemp);
                    gui.appendElementOpenContactComboBox(userNameTemp);
                    gui.appendElementRenameContactComboBox(userNameTemp);
                }
            }
            gui.changeContactList(knownUsersList);
        }

        config = new ConfigFileProcessorClass(System.getProperty("user.dir").concat("\\config.ini"));
        mPort = Integer.parseInt(config.getValueOf("port"));

        try
        {
            ss = new ServerSocket(mPort);
        }
        catch (IOException e)
        {
            System.out.println("CRASH IOException : "+e.getMessage());
        }

        mSystemTray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir").concat("\\Icons\\mainIcon.png"));
        mTrayIcon = new TrayIcon(image, "Encrypted Messaging project");
        //Let the system resize the image if needed
        mTrayIcon.setImageAutoSize(true);
        mTrayIcon.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(gui.getState() == 0)
                {
                    gui.setMinimized();
                }
                else if(gui.getState() == 1)
                {
                    gui.setNormal();
                }
            }
        });
        mSystemTray.add(mTrayIcon);

        Main mainApp = new Main();
        mainApp.run(ss);
    }

    public void run(ServerSocket serversocket)
    {
        System.out.println("====================================\nLocal server is started".concat("\nListen on port ")
                .concat(String.valueOf(mPort)).concat("\nStorage on ").concat(pathToStorage));
         System.out.println("Encryption enabled\n====================================");

        while(true)
        {
            try
            {
                if(emergencyCloseSocket)
                    serversocket.close();

                if(socketCanBeRunning)
                {
                    Socket soc = serversocket.accept();

                    InputStream inputStream = soc.getInputStream();
                    BufferedReader inReader = new BufferedReader(new InputStreamReader(inputStream));

                    OutputStream output = soc.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);

                    LogProcessorClass log = new LogProcessorClass(pathToStorage.concat("\\log.txt"));

                    while(true)
                    {
                        if(soc.isBound())
                        {
                            String receivedMsg = EncryptionProcessorClass.decrypt(inReader.readLine(), mKey);
                            StringTokenizer st = new StringTokenizer(receivedMsg, ";");
                            boolean isHyperlink = false;

                            String mTypeOfRequest = "";
                            if(st.hasMoreTokens())
                                mTypeOfRequest = st.nextToken();

                            String content1 = "";
                            if(st.hasMoreTokens())
                                content1 = st.nextToken();

                            String mOurIpAdress = "";
                            if(st.hasMoreTokens())
                            {
                                String temp = st.nextToken();
                                if(!temp.equals("LINK"))
                                    mOurIpAdress = temp;
                                else
                                    isHyperlink = true;
                            }

                            String content2 = "";
                            if(st.hasMoreTokens())
                            {
                                String temp = st.nextToken();
                                if(isHyperlink)
                                    mOurIpAdress = temp;
                                else
                                    content2 = temp;
                            }

                            switch (mTypeOfRequest)
                            {
                                case "message" ->
                                {
                                    writer.println(EncryptionProcessorClass.encrypt("messageReceived;".concat(mOurIpAdress).concat(";").concat(String.valueOf(mPort)), mKey));

                                    String contactNotification = "";
                                    if(knownUsersList.containsValue(String.valueOf(soc.getInetAddress())))
                                    {
                                        JOptionPane.showMessageDialog(null, String.valueOf(soc.getInetAddress()).concat(knownUsersList.values().toString()), "OUI", JOptionPane.INFORMATION_MESSAGE);
                                        contactNotification = contactsName.get(contactsIP.indexOf(String.valueOf(soc.getInetAddress())));

                                        String lineToSave = contactsName.get(contactsIP.indexOf(String.valueOf(soc.getInetAddress()))).concat(";").concat(content1).concat("\n");
                                        if(isHyperlink)
                                            lineToSave = contactsName.get(contactsIP.indexOf(String.valueOf(soc.getInetAddress()))).concat(";").concat(content1).concat(";LINK").concat("\n");

                                        FileProcessorClass.writeFile(System.getProperty("user.dir").concat("\\Threads\\".concat(String.valueOf(soc.getInetAddress())
                                        .replace(":", ".").replace("/", "")).concat(".txt")), lineToSave, "append");
                                    }
                                    else
                                    {
                                        JOptionPane.showMessageDialog(null, String.valueOf(soc.getInetAddress()).concat(knownUsersList.values().toString()), "NON", JOptionPane.INFORMATION_MESSAGE);
                                        contactNotification = String.valueOf(soc.getInetAddress());

                                        knownUsersList.put(String.valueOf(soc.getInetAddress()), String.valueOf(soc.getInetAddress()));
                                        contactsName.add(String.valueOf(soc.getInetAddress()));
                                        contactsIP.add(String.valueOf(soc.getInetAddress()));
                                        gui.changeContactList(knownUsersList);
                                        gui.appendElementOpenContactComboBox(String.valueOf(soc.getInetAddress()));
                                        gui.appendElementOpenContactComboBox(String.valueOf(soc.getInetAddress()));

                                        FileProcessorClass.writeFile(System.getProperty("user.dir").concat("\\knownUsers.txt"),
                                                String.valueOf(soc.getInetAddress()).concat("=")
                                                        .concat(String.valueOf(soc.getInetAddress())).concat("-"),
                                                "append");

                                        FileProcessorClass.createFile(System.getProperty("user.dir").concat("\\Threads\\".concat(String.valueOf(soc.getInetAddress())
                                                .replace(":", ".").replace("/", ""))).concat(".txt"));

                                        String lineToSave = String.valueOf(soc.getInetAddress()).concat(";").concat(content1).concat("\n");

                                        if(isHyperlink)
                                            lineToSave = String.valueOf(soc.getInetAddress()).concat(";").concat(content1).concat(";LINK").concat("\n");

                                        FileProcessorClass.writeFile(System.getProperty("user.dir").concat("\\Threads\\").concat(String.valueOf(soc.getInetAddress())
                                                        .replace(":", ".").replace("/", "")).concat(".txt"),
                                                lineToSave, "append");

                                    }
                                    if(isHyperlink)
                                        log.writeLog("New link from ".concat((String.valueOf(soc.getInetAddress()))), false);
                                    else
                                        log.writeLog("New message from ".concat((String.valueOf(soc.getInetAddress()))), false);

                                    //Notification
                                    //From StackOverflow : https://stackoverflow.com/questions/34490218/how-to-make-a-windows-notification-in-java
                                    if(isHyperlink)
                                        mTrayIcon.displayMessage("New link from ".concat(contactNotification), content1, TrayIcon.MessageType.INFO);
                                    else
                                        mTrayIcon.displayMessage("New message from ".concat(contactNotification), content1, TrayIcon.MessageType.INFO);

                                    gui.appendMessageOnMessageView(String.valueOf(soc.getInetAddress()), content1, isHyperlink);
                                    soc.close();
                                }
                                case "isOnline" ->
                                {
                                    System.out.println(mOurIpAdress);
                                    writer.println(EncryptionProcessorClass.encrypt("serverIsOnline;".concat(mOurIpAdress).concat(";").concat(String.valueOf(mPort)), mKey));
                                    soc.close();
                                }
                                case "stop" ->
                                {
                                    writer.println(EncryptionProcessorClass.encrypt("Connexion is offline", mKey));
                                    soc.close();

                                    //log.writeLog("request:STOP by '".concat(mUserName).concat("' -> SUCCESS"), true);
                                    //log.writeLog("=============================================", false);

                                    System.exit(0);
                                }
                                default ->
                                {
                                    writer.println(EncryptionProcessorClass.encrypt("Incorrect request type", mKey));
                                    System.out.println("Incorrect request type");
                                }
                            }
                        }
                        else
                        {
                            System.out.println("Disconected ! ");
                        }
                    }
                }
                else
                {
                    emergencyCloseSocket = false;
                    socketCanBeRunning = true;
                    run(serversocket);
                }
            }
            catch (Exception e)
            {
                if(attemptToReconnect <= 2 && attemptToReconnect >= 0)
                {
                    attemptToReconnect = 1;
                }
                else if(attemptToReconnect > 2)
                {
                    emergencyCloseSocket = true;
                    socketCanBeRunning = false;;
                }
            }
        }
    }
    static int attemptToReconnect = 0;
    static boolean emergencyCloseSocket = false;
    static boolean socketCanBeRunning = true;
    static int index = 0;
    static HashMap<String, String> knownUsersList = new HashMap<String, String>();
    static int mPort;
    static String pathToStorage = System.getProperty("user.dir");
    static InetAddress address;
    static
    {
        try
        {
            address = InetAddress.getLocalHost();
        }
        catch (UnknownHostException e)
        {
            throw new RuntimeException(e);
        }
    }
    static GUI gui;
    static ArrayList<String> contactsIP = new ArrayList<String>();
    static ArrayList<String> contactsName = new ArrayList<String>();
    static SystemTray mSystemTray;
    static TrayIcon mTrayIcon;
    static String mKey = "1234";
    private static ConfigFileProcessorClass config;
}