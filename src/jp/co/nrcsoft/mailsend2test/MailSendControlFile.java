package jp.co.nrcsoft.mailsend2test;

import java.io.File;
import java.util.HashMap;

import javax.mail.internet.InternetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;

/*
 * MailSendControlFile.java
 *  �u���[�����M����t�@�C���v��Ǎ��݁A����ێ��E�Ǘ�����N���X�ł��B
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

	// �Ɩ��A�v���́u���΃p�X���i�f�B���N�g���[�j�v�B
	public static String MIAL_SEND_CONTROL_CARD_FOLDER;
	// ���[�����M���̃��[���A�h���X�B
	public static String MIAL_SEND_CONTROL_CARD_FROM_MAIL_ADDRESS;

	// ���[�����M��̃��[���A�h���X�B
	public static InternetAddress[] MIAL_SEND_CONTROL_CARD_TO;
	// ���[�����M��i�ʂ��j�̃��[���A�h���X�B
	public static InternetAddress[] MIAL_SEND_CONTROL_CARD_CC;
	// ���[�����M��i�B���j�̃��[���A�h���X�B
	public static InternetAddress[] MIAL_SEND_CONTROL_CARD_BCC;

	// ���[�����M���̌����B
	public static String MIAL_SEND_CONTROL_CARD_SUBJECT;
	// ���[�����M���̖{���B
	public static String MIAL_SEND_CONTROL_CARD_BODY;
	// ���[�����M���̓Y�t�t�@�C���i�����w��j�B
	public static String MIAL_SEND_CONTROL_CARD_ATTACHED_FILES;

    // ���Y�N���X�̃R���X�g���N�^�[
	MailSendControlFile( String _sendControl ) {
		// ���M����ė����u���[�����M����J�[�h�v���烁�[�����M����HashMap�^�Ŏ擾����B
        log.info( "08I�F�u���[�����M����J�[�h�v��ǎ��J�n���܂��B" );
        if ( !MailUtil2.isExists( _sendControl ) ) {
        	log.error( MarkerManager.getMarker( "ERROR" ),
        			"01E�F�w�肳�ꂽ�u���[�����M����J�[�h�v�t�@�C���i{}�j��������܂���ł����̂ŁA�����𒆎~���܂��B", _sendControl );
            System.exit( 16 ); // �������~�I�I�I
        }

        try {
        	// ���M����ė����u���[�����M����J�[�h�v����擾��������ێ�����N���X�ł��B
			HashMap<String, String> map = MailUtil2.readMailSendControllCards( _sendControl );

			MIAL_SEND_CONTROL_CARD_FOLDER = map.get( "fld" );
			MIAL_SEND_CONTROL_CARD_FROM_MAIL_ADDRESS = map.get( "frm" );

            // ���[�����M��ito�Acc�Abcc�j�́A�u���[�����M����J�[�h�v����擾����B
			MIAL_SEND_CONTROL_CARD_TO = MailUtil2.getMailAddress( map.get( "to" ),		"TO" );
			MIAL_SEND_CONTROL_CARD_CC  = MailUtil2.getMailAddress( map.get( "cc" ),	"CC" );
			MIAL_SEND_CONTROL_CARD_BCC = MailUtil2.getMailAddress( map.get( "bcc" ),	"BCC" );

            // ���[�����M�̍ۂ̌������u���[�����M����J�[�h�v����P�������A�擾����B
			MIAL_SEND_CONTROL_CARD_SUBJECT = map.get( "sbj" );
            // �����̗L���m�F�i�����ꍇ�́A�����𒆎~���܂��B�j
            if ( ( MIAL_SEND_CONTROL_CARD_SUBJECT == null ) ||
            		( MIAL_SEND_CONTROL_CARD_SUBJECT.equals( "" ) ) ) {
            	log.error( MarkerManager.getMarker( "ERROR" ),
            			"E02({})�F�u���[�����M����J�[�h�v�́u�����iSBJ=�j�v�J�[�h������܂���ł����̂ŁA�����𒆎~���܂����B", MIAL_SEND_CONTROL_CARD_FOLDER );
            	System.exit( 16 ); // �������~�I�I�I
            }

            // ���[�����M�̍ۂ̖{�����u���[�����M����J�[�h�v����擾����B�i�����j
            MIAL_SEND_CONTROL_CARD_BODY = map.get( "bdy" );

            // ���[���Y�t�t�@�C�������擾����B�i�����j
            MIAL_SEND_CONTROL_CARD_ATTACHED_FILES = map.get( "fle" );
            // �����̓Y�t�t�@�C��������̕����I�u�W�F�N�g�i�Y�t�t�@�C���j�����݂���
            // �����Ȃ����̊m�F�����āA���݂��Ȃ��ꍇ�́A�{�����𒆎~���܂��B
			String _aplfolder = ResourceBundleManager.APPLICATION_PATH + File.separator + MIAL_SEND_CONTROL_CARD_FOLDER;
            if ( ( MIAL_SEND_CONTROL_CARD_ATTACHED_FILES != null ) && ( !MIAL_SEND_CONTROL_CARD_ATTACHED_FILES.equals( "" ) ) ) {
                if ( !MailUtil2.isExsistAttachedFiles( MailUtil2.getAttachedFileNamesWithFullPath( _aplfolder, MIAL_SEND_CONTROL_CARD_ATTACHED_FILES ) ) ) {
                	System.exit( 16 ); // �������~�I�I�I
                }
            }
            // �u�Ɩ��A�v���p�t�H���_�[�v�̑��݂��m�F���A���݂��Ȃ��ꍇ�́A�쐬���܂��B
            if ( !MailUtil2.isExists( _aplfolder ) ) {
            	if  ( MailUtil2.createDirectory( _aplfolder ) ) {
                    log.warn( "01W({})�F�w�肳�ꂽ�u�Ɩ��A�v���p�t�H���_�[�v�����݂��܂���ł����̂ŁA�쐬���܂����B",
                    		MailSendControlFile.MIAL_SEND_CONTROL_CARD_FOLDER );
            	}
            }

		} catch ( Exception e ) {
			log.error( MarkerManager.getMarker( "ERROR" ), "99E�F�����s���̃G���[���������܂����A�ȉ���StackTrace���m�F���ĉ������B", e );
        	System.exit( 16 ); // �������~�I�I�I
		}
	}

}
