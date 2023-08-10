package utilPackage;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionProcessorClass
{
    /*
     * This class is used to encrypt and decrypt string.
     * The technical operation of encryption and decryption comes from Baeldung : https://www.baeldung.com/java-aes-encryption-decryption
     * It can return 1 type of value :
     *      - a string containing the result of the encryption or decryption
     */

    private static void setKey(final String myKey)
    {
        /*
         * This method is used to hash the string containing the key.
         */
        MessageDigest sha = null;
        try
        {
            mKey = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            mKey = sha.digest(mKey);
            mKey = Arrays.copyOf(mKey, 16);
            mSecretKey = new SecretKeySpec(mKey, "AES");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    public static String encrypt(final String strToEncrypt, final String secret)
    {
        /*
         * This method is used to encrypt the string 'strToEncrypt' with the key 'secret'.
         */
        try
        {
            setKey(secret); //Setting the key (hash the key)
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, mSecretKey);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        }
        catch (Exception e)
        {
            System.err.println("Error while encrypting: " + e.toString());
            e.printStackTrace();
        }
        return "null";
    }

    public static String decrypt(final String strToDecrypt, final String secret)
    {
        /*
         * This method is used to decrypt the string 'strToEncrypt' with the key 'secret'.
         */
        try
        {
            setKey(secret); //Setting the key (hash the key)
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, mSecretKey);
            return new String(cipher.doFinal(Base64.getDecoder()
                    .decode(strToDecrypt)));
        }
        catch (Exception e)
        {
            System.err.println("Error while decrypting: " + e.toString());
            e.printStackTrace();
        }
        return "null";
    }

    private static SecretKeySpec mSecretKey;
    private static byte[] mKey;
}
