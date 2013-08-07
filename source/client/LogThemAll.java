

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class LogThemAll {

    private String         fileName;
    private File           log;
    private BufferedWriter out;
    private FileWriter     writer;

    public LogThemAll ( String fileName, String header ) throws IOException {

        this.fileName = fileName;
        setLog ();
        setWriter ();
        setOut ();
        logit ( header );
    }

    private void setLog () throws IOException {

        log = new File ( fileName );

        if ( !log.exists () ) {
            log.createNewFile ();
        }
    }

    private void setWriter () throws IOException {
        writer = new FileWriter ( log.getName (), true );
    }

    private void setOut () {
        out = new BufferedWriter ( writer );
    }

    public void logit ( String str ) throws IOException {

        setLog ();
        setWriter ();
        setOut ();
        out.write ( str );
        out.close ();
    }
}
