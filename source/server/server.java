/*
 * @(#)server.java   12/03/27
 * 
 * Copyright (c) 2005 your company name
 *
 * License agreement text here ...
 *
 *
 *
 */





//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Class description
 *
 *
 * @version        Enter version here..., 12/03/26
 * @author         Osman Alperen Elhan
 */
public class server {

    public static LogThemAll logger;

    /**
     * Method description
     *
     *
     * @param args
     *
     * @throws IOException
     */
    public static void main ( String[] args ) throws IOException {

        ServerSocket      serverSocket = new ServerSocket ( Integer.parseInt ( args[0] ) );
        ArrayList<User>   users        = new ArrayList<User> ();
        ArrayList<Socket> sockets      = new ArrayList<Socket> ();

        logger = new LogThemAll ();

        while ( true ) {
            try {
                Socket clientSocket = serverSocket.accept ();

                logger.logit ( clientSocket.getInetAddress ().toString ().split ( "/" )[1] + " "
                               + clientSocket.getPort () + " connected\n" );
                sockets.add ( clientSocket );

                UpdateUsers cliThread = new UpdateUsers ( clientSocket, users, sockets );

                cliThread.start ();
            }
            catch ( Exception e ) {
                continue;
            }
        }
    }
}
