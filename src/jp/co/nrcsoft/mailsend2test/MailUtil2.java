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
 * メール送信用クラス
 */
public class MailUtil2 {

	/** Log4j2 Logger Instance */
	private static Logger log = LogManager.getLogger( MailUtil2.class );

	/** Application Folder Name  */
	private static String aplFolder = "";

	/**
	 * メールを送信処理。
	 * @param     _fromAddress			String					送信元メールアドレス
	 * @param     _toAddress				InternetAddress[]	送信先メールアドレス（TO複数指定可）
	 * @param     _ccAddress				InternetAddress[]	送信先メールアドレス（CC複数指定可）
	 * @param     _bccAddress				InternetAddress[]	送信先メールアドレス（BCC複数指定可）
	 * @param     _returnToAddress		String 					不到達メールのreturn先のメールアドレス
	 * @param     _subjectStr 				String					件名
	 * @param     _msgStr					String					送信本文
	 * @param     _smtpHost				String					SMTPのサーバー名
	 * @param     _pop3Hst					String					pop3のサーバー名
	 * @param     _pop3id					String					pop3の認証時のユーザーID
	 * @param     _pop3pwd				String					pop3の認証時のパスワード
	 * @param     _smtpPort				String					SMTP送信ポート番号
	 * @param     _debug						boolean 				デバッグモード
	 * @param     _subjectEncording		String					メールの件名の文字コード
	 * @param     _textEncording			String					メールの本文の文字コード
	 * @param	_pathfolder  	    		String 					メール添付ファイルをドライブレターを含むフォルダー名
	 * @param	_attachedFiles			String					メール添付ファイル名（フル・パス複数指定可）
	 * @param	_authenticationMode	boolean 				認証モード
	 * @param	_processMode			boolean 				プロセンスモード
	 * @param	_attachmentFileSize	long		 				メール送信の際の添付ファイルの総合計サイズの「閾値」
	 * @return    _retValue					boolean 				メール送信結果(true:成功、false:失敗)
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

		/* 戻り値 */
		Boolean retValue = true;
		try {
			Properties props = new Properties();
			// 「認証モード」確認
			if ( _authenticationMode ) {
				props.put( "mail.smtp.auth", "true" );
			}
            props.put( "mail.smtp.host", _smtpHost );
            props.put( "mail.smtp.port", _smtpPort );
            props.put( "mail.smtp.from", _returnToAddress );
            // デバッグモードの指定
            if ( _debug ) {
            	props.put( "mail.debug", "true" );
            }
            // メールセッション確立
            Session session = null;
			if ( _authenticationMode ) {
	            	session = Session.getDefaultInstance( props, new javax.mail.Authenticator() {
	            			protected PasswordAuthentication getPasswordAuthentication() {
	            				return new PasswordAuthentication( _pop3id, _pop3pwd );
	            			}
	            	} );
	            	// Debugモードの指定
	                session.setDebug( _debug );
	                /*pop before smtp対策start */
	                Store store = session.getStore( "pop3" );
	                store.connect( _pop3Hst, _pop3id, _pop3pwd );
	                /*pop before smtp対策end */
			}
			else {
				session = Session.getDefaultInstance( props, null );
			}
			MimeMessage msg = new MimeMessage( session );

			// 宛先有無の確認
            if ( _toAddress != null ) {
            	// 送信元（FROM）
                msg.setFrom( new InternetAddress( _fromAddress ) );
            	// 送信先（TO）
            	msg.setRecipients( Message.RecipientType.TO, _toAddress );
            	// 送信先（写し（CC））
                if ( _ccAddress != null ) {
                	msg.setRecipients( Message.RecipientType.CC, _ccAddress );
                }
    			// 送信先（写し（BCC））
                if ( _ccAddress != null ) {
                	msg.setRecipients( Message.RecipientType.BCC, _bccAddress );
                }
    			// メール送信日時
                msg.setSentDate( new Date() );
                // 件名
               	msg.setSubject( _subjectStr , _subjectEncording );
               	// 添付ファイル処理
            	String _attachedFullPathFiles = MailUtil2.getAttachedFileNamesWithFullPath( _pathfolder, _attachedFiles );
            	// 添付ファイル有無の確認
                if  (  ( _attachedFullPathFiles != null ) && ( !_attachedFullPathFiles.equals( "" ) ) ) {
                	// 添付ファイルの有の場合
                    MimeBodyPart mbp1 = new MimeBodyPart();
                    MimeBodyPart mbps = new MimeBodyPart();
                    Multipart mp = new MimeMultipart();
                    // メールの本文
                    mbp1.setText( setNewlineCharacter( _msgStr ), _textEncording );

                    // 添付ファイルサイズの総合計ファイルサイズを確認します。
                    String[] _attachedFileNames = MailUtil2.getAttachedFileNames( _attachedFullPathFiles );
                    long _totalFileSize = 0;
                    for ( String _attachFile : _attachedFileNames ) {
                    	_totalFileSize = _totalFileSize + new File( _attachFile ).length();
                    }
                    if ( _attachmentFileSize < _totalFileSize ) {
                		  log.error( MarkerManager.getMarker( "ERROR" ),
                  				  "E51({})：添付ファイルの「総合計ファイルサイズ（{}）」が、「閾値（{}）」を超過している為、処理を中止しました。", aplFolder, _totalFileSize, _attachmentFileSize );
                          System.exit( 16 ); // 処理中止！！！
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
                	// 添付ファイルの無の場合は、メールの本文のみ設定。
                	msg.setText( setNewlineCharacter( _msgStr ), _textEncording );
                }
                //  プロセスモードの確認（trueなら実際に送信まで実行します。）
                if ( _processMode ) {
                    // メールの送信
                	Transport.send( msg );
                }
            }
            else {
      		  log.error( MarkerManager.getMarker( "ERROR" ),
      				  "E52({})：「宛先アドレス（TO）」カードが一つもありませんでしたので、処理を中止しました。", aplFolder );
            	retValue = false;
            }
		} catch ( Exception e ) {
    		  log.error( MarkerManager.getMarker( "ERROR" ),
      				  "99E({})：原因不明のエラーが発生しました、以下のStackTraceを確認して下さい。", aplFolder, e );
//			e.printStackTrace();
			retValue = false;
		}

		return retValue;
	}

      /**
       * 区切り文字がカンマの複数メールアドレスの文字列をInternetAddress型配列で、取得する。
       * この際に、重複したメールアドレスが存在した場合は、片方のメールアドレスの文字列は除外する。
       * @param	_propAddresses		String					区切り文字がカンマの複数メールアドレスの文字列を指定する。
       * @param	_cardCC				String					「メール送信制御カード」内のカードコードを指定する。
  	   * @return  	_address				InternetAddress[]	InternetAddress型配列（重複除外されたメールアドレス）。
       * @throws	AddressException								間違った形式のメールアドレスが検知された時にthrowされる。
       */
      public static InternetAddress[] getMailAddress ( String _propAddresses, String _cardCC ) throws AddressException {

    	  InternetAddress _address[] = null;
          if ( ( _propAddresses != null ) && ( !_propAddresses.equals( "" ) ) ) {
        	  String[] _addresses = _propAddresses.split( "," );

        	  // 「メール送信制御カード」で指定された順番で、重複したメールアドレスを除外する。
        	  List<String> _addressLists =Arrays.asList( _addresses );
        	  List<String> _addressLists2 = new ArrayList<>( new LinkedHashSet<String>( _addressLists ) );
        	  String[] _addresses2 = ( String[] ) _addressLists2.toArray( new String[ _addressLists2.size() ] );

              int count = _addresses2.length;
        	  int count2 = 0;
              for ( int i = 0; i < count; i++ ) {
            	  _addresses2[ i ] = _addresses2[ i ].trim() ;
            	  // メールアドレスの確認（メールアドレスに相応しい数を求めています）
            	  if ( MailUtil2.checkMailAddressByRegularExpression( _addresses2[ i ] ) ) {
            		  count2++;
            	  }
            	  else {
                      log.warn( "51W({})：「メールアドレス」に相応しくないアドレスが見つかりました、", aplFolder );
                      log.warn( "52W({})：当該メールアドレス（{}）は除外されますが、処理は続行します。（カード：{}）", aplFolder, _addresses2[ i ], _cardCC );
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
       * 引数のメールアドレス文字列がメールアドレスに相応しいかの確認をします。
       * @param	_mailAddressStr	String		メールアドレス確認文字列を指定する。
  	   * @return  	_result					boolean		true：合格、  false：不合格。
       */
      public  static boolean checkMailAddressByRegularExpression( String _mailAddressStr ) {

    	  boolean _result = false;
    	  // 正規表現文字列の生成
    	  String aText = "[\\w!#%&'/=~`\\*\\+\\?\\{\\}\\^\\$\\-\\|]";
    	  String dotAtom = aText + "+" + "(\\." + aText + "+)*";
    	  String regularExpression = "^" + dotAtom + "@" + dotAtom + "$";

    	  if ( Pattern.compile( regularExpression ).matcher( _mailAddressStr ).find() ) {
    		  _result = true;
    	  }

    	  return _result;
      }

      /**
       * 引数の添付ファイル文字列の物理オブジェクト（添付ファイル）が存在する
       * かしないかの確認をして、存在しない場合は、本処理を中止します。
       * 添付複数あった場合、一つのファイルでも存在しない場合は、本処理を中止します。
       * @param	_str		String		カンマ区切りの添付ファイル文字列
  	   * @return  	_return	boolean		true：全ファイル存在する、false：何れかのファイルが存在しない。
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
                  			"53E({})：指定された「メール送信制御カード」ファイル内の添付ファイル（{}）が存在しませんので、処理を中止します。", aplFolder, _attachFile );
            		  return false;
            	  }
              }
          }

          return _return;
     }

      /**
       * カンマ区切りの添付ファイル文字列を文字String配列型で返します。
       * @param	_str							String		カンマ区切りの文字列
  	   * @return  	_attachedFileNames	String[]		配列の文字列
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
       * ドライブレターを含むカンマ区切りの添付ファイル文字String列を返します。
       * @param	_folderName				String		フォルダー名の文字列
       * @param	_str							String		ファイル名のカンマ区切りの文字列
  	   * @return  	_attachedFileNames	String		配列の文字列
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
       * キーワードに対するArgumentを返します。
       * キーワードに一致しない場合は、nullを返します。
       * @param	_stringWithKeyWord	String	検索される対象文字列
       * @param	_keyWord					String	検索キーワード文字列
  	   * @return  	_argument 				String	キーワードに対するArgumet文字列（5桁目以降）
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
       * 「メール送信制御カード」を読取りメール制御情報を抽出し、HashMapへ格納し返します。
       * @param	_mailSendControllCards	String	「メール送信制御カード」のドライブレターからのフル・パスのファイル名を指定。
  	   * @return  	_argument		 			String	キーワードに対するArgumet文字列
       * @throws	FileNotFoundException				引数の「メール送信制御カード」が見つからない場合のException。
       * 				Exception									その他の原因不明のException。
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
							// メール送信元は、１ユーザー必須の為、２件目以降は存在していても、無視（スキップ）する。
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
							// メールの件名は、１行必須の為、２行目以降は存在していても、無視（スキップ）する。
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
							// 「メール送信制御カード」の送信先サーバーの「絶対パス名」は、１行必須の為、２行目以降は存在していても、無視（スキップ）する。
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
    				  "54E({})：指定した「メール送信制御カード」（{}）が存在しませんので、処理を中止します。", aplFolder, _mailSendControllCards, e );
 //    		  e.printStackTrace();
              System.exit( 16 ); // 処理中止！！！
    	  } catch ( Exception e2 ) {
    		  log.error( MarkerManager.getMarker( "ERROR" ),
    				  "99E({})：原因不明のエラーが発生しました、以下のStackTraceを確認して下さい。", aplFolder, e2 );
 //   		  e.printStackTrace();
              System.exit( 16 ); // 処理中止！！！
    	  }

		return map;
    }

      /**
       * プラットフォーム依存の改行コードを変換します。
       * @param		_str		String	変換したい文字列
  	   * @return  	_returnStr	String	変換後の文字列
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
       * ※指定した日付フォーマット記号「yyyyMMdd_HHmmss」などの日時形式文字列を返します。
       * @param	_format		String	日付フォーマット記号			例）yyyyMMdd_HHmmss
  	   * @return  	_str			String	求めた文字列を返します。	例）20170911_121530
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
       * ※ 親ディレクトリーの配下にサブディレクトリーを作成します。
       * @param	_tragetPath			String	作成する親ディレクトリー名。			( 例: C:/sample )
  	   * @return  	_returnCode			boolean	true：作成成功、  false：作成失敗。
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
       * ※ 指定のディレクトリー内のファイルのみを全て削除します（指定のディレクトリー内のディレクトリーは、無視します）。
       * @param	_targetDirectoryPath			String	作成する親ディレクトリー名。				( 例: C:/sample )
  	   * @return  	_returnCode						boolean	true：削除成功、  false：削除失敗。
       */
      public static boolean  deleteFiles( String _targetDirectoryPath ) {

    	  boolean _returnCode = false;
    	  boolean _returnChkCode = true;
    	  if ( ( _targetDirectoryPath !=null ) && ( !_targetDirectoryPath.equals( "" ) ) ) {

    		  File td = new File( _targetDirectoryPath );
    		  // 引数で指定したディレクトリー又はファイルの存在チェックし、存在しない場合は、falseをreturn。
    		  if ( !td.exists() ) {
    			  return _returnCode;
    		  }
    		  // 指定した引数がディレクトリーの場合
    		  if ( td.isDirectory() ) {
    			  // ディレクトリ内の一覧を取得
    			  File[] files = td.listFiles();
    			  // ディレクトリ内のファイルのみを全て削除する。
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
       * ※必ず以下の４つの引数を指定。
       * @param	_zipTargetDirectoryPath		String	圧縮するディレクトリー名、又は、ファイル名。	( 例; C:/sample、又は、C:/sample/sample.txt )
       * @param	_directoryPathBeforeZip		String	圧縮後の出力ファイル名をフルパスで指定。		( 例: C:/sample.zip )
       * @param	_fileEncordingCode				String	圧縮時のファイルエンコーディングコードを指定。 ( 例: Shift_JIS、UTF-8など )
       * @param	_compressLevel					int			圧縮時の圧縮レベルを指定。								 ( 例:5 )
  	   * @return  	_returnCode						boolean	true：圧縮処理成功、  false：圧縮処理失敗。
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
       * 当該ファイルが存在するかの確認をします。
       * @param	_fileName			String		ファイル名をドライブレターふを含めて指定する。	( 例; C:/sample/sample.txt )
  	   * @return  	_returnCode		boolean		true：存在する、  false：存在しない。
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
       * 引数１のファイル・オブジェクト・サイズが、引数２を超過しているかの確認をします。
       * @param	_fileName			String		ファイル名をドライブレターふを含めて指定する。	( 例; C:/sample/sample.txt )
       * @param	_thresholdValue	long			ファイルサイズの閾値。
  	   * @return  	_returnCode		boolean		true：閾値を超過していない、  false：閾値を超過している。
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
       * アプリケーションフォルダー名を設定します。
       * @param	_aplFolderName		String	アプリケーションフォルダー名
       */
      public static void setAplFolderName ( String _aplFolderName ) {
    	  aplFolder = _aplFolderName;
     }

}
