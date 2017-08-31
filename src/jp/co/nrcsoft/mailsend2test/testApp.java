package jp.co.nrcsoft.mailsend2test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;

public class testApp {
	/** Log4j2 Logger Instance */
	private static Logger log = LogManager.getLogger();
	 /**
	  * @param args
	  *
	  */
	 public static void main( String[] args ) {
		 String s = null;
		 log.info( "Starting my application..." );
		 if ( args != null && args.length > 0 ) {
			 s = args[ 0 ];
		 }
		 try {
			 log.debug( "Debug Message", s );
			 log.info( "Parsing integer: {}", s );
			 Integer.parseInt( s );
			 log.info( "success: parse integer" );
		 } catch ( NumberFormatException nfe ) {
//			 log.error( "Exception caught: ", nfe );
			 log.error( MarkerManager.getMarker( "ERROR" ), "Exception caught: ", nfe );
		 }

		 System.exit( 0 );
	 }
}
