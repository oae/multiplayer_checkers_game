

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ListenServer extends Thread {

    private ClientsUI       clients;
    private String          name;
    private ArrayList<User> users;

    public ListenServer ( ArrayList<User> users, String name, ClientsUI clients ) {

        this.users   = users;
        this.name    = name;
        this.clients = clients;
    }

    @Override
    public void run () {

        try {
            InputStream    in    = client.server.getInputStream ();
            BufferedReader input = new BufferedReader ( new InputStreamReader ( client.server.getInputStream () ) );
            PrintWriter    out   = new PrintWriter ( client.server.getOutputStream (), true );

            out.println ( "User#" + name + "#" + Integer.toString ( client.selfServer.getLocalPort () ) );

            while ( true ) {
                try {
                    if ( out.checkError () ) {
                        break;
                    }

                    ArrayList<User> tmpUsers = new ArrayList<User> ();
                    String[]        userList = input.readLine ().split ( " " );

                    for ( String u : userList ) {
                        String[] us = u.split ( "#" );

                        tmpUsers.add ( new User ( us[3], us[0], Integer.parseInt ( us[1] ),
                                                  Integer.parseInt ( us[2] ) ) );
                    }

                    users = tmpUsers;
                    clients.updateUserList ( name, users );
                }
                catch ( Exception e ) {
                    continue;
                }
            }
        }
        catch ( Exception e ) {}
    }
}
