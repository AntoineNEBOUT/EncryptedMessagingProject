package EncryptedMessagingPackage;

import utilPackage.EncryptionProcessorClass;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class Sender
{
    /*
       * This class is used to send message to a server.
       * It takes the recipient's IP address ('hostname') and connection port ('message') as parameters.
       * Sending messages is encrypted using the 'EncryptionProcessorClass'. The encryption key can be modified by changing the variable 'mKey'
       * It can return 3 different values :
       *    - 0 : the message has been received
       *    - 1 : the server is connected
       *    - 2 : no response or response unknown
     */
    public Sender()
    {
    }

    public static int sendMessage(String hostname, int port, String message)
    {
        try
        {
            // Initializing the socket
            Socket socket = new Socket(hostname, port);
            socket.setSoTimeout(3000);

            // Initializing the output stream
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            // Sending the message
            writer.println(EncryptionProcessorClass.encrypt(message.concat(";").concat(String.valueOf(socket.getInetAddress())), mKey));

            // Input stream initialization
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            // Message reception
            String line = EncryptionProcessorClass.decrypt(reader.readLine(), mKey); // Message decrypting
            StringTokenizer st = new StringTokenizer(line, ";"); // Message splitting
            if(st.hasMoreTokens())
            {
                String temp = st.nextToken();
                if(temp.equals("messageReceived"))
                {
                    socket.close();
                    return(0);
                }
                else if(temp.equals("serverIsOnline"))
                {
                    socket.close();
                    return(1);
                }
                else
                {
                    socket.close();
                    return(2);
                }
            }
            else
            {
                socket.close();
                return(2);
            }
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Server not found: " + ex.getMessage());
            return(2);
        }
        catch (IOException ex)
        {
            System.out.println("I/O error: " + ex.getMessage());
            return(2);
        }
    }

    static String mKey = "1234"; // Encryption key
}
