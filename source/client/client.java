

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class client {

    public static ArrayList<String> matchList   = new ArrayList<String> ();
    public static ArrayList<Socket> clientsList = new ArrayList<Socket> ();
    public static User              me;
    public static ServerSocket      selfServer;
    public static Socket            server;

    public static boolean canMatch ( String name ) {

        for ( String u : matchList )
            if ( u.equals ( name ) )
                return false;
        return true;
    }

    public static void main ( String args[] ) throws IOException {

        server     = new Socket ( args[0], Integer.parseInt ( args[1] ) );
        selfServer = new ServerSocket ( 0 );

        ClientsUI clients = new ClientsUI ( server );

        clients.setTitle ( args[2] );
        clients.setVisible ( true );

        ArrayList<User> users  = new ArrayList<User> ();
        ListenServer    listen = new ListenServer ( users, args[2], clients );

        listen.start ();

        WaitGameRequest request = new WaitGameRequest ();

        request.start ();
    }
}
