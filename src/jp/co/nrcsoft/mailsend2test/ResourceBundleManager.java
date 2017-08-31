package jp.co.nrcsoft.mailsend2test;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * ResouceBundleManager.java
 *  �umail2.properties�v�t�@�C����Ǎ��݁A�ێ��E�Ǘ�����N���X�ł��B
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

	// ���[���A�h���X���̌�A�Ń��[���s���B�̏ꍇ�A�ԐM�������w��B
	public static String RETURN_TO_MAIL_ADDRESS;
    // ���[�����M��́uSMTP�v�T�[�o�[�́u���O�����\�Ȗ��O�v�Ⴕ���́A�uIP�A�h���X�v���w��B
	public static String SMTP_SERVER_NAME;
    // ���O�F�ؕ����̏ꍇ�́uPOP�v�T�[�o�[�́u���O�����\�Ȗ��O�v�Ⴕ���́A�uIP�A�h���X�v���w��B
	public static String POP3_SERVER_NAME;
    // ���O�F�ؕ����̏ꍇ�́uPOP�v�T�[�o�[�֐ڑ�����ۂ́u���[�U�[ID�v���w��B
	public static String POP3_USERID;
    // ���O�F�ؕ����̏ꍇ�́uPOP�v�T�[�o�[�֐ڑ�����ۂ́u���[�U�[ID�v�ɑ΂���u�p�X���[�h�v���w��B
	public static String POP3_PASSWORD;
    // �uSMTP�v�T�[�o�[�֐ڑ�����ۂ́u�|�[�g�ԍ��v���w��B
	public static String SMTP_PORT_NO;

    // ���[�����M�̍ۂ́u�^�C�g���i�����j�v�̕����R�[�h���w��B
	public static String SUBJECT_ENCORDING;
    // ���[�����M�̍ۂ́u�{���v�̕����R�[�h���w��B
	public static String TEXT_ENCORDING;
    // ���[�����M�̍ۂ̓Y�t�t�@�C���̑����v�T�C�Y�́u臒l�v���w��B
	public static long ATTACHMENT_FILE_TOTAL_SIZE = 0;

    // ���[�����M�̃��[�h�iture:���M����Afalse:�[�����M�i���M���O�܂ŏ����͎��{���邪�A���ۂɂ͑��M���Ȃ��Adebug�p�ł��B�j�j
	public static boolean PROCESS_MODE = false;
    // ���[�����M�̍ۂ́u�f�o�b�O���[�h�v�̂��utrue�v�Ɏw�肵����Ɓu�ڍ׃��O�v���\������邪�A
    // �ufalse�v���w�肵����Ɓu�ڍ׃��O�v�͕\�����ꂸ�A�I�����b�Z�[�W�݂̂����\�����Ȃ��B
	public static boolean DEBUG_MODE = false;
	// ���O�F�ؕ����ő��M���邩�ۂ��̎w�������B�i�utrue�v�́A���O�F�ؕ����ő��M����A�ufalse�v�́A���F�ؕ����ő��M����B�j
	public static boolean AUTHENTICATION_MODE = false;

	// �A�v���P�[�V�����̔z���ꏊ�t�H���_�[��
	public static String APPLICATION_PATH;
    // ���[�����M���́A�u���[�����M����J�[�h�v�̎w�肪�����ꍇ�́A���Y�̃v���p�e�B�[�t�@�C������ȗ��l�Ƃ��Đݒ肳���B
	public static String FROM_MAIL_ADDRESS;

    // �T�[�o�[���Ŏ��{���鈳�k�o�b�N�A�b�v�ۂ̃t�@�C���̕����R�[�h�B
	public static String FILE_ENCORDING_CODE;
    // �T�[�o�[���Ŏ��{���鈳�k���x���B
	public static String COMPRESS_LEVEL;


    // ���Y�N���X�̃R���X�g���N�^�[
	ResourceBundleManager() {

		log.info( "01I�F�A�v���P�[�V�����́u�v���p�e�B�\�t�@�C���v��Ǎ��݂܂��B" );

		// �umail2.properties�v�t�@�C���Ǎ��݂̏����B
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

        // ���[�����M�̃��[�h
        if ( ( prop.getString( "ProcessMode" ).trim() ).equalsIgnoreCase( "true" ) ) {
        	PROCESS_MODE = true;
        }

        // ���[�����M�̍ۂ́u�f�o�b�O���[�h�v�̎w��B
        log.info( "02I�F�f�o�b�O���[�h���m�F���܂��B" );
        if ( ( prop.getString( "DebugMode" ).trim() ).equalsIgnoreCase( "true" ) ) {
        	DEBUG_MODE = true;
        	log.info( "03I�F�f�o�b�O���[�h�́u�L���v�ł��B" );
        }
        else {
        	DEBUG_MODE = false;
            log.info( "04I�F�f�o�b�O���[�h�́u�����v�ł��B" );
        }

		// ���O�F�ؕ����ő��M���邩�ۂ��̎w��B
        log.info( "05I�F���[�����M���̔F�ؕ������m�F���܂��B" );
        if ( ( prop.getString( "AuthenticationMode" ).trim() ).equalsIgnoreCase( "true" ) ) {
        	AUTHENTICATION_MODE = true;
        	log.info( "06I�F���[�����M���̔F�ؕ����́u���O�F�ؕ����v�ł��B" );
        }
        else {
        	AUTHENTICATION_MODE = false;
        	log.info( "07I�F���[�����M���̔F�ؕ����́u���F�ؕ����v�ł��B" );
        }

        // ���[�����M���A�h���X
        FROM_MAIL_ADDRESS = prop.getString( "fromMailAddress" ).trim();

	}

}
