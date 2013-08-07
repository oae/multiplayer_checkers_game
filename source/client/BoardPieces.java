

//~--- JDK imports ------------------------------------------------------------

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class BoardPieces extends JLabel {

    public String side;
    public String type;

    public BoardPieces ( Icon icon, String side ) {

        super ( icon );
        this.side = side;
        type      = "normal";
    }

    /******************* Getters and Setters *****************/
    public String getSide () { return side; }
    public void setSide ( String side ) { this.side = side; }
    public String getType () { return type; }
    public void setType ( String type ) { this.type = type; }
    /*********************************************************/
    
    public void makeKing ( ImageIcon icon ) {

        this.type = "king";
        this.setIcon ( icon );
    }
}
