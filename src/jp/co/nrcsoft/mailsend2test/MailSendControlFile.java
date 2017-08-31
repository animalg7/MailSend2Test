package jp.co.nrcsoft.mailsend2test;

import java.io.File;
import java.util.HashMap;

import javax.mail.internet.InternetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;

/*
 * MailSendControlFile.java
 *  「メール送信制御ファイル」を読込み、情報を保持・管理するクラスです。
 *
 * Created on 2017/08/03, 11:35
 * Update on 2017/08/03, 11:35
 *
 */
/**
 * MailSendControlFile
 * @author  y.izumi
 * @version 2.0
 */
public class MailSendControlFile {

	/** Log4j2 Logger Instance */
	private static Logger log = LogManager.getLogger( MailSend2.class );

	// 業務アプリの「相対パス名（ディレクトリー）」。
	public static String MIAL_SEND_CONTROL_CARD_FOLDER;
	// メール送信元のメールアドレス。
	public static String MIAL_SEND_CONTROL_CARD_FROM_MAIL_ADDRESS;

	// メール送信先のメールアドレス。
	public static InternetAddress[] MIAL_SEND_CONTROL_CARD_TO;
	// メール送信先（写し）のメールアドレス。
	public static InternetAddress[] MIAL_SEND_CONTROL_CARD_CC;
	// メール送信先（隠し）のメールアドレス。
	public static InternetAddress[] MIAL_SEND_CONTROL_CARD_BCC;

	// メール送信時の件名。
	public static String MIAL_SEND_CONTROL_CARD_SUBJECT;
	// メール送信時の本文。
	public static String MIAL_SEND_CONTROL_CARD_BODY;
	// メール送信時の添付ファイル（複数指定可）。
	public static String MIAL_SEND_CONTROL_CARD_ATTACHED_FILES;

    // 当該クラスのコンストラクター
	MailSendControlFile( String _sendControl ) {
		// 送信されて来た「メール送信制御カード」からメール送信情報をHashMap型で取得する。
        log.info( "08I：「メール送信制御カード」を読取り開始します。" );
        if ( !MailUtil2.isExists( _sendControl ) ) {
        	log.error( MarkerManager.getMarker( "ERROR" ),
        			"01E：指定された「メール送信制御カード」ファイル（{}）が見つかりませんでしたので、処理を中止します。", _sendControl );
            System.exit( 16 ); // 処理中止！！！
        }

        try {
        	// 送信されて来た「メール送信制御カード」から取得した情報を保持するクラスです。
			HashMap<String, String> map = MailUtil2.readMailSendControllCards( _sendControl );

			MIAL_SEND_CONTROL_CARD_FOLDER = map.get( "fld" );
			MIAL_SEND_CONTROL_CARD_FROM_MAIL_ADDRESS = map.get( "frm" );

            // メール送信先（to、cc、bcc）は、「メール送信制御カード」から取得する。
			MIAL_SEND_CONTROL_CARD_TO = MailUtil2.getMailAddress( map.get( "to" ),		"TO" );
			MIAL_SEND_CONTROL_CARD_CC  = MailUtil2.getMailAddress( map.get( "cc" ),	"CC" );
			MIAL_SEND_CONTROL_CARD_BCC = MailUtil2.getMailAddress( map.get( "bcc" ),	"BCC" );

            // メール送信の際の件名を「メール送信制御カード」から１件だけ、取得する。
			MIAL_SEND_CONTROL_CARD_SUBJECT = map.get( "sbj" );
            // 件名の有無確認（無い場合は、処理を中止します。）
            if ( ( MIAL_SEND_CONTROL_CARD_SUBJECT == null ) ||
            		( MIAL_SEND_CONTROL_CARD_SUBJECT.equals( "" ) ) ) {
            	log.error( MarkerManager.getMarker( "ERROR" ),
            			"E02({})：「メール送信制御カード」の「件名（SBJ=）」カードがありませんでしたので、処理を中止しました。", MIAL_SEND_CONTROL_CARD_FOLDER );
            	System.exit( 16 ); // 処理中止！！！
            }

            // メール送信の際の本文を「メール送信制御カード」から取得する。（複数可）
            MIAL_SEND_CONTROL_CARD_BODY = map.get( "bdy" );

            // メール添付ファイル名を取得する。（複数可）
            MIAL_SEND_CONTROL_CARD_ATTACHED_FILES = map.get( "fle" );
            // 引数の添付ファイル文字列の物理オブジェクト（添付ファイル）が存在する
            // かしないかの確認をして、存在しない場合は、本処理を中止します。
			String _aplfolder = ResourceBundleManager.APPLICATION_PATH + File.separator + MIAL_SEND_CONTROL_CARD_FOLDER;
            if ( ( MIAL_SEND_CONTROL_CARD_ATTACHED_FILES != null ) && ( !MIAL_SEND_CONTROL_CARD_ATTACHED_FILES.equals( "" ) ) ) {
                if ( !MailUtil2.isExsistAttachedFiles( MailUtil2.getAttachedFileNamesWithFullPath( _aplfolder, MIAL_SEND_CONTROL_CARD_ATTACHED_FILES ) ) ) {
                	System.exit( 16 ); // 処理中止！！！
                }
            }
            // 「業務アプリ用フォルダー」の存在を確認し、存在しない場合は、作成します。
            if ( !MailUtil2.isExists( _aplfolder ) ) {
            	if  ( MailUtil2.createDirectory( _aplfolder ) ) {
                    log.warn( "01W({})：指定された「業務アプリ用フォルダー」が存在しませんでしたので、作成しました。",
                    		MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
            	}
            }

		} catch ( Exception e ) {
			log.error( MarkerManager.getMarker( "ERROR" ), "99E：原因不明のエラーが発生しました、以下のStackTraceを確認して下さい。", e );
        	System.exit( 16 ); // 処理中止！！！
		}
	}

}
