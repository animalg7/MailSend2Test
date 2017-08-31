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
     * �w�肳�ꂽ�f�B���N�g�����̃t�@�C���� ZIP �A�[�J�C�u���A�w�肳�ꂽ�p�X�ɍ쐬���܂��B
     * �f�t�H���g�����R�[�h�� Shift_JIS �ł��̂ŁA���{��t�@�C�������Ή��ł��܂��B
     * @param filePath String		 			���k��̏o�̓t�@�C�������t���p�X�Ŏw��B   		 ( ��: C:/sample.zip )
     * @param directory String		 		���k����f�B���N�g���[���A���́A�t�@�C�����B	 ( ��: C:/sample�A���́AC:/sample/sample.txt )
     * @param fileEncordingCode String	�t�@�C���̃G���R�[�f�B���O�E�R�[�h					 ( ��: Shift_JIS�AUTF-8�Ȃ� )
     * @param compressLevel String		���k���鎞�̃��x���̎w��B								 ( ��:5 )
     * @return �������� true:���k���� false:���k���s
     */
	public static boolean compressDirectory( String filePath, String directory,
			String fileEncordingCode, int compressLevel ) {

		File baseFile = new File( filePath );
        File file = new File( directory );
        ZipOutputStream outZip = null;

        try {
            // ZIP�t�@�C���o�̓I�u�W�F�N�g�쐬
            outZip = new ZipOutputStream( new FileOutputStream( baseFile ) );
            archive( outZip, baseFile, file, fileEncordingCode, compressLevel );
        }
        catch ( Exception e ) {
        	// �G���[���O�o��
            e.printStackTrace();
            // ZIP���k���s
            return false;
        }
        finally {
            // ZIP�G���g���N���[�Y
            if ( outZip != null ) {
                try {
                	outZip.closeEntry();
                	outZip.flush();
                	outZip.close();
                } catch ( Exception e ) {
                	// �G���[���O�o��
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    /**
     * �w�肳�ꂽ ArrayList �̃t�@�C���� ZIP �A�[�J�C�u���A�w�肳�ꂽ�p�X�ɍ쐬���܂��B
     * �f�t�H���g�����R�[�h�� Shift_JIS �ł��̂ŁA���{��t�@�C�������Ή��ł��܂��B
     * @param filePath							���k��̃t�@�C�������t���p�X�Ŏw��			( ��: C:/sample.zip )
     * @param fileList								���k����t�@�C�����X�g  							( ��: {C:/sample1.txt, C:/sample2.txt} )
     * @param compressLevel String		���k���鎞�̃��x���̎w��B 					( ��: 5 )
     * @return ��������							true:���k���� false:���k���s
     */
    public static boolean compressFileList(
    		String filePath, ArrayList<String> fileList, String enc, int compressLevel ) {

        ZipOutputStream outZip = null;
        File baseFile = new File( filePath );

        try {
            // ZIP�t�@�C���o�̓I�u�W�F�N�g�쐬
            outZip = new ZipOutputStream( new FileOutputStream( baseFile ) );
            // ���k�t�@�C�����X�g�̃t�@�C����A�����k
            for ( int i = 0 ; i < fileList.size() ; i++ ) {
                // �t�@�C���I�u�W�F�N�g�쐬
                File file = new File( ( String )fileList.get( i ) );
                archive( outZip, baseFile, file, file.getName(), enc, compressLevel );
            }
        }
        catch ( Exception e ) {
        	// �G���[���O�o��
            e.printStackTrace();
            // ZIP���k���s
            return false;
        }
        finally {

        	// ZIP�G���g���N���[�Y
            if ( outZip != null ) {
                try { outZip.closeEntry(); } catch ( Exception e ) {}
                try { outZip.flush();	   } catch ( Exception e ) {}
                try { outZip.close();	   } catch ( Exception e ) {}
            }
        }

        return true;
    }
    /**
     * �f�B���N�g�����k�̂��߂̍ċA����
     * @param outZip ZipOutputStream
     * @param baseFile File						�ۑ���t�@�C��
     * @param targetFile File					���k�������t�@�C��
     * @param fileEncordingCode String	�t�@�C���̃G���R�[�f�B���O�E�R�[�h   ( ��: Shift_JIS�AUTF-8�Ȃ� )
     * @param compressLevel String		���k���鎞�̃��x���̎w��B 			  ( ��: 5 )
     */
    private static void archive( ZipOutputStream outZip,
    		File baseFile, File targetFile, String enc, int compressLevel ) {

    	// targetFile���f�B���N�g���[��������A���̃f�B���N�g���[���̃t�@�C����f�B���N�g���[�����k����B
    	if ( targetFile.isDirectory() ) {
    		File[] files = targetFile.listFiles();
            for ( File f : files ) {
                if ( f.isDirectory() ) {
                    archive( outZip, baseFile, f, enc, compressLevel );
                }
                else {
                    if ( !f.getAbsoluteFile().equals( baseFile ) ) {
                        // ���k����
                        archive( outZip, baseFile, f,
                        		f.getAbsolutePath().replace(baseFile.getParent(), "" ).substring( 1 ), enc, compressLevel );
                    }
                }
            }
        }
    	else {
        	// targetFile���t�@�C���̏ꍇ�A���̃t�@�C�������k����B
            archive( outZip, baseFile, targetFile,
            		targetFile.getAbsolutePath().replace( baseFile.getParent(), "" ).substring( 1 ), enc, compressLevel );
        }

    }

    /**
     * ���k����
     * @param outZip ZipOutputStream
     * @param baseFile File						�ۑ���t�@�C��
     * @param targetFile File					���k�������t�@�C��
     * @parma entryName String				�ۑ��t�@�C����
     * @param fileEncordingCode String	�t�@�C���̃G���R�[�f�B���O�E�R�[�h   ( ��: Shift_JIS�AUTF-8�Ȃ� )
     * @param compressLevel String		���k���鎞�̃��x���̎w��B				  ( ��: 5 )
     */
    private static boolean archive( ZipOutputStream outZip, File baseFile,
    	File targetFile, String entryName, String enc, int compressLevel ) {

    	// ���k���x���ݒ�
        outZip.setLevel( compressLevel );
        // �����R�[�h���w��
        outZip.setEncoding( enc );
        try {
            // ZIP�G���g���쐬
            outZip.putNextEntry( new ZipEntry( entryName ) );
            // ���k�t�@�C���ǂݍ��݃X�g���[���擾
            BufferedInputStream in = new BufferedInputStream( new FileInputStream( targetFile ) );
            // ���k�t�@�C����ZIP�t�@�C���ɏo��
            int readSize = 0;
            byte buffer[] = new byte[ 1024 ]; // �ǂݍ��݃o�b�t�@
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
     * �f�B���N�g���̒���zip�Ɉ��k����B<br>
     * �w�肳�ꂽ�f�B���N�g�����̃t�@�C���� ZIP �A�[�J�C�u���A�w�肳�ꂽ�p�X�ɍ쐬���܂��B
     * �f�t�H���g�����R�[�h�� Shift_JIS �ł��̂ŁA���{��t�@�C�������Ή��ł��܂��B
     * @param srcDir �Ώۃf�B���N�g���|
     * @param dest �o�͐�zip�t�@�C��
     * @param fileEncordingCode String	�t�@�C���̃G���R�[�f�B���O�E�R�[�h			 ( ��: Shift_JIS�AUTF-8�Ȃ� )
     * @param compressLevel String		���k���鎞�̃��x���̎w��B						 ( ��:5 )
     */
	public static boolean archiveZip( File srcDir, File dest, String fileEncordingCode, int compressLevel ) {

    	ZipOutputStream zos = null;
    	boolean process = true;
        try {
        	// System.out.println( System.getProperty( "user.name" ) );
        	zos = new ZipOutputStream( dest );
            zos.setEncoding( fileEncordingCode );
        	// ���k���x���ݒ�
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
     * �f�B���N�g���̒���zip�Ɉ��k����B<br>
     * �f�B���N�g������File��zip���J����root�ɂ���
     * @param srcDir �Ώۃf�B���N�g��
     * @param dest �o�͐�zip�t�@�C��
     */
    private static void addEntry( ZipOutputStream zos, File[] files, String rootPath ) {

        for ( File file : files ) {
            //�f�B���N�g���̏ꍇ�ċA��������
            if ( file.isDirectory() ){
                addEntry( zos, file.listFiles(), rootPath );
            }
            else {
                BufferedInputStream input = null;
                try {
                	input = new BufferedInputStream( new FileInputStream( file ) );
                    //Entry�̖���
                    String entryName = file.getAbsolutePath().replace( rootPath, "" ).substring( 1 );
                    zos.putNextEntry( new ZipEntry( entryName ) );
                    //��������
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
