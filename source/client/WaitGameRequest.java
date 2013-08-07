

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class WaitGameRequest extends Thread {

    @Override
    public void run () {

        Socket      opposite = null;
        PrintWriter out      = null;

        try {
            while ( true ) {
                opposite = client.selfServer.accept ();

                BufferedReader in = new BufferedReader ( new InputStreamReader ( opposite.getInputStream () ) );

                out = new PrintWriter ( opposite.getOutputStream (), true );

                String   input = in.readLine ();
                String[] str   = input.split ( "#" );

                if ( str[0].equals ( "I want to play a game" ) ) {
                    int n = JOptionPane.showConfirmDialog ( new JFrame (), str[1] + " wants a match", "Match Request",
                                JOptionPane.YES_NO_OPTION );

                    if ( n == 0 ) {
                        out.println ( "Yes" );

                        if ( !client.canMatch ( str[1] ) ) {
                            JOptionPane.showMessageDialog ( null, "You are already playing with " + str[1] );

                            continue;
                        }

                        client.clientsList.add ( opposite );
                        client.matchList.add ( str[1] );

                        ReadMovements read = new ReadMovements ( str[1], opposite );

                        read.start ();
                    }
                    else {
                        out.println ( "No" );
                        out.close ();
                        opposite.close ();

                        continue;
                    }
                }
            }
        }
        catch ( Exception e ) {
            try {
                out.close ();
                opposite.close ();
            }
            catch ( IOException ex ) {
                Logger.getLogger ( WaitGameRequest.class.getName () ).log ( Level.SEVERE, null, ex );
            }
        }
    }
}
