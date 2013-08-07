

//~--- JDK imports ------------------------------------------------------------

import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class ClientsUI extends javax.swing.JFrame {

    private static final long        serialVersionUID = -1430979614533475831L;
    private javax.swing.JButton      disconnectButton;
    private javax.swing.JLabel       jLabel1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JScrollPane  jScrollPane1;
    private Socket                   server;
    private javax.swing.JButton      startButton;
    private javax.swing.JList        userList;


    public ClientsUI ( Socket server ) {

        this.server = server;
        initComponents ();
    }

    private void initComponents () {

        startButton      = new javax.swing.JButton ();
        jLayeredPane1    = new javax.swing.JLayeredPane ();
        jLabel1          = new javax.swing.JLabel ();
        jScrollPane1     = new javax.swing.JScrollPane ();
        userList         = new javax.swing.JList ();
        disconnectButton = new javax.swing.JButton ();
        startButton.setEnabled ( false );
        setDefaultCloseOperation ( javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
        setResizable ( false );
        startButton.setText ( "Start Match" );
        startButton.addActionListener ( new java.awt.event.ActionListener () {

            @Override
            public void actionPerformed ( java.awt.event.ActionEvent evt ) {
                startButtonActionPerformed ( evt );
            }

        } );
        disconnectButton.setText ( "Disconnect" );
        disconnectButton.addActionListener ( new java.awt.event.ActionListener () {

            @Override
            public void actionPerformed ( ActionEvent evt ) {
                disconnectButtonActionPerformed ( evt );
            }

        } );
        userList.addMouseListener ( new MouseAdapter () {

            @Override
            public void mouseClicked ( MouseEvent me ) {

                if ( !userList.isSelectionEmpty () ) {
                    startButton.setEnabled ( true );
                }
                else {
                    startButton.setEnabled ( false );
                }
            }

        } );
        this.addWindowListener ( new WindowAdapter () {

            @Override
            public void windowClosing ( WindowEvent we ) {

                try {
                    PrintWriter out = new PrintWriter ( server.getOutputStream (), true );

                    out.println ( "Close Connection" );
                    out.flush ();

                    for ( Socket s : client.clientsList ) {
                        PrintWriter sout = new PrintWriter ( s.getOutputStream (), true );

                        sout.println ( "Close Connection" );
                        s.close ();
                    }

                    server.close ();
                    System.exit ( 0 );
                }
                catch ( IOException ex ) {}
            }

        } );
        jLabel1.setText ( "Users" );
        jLabel1.setBounds ( 50, 10, 60, 21 );
        jLayeredPane1.add ( jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER );
        userList.setModel ( new javax.swing.AbstractListModel () {

            private static final long serialVersionUID = 6744404892218488492L;
            String[]                  strings          = {};
            @Override
            public int getSize () {
                return strings.length;
            }
            @Override
            public Object getElementAt ( int i ) {
                return strings[i];
            }

        } );
        jScrollPane1.setViewportView ( userList );
        jScrollPane1.setBounds ( 20, 40, 280, 350 );
        jLayeredPane1.add ( jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout ( getContentPane () );

        getContentPane ().setLayout ( layout );
        layout.setHorizontalGroup (
            layout.createParallelGroup ( javax.swing.GroupLayout.Alignment.LEADING ).addGroup (
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup ().addContainerGap ().addComponent (
                    jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 333,
                    javax.swing.GroupLayout.PREFERRED_SIZE ).addPreferredGap (
                        javax.swing.LayoutStyle.ComponentPlacement.RELATED ).addGroup (
                        layout.createParallelGroup ( javax.swing.GroupLayout.Alignment.LEADING ).addComponent (
                            startButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE ).addComponent (
                                disconnectButton, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) ).addContainerGap () ) );
        layout.setVerticalGroup (
            layout.createParallelGroup ( javax.swing.GroupLayout.Alignment.LEADING ).addGroup (
                layout.createSequentialGroup ().addContainerGap ().addComponent (
                    jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 416,
                    javax.swing.GroupLayout.PREFERRED_SIZE ).addContainerGap () ).addGroup (
                        layout.createSequentialGroup ().addGap ( 52, 52, 52 ).addComponent (
                            startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 46,
                            javax.swing.GroupLayout.PREFERRED_SIZE ).addPreferredGap (
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED ).addComponent (
                                disconnectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 46,
                                javax.swing.GroupLayout.PREFERRED_SIZE ).addPreferredGap (
                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ).addGap (
                                        18, 18, 18 ).addGap ( 26, 26, 26 ) ) );
        pack ();
    }

    private void startButtonActionPerformed ( java.awt.event.ActionEvent evt ) {

        String opponent = ( ( User ) userList.getSelectedValue () ).getName ();
        if ( !client.canMatch ( opponent ) ) {
            JOptionPane.showMessageDialog ( null, "You are already playing with " + opponent );

            return;
        }

        client.matchList.add ( opponent );

        MakeGameRequest make = new MakeGameRequest ( ( User ) userList.getSelectedValue () );

        make.start ();
    }

    private void disconnectButtonActionPerformed ( java.awt.event.ActionEvent evt ) {

        try {
            PrintWriter out = new PrintWriter ( server.getOutputStream (), true );

            out.println ( "Close Connection" );

            for ( Socket s : client.clientsList ) {
                PrintWriter sout = new PrintWriter ( s.getOutputStream (), true );

                sout.println ( "Close Connection" );
            }

            server.close ();
            System.exit ( 0 );
        }
        catch ( IOException ex ) {}
    }

    public void updateUserList ( String name, ArrayList<User> users ) {

        ArrayList<User> dummyUsers = new ArrayList<User> ();

        for ( User u : users ) {
            if ( !u.getName ().equals ( name ) ) {
                dummyUsers.add ( u );
            }
            else {
                client.me = u;
            }
        }

        if ( dummyUsers.isEmpty () ) {
            startButton.setEnabled ( false );
        }

        userList.setListData ( dummyUsers.toArray () );
        userList.repaint();
        repaint();
    }

}
