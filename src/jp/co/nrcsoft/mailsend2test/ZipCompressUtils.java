package jp.co.nrcsoft.mailsend2test;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class ZipCompressUtils {

	/**
     * 指定されたディレクトリ内のファイルを ZIP アーカイブし、指定されたパスに作成します。
     * デフォルト文字コードは Shift_JIS ですので、日本語ファイル名も対応できます。
     * @param filePath String		 			圧縮後の出力ファイル名をフルパスで指定。   		 ( 例: C:/sample.zip )
     * @param directory String		 		圧縮するディレクトリー名、又は、ファイル名。	 ( 例: C:/sample、又は、C:/sample/sample.txt )
     * @param fileEncordingCode String	ファイルのエンコーディング・コード					 ( 例: Shift_JIS、UTF-8など )
     * @param compressLevel String		圧縮する時のレベルの指定。								 ( 例:5 )
     * @return 処理結果 true:圧縮成功 false:圧縮失敗
     */
	public static boolean compressDirectory( String filePath, String directory,
			String fileEncordingCode, int compressLevel ) {

		File baseFile = new File( filePath );
        File file = new File( directory );
        ZipOutputStream outZip = null;

        try {
            // ZIPファイル出力オブジェクト作成
            outZip = new ZipOutputStream( new FileOutputStream( baseFile ) );
            archive( outZip, baseFile, file, fileEncordingCode, compressLevel );
        }
        catch ( Exception e ) {
        	// エラーログ出力
            e.printStackTrace();
            // ZIP圧縮失敗
            return false;
        }
        finally {
            // ZIPエントリクローズ
            if ( outZip != null ) {
                try {
                	outZip.closeEntry();
                	outZip.flush();
                	outZip.close();
                } catch ( Exception e ) {
                	// エラーログ出力
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    /**
     * 指定された ArrayList のファイルを ZIP アーカイブし、指定されたパスに作成します。
     * デフォルト文字コードは Shift_JIS ですので、日本語ファイル名も対応できます。
     * @param filePath							圧縮後のファイル名をフルパスで指定			( 例: C:/sample.zip )
     * @param fileList								圧縮するファイルリスト  							( 例: {C:/sample1.txt, C:/sample2.txt} )
     * @param compressLevel String		圧縮する時のレベルの指定。 					( 例: 5 )
     * @return 処理結果							true:圧縮成功 false:圧縮失敗
     */
    public static boolean compressFileList(
    		String filePath, ArrayList<String> fileList, String enc, int compressLevel ) {

        ZipOutputStream outZip = null;
        File baseFile = new File( filePath );

        try {
            // ZIPファイル出力オブジェクト作成
            outZip = new ZipOutputStream( new FileOutputStream( baseFile ) );
            // 圧縮ファイルリストのファイルを連続圧縮
            for ( int i = 0 ; i < fileList.size() ; i++ ) {
                // ファイルオブジェクト作成
                File file = new File( ( String )fileList.get( i ) );
                archive( outZip, baseFile, file, file.getName(), enc, compressLevel );
            }
        }
        catch ( Exception e ) {
        	// エラーログ出力
            e.printStackTrace();
            // ZIP圧縮失敗
            return false;
        }
        finally {

        	// ZIPエントリクローズ
            if ( outZip != null ) {
                try { outZip.closeEntry(); } catch ( Exception e ) {}
                try { outZip.flush();	   } catch ( Exception e ) {}
                try { outZip.close();	   } catch ( Exception e ) {}
            }
        }

        return true;
    }
    /**
     * ディレクトリ圧縮のための再帰処理
     * @param outZip ZipOutputStream
     * @param baseFile File						保存先ファイル
     * @param targetFile File					圧縮したいファイル
     * @param fileEncordingCode String	ファイルのエンコーディング・コード   ( 例: Shift_JIS、UTF-8など )
     * @param compressLevel String		圧縮する時のレベルの指定。 			  ( 例: 5 )
     */
    private static void archive( ZipOutputStream outZip,
    		File baseFile, File targetFile, String enc, int compressLevel ) {

    	// targetFileがディレクトリーだったら、そのディレクトリー内のファイルやディレクトリーを圧縮する。
    	if ( targetFile.isDirectory() ) {
    		File[] files = targetFile.listFiles();
            for ( File f : files ) {
                if ( f.isDirectory() ) {
                    archive( outZip, baseFile, f, enc, compressLevel );
                }
                else {
                    if ( !f.getAbsoluteFile().equals( baseFile ) ) {
                        // 圧縮処理
                        archive( outZip, baseFile, f,
                        		f.getAbsolutePath().replace(baseFile.getParent(), "" ).substring( 1 ), enc, compressLevel );
                    }
                }
            }
        }
    	else {
        	// targetFileがファイルの場合、そのファイルを圧縮する。
            archive( outZip, baseFile, targetFile,
            		targetFile.getAbsolutePath().replace( baseFile.getParent(), "" ).substring( 1 ), enc, compressLevel );
        }

    }

    /**
     * 圧縮処理
     * @param outZip ZipOutputStream
     * @param baseFile File						保存先ファイル
     * @param targetFile File					圧縮したいファイル
     * @parma entryName String				保存ファイル名
     * @param fileEncordingCode String	ファイルのエンコーディング・コード   ( 例: Shift_JIS、UTF-8など )
     * @param compressLevel String		圧縮する時のレベルの指定。				  ( 例: 5 )
     */
    private static boolean archive( ZipOutputStream outZip, File baseFile,
    	File targetFile, String entryName, String enc, int compressLevel ) {

    	// 圧縮レベル設定
        outZip.setLevel( compressLevel );
        // 文字コードを指定
        outZip.setEncoding( enc );
        try {
            // ZIPエントリ作成
            outZip.putNextEntry( new ZipEntry( entryName ) );
            // 圧縮ファイル読み込みストリーム取得
            BufferedInputStream in = new BufferedInputStream( new FileInputStream( targetFile ) );
            // 圧縮ファイルをZIPファイルに出力
            int readSize = 0;
            byte buffer[] = new byte[ 1024 ]; // 読み込みバッファ
            while ( ( readSize = in.read( buffer, 0, buffer.length ) ) != -1 ) {
                outZip.write( buffer, 0, readSize );
            }

            in.close();
            outZip.closeEntry();
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * ディレクトリの中をzipに圧縮する。<br>
     * 指定されたディレクトリ内のファイルを ZIP アーカイブし、指定されたパスに作成します。
     * デフォルト文字コードは Shift_JIS ですので、日本語ファイル名も対応できます。
     * @param srcDir 対象ディレクトリ－
     * @param dest 出力先zipファイル
     * @param fileEncordingCode String	ファイルのエンコーディング・コード			 ( 例: Shift_JIS、UTF-8など )
     * @param compressLevel String		圧縮する時のレベルの指定。						 ( 例:5 )
     */
	public static boolean archiveZip( File srcDir, File dest, String fileEncordingCode, int compressLevel ) {

    	ZipOutputStream zos = null;
    	boolean process = true;
        try {
        	// System.out.println( System.getProperty( "user.name" ) );
        	zos = new ZipOutputStream( dest );
            zos.setEncoding( fileEncordingCode );
        	// 圧縮レベル設定
            zos.setLevel( compressLevel );
            addEntry( zos, srcDir.listFiles(), srcDir.getAbsolutePath() );

            return process;

        }
        catch ( IOException e ) {
            e.printStackTrace();
            return false;
        }
        finally {
            if ( zos != null ) {
                try {
                    zos.close();
                }
                catch ( IOException e ) {

                }
            }
        }
    }

    /**
     * ディレクトリの中をzipに圧縮する。<br>
     * ディレクトリ内のFileがzipを開くとrootにある
     * @param srcDir 対象ディレクトリ
     * @param dest 出力先zipファイル
     */
    private static void addEntry( ZipOutputStream zos, File[] files, String rootPath ) {

        for ( File file : files ) {
            //ディレクトリの場合再帰処理する
            if ( file.isDirectory() ){
                addEntry( zos, file.listFiles(), rootPath );
            }
            else {
                BufferedInputStream input = null;
                try {
                	input = new BufferedInputStream( new FileInputStream( file ) );
                    //Entryの名称
                    String entryName = file.getAbsolutePath().replace( rootPath, "" ).substring( 1 );
                    zos.putNextEntry( new ZipEntry( entryName ) );
                    //書き込み
                    byte[] buf = new byte[ 1024 ];
                    for ( ;; ) {
                        int len = input.read( buf );
                        if ( len < 0 ) {
                        	break;
                        }
                        zos.write( buf, 0, len );
                    }
                    zos.closeEntry();

                }
                catch ( FileNotFoundException e ) {
                    e.printStackTrace();
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
                finally {
                    if ( input != null ) {
                        try {
                            input.close();
                        }
                        catch ( IOException e ) {
                        }
                    }
                }
            }
        }
    }

}
