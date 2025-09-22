package ap;

import static org.testng.Assert.*;
import org.testng.annotations.*;
import java.util.ArrayList;
import java.util.List;

@Test
public class TestTerminalNetwork {


    /* Test case 1:
        #clients = 50000
        t.name.size = 6 ("abcdef")
        c.name = unique
        t.client = valid

        result: pass
    * */
    public void testValidNumberOfClients(){
        TerminalNetwork tn = new TerminalNetwork("abcdef",50000);

        assertEquals(tn.getName(),"abcdef");
        assertEquals(tn.getMaxClients(),50000);
        assertNull(tn.getClients());
        assertNull(tn.getTerminal());

    }

    /* Test case 2:
        #clients = 50001
        t.name.size = 7 ("abcdefg")
        c.name = unique
        t.client = valid

        result: fail
    * */
    public void testInvalidNumberOfClients(){
        TerminalNetwork tn = new TerminalNetwork("abcdefg",10);

        try{
            tn.setMaxClients(50001);
            fail("Test should've failed.");
        } catch (InvalidInvocationException e){

            assertEquals(tn.getName(),"abcdefg");
            assertEquals(tn.getMaxClients(),10);
            assertNull(tn.getClients());
            assertNull(tn.getTerminal());
        }

    }

    /* Test case 3:
        #clients = 1
        t.name.size = 3 ("abc")
        c.name = unique
        t.client = valid

        result: pass
    * */
    public void testValidSizeNameOfTerminalNetwork(){
        TerminalNetwork tn = new TerminalNetwork("abc",1);

        assertEquals(tn.getName(),"abc");
        assertEquals(tn.getMaxClients(),1);
        assertNull(tn.getClients());
        assertNull(tn.getTerminal());

    }

    /* Test case 4:
        #clients = 10000
        t.name.size = 10 ("abcdefghij")
        c.name = unique
        t.client = valid

        result: fail
    * */
    public void testInvalidSizeNameOfTerminalNetwork(){
        TerminalNetwork tn = new TerminalNetwork("abc",10000);

        try {
            tn.setName("abcdefghij");
            fail("Test should've failed.");
        } catch(InvalidInvocationException e) {

            assertEquals(tn.getName(), "abc");
            assertEquals(tn.getMaxClients(), 10000);
            assertNull(tn.getClients());
            assertNull(tn.getTerminal());
        }
    }

    /* Test case 5:
        #clients = 3000
        t.name.size = 8 ("abcdefgh")
        c.name = unique ("abc") and ("abcd")
        t.client = valid

        result: pass
    * */
    public void testUniqueClientsNames(){
        TerminalNetwork tn = new TerminalNetwork("abcdefgh",3000);

        Client cl1 = new Client("abc",ClientLevel.NORMAL);

        tn.addClient(cl1);

        Client cl2 = new Client("abcd",ClientLevel.NORMAL);

        tn.addClient(cl2);

        assertEquals(tn.getName(), "abcdefgh");
        assertEquals(tn.getMaxClients(), 3000);

        int nClients = tn.getClients().size();

        assertEquals(nClients, 2);
        assertTrue(tn.getClients().contains(cl1));
        assertTrue(tn.getClients().contains(cl2));

        assertNull(tn.getTerminal());

    }

    /* Test case 6:
        #clients = 4000
        t.name.size = 4 ("abcd")
        c.name = duplicate ("abc") and ("abc")
        t.client = valid

        result: fail
    * */
    public void testDuplicateClientsNames(){
        TerminalNetwork tn = new TerminalNetwork("abcd",4000);

        Client cl1 = new Client("abc",ClientLevel.NORMAL);

        tn.addClient(cl1);

        try {
            Client cl2 = new Client("abc",ClientLevel.NORMAL);

            tn.addClient(cl2);
            fail("Test should've failed.");

        } catch (InvalidInvocationException e) {
            assertEquals(tn.getName(), "abcd");
            assertEquals(tn.getMaxClients(), 4000);

            int nClients = tn.getClients().size();

            assertEquals(nClients, 1);
            assertTrue(tn.getClients().contains(cl1));
            assertFalse(tn.getClients().contains(cl2));

            assertNull(tn.getTerminal());
        }

    }

    /* Test case 7:
        #clients = 5000
        t.name.size = 5 ("abcde")
        c.name = unique ("abc")
        t.client = valid ("t1") ("abc")

        result: pass
    * */
    public void testClientAssociatedWithTerminalInClients(){
        TerminalNetwork tn = new TerminalNetwork("abcde",5000);

        Client cl1 = new Client("abc",ClientLevel.NORMAL);

        Terminal t1 = new Terminal("t1");

        tn.addClient(cl1);

        tn.addTerminal(t1,"abc");


        assertEquals(tn.getName(), "abcde");
        assertEquals(tn.getMaxClients(), 5000);

        int nClients = tn.getClients().size();
        int nTerminals = tn.getTerminal().size();

        assertEquals(nClients, 1);
        assertEquals(nTerminals, 1);
        assertTrue(tn.getClients().contains(cl1));
        assertTrue(tn.getTerminal().contains(t1));

    }

    /* Test case 8:
        #clients = 6000
        t.name.size = 6 ("abcdef")
        c.name = unique ("abc")
        t.client = invalid ("t1") ("abcd")

        result: fail
    * */
    public void testClientAssociatedWithTerminalNotInClients(){
        TerminalNetwork tn = new TerminalNetwork("abcdef",6000);

        Client cl1 = new Client("abc",ClientLevel.NORMAL);

        tn.addClient(cl1);

        try {
            Terminal t1 = new Terminal("t1");

            tn.addTerminal(t1,"abcd");
        } catch (InvalidInvocationException e) {

            assertEquals(tn.getName(), "abcdef");
            assertEquals(tn.getMaxClients(), 6000);

            int nClients = tn.getClients().size();

            assertEquals(nClients, 1);
            assertTrue(tn.getClients().contains(cl1));

            assertNull(tn.getTerminal());
        }

    }

}
