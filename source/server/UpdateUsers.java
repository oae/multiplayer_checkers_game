/*
 * @(#)UpdateUsers.java   12/03/27
 * 
 * Copyright (c) 2005 your company name
 *
 * License agreement text here ...
 *
 *
 *
 */





//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;

import java.util.ArrayList;

/**
 * Class description
 *
 *
 * @version        Enter version here..., 12/03/26
 * @author         Osman Alperen Elhan
 */
public class UpdateUsers extends Thread {

    private Socket            client;
    private ArrayList<Socket> sockets;
    private String            userName;
    private ArrayList<User>   users;

    /**
     * Constructs ...
     *
     *
     * @param client
     * @param users
     * @param sockets
     */
    public UpdateUsers ( Socket client, ArrayList<User> users, ArrayList<Socket> sockets ) {

        this.client  = client;
        this.users   = users;
        this.sockets = sockets;
    }

    /**
     * Method description
     *
     *
     * @param client
     */
    public void setClient ( Socket client ) {
        this.client = client;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public Socket getClient () {
        return client;
    }

    /**
     * Method description
     *
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void letUsersKnow () throws IOException, InterruptedException {

        String userList = "";

        for ( User u : users ) {
            userList = userList.concat ( u.getIp () + "#" + Integer.toString ( u.getPort () ) + "#"
                                         + Integer.toString ( u.getSelfServerPort () ) + "#" + u.getName () + " " );
        }

        for ( Socket s : sockets ) {
            PrintWriter out = new PrintWriter ( s.getOutputStream (), true );

            out.println ( userList );
            out.flush ();
        }
    }

    /**
     * Method description
     *
     *
     * @param name
     *
     * @throws IOException
     */
    private void removeFromUsers ( String name ) throws IOException {

        for ( User u : users ) {
            if ( u.getName ().equals ( name ) ) {
                users.remove ( u );

                break;
            }
        }
    }

    /**
     * Method description
     *
     */
    @Override
    public void run () {

        String ip   = null;
        int    port = 0;

        try {
            ip   = client.getInetAddress ().toString ().split ( "/" )[1];
            port = client.getPort ();

            BufferedReader in = new BufferedReader ( new InputStreamReader ( client.getInputStream () ) );
            String         inputLine;

            while ( !( inputLine = in.readLine () ).equals ( "Close Connection" ) ) {
                String[] line = inputLine.split ( "#" );

                if ( line[0].equals ( "User" ) ) {
                    users.add ( new User ( line[1], client, Integer.parseInt ( line[2] ) ) );
                    userName = line[1];
                    letUsersKnow ();
                }
            }

            removeFromUsers ( userName );
            sockets.remove ( client );
            client.close ();
            letUsersKnow ();
            server.logger.logit ( ip + " " + port + " disconnected\n" );
        }
        catch ( Exception e ) {
            try {
                server.logger.logit ( ip + " " + port + " disconnected\n" );
            }
            catch ( IOException ex ) {
                System.err.println ( "Couldn't write to log file" );
            }
        }
    }
}
