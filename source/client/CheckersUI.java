

//~--- JDK imports ------------------------------------------------------------

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;

public class CheckersUI extends JFrame implements MouseListener, MouseMotionListener {

    private boolean      anyEat            = false;
    private int          black_piece_count = 12;
    private BoardPieces  current           = null;
    private int          red_piece_count   = 12;
    private int          adjustX;
    private int          adjustY;
    private LogThemAll   logger;
    private int          mouseX;
    private int          mouseY;
    private String       oppoName;
    private Socket       opponent;
    private JLayeredPane pane;
    private JPanel       panel;
    private String       playerColor;
    private String       turn;

    public CheckersUI ( String title, String opponent ) throws IOException {

        super ( title );
        this.oppoName = opponent;
        turn          = "black";
        playerColor   = "black";
        logger        = new LogThemAll ( client.me.getName () + "-" + oppoName + ".log",
                                  client.me.getName () + " - " + oppoName + " start match\n" );
        initComponents ();
    }

    /****************** Getters and Setters ****************************/
    public String getOppoName () { return oppoName; }
    public void setOppoName ( String oppName ) { this.oppoName = oppName; }
    public Socket getOpponent () { return opponent; }
    public void setOpponent ( Socket opponent ) { this.opponent = opponent; }
    public String getPlayerColor () { return playerColor; }
    public void setPlayerColor ( String playerColor ) { this.playerColor = playerColor; }
    public int getAdjustX () { return adjustX; }
    public int getAdjustY () { return adjustY; }
    public BoardPieces getCurrent () { return current; }
    public int getMouseX () { return mouseX; }
    public int getMouseY () { return mouseY; }
    public String getTurn () { return turn; }
    public void setTurn ( String turn ) { this.turn = turn; }
    /********************************************************************/
    

    private void initComponents () {

        this.addWindowListener ( new WindowAdapter () {

            @Override
            public void windowClosing ( WindowEvent we ) {
                try {
                    client.matchList.remove ( oppoName );
                    client.clientsList.remove ( opponent );
                    PrintWriter out = new PrintWriter ( opponent.getOutputStream (), true );
                    out.println ( "Close Connection" );
                } catch (IOException ex) {}
            }

        } );
        
        setSize ( 600, 600 );
        setDefaultCloseOperation ( JFrame.DISPOSE_ON_CLOSE );
        pane = new JLayeredPane ();
        getContentPane ().add ( pane );
        pane.setPreferredSize ( getSize () );
        pane.addMouseListener ( this );
        pane.addMouseMotionListener ( this );
        panel = new JPanel ();
        pane.add ( panel, JLayeredPane.DEFAULT_LAYER );
        panel.setLayout ( new GridLayout ( 8, 8 ) );
        panel.setBounds ( 0, 0, getSize ().width, getSize ().height );
        setResizable ( false );
        fillPieces ();
        pack ();
    }

    private void fillPieces () {

        for ( int i = 1; i <= 64; i++ ) {

            BoardPieces piece;
            int         altr;
            altr = ( i - 1 ) % 8;
            altr += ( i - 1 ) / 8;
            JPanel panel2 = new JPanel ();
            if ( altr % 2 == 0 ) {
                panel2.setBackground ( Color.white );
                if ( i < 24 ) {
                    piece = new BoardPieces ( new ImageIcon ( "piece2.gif" ), "red" );
                    panel2.add ( piece );
                }
                if ( i > 40 ) {
                    piece = new BoardPieces ( new ImageIcon ( "piece1.gif" ), "black" );
                    panel2.add ( piece );
                }
            }
            else panel2.setBackground ( Color.black );
            panel.add ( panel2 );
        }
    }

    public void makeMovement ( String fromTo ) throws Exception {
        if (fromTo == null) {
            return;
        }
        String[] str = fromTo.split("#");
        int fromX = Integer.parseInt(str[0].split(",")[0]);
        int fromY = Integer.parseInt(str[0].split(",")[1]);
        int toX = Integer.parseInt(str[1].split(",")[0]);
        int toY = Integer.parseInt(str[1].split(",")[1]);
        current = null;
        Component cmp = panel.findComponentAt(fromX, fromY);
        if (cmp instanceof JPanel) {
            Component[] cmps = ((Container) cmp).getComponents();
            for (Component c : cmps) {
                if (c instanceof BoardPieces) {
                    cmp = c;
                    break;
                }
            }
        }
        if (cmp instanceof JPanel) {
            return;
        }
        initCurrent(cmp, fromX, fromY);
        if (current != null) {
            Component cmp2 = panel.findComponentAt(toX, toY);
            finalizeCurrent(cmp2, toX, toY);
        }
        repaint();
    }

    private void isGameFinished () {

        try {
            if ( red_piece_count == 0 || black_piece_count == 0) {
                PrintWriter out = new PrintWriter ( opponent.getOutputStream (), true );
                out.println ( "Game Finished" );
                dispose ();
                if ( playerColor.equals ( current.side ) ){
                    client.matchList.remove(oppoName);
                    client.clientsList.remove ( opponent );
                    logger.logit(client.me.getName() + "- wins the match\n");
                    JOptionPane.showMessageDialog ( null, "You Win" );
                }
                else {
                    client.matchList.remove(oppoName);
                    client.clientsList.remove ( opponent );
                    logger.logit(oppoName + "- wins the match\n");
                    JOptionPane.showMessageDialog ( null, "You lost" );
                }
            }
        }
        catch ( IOException ex ) {}
    }

    private boolean isLegalMove ( int Xpos, int Ypos ) throws IOException {

        Component cmp = panel.findComponentAt ( Xpos, Ypos );
        if ( cmp == null ) return false;
        Container parent = ( Container ) cmp;
        if ( ( Math.abs ( ( adjustX + mouseX ) / 75 - parent.getX () / 75 ) == 2 )
                && ( Math.abs ( ( adjustY + mouseY ) / 75 - parent.getY () / 75 ) == 2 ) ) {

            Component eatComp = panel.findComponentAt ( ( adjustX + mouseX + parent.getX () ) / 2,
                                    ( adjustY + mouseY + parent.getY () ) / 2 );
            Component[] comps = ( ( Container ) eatComp ).getComponents ();

            if ( comps.length == 0 ) return false;
            if ( ( ( BoardPieces ) comps[0] ).getSide ().equals ( current.side ) ) return false;

            if ( current.getType ().equals ( "normal" ) ) {

                if ( current.getSide ().equals ( "red" ) ) {
                    if ( ( parent.getY () - ( adjustY + mouseY ) ) < 0 ) return false;
                }
                else {
                    if ( ( parent.getY () - ( adjustY + mouseY ) ) > 0 ) return false;
                }
            }

            if ( current.getSide ().equals ( playerColor ) ) {

                logger.logit ( client.me.getName () + " - move <" + ( mouseX + adjustX ) / 75 + "-"
                               + ( mouseY + adjustY ) / 75 + "> to <" + Xpos / 75 + "-" + Ypos / 75 + ">, piece at <"
                               + ( ( BoardPieces ) comps[0] ).getParent ().getX () / 75 + "-"
                               + ( ( BoardPieces ) comps[0] ).getParent ().getY () / 75 + "> is taken\n" );
            }
            else {

                logger.logit ( oppoName + " - move <" + ( mouseX + adjustX ) / 75 + "-" + ( mouseY + adjustY ) / 75
                               + "> to <" + Xpos / 75 + "-" + Ypos / 75 + ">, piece at <"
                               + ( ( BoardPieces ) comps[0] ).getParent ().getX () / 75 + "-"
                               + ( ( BoardPieces ) comps[0] ).getParent ().getY () / 75 + "> is taken\n" );
            }

            ( ( BoardPieces ) comps[0] ).getParent ().remove ( 0 );
            anyEat = true;

            return true;
        }
        else if ( ( Math.abs ( ( adjustX + mouseX ) / 75 - parent.getX () / 75 ) == 1 )
                  && ( Math.abs ( ( adjustY + mouseY ) / 75 - parent.getY () / 75 ) == 1 ) ) {
            
            if ( current.getType ().equals ( "normal" ) ) {

                if ( current.getSide ().equals ( "red" ) ) {
                    if ( ( parent.getY () - ( adjustY + mouseY ) ) < 0 ) return false;
                }
                else {
                    if ( ( parent.getY () - ( adjustY + mouseY ) ) > 0 ) return false;
                }
            }
            return true;
        }
        return false;
    }
    
    
    private void initCurrent(Component cmp, int fromX, int fromY){
        Point parentLoc = cmp.getParent().getLocation();
        adjustX = parentLoc.x - fromX;
        adjustY = parentLoc.y - fromY;
        mouseX = fromX;
        mouseY = fromY;
        current = (BoardPieces) cmp;
        current.setLocation(fromX + adjustX, fromY + adjustY);
        current.setSize(current.getWidth(), current.getHeight());
        pane.add(current, JLayeredPane.DRAG_LAYER);
    }
    
    private void finalizeCurrent(Component cmp, int fromX, int fromY) throws Exception{
        try {
            Boolean hasPiece = false;
            if(((Container) cmp).getComponents().length != 0) hasPiece = true;
            current.setVisible(false);
            if (cmp instanceof BoardPieces || cmp == null || hasPiece == true || !isLegalMove(fromX, fromY)) {
                Component c = panel.findComponentAt(mouseX, mouseY);
                Container parent = (Container) c;
                current.setLocation(mouseX, mouseX);
                parent.add(current);
            } 
            else {
                Container parent = (Container) cmp;
                parent.add(current);
                if (anyEat) {
                    if (playerColor.equals(current.side)) {
                        if (playerColor.equals("red")) {
                            black_piece_count--;
                        } else {
                            red_piece_count--;
                        }
                    } else {
                        if (playerColor.equals("red")) {
                            red_piece_count--;
                        } else {
                            black_piece_count--;
                        }
                    }
                    PrintWriter out = new PrintWriter(opponent.getOutputStream(), true);
                    out.println(Integer.toString((adjustX + mouseX)) + "," + Integer.toString((adjustY + mouseY)) + "#" + Integer.toString(current.getParent().getLocation().x) + "," + Integer.toString(current.getParent().getLocation().y));
                }
                else {
                    if (current.getSide().equals(playerColor)) {
                        logger.logit(client.me.getName() + " - move <" + ((mouseX + adjustX)) / 75 + "-" + ((mouseY + adjustY)) / 75 + "> to <" + current.getParent().getLocation().x / 75 + "-" + current.getParent().getLocation().y / 75 + ">\n");
                    } 
                    else {
                        logger.logit(oppoName + " - move <" + ((mouseX + adjustX)) / 75 + "-" + ((mouseY + adjustY)) / 75 + "> to <" + current.getParent().getLocation().x / 75 + "-" + current.getParent().getLocation().y / 75 + ">\n");
                    }
                }
                if (current.getSide().equals("red")) {
                    if (anyEat) {
                        anyEat = false;
                        isGameFinished();
                    } else {
                        turn = "black";
                    }
                    if (parent.getY() == 525) {
                        current.makeKing(new ImageIcon("piece2king.gif"));
                    }
                } 
                else {
                    if (anyEat) {
                        anyEat = false;
                        isGameFinished();
                    } else {
                        turn = "red";
                    }
                    if (parent.getY() == 0) {
                        current.makeKing(new ImageIcon("piece1king.gif"));
                    }
                }
            }
            current.setVisible(true);
        } catch (IOException ex) {}
    }

    @Override
    public void mouseClicked ( MouseEvent me ) {}

    @Override
    public void mouseEntered ( MouseEvent me ) {}

    @Override
    public void mouseExited ( MouseEvent me ) {}

    @Override
    public void mousePressed ( MouseEvent me ) {
        try{
            Component cmp = panel.findComponentAt(me.getX(), me.getY());
            current = null;
            if (cmp instanceof JPanel) {
                return;
            }
            if (!((BoardPieces) cmp).getSide().equals(playerColor) || !playerColor.equals(turn)) {
                return;
            }
            initCurrent(cmp, me.getX(), me.getY());
            repaint();
        }
        catch(Exception e){}
    }
    
    @Override
    public void mouseReleased(MouseEvent me) {
        if (current != null) {
            try {
                Component cmp = panel.findComponentAt(me.getX(), me.getY());
                finalizeCurrent(cmp, me.getX(), me.getY());
            } catch (Exception ex) {}
        }
        repaint();
    }

    @Override
    public void mouseDragged ( MouseEvent me ) {

        if ( current != null ) current.setLocation ( me.getX () + adjustX, me.getY () + adjustY );
        repaint ();
    }

    @Override
    public void mouseMoved ( MouseEvent me ) {}
}
