package utilPackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Objects;
import java.util.StringTokenizer;

public class ConfigFileProcessorClass
{
    /*
     * This class is used to add and retrieve data from configuration files (.ini, ...).
     * It takes the path to the config file as a parameter.
     * It can return 1 value :
     *    - the whole content of the config file
     */
    public ConfigFileProcessorClass(String path)
    {
        mConfigFilePath = Paths.get(path);
        mParsedFile = new HashMap<String, String>();
        mConfigFileContent = openAndParseConfigFile(true);
    }

    private void openAndParseConfigFile()
    {
        openAndParseConfigFile(false);
    }

    private String openAndParseConfigFile(boolean returnFileContent)
    {
        /*
         * This method is used to parse the configuration file to facilitate data access.
         */
        StringBuilder configFileContent = new StringBuilder("## Config file by ConfigFileProcessorClass\n");
        String configFileLine = "";
        boolean canReadLine = true;

        try
        {
            BufferedReader bfr = Files.newBufferedReader(mConfigFilePath);

            //Line processing
            while(canReadLine)
            {
                configFileLine = bfr.readLine(); //Get the line content
                //Check if the content of line is correct
                if(configFileLine != null && !configFileLine.trim().equals(""))
                {
                    if(configFileLine.contains("="))
                    {
                        //Parsing line content
                        StringTokenizer configFileLineTokenizer = new StringTokenizer(configFileLine.trim(), "=");
                        String tempKey = configFileLineTokenizer.nextToken().trim();
                        String tempValue = configFileLineTokenizer.nextToken().trim();

                        if(!(tempKey.equals("")) && !(tempValue.equals("")))
                        {
                            mParsedFile.put(tempKey.replace("[", "").replace("]", ""), tempValue); //Adding the line content into the HashMap
                            configFileContent.append(configFileLine.concat("\n"));
                        }
                    }
                }
                else
                    canReadLine = false;
            }
            bfr.close();

            if(returnFileContent)
                return(configFileContent.toString());
            else
                return(null);
        }
        catch(IOException e)
        {
            System.err.println("IOException : "+e.getMessage());
            e.printStackTrace();
            System.err.println("Parsing error with file : ".concat(mConfigFilePath.toString()));
            return(null);
        }
        catch(Exception e)
        {
            System.err.println("Exception : "+e.getMessage());
            e.printStackTrace();
            System.err.println("Parsing error with file : ".concat(mConfigFilePath.toString()));
            return(null);
        }
    }

    public void addData(String key, String value)
    {
        /*
         * This method is used to add data to the configuration file and the HashMap containing the parsed configuration file.
         */
        //Check that the key isn't already in the configuration file
        if(!mParsedFile.containsKey(key))
        {
            try
            {
                //Set up the BufferedWriter
                StandardOpenOption option = StandardOpenOption.APPEND; // mode
                Charset charset = StandardCharsets.UTF_8; // encoding
                BufferedWriter bfw = Files.newBufferedWriter(mConfigFilePath, charset, option);

                //Saving data in the configuration file with the BufferedWriter
                bfw.write("[".concat(key).concat("]").concat("=").concat(value).concat("\n"));
                bfw.close();

                //Adding data in the HashMap
                mParsedFile.put(key, value);
            }
            catch (IOException e)
            {
                System.err.println("IOException : " + e.getMessage());
                System.err.println("Adding data error with file : ".concat(mConfigFilePath.toString()));
            }
        }
    }

    public void removeData(String key)
    {
        /*
         * This method is used to remove data from the configuration file and the HashMap containing the parsed configuration file.
         */
        //Check that the key is already in the configuration file
        if(mParsedFile.containsKey(key))
        {
            //Delete and create a new configuration file to have an empty file
            FileProcessorClass.deleteFile(mConfigFilePath.toString());
            FileProcessorClass.createFile(mConfigFilePath.toString());

            //Parsing the configuration file content
            StringTokenizer configFileContentParts = new StringTokenizer(mConfigFileContent, "\n");

            //Filling the new file
            while(configFileContentParts.hasMoreTokens())
            {
                String keyValue = configFileContentParts.nextToken().trim(); //Getting the key for every line (= key/value combination) of configuration file

                if(!keyValue.equals("") && keyValue != null && !keyValue.equals("## Config file by ConfigFileProcessorClass"))
                {
                    StringTokenizer keyValuePart = new StringTokenizer(keyValue, "=");
                    String tempKey = keyValuePart.nextToken().replace("[", "").replace("]", "").trim();

                    //Check that the key to remove isn't int the current line of the configuration file content to avoid it.
                    if(!tempKey.equals(key))
                    {
                        FileProcessorClass.writeFile(mConfigFilePath.toString(), keyValue.concat("\n"), "append");
                    }
                }
                else if (keyValue.equals("## Config file by ConfigFileProcessorClass"))
                {
                    FileProcessorClass.writeFile(mConfigFilePath.toString(), keyValue.concat("\n"), "append");
                }
            }
            mParsedFile.remove(key); //Remove the key/value combination from the HashMap
            mConfigFileContent = FileProcessorClass.openFile(mConfigFilePath.toString()); //Update the configuration file content copy in 'mConfigFileContent'
        }
    }

    public void setData(String key, String value)
    {
        /*
         * This method is used to modify the value of a key/value combination in the configuration file
         */
        //Check that the key is already in the configuration file
        if(mParsedFile.containsKey(key))
        {
            mConfigFileContent = FileProcessorClass.openFile(mConfigFilePath.toString()); //Update the configuration file content copy in 'mConfigFileContent'
            //Replace the value in the configuration file
            FileProcessorClass.writeFile(mConfigFilePath.toString(),
                    mConfigFileContent.replace("[".concat(key).concat("]=").concat(mParsedFile.get(key)), "[".concat(key).concat("]=").concat(value)), "write");
            //Replace the value in the HashMap
            mParsedFile.put(key, value);
        }
    }

    public String getValueOf(String key)
    {
        /*
         * This method is used to get the value of a key/value combination with a key
         */
        return(mParsedFile.get(key));
    }

    public boolean containKey(String key)
    {
        /*
         * This method is used to return if the key already exist in the configuration file
         */
        return(mParsedFile.containsKey(key));
    }
    public boolean containValue(String value)
    {
        /*
         * This method is used to return if the value already exist in the configuration file
         */
        return(mParsedFile.containsValue(value));
    }

    private String mConfigFileContent;
    private final Path mConfigFilePath;
    private final HashMap<String, String> mParsedFile;
}
