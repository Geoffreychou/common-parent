package xin.zero2one.common.utils;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * Created by zhoujundong on 2018/9/28.
 */
public class ZipUtils {

    private static final int BUFFER_SIZE = 1024 * 10;

    /**
     * 将指定的文件压缩至指定路径
     * @param files  需要压缩的文件
     * @param zipFilePath 压缩后文件的路径
     * @throws Exception
     */
    public static void compressFiles2Zip(File[] files, String zipFilePath) throws Exception {
        if(files != null && files.length > 0) {
            if(isEndsWithZip(zipFilePath)) {
                ZipArchiveOutputStream zaos = null;
                try {
                    File zipFile = new File(zipFilePath);
                    zaos = new ZipArchiveOutputStream(zipFile);
                    //Use Zip64 extensions for all entries where they are required
                    zaos.setUseZip64(Zip64Mode.AsNeeded);

                    //将每个文件用ZipArchiveEntry封装
                    //再用ZipArchiveOutputStream写到压缩文件中
                    for(File file : files) {
                        if(file != null) {
                            ZipArchiveEntry zipArchiveEntry  = new ZipArchiveEntry(file,file.getName());
                            zaos.putArchiveEntry(zipArchiveEntry);
                            InputStream is = null;
                            try {
                                is = new BufferedInputStream(new FileInputStream(file));
                                byte[] buffer = new byte[BUFFER_SIZE];
                                int len = -1;
                                while((len = is.read(buffer)) != -1) {
                                    //把缓冲区的字节写入到ZipArchiveEntry
                                    zaos.write(buffer, 0, len);
                                }
                                //Writes all necessary data for this entry.
                                zaos.closeArchiveEntry();
                            }catch(Exception e) {
                                throw new Exception(e);
                            }finally {
                                if(is != null)
                                    is.close();
                            }

                        }
                    }
                    zaos.finish();
                }catch(Exception e){
                    throw new Exception(e);
                }finally {
                    try {
                        if(zaos != null) {
                            zaos.close();
                        }
                    } catch (IOException e) {
                        throw new Exception(e);
                    }
                }
            }
        }
    }


    /**
     * 将zip文件解压到指定的文件夹
     * @param zipFilePath zip文件所在路径
     * @param saveFileDir 解压后文件存放路径
     * @throws Exception
     */
    public static void decompressZip(String zipFilePath,String saveFileDir) throws Exception {
        if(isEndsWithZip(zipFilePath)) {
            File file = new File(zipFilePath);
            if(file.exists()) {
                InputStream is = null;
                //can read Zip archives
                ZipArchiveInputStream zais = null;
                try {
                    is = new FileInputStream(file);
                    zais = new ZipArchiveInputStream(is);
                    ArchiveEntry archiveEntry = null;
                    //把zip包中的每个文件读取出来
                    //然后把文件写到指定的文件夹
                    while(null != (archiveEntry = zais.getNextEntry())) {
                        //获取文件名
                        String entryFileName = archiveEntry.getName();
                        //构造解压出来的文件存放路径
                        String entryFilePath = saveFileDir + entryFileName;
                        byte[] content = new byte[(int) archiveEntry.getSize()];
                        zais.read(content);
                        OutputStream os = null;
                        try {
                            //把解压出来的文件写到指定路径
                            File entryFile = new File(entryFilePath);
                            os = new BufferedOutputStream(new FileOutputStream(entryFile));
                            os.write(content);
                        }catch(IOException e) {
                            throw e;
                        }finally {
                            if(os != null) {
                                os.flush();
                                os.close();
                            }
                        }

                    }
                }catch(Exception e) {
                    throw new Exception(e);
                }finally {
                    try {
                        if(zais != null) {
                            zais.close();
                        }
                        if(is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                        throw new Exception(e);
                    }
                }
            }
        }
    }


    private static boolean isEndsWithZip(String fileName) {
        boolean flag = false;
        if(!StringUtils.isBlank(fileName)) {
            if(fileName.endsWith(".ZIP")||fileName.endsWith(".zip")){
                flag = true;
            }
        }
        return flag;
    }

}
