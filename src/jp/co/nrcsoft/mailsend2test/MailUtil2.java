package jp.co.nrcsoft.mailsend2test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;

/**
 * MailUtil2
 * @author  y.izumi
 * @version 2.0
 * ���[�����M�p�N���X
 */
public class MailUtil2 {

	/** Log4j2 Logger Instance */
	private static Logger log = LogManager.getLogger( MailUtil2.class );

	/** Application Folder Name  */
	private static String aplFolder = "";

	/**
	 * ���[���𑗐M�����B
	 * @param     _fromAddress			String					���M�����[���A�h���X
	 * @param     _toAddress				InternetAddress[]	���M�惁�[���A�h���X�iTO�����w��j
	 * @param     _ccAddress				InternetAddress[]	���M�惁�[���A�h���X�iCC�����w��j
	 * @param     _bccAddress				InternetAddress[]	���M�惁�[���A�h���X�iBCC�����w��j
	 * @param     _returnToAddress		String 					�s���B���[����return��̃��[���A�h���X
	 * @param     _subjectStr 				String					����
	 * @param     _msgStr					String					���M�{��
	 * @param     _smtpHost				String					SMTP�̃T�[�o�[��
	 * @param     _pop3Hst					String					pop3�̃T�[�o�[��
	 * @param     _pop3id					String					pop3�̔F�؎��̃��[�U�[ID
	 * @param     _pop3pwd				String					pop3�̔F�؎��̃p�X���[�h
	 * @param     _smtpPort				String					SMTP���M�|�[�g�ԍ�
	 * @param     _debug						boolean 				�f�o�b�O���[�h
	 * @param     _subjectEncording		String					���[���̌����̕����R�[�h
	 * @param     _textEncording			String					���[���̖{���̕����R�[�h
	 * @param	_pathfolder  	    		String 					���[���Y�t�t�@�C�����h���C�u���^�[���܂ރt�H���_�[��
	 * @param	_attachedFiles			String					���[���Y�t�t�@�C�����i�t���E�p�X�����w��j
	 * @param	_authenticationMode	boolean 				�F�؃��[�h
	 * @param	_processMode			boolean 				�v���Z���X���[�h
	 * @param	_attachmentFileSize	long		 				���[�����M�̍ۂ̓Y�t�t�@�C���̑����v�T�C�Y�́u臒l�v
	 * @return    _retValue					boolean 				���[�����M����(true:�����Afalse:���s)
	 */
	public static Boolean send (
			String _fromAddress,
			InternetAddress[] _toAddress,
			InternetAddress[] _ccAddress,
			InternetAddress[] _bccAddress,
			String _returnToAddress,
			String _subjectStr,
			String _msgStr,
			String _smtpHost,
			String _pop3Hst,
			final String _pop3id,
			final String _pop3pwd,
			String _smtpPort,
			boolean _debug,
			String _subjectEncording,
			String _textEncording,
			String _pathfolder,
			String _attachedFiles,
			boolean _authenticationMode,
			boolean _processMode,
			long _attachmentFileSize
			) {

		/* �߂�l */
		Boolean retValue = true;
		try {
			Properties props = new Properties();
			// �u�F�؃��[�h�v�m�F
			if ( _authenticationMode ) {
				props.put( "mail.smtp.auth", "true" );
			}
            props.put( "mail.smtp.host", _smtpHost );
            props.put( "mail.smtp.port", _smtpPort );
            props.put( "mail.smtp.from", _returnToAddress );
            // �f�o�b�O���[�h�̎w��
            if ( _debug ) {
            	props.put( "mail.debug", "true" );
            }
            // ���[���Z�b�V�����m��
            Session session = null;
			if ( _authenticationMode ) {
	            	session = Session.getDefaultInstance( props, new javax.mail.Authenticator() {
	            			protected PasswordAuthentication getPasswordAuthentication() {
	            				return new PasswordAuthentication( _pop3id, _pop3pwd );
	            			}
	            	} );
	            	// Debug���[�h�̎w��
	                session.setDebug( _debug );
	                /*pop before smtp�΍�start */
	                Store store = session.getStore( "pop3" );
	                store.connect( _pop3Hst, _pop3id, _pop3pwd );
	                /*pop before smtp�΍�end */
			}
			else {
				session = Session.getDefaultInstance( props, null );
			}
			MimeMessage msg = new MimeMessage( session );

			// ����L���̊m�F
            if ( _toAddress != null ) {
            	// ���M���iFROM�j
                msg.setFrom( new InternetAddress( _fromAddress ) );
            	// ���M��iTO�j
            	msg.setRecipients( Message.RecipientType.TO, _toAddress );
            	// ���M��i�ʂ��iCC�j�j
                if ( _ccAddress != null ) {
                	msg.setRecipients( Message.RecipientType.CC, _ccAddress );
                }
    			// ���M��i�ʂ��iBCC�j�j
                if ( _ccAddress != null ) {
                	msg.setRecipients( Message.RecipientType.BCC, _bccAddress );
                }
    			// ���[�����M����
                msg.setSentDate( new Date() );
                // ����
               	msg.setSubject( _subjectStr , _subjectEncording );
               	// �Y�t�t�@�C������
            	String _attachedFullPathFiles = MailUtil2.getAttachedFileNamesWithFullPath( _pathfolder, _attachedFiles );
            	// �Y�t�t�@�C���L���̊m�F
                if  (  ( _attachedFullPathFiles != null ) && ( !_attachedFullPathFiles.equals( "" ) ) ) {
                	// �Y�t�t�@�C���̗L�̏ꍇ
                    MimeBodyPart mbp1 = new MimeBodyPart();
                    MimeBodyPart mbps = new MimeBodyPart();
                    Multipart mp = new MimeMultipart();
                    // ���[���̖{��
                    mbp1.setText( setNewlineCharacter( _msgStr ), _textEncording );

                    // �Y�t�t�@�C���T�C�Y�̑����v�t�@�C���T�C�Y���m�F���܂��B
                    String[] _attachedFileNames = MailUtil2.getAttachedFileNames( _attachedFullPathFiles );
                    long _totalFileSize = 0;
                    for ( String _attachFile : _attachedFileNames ) {
                    	_totalFileSize = _totalFileSize + new File( _attachFile ).length();
                    }
                    if ( _attachmentFileSize < _totalFileSize ) {
                		  log.error( MarkerManager.getMarker( "ERROR" ),
                  				  "E51({})�F�Y�t�t�@�C���́u�����v�t�@�C���T�C�Y�i{}�j�v���A�u臒l�i{}�j�v�𒴉߂��Ă���ׁA�����𒆎~���܂����B", aplFolder, _totalFileSize, _attachmentFileSize );
                          System.exit( 16 ); // �������~�I�I�I
                    }
                    for ( String _attachFile2 : _attachedFileNames ) {
                        mbps = new MimeBodyPart();
                        FileDataSource fds = new FileDataSource( _attachFile2 );
                        mbps.setDataHandler( new DataHandler( fds ) );
                        mbps.setFileName( MimeUtility.encodeWord( fds.getName() ) );
                        mp.addBodyPart( mbps );
                    }
                    mp.addBodyPart( mbp1 );
                    msg.setContent( mp );
                }
                else {
                	// �Y�t�t�@�C���̖��̏ꍇ�́A���[���̖{���̂ݐݒ�B
                	msg.setText( setNewlineCharacter( _msgStr ), _textEncording );
                }
                //  �v���Z�X���[�h�̊m�F�itrue�Ȃ���ۂɑ��M�܂Ŏ��s���܂��B�j
                if ( _processMode ) {
                    // ���[���̑��M
                	Transport.send( msg );
                }
            }
            else {
      		  log.error( MarkerManager.getMarker( "ERROR" ),
      				  "E52({})�F�u����A�h���X�iTO�j�v�J�[�h���������܂���ł����̂ŁA�����𒆎~���܂����B", aplFolder );
            	retValue = false;
            }
		} catch ( Exception e ) {
    		  log.error( MarkerManager.getMarker( "ERROR" ),
      				  "99E({})�F�����s���̃G���[���������܂����A�ȉ���StackTrace���m�F���ĉ������B", aplFolder, e );
//			e.printStackTrace();
			retValue = false;
		}

		return retValue;
	}

      /**
       * ��؂蕶�����J���}�̕������[���A�h���X�̕������InternetAddress�^�z��ŁA�擾����B
       * ���̍ۂɁA�d���������[���A�h���X�����݂����ꍇ�́A�Е��̃��[���A�h���X�̕�����͏��O����B
       * @param	_propAddresses		String					��؂蕶�����J���}�̕������[���A�h���X�̕�������w�肷��B
       * @param	_cardCC				String					�u���[�����M����J�[�h�v���̃J�[�h�R�[�h���w�肷��B
  	   * @return  	_address				InternetAddress[]	InternetAddress�^�z��i�d�����O���ꂽ���[���A�h���X�j�B
       * @throws	AddressException								�Ԉ�����`���̃��[���A�h���X�����m���ꂽ����throw�����B
       */
      public static InternetAddress[] getMailAddress ( String _propAddresses, String _cardCC ) throws AddressException {

    	  InternetAddress _address[] = null;
          if ( ( _propAddresses != null ) && ( !_propAddresses.equals( "" ) ) ) {
        	  String[] _addresses = _propAddresses.split( "," );

        	  // �u���[�����M����J�[�h�v�Ŏw�肳�ꂽ���ԂŁA�d���������[���A�h���X�����O����B
        	  List<String> _addressLists =Arrays.asList( _addresses );
        	  List<String> _addressLists2 = new ArrayList<>( new LinkedHashSet<String>( _addressLists ) );
        	  String[] _addresses2 = ( String[] ) _addressLists2.toArray( new String[ _addressLists2.size() ] );

              int count = _addresses2.length;
        	  int count2 = 0;
              for ( int i = 0; i < count; i++ ) {
            	  _addresses2[ i ] = _addresses2[ i ].trim() ;
            	  // ���[���A�h���X�̊m�F�i���[���A�h���X�ɑ��������������߂Ă��܂��j
            	  if ( MailUtil2.checkMailAddressByRegularExpression( _addresses2[ i ] ) ) {
            		  count2++;
            	  }
            	  else {
                      log.warn( "51W({})�F�u���[���A�h���X�v�ɑ��������Ȃ��A�h���X��������܂����A", aplFolder );
                      log.warn( "52W({})�F���Y���[���A�h���X�i{}�j�͏��O����܂����A�����͑��s���܂��B�i�J�[�h�F{}�j", aplFolder, _addresses2[ i ], _cardCC );
            	  }
              }

              _address = new InternetAddress[ count2 ];
              int k = 0;
              for ( int j = 0; j < count; j++ ) {
            	  if ( MailUtil2.checkMailAddressByRegularExpression( _addresses2[ j ] ) ) {
            		  _address[ k ] = new InternetAddress( _addresses2[ j ] );
            		  k++;
            	  }
              }
          }

          return _address;
     }

      /**
       * �����̃��[���A�h���X�����񂪃��[���A�h���X�ɑ����������̊m�F�����܂��B
       * @param	_mailAddressStr	String		���[���A�h���X�m�F��������w�肷��B
  	   * @return  	_result					boolean		true�F���i�A  false�F�s���i�B
       */
      public  static boolean checkMailAddressByRegularExpression( String _mailAddressStr ) {

    	  boolean _result = false;
    	  // ���K�\��������̐���
    	  String aText = "[\\w!#%&'/=~`\\*\\+\\?\\{\\}\\^\\$\\-\\|]";
    	  String dotAtom = aText + "+" + "(\\." + aText + "+)*";
    	  String regularExpression = "^" + dotAtom + "@" + dotAtom + "$";

    	  if ( Pattern.compile( regularExpression ).matcher( _mailAddressStr ).find() ) {
    		  _result = true;
    	  }

    	  return _result;
      }

      /**
       * �����̓Y�t�t�@�C��������̕����I�u�W�F�N�g�i�Y�t�t�@�C���j�����݂���
       * �����Ȃ����̊m�F�����āA���݂��Ȃ��ꍇ�́A�{�����𒆎~���܂��B
       * �Y�t�����������ꍇ�A��̃t�@�C���ł����݂��Ȃ��ꍇ�́A�{�����𒆎~���܂��B
       * @param	_str		String		�J���}��؂�̓Y�t�t�@�C��������
  	   * @return  	_return	boolean		true�F�S�t�@�C�����݂���Afalse�F���ꂩ�̃t�@�C�������݂��Ȃ��B
       */
      public static boolean  isExsistAttachedFiles ( String _str ) {

          boolean  _return = false;
          if ( ( _str != null ) && ( !_str.equals( "" ) ) ) {
        	  String[] _attachedFileNames = _str.split( "," );
              for ( String _attachFile : _attachedFileNames ) {
            	  if ( MailUtil2.isExists( _attachFile ) ) {
            		  _return = true;
            	  }
            	  else {
            		  log.error( MarkerManager.getMarker( "ERROR" ),
                  			"53E({})�F�w�肳�ꂽ�u���[�����M����J�[�h�v�t�@�C�����̓Y�t�t�@�C���i{}�j�����݂��܂���̂ŁA�����𒆎~���܂��B", aplFolder, _attachFile );
            		  return false;
            	  }
              }
          }

          return _return;
     }

      /**
       * �J���}��؂�̓Y�t�t�@�C��������𕶎�String�z��^�ŕԂ��܂��B
       * @param	_str							String		�J���}��؂�̕�����
  	   * @return  	_attachedFileNames	String[]		�z��̕�����
       */
      public static String[] getAttachedFileNames ( String _str ) {

          String _attachedFileNames[] = null;
          if ( ( _str != null ) && ( !_str.equals( "" ) ) ) {
          	String[] _attachedFileNamesTmp = _str.split( "," );
              int count = _attachedFileNamesTmp.length;
              _attachedFileNames = new String[ count ];
              for ( int i = 0; i < count; i++ ) {
            	  _attachedFileNamesTmp[ i ] = _attachedFileNamesTmp[ i ].trim();
            	  _attachedFileNames[ i ] = new String( _attachedFileNamesTmp[ i ] );
              }
          }

          return _attachedFileNames;
     }

      /**
       * �h���C�u���^�[���܂ރJ���}��؂�̓Y�t�t�@�C������String���Ԃ��܂��B
       * @param	_folderName				String		�t�H���_�[���̕�����
       * @param	_str							String		�t�@�C�����̃J���}��؂�̕�����
  	   * @return  	_attachedFileNames	String		�z��̕�����
       */
      public static String getAttachedFileNamesWithFullPath ( String _folderName, String _str ) {

		  StringBuilder _fullPathBuf = new StringBuilder();

		  if ( ( _folderName != null ) && ( !_folderName.equals( "" ) ) ) {
              if ( ( _str != null ) && ( !_str.equals( "" ) ) ) {
            	  String[] _attachedFileNames = _str.split( "," );
                  int count = _attachedFileNames.length;
                  for ( int i = 0; i < count; i++ ) {
                	  if ( i == 0 ) {
                		  _fullPathBuf.append( _folderName.trim() + File.separator + _attachedFileNames[i].trim() );
                	  }
                	  else {
                		  _fullPathBuf.append( "," );
                		  _fullPathBuf.append( _folderName.trim() + File.separator + _attachedFileNames[i].trim() );
                	  }
                  }
              }
		  }

          return _fullPathBuf.toString();
      }

      /**
       * �L�[���[�h�ɑ΂���Argument��Ԃ��܂��B
       * �L�[���[�h�Ɉ�v���Ȃ��ꍇ�́Anull��Ԃ��܂��B
       * @param	_stringWithKeyWord	String	���������Ώە�����
       * @param	_keyWord					String	�����L�[���[�h������
  	   * @return  	_argument 				String	�L�[���[�h�ɑ΂���Argumet������i5���ڈȍ~�j
       * @throws	Exception
       */
      public static String getArgumentForKyeWord ( String _stringWithKeyWord, String _keyWord ) throws Exception {

          String _argument = null;
          if ( ( _stringWithKeyWord != null ) && ( !_stringWithKeyWord.equals( "" ) ) &&
        		  ( _keyWord != null ) && ( !_keyWord.equals( "" ) ) ) {
        	  if ( _stringWithKeyWord.substring( 0, 4 ).equals( _keyWord ) ) {
        		  _argument = _stringWithKeyWord.substring( 4 ).trim();
        	  }
          }

          return _argument;
     }

      /**
       * �u���[�����M����J�[�h�v��ǎ�胁�[��������𒊏o���AHashMap�֊i�[���Ԃ��܂��B
       * @param	_mailSendControllCards	String	�u���[�����M����J�[�h�v�̃h���C�u���^�[����̃t���E�p�X�̃t�@�C�������w��B
  	   * @return  	_argument		 			String	�L�[���[�h�ɑ΂���Argumet������
       * @throws	FileNotFoundException				�����́u���[�����M����J�[�h�v��������Ȃ��ꍇ��Exception�B
       * 				Exception									���̑��̌����s����Exception�B
       */
      public static HashMap<String, String> readMailSendControllCards ( String _mailSendControllCards ) throws Exception {

    	  StringBuilder frmBuf = new StringBuilder();
		  StringBuilder toBuf = new StringBuilder();
		  StringBuilder ccBuf = new StringBuilder();
		  StringBuilder bccBuf = new StringBuilder();
		  StringBuilder sbjBuf = new StringBuilder();
		  StringBuilder bdyBuf = new StringBuilder();
		  StringBuilder fldBuf = new StringBuilder();
		  StringBuilder fleBuf = new StringBuilder();
		  HashMap<String, String> map = new HashMap<String, String>();

    	  try {
    		    BufferedReader br = new BufferedReader( new FileReader( new File( _mailSendControllCards ) ) );
    		    String mailSendControllCard;
    		    while ( ( mailSendControllCard = br.readLine() ) != null ) {
    		    	String keyWord = mailSendControllCard.substring( 0, 4 );
					if ( ( keyWord != null ) && ( !keyWord.equals( "" ) ) ) {
						if ( keyWord.equalsIgnoreCase( "FRM=" ) ) {
							// ���[�����M���́A�P���[�U�[�K�{�ׁ̈A�Q���ڈȍ~�͑��݂��Ă��Ă��A�����i�X�L�b�v�j����B
							if ( ( frmBuf.toString() == null ) || ( frmBuf.toString().equals( "" ) ) ) {
								frmBuf.append( mailSendControllCard.substring( 4 ).trim() );
							}
						} else if ( ( keyWord.equalsIgnoreCase( "TO= " ) || ( keyWord.equalsIgnoreCase( "TO =" ) ) ) ) {
							if ( ( toBuf.toString() == null ) || ( toBuf.toString().equals( "" ) ) ) {
								toBuf.append( mailSendControllCard.substring( 4 ).trim() );
							}
							else {
								toBuf.append( "," );
								toBuf.append( mailSendControllCard.substring( 4 ).trim() );
							}
						} else if ( ( keyWord.equalsIgnoreCase( "CC= " ) || ( keyWord.equalsIgnoreCase( "CC =" ) ) ) ) {
							if ( ( ccBuf.toString() == null ) || ( ccBuf.toString().equals( "" ) ) ) {
								ccBuf.append( mailSendControllCard.substring( 4 ).trim() );
							}
							else {
								ccBuf.append( "," );
								ccBuf.append( mailSendControllCard.substring( 4 ).trim() );
							}
						} else if ( keyWord.equalsIgnoreCase( "BCC=" ) ) {
							if ( ( bccBuf.toString() == null ) || ( bccBuf.toString().equals( "" ) ) ) {
								bccBuf.append( mailSendControllCard.substring( 4 ).trim() );
							}
							else {
								bccBuf.append( "," );
								bccBuf.append( mailSendControllCard.substring( 4 ).trim() );
							}
						} else if ( keyWord.equalsIgnoreCase( "SBJ=" ) ) {
							// ���[���̌����́A�P�s�K�{�ׁ̈A�Q�s�ڈȍ~�͑��݂��Ă��Ă��A�����i�X�L�b�v�j����B
							if ( ( sbjBuf.toString() == null ) || ( sbjBuf.toString().equals( "" ) ) ) {
								sbjBuf.append( mailSendControllCard.substring( 4 ).trim() );
							}
						} else if ( keyWord.equalsIgnoreCase( "BDY=" ) ) {
							if ( ( bdyBuf.toString() == null ) || ( bdyBuf.toString().equals( "" ) ) ) {
								bdyBuf.append( mailSendControllCard.substring( 4 ).trim() );
							}
							else {
								bdyBuf.append( System.getProperty( "line.separator" ) );
								bdyBuf.append( mailSendControllCard.substring( 4 ).trim() );
							}
						} else if ( keyWord.equalsIgnoreCase( "FLD=" ) ) {
							// �u���[�����M����J�[�h�v�̑��M��T�[�o�[�́u��΃p�X���v�́A�P�s�K�{�ׁ̈A�Q�s�ڈȍ~�͑��݂��Ă��Ă��A�����i�X�L�b�v�j����B
							if ( ( fldBuf.toString() == null ) || ( fldBuf.toString().equals( "" ) ) ) {
								fldBuf.append( mailSendControllCard.substring( 4 ).trim() );
							}
						} else if ( keyWord.equalsIgnoreCase( "FLE=" ) ) {
							if ( ( fleBuf.toString() == null ) || ( fleBuf.toString().equals( "" ) ) ) {
								fleBuf.append( mailSendControllCard.substring( 4 ).trim() );
							}
							else {
								fleBuf.append( "," );
								fleBuf.append( mailSendControllCard.substring( 4 ).trim() );
							}
						}
    		    	}
     		    }

    		    map.put( "frm", frmBuf.toString() );
    		    map.put( "to",   toBuf.toString() );
    		    map.put( "cc",   ccBuf.toString() );
    		    map.put( "bcc", bccBuf.toString() );
    		    map.put( "sbj",	sbjBuf.toString() );
    		    map.put( "bdy",	bdyBuf.toString() );
    		    map.put( "fld",	fldBuf.toString() );
    		    map.put( "fle",	fleBuf.toString() );

    		    br.close();

    	  } catch ( FileNotFoundException e ) {
    		  log.error( MarkerManager.getMarker( "ERROR" ),
    				  "54E({})�F�w�肵���u���[�����M����J�[�h�v�i{}�j�����݂��܂���̂ŁA�����𒆎~���܂��B", aplFolder, _mailSendControllCards, e );
 //    		  e.printStackTrace();
              System.exit( 16 ); // �������~�I�I�I
    	  } catch ( Exception e2 ) {
    		  log.error( MarkerManager.getMarker( "ERROR" ),
    				  "99E({})�F�����s���̃G���[���������܂����A�ȉ���StackTrace���m�F���ĉ������B", aplFolder, e2 );
 //   		  e.printStackTrace();
              System.exit( 16 ); // �������~�I�I�I
    	  }

		return map;
    }

      /**
       * �v���b�g�t�H�[���ˑ��̉��s�R�[�h��ϊ����܂��B
       * @param		_str		String	�ϊ�������������
  	   * @return  	_returnStr	String	�ϊ���̕�����
       */
      public static String setNewlineCharacter( String _str ) {

    	  String _returnStr = "";
    	  String _sep = System.getProperty( "line.separator" );
    	  if ( ( _str != null ) && ( !_str.equals( "" ) ) ) {
    		  _returnStr = _str.replaceAll( "NewlineCharacterCode", _sep );
    	  }

          return _returnStr;
     }

      /**
       * ���w�肵�����t�t�H�[�}�b�g�L���uyyyyMMdd_HHmmss�v�Ȃǂ̓����`���������Ԃ��܂��B
       * @param	_format		String	���t�t�H�[�}�b�g�L��			��jyyyyMMdd_HHmmss
  	   * @return  	_str			String	���߂��������Ԃ��܂��B	��j20170911_121530
       */
      public static String getSystemDate( String _format  ) {

          Date date = new Date();
          String _str = "";
      	  if ( ( _format !=null ) && ( !_format.equals( "" ) ) ) {
              SimpleDateFormat sdf = new SimpleDateFormat( _format );
              _str = sdf.format(date);
    	  }

          return _str;
     }

      /**
       * �� �e�f�B���N�g���[�̔z���ɃT�u�f�B���N�g���[���쐬���܂��B
       * @param	_tragetPath			String	�쐬����e�f�B���N�g���[���B			( ��: C:/sample )
  	   * @return  	_returnCode			boolean	true�F�쐬�����A  false�F�쐬���s�B
       */
      public static boolean  createDirectory( String _tragetPath ) {

    	  boolean _returnCode = false;
    	  if ( ( _tragetPath !=null ) && ( !_tragetPath.equals( "" ) )  ) {
    		  File newfile = new File( _tragetPath );
    		  _returnCode = newfile.mkdirs();
    	  }

          return _returnCode;
     }

      /**
       * �� �w��̃f�B���N�g���[���̃t�@�C���݂̂�S�č폜���܂��i�w��̃f�B���N�g���[���̃f�B���N�g���[�́A�������܂��j�B
       * @param	_targetDirectoryPath			String	�쐬����e�f�B���N�g���[���B				( ��: C:/sample )
  	   * @return  	_returnCode						boolean	true�F�폜�����A  false�F�폜���s�B
       */
      public static boolean  deleteFiles( String _targetDirectoryPath ) {

    	  boolean _returnCode = false;
    	  boolean _returnChkCode = true;
    	  if ( ( _targetDirectoryPath !=null ) && ( !_targetDirectoryPath.equals( "" ) ) ) {

    		  File td = new File( _targetDirectoryPath );
    		  // �����Ŏw�肵���f�B���N�g���[���̓t�@�C���̑��݃`�F�b�N���A���݂��Ȃ��ꍇ�́Afalse��return�B
    		  if ( !td.exists() ) {
    			  return _returnCode;
    		  }
    		  // �w�肵���������f�B���N�g���[�̏ꍇ
    		  if ( td.isDirectory() ) {
    			  // �f�B���N�g�����̈ꗗ���擾
    			  File[] files = td.listFiles();
    			  // �f�B���N�g�����̃t�@�C���݂̂�S�č폜����B
    			  for ( int i = 0; i < files.length; i++ ) {
    				  if  ( files[i].isFile() ) {
    					  if ( files[i].delete() ) {
                			  _returnCode = true;
        				  }
    					  else {
        					  _returnChkCode = false;
        				  }
    				  }
    			  }
    			  if ( !_returnChkCode ) {
    				  _returnCode = false;
    			  }
    		  }
    	  }

          return _returnCode;
      }

      /**
       * ���K���ȉ��̂S�̈������w��B
       * @param	_zipTargetDirectoryPath		String	���k����f�B���N�g���[���A���́A�t�@�C�����B	( ��; C:/sample�A���́AC:/sample/sample.txt )
       * @param	_directoryPathBeforeZip		String	���k��̏o�̓t�@�C�������t���p�X�Ŏw��B		( ��: C:/sample.zip )
       * @param	_fileEncordingCode				String	���k���̃t�@�C���G���R�[�f�B���O�R�[�h���w��B ( ��: Shift_JIS�AUTF-8�Ȃ� )
       * @param	_compressLevel					int			���k���̈��k���x�����w��B								 ( ��:5 )
  	   * @return  	_returnCode						boolean	true�F���k���������A  false�F���k�������s�B
       */
      public static boolean  zipComress( String _zipTargetDirectoryPath,
    		  String _directoryPathBeforeZip, String _fileEncordingCode, int _compressLevel ) {

    	  boolean _returnCode = false;
    	  if ( ( _zipTargetDirectoryPath != null ) && ( !_zipTargetDirectoryPath.equals( "" ) ) &&
    			  ( _directoryPathBeforeZip != null ) && ( !_directoryPathBeforeZip.equals( "" ) ) &&
    			  ( _fileEncordingCode != null ) && ( !_fileEncordingCode.equals( "" ) ) &&
    			  ( _compressLevel >= 1 ) ) {

    		  File baseFile = new File( _zipTargetDirectoryPath );
    		  File zipFile    = new File( _directoryPathBeforeZip );

    		  if  ( ZipCompressUtils.archiveZip(  baseFile, zipFile, _fileEncordingCode, _compressLevel ) ) {
    			  _returnCode = true;
    		  }
    	  }

          return _returnCode;
     }

      /**
       * ���Y�t�@�C�������݂��邩�̊m�F�����܂��B
       * @param	_fileName			String		�t�@�C�������h���C�u���^�[�ӂ��܂߂Ďw�肷��B	( ��; C:/sample/sample.txt )
  	   * @return  	_returnCode		boolean		true�F���݂���A  false�F���݂��Ȃ��B
       */
      public static boolean  isExists( String _fileName ) {

    	  boolean _returnCode = false;
    	  if ( ( _fileName != null ) && ( !_fileName.equals( "" ) ) ) {
              if ( new File( _fileName ).exists() ) {
            	  _returnCode = true;
              }
    	  }

          return _returnCode;
     }

      /**
       * �����P�̃t�@�C���E�I�u�W�F�N�g�E�T�C�Y���A�����Q�𒴉߂��Ă��邩�̊m�F�����܂��B
       * @param	_fileName			String		�t�@�C�������h���C�u���^�[�ӂ��܂߂Ďw�肷��B	( ��; C:/sample/sample.txt )
       * @param	_thresholdValue	long			�t�@�C���T�C�Y��臒l�B
  	   * @return  	_returnCode		boolean		true�F臒l�𒴉߂��Ă��Ȃ��A  false�F臒l�𒴉߂��Ă���B
       */
      public static boolean  checkFileSize( String _fileName, long _thresholdValue ) {

    	  boolean _returnCode = false;
    	  if ( ( _fileName != null ) && ( !_fileName.equals( "" ) ) && ( _thresholdValue >= 0 ) ) {
              if ( new File( _fileName ).length() < _thresholdValue ) {
            	  _returnCode = true;
              }
    	  }

          return _returnCode;
     }

      /**
       * �A�v���P�[�V�����t�H���_�[����ݒ肵�܂��B
       * @param	_aplFolderName		String	�A�v���P�[�V�����t�H���_�[��
       */
      public static void setAplFolderName ( String _aplFolderName ) {
    	  aplFolder = _aplFolderName;
     }

}
