

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JOptionPane;

public class ReadMovements extends Thread {

    private String oppoName;
    private Socket opposite;

    public ReadMovements ( String oppoName, Socket opposite ) {

        this.oppoName = oppoName;
        this.opposite = opposite;
    }

    @Override
    public void run () {

        BufferedReader in = null;

        try {
            CheckersUI board = new CheckersUI ( client.me.getName () + " vs " + oppoName, oppoName );

            board.setPlayerColor ( "red" );
            board.setVisible ( true );
            board.setOpponent ( opposite );
            board.setOppoName ( oppoName );

            String movement;

            in = new BufferedReader ( new InputStreamReader ( opposite.getInputStream () ) );

            PrintWriter out = new PrintWriter ( opposite.getOutputStream (), true );
            String      input;

            while ( true ) {
                while ( board.getTurn ().equals ( "black" ) ) {
                    input = in.readLine ();

                    if ( input.equals ( "Close Connection" ) ) {
                        client.matchList.remove ( oppoName );
                        client.clientsList.remove ( opposite );
                        board.dispose ();
                        JOptionPane.showMessageDialog ( null, "Game closed by " + oppoName );
                        out.close ();
                        opposite.close ();

                        return;
                    }
                    else if ( input.equals ( "Game Finished" ) ) {
                        client.matchList.remove ( oppoName );
                        client.clientsList.remove ( opposite );
                        board.dispose ();
                        out.close ();
                        opposite.close ();

                        return;
                    }

                    board.makeMovement ( input );
                }

                while ( board.getTurn ().equals ( "red" ) ) {
                    if ( opposite.getInputStream ().available () > 0 ) {
                        String str = in.readLine ();

                        if ( str.equals ( "Close Connection" ) ) {
                            client.matchList.remove ( oppoName );
                            client.clientsList.remove ( opposite );
                            board.dispose ();
                            JOptionPane.showMessageDialog ( null, "Game closed by " + oppoName );
                            out.close ();
                            opposite.close ();

                            return;
                        }
                        else if ( str.equals ( "Game Finished" ) ) {
                            client.matchList.remove ( oppoName );
                            client.clientsList.remove ( opposite );
                            board.dispose ();
                            out.close ();
                            opposite.close ();

                            return;
                        }
                    }
                }

                movement = Integer.toString ( board.getAdjustX () + board.getMouseX () ) + ","
                           + Integer.toString ( board.getAdjustY () + board.getMouseY () ) + "#"
                           + Integer.toString ( board.getCurrent ().getParent ().getLocation ().x ) + ","
                           + Integer.toString ( board.getCurrent ().getParent ().getLocation ().y );
                out.println ( movement );
            }
        }
        catch ( Exception ex ) {}
        finally {
            try {
                in.close ();
            }
            catch ( IOException ex ) {}
        }
    }
}
