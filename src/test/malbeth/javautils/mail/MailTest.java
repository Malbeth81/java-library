package malbeth.javautils.mail;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class MailTest
{

    @Test
    public void testVoid()
    {

    }

    public void testSend()
    {
    /* Required configuration values:
     * mail.host=
     *
     * Other possible items include: 
     * mail.debug=
     * mail.from=
     * mail.store.protocol= 
     * mail.transport.protocol= 
     * mail.user= 
     */
        Properties mailConfig = new Properties();
        mailConfig.setProperty("mail.host", "flamenco");

        assertTrue(Mail.send(mailConfig, "marc-andre.l@vortexsolution.com", "Test", "Ceci est un test!"));
    }

}
