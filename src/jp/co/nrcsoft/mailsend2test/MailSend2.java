package jp.co.nrcsoft.mailsend2test;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;

/*
 * MailSend2.java
 *
 * Created on 2017/06/08, 20:34
 * Update on 2017/06/08, 11:45
 *
 */
/**
 * MailSend2
 * @author  y.izumi
 * @version 2.0
 */
public class MailSend2 {

	/** Creates new MailSend2 */
	public MailSend2 () {
	}

	/** Log4j2 Logger Instance */
	private static Logger log = LogManager.getLogger( MailSend2.class );

	/**
	 * Mailメソッド
     * @param  msgs	String[]		メール送信制御カードのドライブレターからのフル・パスのファイル名を指定。
	 * @throws Exception
	 */
	public static void main ( String msgs[] ) throws Exception {

		InputStream inStream = null;
		try	{

			// 「mail2.properties」ファイルを読込みます。
			new ResourceBundleManager();
			// 「メール送信制御カード」ファイルを読込みます。
			new MailSendControlFile( msgs[ 0 ] );

            // メール送信元は、「メール送信制御カード」の指定が無い場合は、プロパティーファイルから「Default」として設定される。
            String _fromAddress;
            if  (  ( MailSendControlFile.MIAL_SEND_CONTROL_CARD_FROM_MAIL_ADDRESS != null )
            		&& ( !MailSendControlFile.MIAL_SEND_CONTROL_CARD_FROM_MAIL_ADDRESS.equals( "" ) ) ) {
            	_fromAddress = MailSendControlFile.MIAL_SEND_CONTROL_CARD_FROM_MAIL_ADDRESS;
            }
            else {
            	_fromAddress = ResourceBundleManager.FROM_MAIL_ADDRESS;
            }

            // 「メール送信制御カード」の送信先サーバー「絶対パス名」（バックアップ元のディレクトリー）。
			String _backupTarget =  ResourceBundleManager.APPLICATION_PATH + File.separator + MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER;
            // バックアップ先のディレクトリー。
			String _backupTo = ResourceBundleManager.APPLICATION_PATH  + File.separator +"Backup" +
														File.separator +MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER + File.separator + MailUtil2.getSystemDate( "yyyyMMdd_HHmmss" );

			log.info( "09I({})：メール送信処理を開始します 。", MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
			if  ( MailUtil2.send (
    					_fromAddress,																					// 送信元メールアドレス
    					MailSendControlFile.MIAL_SEND_CONTROL_CARD_TO,						// 送信先メールアドレス（TO複数指定可）
    					MailSendControlFile.MIAL_SEND_CONTROL_CARD_CC,						// 送信先メールアドレス（CC複数指定可）
    					MailSendControlFile.MIAL_SEND_CONTROL_CARD_BCC,						// 送信先メールアドレス（BCC複数指定可）
    					ResourceBundleManager.RETURN_TO_MAIL_ADDRESS,						// 不到達メールのreturn先のメールアドレス
    					MailSendControlFile.MIAL_SEND_CONTROL_CARD_SUBJECT,				// 件名
    					MailSendControlFile.MIAL_SEND_CONTROL_CARD_BODY,					// 送信メール本文
    					ResourceBundleManager.SMTP_SERVER_NAME,									// SMTPのサーバー名
    					ResourceBundleManager.POP3_SERVER_NAME,									// POP3のサーバー名
    					ResourceBundleManager.POP3_USERID,												// pop3の認証時のユーザーID
    					ResourceBundleManager.POP3_PASSWORD,										// pop3の認証時のパスワード
    					ResourceBundleManager.SMTP_PORT_NO,											// SMTP送信ポート番号
    					ResourceBundleManager.DEBUG_MODE,												// デバッグモード
    					ResourceBundleManager.SUBJECT_ENCORDING,									// メールの件名の文字コード
    					ResourceBundleManager.TEXT_ENCORDING,					 					// メールの本文の文字コード
    					_backupTarget, 																					// ドライブレターを含むフォルダー名
    					MailSendControlFile.MIAL_SEND_CONTROL_CARD_ATTACHED_FILES,	// メール添付ファイル名（フル・パス複数指定可）
    					ResourceBundleManager.AUTHENTICATION_MODE, 							// 認証モード
    					ResourceBundleManager.PROCESS_MODE,											// プロセスモード
    					ResourceBundleManager.ATTACHMENT_FILE_TOTAL_SIZE  ) )				// メール送信の際の添付ファイルの総合計サイズの「閾値」
			{
                    log.info( "10I({})：メール送信処理は、正常終了しました。",
                    		MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );

    				// バックアップ先のディレクトリーを作成します。
    				if ( MailUtil2.createDirectory( _backupTo ) ) {
                        log.info( "11I({})：「バックアップ先のディレクトリー作成処理」は、正常終了しました。",
                        		MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
    				}
    				else {
    	            	log.error( MarkerManager.getMarker( "ERROR" ), "03E：「バックアップ先のディレクトリー作成処理」は、異常終了しました。",
    	            			MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
                        System.exit( 16 ); // 処理中止！！！
    				}
                    log.info( "12I({})：「圧縮バックアップ処理」を開始します。",
                    		MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
    				if ( MailUtil2.zipComress( _backupTarget, _backupTo + File.separator + MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER + ".zip",
    						ResourceBundleManager.FILE_ENCORDING_CODE, Integer.parseInt( ResourceBundleManager.COMPRESS_LEVEL ) ) ) {
    					log.info( "13I({})：「圧縮バックアップ処理」は、正常終了しました。",
                        		MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
    				}
    				else {
    	            	log.error( MarkerManager.getMarker( "ERROR" ), "04E({})：「圧縮バックアップ処理」は、異常終了しました。",
    	            			MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
                        System.exit( 16 ); // 処理中止！！！
    				}
                    log.info( "14I({})：「バックアップしたオリジナルファイルの削除処理」を開始します。",
                    		MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
    				if ( MailUtil2.deleteFiles( _backupTarget ) ) {
                        log.info( "15I({})：「バックアップしたオリジナルファイルの削除処理」は、正常終了しました。",
                        		MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
    				}
    				else {
    	            	log.error( MarkerManager.getMarker( "ERROR" ), "05E({})：「バックアップしたオリジナルファイルの削除処理」は、異常終了しました。",
    	            			MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
                        System.exit( 16 ); // 処理中止！！！
    				}
			}
			else {
					log.error( MarkerManager.getMarker( "ERROR" ), "06E({})：「メール送信」は、異常終了しました。",
							MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
                    System.exit( 16 ); // 処理中止！！！
			}
		}
		finally {
            try {
                if ( inStream != null ) {
                    inStream.close();
                }
            } catch ( IOException e2 ) {
    			log.error( MarkerManager.getMarker( "ERROR" ), "99E：原因不明のエラーが発生しました、以下のStackTraceを確認して下さい。", e2 );
 //           	e2.printStackTrace();
            	System.exit( 16 ); // 処理中止！！！
            }
		}

        log.info( "99I：「メール送信処理」は、正常に終了しました。" );
        System.exit( 0 );
	}
}
