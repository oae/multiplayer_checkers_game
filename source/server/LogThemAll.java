/*
 * @(#)LogThemAll.java   12/03/27
 * 
 * Copyright (c) 2005 your company name
 *
 * License agreement text here ...
 *
 *
 *
 */





//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class description
 *
 *
 * @version        Enter version here..., 12/03/26
 * @author         Osman Alperen Elhan
 */
public class LogThemAll {

    private File           log;
    private BufferedWriter out;
    private FileWriter     writer;

    /**
     * Method description
     *
     *
     * @throws IOException
     */
    private void setLog () throws IOException {

        log = new File ( "server.log" );

        if ( !log.exists () ) {
            log.createNewFile ();
        }
    }

    /**
     * Method description
     *
     *
     * @throws IOException
     */
    private void setWriter () throws IOException {
        writer = new FileWriter ( log.getName (), true );
    }

    /**
     * Method description
     *
     */
    private void setOut () {
        out = new BufferedWriter ( writer );
    }

    /**
     * Method description
     *
     *
     * @param str
     *
     * @throws IOException
     */
    public void logit ( String str ) throws IOException {

        setLog ();
        setWriter ();
        setOut ();
        out.write ( str );
        out.close ();
    }
}
