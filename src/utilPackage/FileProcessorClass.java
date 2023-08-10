package utilPackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileProcessorClass
{
    public FileProcessorClass(String path)
    {
        filePath = Paths.get(path);
        filePathString = path;
    }

    public static String openFile(String path)
    {
        StringBuilder fileContent = new StringBuilder();
        String fileContentLine = "";
        boolean canReadLine = true;

        try
        {
            BufferedReader bfr = Files.newBufferedReader(Paths.get(path));

            while(canReadLine)
            {
                fileContentLine = bfr.readLine();

                if(fileContentLine != null && !fileContentLine.trim().equals(""))
                    fileContent.append(fileContentLine.concat("\n"));
                else
                    canReadLine = false;
            }
            bfr.close();
        }
        catch(IOException e)
        {
            System.err.println("IOException : "+e.getMessage());
            e.printStackTrace();
            System.err.println("Opening error with file : ".concat(path));
        }
        catch(Exception e)
        {
            System.err.println("Exception : "+e.getMessage());
            e.printStackTrace();
            System.err.println("Opening error with file : ".concat(path));
        }

        return fileContent.toString();
    }

    public String openFile()
    {
        return openFile(filePathString);
    }

    public static String getLine(String path)
    {
        String fileContentLine = "";
        boolean canReadLine = true;

        try
        {
            BufferedReader bfr = Files.newBufferedReader(Paths.get(path));

            while(canReadLine)
            {
                fileContentLine = bfr.readLine();

                if(fileContentLine != null && !fileContentLine.trim().equals(""))
                    canReadLine = false;
            }
            bfr.close();
        }
        catch(IOException e)
        {
            System.err.println("IOException : "+e.getMessage());
            e.printStackTrace();
            System.err.println("Getting line error with file : ".concat(path));
        }
        catch(Exception e)
        {
            System.out.println("Exception : "+e.getMessage());
            e.printStackTrace();
            System.err.println("Getting line error with file : ".concat(path));
        }

        return fileContentLine.concat("\n");
    }

    public static void writeFile(String path, String text, String mode)
    {
        StandardOpenOption option;

        if(mode.trim().equals("write"))
        {
            try
            {
                option = StandardOpenOption.WRITE; // mode
                Charset c = StandardCharsets.UTF_8; // encodage
                BufferedWriter bfw = Files.newBufferedWriter(Paths.get(path), c, option);

                bfw.write(text);

                bfw.close();
            }
            catch(IOException e)
            {
                System.out.println("IOException : "+e.getMessage());
                e.printStackTrace();
                System.err.println("Writing file (write) error with file : ".concat(path));
            }
        }
        else if(mode.trim().equals("append"))
        {
            try
            {
                option = StandardOpenOption.APPEND; // mode
                Charset c = StandardCharsets.UTF_8; // encodage
                BufferedWriter bfw = Files.newBufferedWriter(Paths.get(path), c, option);

                bfw.write(text);

                bfw.close();
            }
            catch(IOException e)
            {
                System.err.println("IOException : "+e.getMessage());
                e.printStackTrace();
                System.err.println("Writing file (append) error with file : ".concat(path));
            }
        }
    }

    public void writeFile(String text, String mode)
    {
        writeFile(filePathString, text, mode);
    }

    public static boolean createFile(String path)
    {
        try
        {
            Files.createFile(Paths.get(path));
            return true;
        }
        catch (IOException e)
        {
            System.err.println("FileProcessorClass Error on 'createFile' : IOException : "+e);
            e.printStackTrace();
            System.err.println("Creating file error with file : ".concat(path));
            return false;
        }
    }

    public boolean createFile()
    {
        return createFile(filePathString);
    }

    public static boolean deleteFile(String path)
    {
        try
        {
            Files.deleteIfExists(Paths.get(path));
            return true;
        }
        catch (IOException e)
        {
            System.out.println("FileProcessorClass Error on 'deleteFile' : IOException : "+e);
            e.printStackTrace();
            System.err.println("Removing file error with file : ".concat(path));
            return false;
        }
    }

    public boolean deleteFile()
    {
        return deleteFile(filePathString);
    }

    public static boolean isFileExist(String path)
    {
        return Files.exists(Paths.get(path));
    }

    public boolean isFileExist()
    {
        return Files.exists(filePath);
    }

    public static void copyFile(String targetPath, String destinationPath) throws Exception
    {
        Files.copy(Paths.get(targetPath), Paths.get(destinationPath), REPLACE_EXISTING);
    }

    public void copyFile(String destinationPath) throws Exception
    {
        Files.copy(filePath, Paths.get(destinationPath));
    }

    private static Path filePath;
    private static String filePathString;
}
