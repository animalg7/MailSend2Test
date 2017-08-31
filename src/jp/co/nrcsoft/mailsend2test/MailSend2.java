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
	 * Mail���\�b�h
     * @param  msgs	String[]		���[�����M����J�[�h�̃h���C�u���^�[����̃t���E�p�X�̃t�@�C�������w��B
	 * @throws Exception
	 */
	public static void main ( String msgs[] ) throws Exception {

		InputStream inStream = null;
		try	{

			// �umail2.properties�v�t�@�C����Ǎ��݂܂��B
			new ResourceBundleManager();
			// �u���[�����M����J�[�h�v�t�@�C����Ǎ��݂܂��B
			new MailSendControlFile( msgs[ 0 ] );

            // ���[�����M���́A�u���[�����M����J�[�h�v�̎w�肪�����ꍇ�́A�v���p�e�B�[�t�@�C������uDefault�v�Ƃ��Đݒ肳���B
            String _fromAddress;
            if  (  ( MailSendControlFile.MIAL_SEND_CONTROL_CARD_FROM_MAIL_ADDRESS != null )
            		&& ( !MailSendControlFile.MIAL_SEND_CONTROL_CARD_FROM_MAIL_ADDRESS.equals( "" ) ) ) {
            	_fromAddress = MailSendControlFile.MIAL_SEND_CONTROL_CARD_FROM_MAIL_ADDRESS;
            }
            else {
            	_fromAddress = ResourceBundleManager.FROM_MAIL_ADDRESS;
            }

            // �u���[�����M����J�[�h�v�̑��M��T�[�o�[�u��΃p�X���v�i�o�b�N�A�b�v���̃f�B���N�g���[�j�B
			String _backupTarget =  ResourceBundleManager.APPLICATION_PATH + File.separator + MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER;
            // �o�b�N�A�b�v��̃f�B���N�g���[�B
			String _backupTo = ResourceBundleManager.APPLICATION_PATH  + File.separator +"Backup" +
														File.separator +MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER + File.separator + MailUtil2.getSystemDate( "yyyyMMdd_HHmmss" );

			log.info( "09I({})�F���[�����M�������J�n���܂� �B", MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
			if  ( MailUtil2.send (
    					_fromAddress,																					// ���M�����[���A�h���X
    					MailSendControlFile.MIAL_SEND_CONTROL_CARD_TO,						// ���M�惁�[���A�h���X�iTO�����w��j
    					MailSendControlFile.MIAL_SEND_CONTROL_CARD_CC,						// ���M�惁�[���A�h���X�iCC�����w��j
    					MailSendControlFile.MIAL_SEND_CONTROL_CARD_BCC,						// ���M�惁�[���A�h���X�iBCC�����w��j
    					ResourceBundleManager.RETURN_TO_MAIL_ADDRESS,						// �s���B���[����return��̃��[���A�h���X
    					MailSendControlFile.MIAL_SEND_CONTROL_CARD_SUBJECT,				// ����
    					MailSendControlFile.MIAL_SEND_CONTROL_CARD_BODY,					// ���M���[���{��
    					ResourceBundleManager.SMTP_SERVER_NAME,									// SMTP�̃T�[�o�[��
    					ResourceBundleManager.POP3_SERVER_NAME,									// POP3�̃T�[�o�[��
    					ResourceBundleManager.POP3_USERID,												// pop3�̔F�؎��̃��[�U�[ID
    					ResourceBundleManager.POP3_PASSWORD,										// pop3�̔F�؎��̃p�X���[�h
    					ResourceBundleManager.SMTP_PORT_NO,											// SMTP���M�|�[�g�ԍ�
    					ResourceBundleManager.DEBUG_MODE,												// �f�o�b�O���[�h
    					ResourceBundleManager.SUBJECT_ENCORDING,									// ���[���̌����̕����R�[�h
    					ResourceBundleManager.TEXT_ENCORDING,					 					// ���[���̖{���̕����R�[�h
    					_backupTarget, 																					// �h���C�u���^�[���܂ރt�H���_�[��
    					MailSendControlFile.MIAL_SEND_CONTROL_CARD_ATTACHED_FILES,	// ���[���Y�t�t�@�C�����i�t���E�p�X�����w��j
    					ResourceBundleManager.AUTHENTICATION_MODE, 							// �F�؃��[�h
    					ResourceBundleManager.PROCESS_MODE,											// �v���Z�X���[�h
    					ResourceBundleManager.ATTACHMENT_FILE_TOTAL_SIZE  ) )				// ���[�����M�̍ۂ̓Y�t�t�@�C���̑����v�T�C�Y�́u臒l�v
			{
                    log.info( "10I({})�F���[�����M�����́A����I�����܂����B",
                    		MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );

    				// �o�b�N�A�b�v��̃f�B���N�g���[���쐬���܂��B
    				if ( MailUtil2.createDirectory( _backupTo ) ) {
                        log.info( "11I({})�F�u�o�b�N�A�b�v��̃f�B���N�g���[�쐬�����v�́A����I�����܂����B",
                        		MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
    				}
    				else {
    	            	log.error( MarkerManager.getMarker( "ERROR" ), "03E�F�u�o�b�N�A�b�v��̃f�B���N�g���[�쐬�����v�́A�ُ�I�����܂����B",
    	            			MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
                        System.exit( 16 ); // �������~�I�I�I
    				}
                    log.info( "12I({})�F�u���k�o�b�N�A�b�v�����v���J�n���܂��B",
                    		MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
    				if ( MailUtil2.zipComress( _backupTarget, _backupTo + File.separator + MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER + ".zip",
    						ResourceBundleManager.FILE_ENCORDING_CODE, Integer.parseInt( ResourceBundleManager.COMPRESS_LEVEL ) ) ) {
    					log.info( "13I({})�F�u���k�o�b�N�A�b�v�����v�́A����I�����܂����B",
                        		MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
    				}
    				else {
    	            	log.error( MarkerManager.getMarker( "ERROR" ), "04E({})�F�u���k�o�b�N�A�b�v�����v�́A�ُ�I�����܂����B",
    	            			MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
                        System.exit( 16 ); // �������~�I�I�I
    				}
                    log.info( "14I({})�F�u�o�b�N�A�b�v�����I���W�i���t�@�C���̍폜�����v���J�n���܂��B",
                    		MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
    				if ( MailUtil2.deleteFiles( _backupTarget ) ) {
                        log.info( "15I({})�F�u�o�b�N�A�b�v�����I���W�i���t�@�C���̍폜�����v�́A����I�����܂����B",
                        		MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
    				}
    				else {
    	            	log.error( MarkerManager.getMarker( "ERROR" ), "05E({})�F�u�o�b�N�A�b�v�����I���W�i���t�@�C���̍폜�����v�́A�ُ�I�����܂����B",
    	            			MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
                        System.exit( 16 ); // �������~�I�I�I
    				}
			}
			else {
					log.error( MarkerManager.getMarker( "ERROR" ), "06E({})�F�u���[�����M�v�́A�ُ�I�����܂����B",
							MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
                    System.exit( 16 ); // �������~�I�I�I
			}
		}
		finally {
            try {
                if ( inStream != null ) {
                    inStream.close();
                }
            } catch ( IOException e2 ) {
    			log.error( MarkerManager.getMarker( "ERROR" ), "99E�F�����s���̃G���[���������܂����A�ȉ���StackTrace���m�F���ĉ������B", e2 );
 //           	e2.printStackTrace();
            	System.exit( 16 ); // �������~�I�I�I
            }
		}

        log.info( "99I�F�u���[�����M�����v�́A����ɏI�����܂����B" );
        System.exit( 0 );
	}
}
