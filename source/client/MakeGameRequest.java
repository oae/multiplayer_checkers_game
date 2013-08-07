

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JOptionPane;

public class MakeGameRequest extends Thread {

    private User opponent;

    public MakeGameRequest ( User opponent ) {
        this.opponent = opponent;
    }

    @Override
    public void run () {

        Socket      oppoSocket = null;
        PrintWriter out;

        try {
            oppoSocket = new Socket ( opponent.getIp (), opponent.getSelfServerPort () );
            out        = new PrintWriter ( oppoSocket.getOutputStream (), true );
            out.println ( "I want to play a game#" + client.me.getName () );
            BufferedReader in    = new BufferedReader ( new InputStreamReader ( oppoSocket.getInputStream () ) );
            String         input = in.readLine ();

            if ( input.equals ( "Yes" ) ) {
                client.clientsList.add ( oppoSocket );

                String     movement;
                CheckersUI board = new CheckersUI ( client.me.getName () + " vs " + opponent.getName (),
                                       opponent.getName () );

                board.setOpponent ( oppoSocket );
                board.setOppoName ( opponent.getName () );
                board.setVisible ( true );

                while ( true ) {
                    while ( board.getTurn ().equals ( "black" ) ) {
                        if ( oppoSocket.getInputStream ().available () > 0 ) {
                            String str = in.readLine ();

                            if ( str.equals ( "Close Connection" ) ) {
                                client.matchList.remove ( opponent.getName () );
                                client.clientsList.remove ( oppoSocket );
                                board.dispose ();
                                JOptionPane.showMessageDialog ( null, "Game closed by " + opponent.getName () );
                                out.close ();
                                oppoSocket.close ();

                                return;
                            }
                            else if ( str.equals ( "Game Finished" ) ) {
                                client.matchList.remove ( opponent.getName () );
                                client.clientsList.remove ( oppoSocket );
                                board.dispose ();
                                out.close ();
                                oppoSocket.close ();

                                return;
                            }
                        }
                    }

                    movement = Integer.toString ( board.getAdjustX () + board.getMouseX () ) + ","
                               + Integer.toString ( board.getAdjustY () + board.getMouseY () ) + "#"
                               + Integer.toString ( board.getCurrent ().getParent ().getLocation ().x ) + ","
                               + Integer.toString ( board.getCurrent ().getParent ().getLocation ().y );
                    out.println ( movement );

                    while ( board.getTurn ().equals ( "red" ) ) {
                        input = in.readLine ();

                        if ( input.equals ( "Close Connection" ) ) {
                            client.matchList.remove ( opponent.getName () );
                            client.clientsList.remove ( oppoSocket );
                            board.dispose ();
                            JOptionPane.showMessageDialog ( null, "Game closed by " + opponent.getName () );
                            out.close ();
                            oppoSocket.close ();

                            return;
                        }
                        else if ( input.equals ( "Game Finished" ) ) {
                            client.matchList.remove ( opponent.getName () );
                            client.clientsList.remove ( oppoSocket );
                            board.dispose ();
                            out.close ();
                            oppoSocket.close ();

                            return;
                        }

                        board.makeMovement ( input );
                    }
                }
            }
            else {
                client.matchList.remove ( opponent.getName () );
            }
        }
        catch ( Exception e ) {
            try {
                oppoSocket.close ();
            }
            catch ( IOException ex ) {}
        }
    }
}
