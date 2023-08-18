import EncryptedMessagingPackage.Sender;
import utilPackage.ConfigFileProcessorClass;
import utilPackage.FileProcessorClass;
import utilPackage.LogProcessorClass;
import utilPackage.ZipFileProcessorClass;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.io.*;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

public class GUI
{
    private JFrame frame;
    private JButton sendMessageButton;
    private JPanel mainPanel;
    private JTextField messageEditor;
    private JScrollPane textEditArea;
    private JPanel messageSenderPanel;
    private JComboBox openContactComboBox;
    private JButton openContactButton;
    private JComboBox renameContactComboBox;
    private JButton renameContactButton;
    private JTextField renameContactTextField;
    private JLabel openContactLabel;
    private JPanel openContactPanel;
    private JPanel renameContactPanel;
    private JLabel renameContactLabel;
    private JPanel contactPanel;
    private JButton isOnlineCeckButton;
    private JPanel isOnlinePanel;
    private JLabel isOnlineLabel;
    private JPanel addContactPanel;
    private JLabel addContactLabel;
    private JTextField addContactNameTextField;
    private JButton addContactButton;
    private JTextField addContactIPTextField;
    private JPanel messagePanel;
    private JLabel stateColorLabel;
    private JButton sendLinkButton;
    private JMenuBar menuBar;
    private LogProcessorClass log;
    private int rowNumber = 0;
    private GridBagLayout gridBag = new GridBagLayout();

    public GUI(String contactName, String contactIP)
    {
        messagePanel.setLayout(gridBag);
        menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        JMenu menuView = new JMenu("View");
        JMenu menuMyIP = new JMenu("My IP");
        menuBar.add(menuFile);
        menuBar.add(menuView);
        menuBar.add(menuMyIP);

        //JMenuItem of menuFile
        JMenuItem menuItemQuit = new JMenuItem("Quit");
        JMenuItem menuItemExportContactFile = new JMenuItem("Export contacts");
        JMenuItem menuItemImportContactFile = new JMenuItem("Import contacts");
        JMenuItem menuItemEditConfigFile = new JMenuItem("Edit config file");
        //menuFile config
        menuFile.add(menuItemImportContactFile);
        menuFile.add(menuItemExportContactFile);
        menuFile.addSeparator();
        menuFile.add(menuItemEditConfigFile);
        menuFile.addSeparator();
        menuFile.add(menuItemQuit);

        //JMenu of menuView
        JMenu menuPartsOfContactPanel = new JMenu("Contact panel parts");
        //JCheckBoxMenuItem of menuView
        JCheckBoxMenuItem menuCheckBoxContact = new JCheckBoxMenuItem("Contact panel", true);
        JCheckBoxMenuItem menuCheckBoxOpenContact = new JCheckBoxMenuItem("Open contact panel", true);
        JCheckBoxMenuItem menuCheckBoxAddContact = new JCheckBoxMenuItem("Add contact panel", true);
        JCheckBoxMenuItem menuCheckBoxRenameContact = new JCheckBoxMenuItem("Rename contact panel", true);
        JCheckBoxMenuItem menuCheckBoxCheckContact = new JCheckBoxMenuItem("Connection state panel", true);
        JCheckBoxMenuItem menuCheckBoxSendButton = new JCheckBoxMenuItem("Send message button", true);
        //menuView config
        menuView.add(menuCheckBoxContact);
        menuView.add(menuPartsOfContactPanel);
        menuView.addSeparator();
        menuView.add(menuCheckBoxSendButton);
        //menuPartsOfContactPanel config
        menuPartsOfContactPanel.add(menuCheckBoxOpenContact);
        menuPartsOfContactPanel.add(menuCheckBoxAddContact);
        menuPartsOfContactPanel.add(menuCheckBoxRenameContact);
        menuPartsOfContactPanel.add(menuCheckBoxCheckContact);

        //JMenuItem of menuMyIP
        JMenuItem menuItemLocalIP = new JMenuItem("My local IP");
        JMenuItem menuItemPublicIPv4 = new JMenuItem("My public IP (IPv4)");
        JMenuItem menuItemPublicIPv6 = new JMenuItem("My public IP (IPv6)");
        //menuMyIP config
        menuMyIP.add(menuItemLocalIP);
        menuMyIP.add(menuItemPublicIPv4);
        menuMyIP.add(menuItemPublicIPv6);

        //Set icons
        sendMessageButton.setIcon(new ImageIcon(mIconsPath.concat("send.png")));
        sendMessageButton.setText("");
        sendLinkButton.setIcon(new ImageIcon(mIconsPath.concat("link.png")));
        sendLinkButton.setText("");
        menuItemImportContactFile.setIcon(new ImageIcon(mIconsPath.concat("import.png")));
        menuItemExportContactFile.setIcon(new ImageIcon(mIconsPath.concat("export.png")));
        menuItemQuit.setIcon(new ImageIcon(mIconsPath.concat("quit.png")));
        menuItemLocalIP.setIcon(new ImageIcon(mIconsPath.concat("localIP.png")));
        menuItemPublicIPv4.setIcon(new ImageIcon(mIconsPath.concat("publicIP.png")));
        menuItemPublicIPv6.setIcon(new ImageIcon(mIconsPath.concat("publicIP.png")));
        menuItemEditConfigFile.setIcon(new ImageIcon(mIconsPath.concat("config.png")));
        stateColorLabel.setIcon(new ImageIcon(mIconsPath.concat("grayState.png")));

        messageEditor.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        //Add placeholder style
        addPlaceHolderStyle(messageEditor);
        addPlaceHolderStyle(addContactNameTextField);
        addPlaceHolderStyle(addContactIPTextField);
        addPlaceHolderStyle(renameContactTextField);

        //Initialising the log class
        log = new LogProcessorClass(System.getProperty("user.dir").concat("\\log.txt"));

        //Initialising the configFile class
        config = new ConfigFileProcessorClass(System.getProperty("user.dir").concat("\\config.ini"));

        //Initialising check box
        //Send message button
        sendMessageButton.setVisible(intToBoolean(Integer.parseInt(config.getValueOf("sendMessageButtonVisible"))));
        menuCheckBoxSendButton.setState(intToBoolean(Integer.parseInt(config.getValueOf("sendMessageButtonVisible"))));
        //Contact panel
        contactPanel.setVisible(intToBoolean(Integer.parseInt(config.getValueOf("contactPanelVisible"))));
        menuCheckBoxContact.setState(intToBoolean(Integer.parseInt(config.getValueOf("contactPanelVisible"))));
        //Menu parts of contact panel
        menuPartsOfContactPanel.setEnabled(intToBoolean(Integer.parseInt(config.getValueOf("contactPanelVisible"))));
        //Open contact panel
        openContactPanel.setVisible(intToBoolean(Integer.parseInt(config.getValueOf("openContactPanelVisible"))));
        menuCheckBoxOpenContact.setState(intToBoolean(Integer.parseInt(config.getValueOf("openContactPanelVisible"))));
        //Add contact panel
        addContactPanel.setVisible(intToBoolean(Integer.parseInt(config.getValueOf("addContactPanelVisible"))));
        menuCheckBoxAddContact.setState(intToBoolean(Integer.parseInt(config.getValueOf("addContactPanelVisible"))));
        //Rename contact panel
        renameContactPanel.setVisible(intToBoolean(Integer.parseInt(config.getValueOf("renameContactPanelVisible"))));
        menuCheckBoxRenameContact.setState(intToBoolean(Integer.parseInt(config.getValueOf("renameContactPanelVisible"))));
        //Is online panel
        isOnlinePanel.setVisible(intToBoolean(Integer.parseInt(config.getValueOf("checkContactPanelVisible"))));
        menuCheckBoxCheckContact.setState(intToBoolean(Integer.parseInt(config.getValueOf("checkContactPanelVisible"))));

        //Initialising port
        mPort = Integer.parseInt(config.getValueOf("port"));

        mCurrentContactName = contactName;
        mCurrentContactIP = contactIP;

        openContactButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                stateColorLabel.setIcon(new ImageIcon(System.getProperty("user.dir").concat("\\Icons\\grayState.png")));
                rowNumber = 0;
                messagePanel.removeAll();
                messagePanel.revalidate();
                messagePanel.repaint();

                String comboboxContent = Objects.requireNonNull(openContactComboBox.getSelectedItem()).toString();
                mCurrentContactName = comboboxContent;
                mCurrentContactIP = mContactsList.get(comboboxContent);

                String contactThread = FileProcessorClass.openFile(System.getProperty("user.dir").concat("\\Threads\\").concat(mCurrentContactIP.replace(":", ".").replace("/", "")).concat(".txt"));
                if(!contactThread.equals(""))
                {
                    StringTokenizer threadPart = new StringTokenizer(contactThread, "\n");

                    while(threadPart.hasMoreTokens())
                    {
                        String messageTemp = threadPart.nextToken().trim();

                        if(!messageTemp.equals("") && messageTemp != null)
                        {
                            StringTokenizer messagePart = new StringTokenizer(messageTemp, ";");
                            String tempContactName = messagePart.nextToken();
                            String tempMessageContent = messagePart.nextToken();

                            boolean isHyperlink = false;

                            if(messagePart.hasMoreTokens())
                                if(messagePart.nextToken().equals("LINK"))
                                    isHyperlink = true;

                            if(tempContactName.equals("YOU"))
                            {
                                showMessage(0, isHyperlink, tempMessageContent, tempContactName, Color.GRAY, Color.DARK_GRAY, Color.BLACK);
                            }
                            else
                            {
                                showMessage(1, isHyperlink, tempMessageContent, tempContactName, Color.GRAY, Color.DARK_GRAY, Color.BLACK);
                            }
                        }
                    }
                }

                frame.setTitle("EncryptedMessagingProject - ".concat(mCurrentContactName));
                checkConnection();
            }
        });

        sendMessageButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                sendMessage();
            }
        });

        sendLinkButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                sendLink();
            }
        });

        renameContactButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(mContactsList.containsKey(Objects.requireNonNull(renameContactComboBox.getSelectedItem()).toString()) && !(renameContactTextField.getText().equals("")))
                {
                    mContactsList.put(renameContactTextField.getText(), mContactsList.get(renameContactComboBox.getSelectedItem().toString()));
                    String knownContactsFileContent = FileProcessorClass.openFile(System.getProperty("user.dir").concat("\\knownUsers.txt"));
                    StringTokenizer knownContacts = new StringTokenizer(knownContactsFileContent, "-");

                    //Cleaning contacts file
                    FileProcessorClass.deleteFile(System.getProperty("user.dir").concat("\\knownUsers.txt"));
                    FileProcessorClass.createFile(System.getProperty("user.dir").concat("\\knownUsers.txt"));

                    //Filling the new file
                    while(knownContacts.hasMoreTokens())
                    {
                        String contactInfo = knownContacts.nextToken().trim();
                        if(!contactInfo.equals("") && contactInfo != null)
                        {
                            StringTokenizer contactPart = new StringTokenizer(contactInfo, "=");
                            String tempContactIP = contactPart.nextToken();
                            String tempContactName = contactPart.nextToken();
                            if(tempContactIP.equals(mContactsList.get(renameContactComboBox.getSelectedItem().toString())) && tempContactName.equals(renameContactComboBox.getSelectedItem().toString()))
                            {
                                FileProcessorClass.writeFile(System.getProperty("user.dir").concat("\\knownUsers.txt"),
                                        tempContactIP.concat("=").concat(renameContactTextField.getText()).concat("-"), "append");
                            }
                            else
                            {
                                FileProcessorClass.writeFile(System.getProperty("user.dir").concat("\\knownUsers.txt"),
                                        contactInfo.concat("-"), "append");
                            }
                        }
                    }
                    mContactsList.remove(renameContactComboBox.getSelectedItem().toString());
                    JOptionPane.showMessageDialog(null, "You will need to restart the application to ensure that the changes are taken into account.", "Contact has been renamed", JOptionPane.WARNING_MESSAGE);
                    System.exit(0);
                }
            }
        });

        addContactButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if((!(addContactNameTextField.getText().equals("")) && !(addContactNameTextField.getText().equals("Contact name"))) && (!(addContactIPTextField.getText().equals("")) && !(addContactIPTextField.getText().equals("Contact IP"))))
                {
                    if(!mContactsList.containsKey(addContactNameTextField.getText()) && !mContactsList.containsValue("/".concat(addContactIPTextField.getText())))
                    {
                        FileProcessorClass.writeFile(System.getProperty("user.dir").concat("\\knownUsers.txt"),
                                "/".concat(addContactIPTextField.getText().concat("="))
                                        .concat(addContactNameTextField.getText()).concat("-"),
                                "append");
                        FileProcessorClass.createFile(System.getProperty("user.dir").concat("\\Threads\\".concat(addContactIPTextField.getText())
                                .replace(":", ".").replace("/", "")).concat(".txt"));
                        mContactsList.put(addContactNameTextField.getText(), addContactIPTextField.getText());
                        openContactComboBox.addItem(addContactNameTextField.getText());
                        renameContactComboBox.addItem(addContactNameTextField.getText());

                                                addPlaceHolderStyle(addContactNameTextField);
                        addPlaceHolderStyle(addContactIPTextField);

                        log.writeLog("Contact added : ".concat(addContactNameTextField.getText()).concat(" (").concat(addContactIPTextField.getText()).concat(")"), false);

                        addContactNameTextField.setText("Contact name");
                        addContactIPTextField.setText("Contact IP");
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "The contact name or contact IP is already known.", "Add contact error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "The contact name or contact IP is incorrect.", "Add contact error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        menuItemQuit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });

        menuCheckBoxContact.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                contactPanel.setVisible(menuCheckBoxContact.getState());
                menuPartsOfContactPanel.setEnabled(menuCheckBoxContact.getState());
                config.setData("contactPanelVisible", String.valueOf(booleanToInt(menuCheckBoxContact.getState())));
            }
        });

        menuCheckBoxOpenContact.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                openContactPanel.setVisible(menuCheckBoxOpenContact.getState());
                config.setData("openContactPanelVisible", String.valueOf(booleanToInt(menuCheckBoxOpenContact.getState())));
            }
        });

        menuCheckBoxAddContact.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                addContactPanel.setVisible(menuCheckBoxAddContact.getState());
                config.setData("addContactPanelVisible", String.valueOf(booleanToInt(menuCheckBoxAddContact.getState())));
            }
        });

        menuCheckBoxRenameContact.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                renameContactPanel.setVisible(menuCheckBoxRenameContact.getState());
                config.setData("renameContactPanelVisible", String.valueOf(booleanToInt(menuCheckBoxRenameContact.getState())));
            }
        });

        menuCheckBoxCheckContact.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                isOnlinePanel.setVisible(menuCheckBoxCheckContact.getState());
                config.setData("checkContactPanelVisible", String.valueOf(booleanToInt(menuCheckBoxCheckContact.getState())));
            }
        });

        menuCheckBoxSendButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                sendMessageButton.setVisible(menuCheckBoxSendButton.getState());
                config.setData("sendMessageButtonVisible", String.valueOf(booleanToInt(menuCheckBoxSendButton.getState())));
            }
        });

        menuItemExportContactFile.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Export contact file");
                fileChooser.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Zip file", "zip");
                fileChooser.addChoosableFileFilter(filter);

                int userSelection = fileChooser.showSaveDialog(null);

                if(userSelection == JFileChooser.APPROVE_OPTION)
                {
                    try
                    {
                        FileProcessorClass.copyFile(System.getProperty("user.dir").concat("\\knownUsers.txt"), System.getProperty("user.dir").concat("\\Threads\\knownUsers.txt"));
                        ZipFileProcessorClass.zipFolder(Paths.get(System.getProperty("user.dir").concat("\\Threads")));
                        FileProcessorClass.copyFile(System.getProperty("user.dir").concat("\\Threads.zip"), fileChooser.getSelectedFile().toString().concat(".zip"));
                        FileProcessorClass.deleteFile(System.getProperty("user.dir").concat("\\Threads\\knownUsers.txt"));
                        FileProcessorClass.deleteFile(System.getProperty("user.dir").concat("\\Threads.zip"));
                    }
                    catch (Exception ex)
                    {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        menuItemImportContactFile.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(null, "Importing is not without risk: imported contacts will be " +
                        "added to existing contacts and messages exchanged. However, if there are duplicates in the contacts, " +
                        "the messages cannot be imported, which can lead to unforeseen actions.", "Warning", JOptionPane.WARNING_MESSAGE);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Import contact file");
                fileChooser.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Zip file", "zip");
                fileChooser.addChoosableFileFilter(filter);

                int userSelection = fileChooser.showOpenDialog(null);

                if(userSelection == JFileChooser.APPROVE_OPTION)
                {
                    try
                    {
                        ZipFileProcessorClass.unZip(Paths.get(fileChooser.getSelectedFile().toString()), Paths.get(System.getProperty("user.dir").concat("\\Threads")));
                        String contactsToAdd = FileProcessorClass.openFile(System.getProperty("user.dir").concat("\\Threads\\knownUsers.txt"));
                        FileProcessorClass.writeFile(System.getProperty("user.dir").concat("\\knownUsers.txt"), contactsToAdd, "append");
                        FileProcessorClass.deleteFile(System.getProperty("user.dir").concat("\\Threads\\knownUsers.txt"));
                        JOptionPane.showMessageDialog(null, "You will need to restart the application to ensure that the changes are taken into account.", "Import is complete", JOptionPane.WARNING_MESSAGE);
                    }
                    catch (Exception ex)
                    {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        menuItemLocalIP.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    InetAddress localhost = InetAddress.getLocalHost();
                    String localIPAddress = localhost.getHostAddress();
                    int result = JOptionPane.showConfirmDialog(null, "Your local IP address is : ".concat(localIPAddress)
                            .concat("\nWould you like to copy this address to your paper press?"), "See my local IP (IPv4)",
                            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                    if (result == 0)
                    {
                        StringSelection stringSelection = new StringSelection(localIPAddress);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null);
                    }
                }
                catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        });

        menuItemPublicIPv4.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try 
                {
                    URL whatismyip = new URL("http://checkip.amazonaws.com");
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            whatismyip.openStream()));

                    String publicIpAddress = in.readLine(); //you get the IP as a String
                    int result = JOptionPane.showConfirmDialog(null, "Your public IP address (IPv4 format) is : ".concat(publicIpAddress)
                                    .concat("\nWould you like to copy this address to your paper press?"), "See my public IP (IPv4)",
                            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                    if(result == 0)
                    {
                        StringSelection stringSelection = new StringSelection(publicIpAddress);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null);
                    }
                }
                catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        });

        menuItemPublicIPv6.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    JOptionPane.showMessageDialog(null, "Please note that it may change regularly.", "Warning", JOptionPane.WARNING_MESSAGE);
                    int result = JOptionPane.showConfirmDialog(null, "Your public IP address (IPv6 format) it can be seen on \"whatismyipaddress.com\""
                                    .concat("\nWould you like to go to this website?"), "See my public IP (IPv6)",
                            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                    if(result == 0)
                    {
                        Desktop desktop = java.awt.Desktop.getDesktop();
                        URI oURL = new URI("https://whatismyipaddress.com/");
                        desktop.browse(oURL);
                    }
                }
                catch (URISyntaxException | IOException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        });

        menuItemEditConfigFile.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                File configurationFile = new File(System.getProperty("user.dir").concat("\\config.ini"));
                if(configurationFile.exists())
                {
                    try
                    {
                        JOptionPane.showMessageDialog(null, "For changes to the configuration file to take effect, the program must be restarted.", "Open configuration file", JOptionPane.WARNING_MESSAGE);
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(configurationFile);
                    }
                    catch (IOException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        messageEditor.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                super.keyReleased(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    sendMessage();
                }
            }
        });

        messageEditor.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                super.focusGained(e);
                if(messageEditor.getText().equals("Enter a message"))
                {
                    messageEditor.setText(null);
                    messageEditor.requestFocus();
                    //Remove placeholder style
                    removePlaceHolderStyle(messageEditor);
                }
            }
        });

        messageEditor.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                super.focusLost(e);
                if(messageEditor.getText().length() == 0)
                {
                    //Add placeholder style
                    addPlaceHolderStyle(messageEditor);
                    messageEditor.setText("Enter a message");
                }
            }
        });

        addContactNameTextField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                super.focusGained(e);
                if(addContactNameTextField.getText().equals("Contact name"))
                {
                    addContactNameTextField.setText(null);
                    addContactNameTextField.requestFocus();
                    //Remove placeholder style
                    removePlaceHolderStyle(addContactNameTextField);
                }
            }
        });

        addContactNameTextField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                super.focusLost(e);
                if(addContactNameTextField.getText().length() == 0)
                {
                    //Add placeholder style
                    addPlaceHolderStyle(addContactNameTextField);
                    addContactNameTextField.setText("Contact name");
                }
            }
        });

        addContactIPTextField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                super.focusGained(e);
                if(addContactIPTextField.getText().equals("Contact IP"))
                {
                    addContactIPTextField.setText(null);
                    addContactIPTextField.requestFocus();
                    //Remove placeholder style
                    removePlaceHolderStyle(addContactIPTextField);
                }
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                super.focusLost(e);
                if(addContactIPTextField.getText().length() == 0)
                {
                    //Add placeholder style
                    addPlaceHolderStyle(addContactIPTextField);
                    addContactIPTextField.setText("Contact IP");
                }
            }
        });

        renameContactTextField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                super.focusGained(e);
                if(renameContactTextField.getText().equals("New contact name"))
                {
                    renameContactTextField.setText(null);
                    renameContactTextField.requestFocus();
                    //Remove placeholder style
                    removePlaceHolderStyle(renameContactTextField);
                }
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                super.focusLost(e);
                if(renameContactTextField.getText().length() == 0)
                {
                    //Add placeholder style
                    addPlaceHolderStyle(renameContactTextField);
                    renameContactTextField.setText("New contact name");
                }
            }
        });
        stateColorLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                checkConnection();
            }
        });
    }

    public static void main(GUI gui) throws Exception
    {
        gui.frame = new JFrame("EncryptedMessagingProject");
        gui.frame.setContentPane(gui.mainPanel);
        gui.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.frame.setJMenuBar(gui.menuBar);
        gui.frame.pack();
        gui.frame.setVisible(true);
    }

    public void appendElementOpenContactComboBox(String element)
    {
        openContactComboBox.addItem(element);
    }

    public void appendElementRenameContactComboBox(String element)
    {
        renameContactComboBox.addItem(element);
    }

    public void changeContactList(HashMap<String, String> contactsList)
    {
        mContactsList = contactsList;
    }

    public void appendMessageOnMessageView(String contactIP, String message, boolean isHyperlink)
    {
        if(contactIP.equals(mCurrentContactIP))
        {
            showMessage(1, isHyperlink, message, mCurrentContactName, Color.GRAY, Color.DARK_GRAY, Color.BLACK);

            messagePanel.revalidate();
            messagePanel.repaint();
            messageEditor.setText("");
        }
    }

    public void addPlaceHolderStyle(JTextField textField)
    {
        Font font = textField.getFont();
        font = font.deriveFont(Font.ITALIC);
        textField.setFont(font);
        textField.setForeground(Color.gray);
    }

    public void removePlaceHolderStyle(JTextField textField)
    {
        Font font = textField.getFont();
        font = font.deriveFont(Font.PLAIN);
        textField.setFont(font);
        textField.setForeground(Color.black);
    }

    private void sendMessage()
    {
        if(!(messageEditor.getText().contains(";")) && !(messageEditor.getText().equals("Enter a message")) && !(messageEditor.getText().equals("")))
        {
            FileProcessorClass.writeFile(System.getProperty("user.dir").concat("\\Threads\\".concat(mCurrentContactIP.replace(":", ".").replace("/", ""))
                .concat(".txt")), "YOU;".concat(messageEditor.getText()).concat("\n"), "append");
            log.writeLog("New message send to ".concat(mCurrentContactIP), false);

            showMessage(0, false, true, messageEditor.getText(), "YOU", Color.GRAY, Color.DARK_GRAY, Color.BLACK, Color.ORANGE);

            messagePanel.revalidate();
            messagePanel.repaint();
            messageEditor.setText("");
        }
        else
        {
            JOptionPane.showMessageDialog(null, "The message is incorrect.", "Send message error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendLink()
    {
        if(!(messageEditor.getText().contains(";")) && !(messageEditor.getText().equals("Enter a message")) && !(messageEditor.getText().equals("")))
        {
            FileProcessorClass.writeFile(System.getProperty("user.dir").concat("\\Threads\\".concat(mCurrentContactIP.replace(":", ".").replace("/", ""))
                    .concat(".txt")), "YOU;".concat(messageEditor.getText()).concat(";LINK").concat("\n"), "append");
            log.writeLog("New kyperlink send to ".concat(mCurrentContactIP), false);

            showMessage(0, true, true, messageEditor.getText(), "YOU", Color.GRAY, Color.DARK_GRAY, Color.BLUE, Color.ORANGE);

            messagePanel.revalidate();
            messagePanel.repaint();
            messageEditor.setText("");
        }
        else
        {
            JOptionPane.showMessageDialog(null, "The message is incorrect.", "Send message error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkConnection()
    {
        int answerValue = Sender.sendMessage(mCurrentContactIP.replace("/", ""), mPort, "isOnline");
        if (answerValue == 1)
        {
            stateColorLabel.setIcon(new ImageIcon(System.getProperty("user.dir").concat("\\Icons\\greenState.png")));
        }
        else if (answerValue == 2)
        {
            stateColorLabel.setIcon(new ImageIcon(System.getProperty("user.dir").concat("\\Icons\\redState.png")));
        }
    }

    private void showMessage(int typeOfMessage, boolean isHyperlink, String message, String name, Color backgroundColor, Color contactColor, Color textColor)
    {
        showMessage(typeOfMessage, isHyperlink, false, message, name, backgroundColor, contactColor, textColor, null);
    }

    private void showMessage(int typeOfMessage, boolean isHyperlink, boolean sendMessage, String message, String name, Color backgroundColor, Color contactColor, Color textColor, Color notReceivedColor)
    {
        /*
            typeOfMessage (int) : 0 => sender
                                  1 => receiver
            backgroundColor (Color)
            contactColor (Color)
            textColor (Color)
            notReceivedColor (Color)
            answerValue (int)
        */

        //Initialising the message box with a JPanel
        JPanel messageBox = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                //From StackOverflow : https://stackoverflow.com/questions/76214081/how-to-make-a-jpanel-with-round-corners-and-box-shadow-in-swing
                super.setBorder(new EmptyBorder(5, 5, 5, 10));
                super.paintComponent(g);
                Graphics2D graphics = (Graphics2D) g;
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setColor(backgroundColor);
                graphics.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
            }
        };
        messageBox.setLayout(new GridLayout(2, 1)); //Layout for the JLabel

        //Initialising the JLabel for the sender name
        JLabel senderName = new JLabel(name);
        senderName.setForeground(contactColor);
        senderName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        messageBox.add(senderName);

        //Initialising the JLabel for the message
        JLabel messageText = new JLabel(message);
        messageText.setForeground(textColor);
        messageText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        messageBox.add(messageText);

        if(isHyperlink)
        {
            messageText.setForeground(Color.BLUE);
            Font font = messageText.getFont();
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            messageText.setFont(font.deriveFont(attributes));
            messageText.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    super.mouseClicked(e);
                    try
                    {
                        Desktop desktop = java.awt.Desktop.getDesktop();
                        URI oURL = new URI(messageText.getText());
                        desktop.browse(oURL);
                    }
                    catch (URISyntaxException | IOException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }

        if(sendMessage)
        {
            if(!isHyperlink)
            {
                int answerValue = Sender.sendMessage(mCurrentContactIP.replace("/", ""), mPort, "message;".concat(messageEditor.getText()));
                if (answerValue == 2)
                {
                    messageBox.setLayout(new GridLayout(3, 1));
                    JLabel notReceivedLabel = new JLabel("(not received)");
                    notReceivedLabel.setForeground(notReceivedColor);
                    messageBox.add(notReceivedLabel);
                }
            }
            else
            {
                int answerValue = Sender.sendMessage(mCurrentContactIP.replace("/", ""), mPort, "message;".concat(messageEditor.getText()).concat(";LINK"));
                if (answerValue == 2)
                {
                    messageBox.setLayout(new GridLayout(3, 1));
                    JLabel notReceivedLabel = new JLabel("(not received)");
                    notReceivedLabel.setForeground(notReceivedColor);
                    messageBox.add(notReceivedLabel);
                }
            }
        }

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.NONE;

        if(typeOfMessage == 0)
        {
            c.anchor = GridBagConstraints.NORTHEAST;
            c.gridx = 2;
        }
        else if(typeOfMessage == 1)
        {
            c.anchor = GridBagConstraints.NORTHWEST;
            c.gridx = 1;
        }
        c.gridy = rowNumber;
        c.gridwidth = 1;

        messagePanel.add(messageBox, c);
        rowNumber++;
    }

    public void setMinimized()
    {
        frame.setState(Frame.ICONIFIED);
    }

    public void setNormal()
    {
        frame.setState(Frame.NORMAL);
    }

    public int getState()
    {
        return(frame.getState());
    }

    private boolean intToBoolean(int integer)
    {
        switch(integer)
        {
            case 1:
                return(true);

            default:
                return(false);
        }
    }

    private int booleanToInt(boolean bool)
    {
        if(bool)
        {
            return(1);
        }
        else
        {
            return(0);
        }
    }

    private String mCurrentContactName;
    private String mCurrentContactIP;
    private HashMap<String, String> mContactsList;
    private String mIconsPath = System.getProperty("user.dir").concat("\\Icons\\");
    private ConfigFileProcessorClass config;
    private int mPort;
}
