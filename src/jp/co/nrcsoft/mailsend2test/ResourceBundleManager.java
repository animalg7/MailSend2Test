package jp.co.nrcsoft.mailsend2test;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * ResouceBundleManager.java
 *  「mail2.properties」ファイルを読込み、保持・管理するクラスです。
 *
 * Created on 2017/08/03, 11:35
 * Update on 2017/08/03, 11:35
 *
 */
/**
 * ResouceBundleManager
 * @author  y.izumi
 * @version 2.0
 */
public class ResourceBundleManager {

	/** Log4j2 Logger Instance */
	private static Logger log = LogManager.getLogger( MailSend2.class );

	// メールアドレス等の誤植でメール不到達の場合、返信する先を指定。
	public static String RETURN_TO_MAIL_ADDRESS;
    // メール送信先の「SMTP」サーバーの「名前解決可能な名前」若しくは、「IPアドレス」を指定。
	public static String SMTP_SERVER_NAME;
    // 事前認証方式の場合の「POP」サーバーの「名前解決可能な名前」若しくは、「IPアドレス」を指定。
	public static String POP3_SERVER_NAME;
    // 事前認証方式の場合の「POP」サーバーへ接続する際の「ユーザーID」を指定。
	public static String POP3_USERID;
    // 事前認証方式の場合の「POP」サーバーへ接続する際の「ユーザーID」に対する「パスワード」を指定。
	public static String POP3_PASSWORD;
    // 「SMTP」サーバーへ接続する際の「ポート番号」を指定。
	public static String SMTP_PORT_NO;

    // メール送信の際の「タイトル（件名）」の文字コードを指定。
	public static String SUBJECT_ENCORDING;
    // メール送信の際の「本文」の文字コードを指定。
	public static String TEXT_ENCORDING;
    // メール送信の際の添付ファイルの総合計サイズの「閾値」を指定。
	public static long ATTACHMENT_FILE_TOTAL_SIZE = 0;

    // メール送信のモード（ture:送信する、false:擬似送信（送信直前まで処理は実施するが、実際には送信しない、debug用です。））
	public static boolean PROCESS_MODE = false;
    // メール送信の際の「デバッグモード」のを「true」に指定しすると「詳細ログ」が表示されるが、
    // 「false」を指定しすると「詳細ログ」は表示されず、終了メッセージのみしか表示しない。
	public static boolean DEBUG_MODE = false;
	// 事前認証方式で送信するか否かの指定をする。（「true」は、事前認証方式で送信する、「false」は、無認証方式で送信する。）
	public static boolean AUTHENTICATION_MODE = false;

	// アプリケーションの配備場所フォルダー名
	public static String APPLICATION_PATH;
    // メール送信元は、「メール送信制御カード」の指定が無い場合は、当該のプロパティーファイルから省略値として設定される。
	public static String FROM_MAIL_ADDRESS;

    // サーバー側で実施する圧縮バックアップ際のファイルの文字コード。
	public static String FILE_ENCORDING_CODE;
    // サーバー側で実施する圧縮レベル。
	public static String COMPRESS_LEVEL;


    // 当該クラスのコンストラクター
	ResourceBundleManager() {

		log.info( "01I：アプリケーションの「プロパティ―ファイル」を読込みます。" );

		// 「mail2.properties」ファイル読込みの準備。
		ResourceBundle prop = ResourceBundle.getBundle( "mail2" );

		RETURN_TO_MAIL_ADDRESS			= prop.getString( "returnToMailAddress" ).trim();
		SMTP_SERVER_NAME						= prop.getString( "SMTPServerName" ).trim();
		POP3_SERVER_NAME						= prop.getString( "POP3ServerName" ).trim();
		POP3_USERID								= prop.getString( "POP3UserId" ).trim();
		POP3_PASSWORD							= prop.getString( "POP3Password" ).trim();
		SMTP_PORT_NO							= prop.getString( "SMTPPortNo" ).trim();
		SUBJECT_ENCORDING					= prop.getString( "SubjectEncording" ).trim();
		TEXT_ENCORDING						 	= prop.getString( "TextEncording" ).trim();
		ATTACHMENT_FILE_TOTAL_SIZE	= Long.parseLong( prop.getString( "AttachmentFileTotalSize" ).trim() );
		APPLICATION_PATH						= prop.getString( "ApplicationPath" ).trim();
		FILE_ENCORDING_CODE				= prop.getString( "fileEncordingCode" ).trim();
		COMPRESS_LEVEL							=prop.getString( "CompressLevel" ).trim();

        // メール送信のモード
        if ( ( prop.getString( "ProcessMode" ).trim() ).equalsIgnoreCase( "true" ) ) {
        	PROCESS_MODE = true;
        }

        // メール送信の際の「デバッグモード」の指定。
        log.info( "02I：デバッグモードを確認します。" );
        if ( ( prop.getString( "DebugMode" ).trim() ).equalsIgnoreCase( "true" ) ) {
        	DEBUG_MODE = true;
        	log.info( "03I：デバッグモードは「有効」です。" );
        }
        else {
        	DEBUG_MODE = false;
            log.info( "04I：デバッグモードは「無効」です。" );
        }

		// 事前認証方式で送信するか否かの指定。
        log.info( "05I：メール送信時の認証方式を確認します。" );
        if ( ( prop.getString( "AuthenticationMode" ).trim() ).equalsIgnoreCase( "true" ) ) {
        	AUTHENTICATION_MODE = true;
        	log.info( "06I：メール送信時の認証方式は「事前認証方式」です。" );
        }
        else {
        	AUTHENTICATION_MODE = false;
        	log.info( "07I：メール送信時の認証方式は「無認証方式」です。" );
        }

        // メール送信元アドレス
        FROM_MAIL_ADDRESS = prop.getString( "fromMailAddress" ).trim();

	}

}
