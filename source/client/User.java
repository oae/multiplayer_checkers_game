

//~--- JDK imports ------------------------------------------------------------

import java.net.Socket;

public class User {

    private String ip;
    private String name;
    private int    port;
    private int    selfServerPort;

    public User ( String name, Socket client ) {

        this.name = name;

        String[] str = client.getInetAddress ().toString ().split ( "/" );

        this.ip   = str[1];
        this.port = client.getPort ();
    }

    public User ( String name, String ip, int port, int selfServerPort ) {

        this.name           = name.trim ();
        this.ip             = ip;
        this.port           = port;
        this.selfServerPort = selfServerPort;
    }

    /******************* Getters and Setters *****************/ 
    public int getSelfServerPort () { return selfServerPort; }
    public void setSelfServerPort ( int selfServerPort ) { this.selfServerPort = selfServerPort; }
    public String getIp () { return ip; }
    public String getName () { return name; }
    /*********************************************************/

    @Override
    public String toString () {
        return name;
    }
}
