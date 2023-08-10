package utilPackage;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileProcessorClass
{
    /*
     * This class is used to zip and unzip folders.
     * The technical operation of encryption and decryption comes from Mykong : https://mkyong.com/java/how-to-compress-files-in-zip-format/
     */

    public static void zipFolder(Path source) throws IOException
    {
        // get folder name as zip file name
        String zipFileName = source.getFileName().toString() + ".zip";

        try (
                ZipOutputStream zos = new ZipOutputStream(
                        new FileOutputStream(zipFileName))
        )
        {

            Files.walkFileTree(source, new SimpleFileVisitor<>()
            {
                @Override
                public FileVisitResult visitFile(Path file,
                                                 BasicFileAttributes attributes)
                {

                    // only copy files, no symbolic links
                    if (attributes.isSymbolicLink())
                    {
                        return FileVisitResult.CONTINUE;
                    }

                    try (FileInputStream fis = new FileInputStream(file.toFile()))
                    {

                        Path targetFile = source.relativize(file);
                        zos.putNextEntry(new ZipEntry(targetFile.toString()));

                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = fis.read(buffer)) > 0)
                        {
                            zos.write(buffer, 0, len);
                        }

                        // if large file, throws out of memory
                        //byte[] bytes = Files.readAllBytes(file);
                        //zos.write(bytes, 0, bytes.length);

                        zos.closeEntry();

                    }
                    catch (IOException e)
                    {
                        System.err.println(e);
                        e.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    System.err.printf("Unable to zip : %s%n%s%n", file, exc);
                    return FileVisitResult.CONTINUE;
                }
            });

        }
        catch (IOException e)
        {
            System.err.println(e);
            e.printStackTrace();
        }

    }

    public static void unZip(Path zipFilePath, Path outputPath) throws ZipException
    {
        new ZipFile(zipFilePath.toFile()).extractAll(outputPath.toString());
    }
}
