package com.njust.wanyuan_display.util;

import android.content.Context;

import com.njust.wanyuan_display.config.Resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 读res下的文件内容
 */
public class FileUtil {

	/**
	 * 读文件
	 */
	public static String readFile(File file) {
		FileInputStream fis = null;
		StringBuffer data = new StringBuffer();
		try {
			fis = new FileInputStream(file);
			int length = fis.available();
			byte[] buffer = new byte[length];
			fis.read(buffer);
			data.append(new String(buffer));
			
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) fis.close();// 关闭资源
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data.toString();
	}
	/**
	 * 复制数据库文件到SD卡中
	 */
	public static String copyDBToSDcrad(Context context, String filename, String db_name) {
		String fileName = "";
		try {
			String dbtime = DateUtil.getDBTime();
			String oldPath = "data/data/" + context.getPackageName() + "/databases/" + db_name;
			String newPath = Resources.dbURL() + filename + "." + dbtime + ".db";

			copyFile(oldPath, newPath);
			
			fileName = filename + "." + dbtime + ".db";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}
	/**
	 * 复制数据库到应用目录下
	 */
	public static boolean copyDBToData(Context context, String sourceName, String destFileName) {
		boolean success = false;
		try {
			String newPath = "data/data/" + context.getPackageName() + "/databases/" + destFileName;
			File newfile = new File(newPath);
			if ( !newfile.exists() ) {
				copyEmbassy2Databases(context, sourceName, newfile);
			}

			success = true;
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}
	/**
	 * 复制asseet当中的文件到SD卡
	 */
	public static boolean copyToStorage(Context context, String sourceName, String newPath) {
		boolean success = false;
		try {
			File newfile = new File(newPath);
			if ( !newfile.exists() ) {
				copyEmbassy2Databases(context, sourceName, newfile);
			}

			success = true;
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}
	/**
	 * 复制单个文件
	 * @param oldPath String 原文件路径
	 * @param newPath String 复制后路径
	 * @return boolean
	 */
	@SuppressWarnings("resource")
	public static void copyFile(String oldPath, String newPath) {
		try {
			@SuppressWarnings("unused")
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			File newfile = new File(newPath);
			if (!newfile.exists()) {
				newfile.createNewFile();
			}
			
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将asseet当中的文件拷到程序目录
	 */
	public static void copyEmbassy2Databases(Context context, String fileName, File file){
		if ( !file.getParentFile().exists() ) {// 没有上级目录则创建
			file.getParentFile().mkdirs();
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			byte[] buff = new byte[1024];
			int len = 0;
			in = context.getAssets().open(fileName);
			while ((len = in.read(buff)) > 0) {
				out.write(buff, 0, len);
			}
			out.flush();
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {out.close();}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 读日志文件夹下所有的文件
	 */
	public static List<File> loadLogFile(String searchdate) {
		List<File> fileList = new ArrayList<File>();
		String logPath = Resources.logURL();
		try {
			File dirFile = new File(logPath);
			if (dirFile.isDirectory()) {
                String[] filelist = dirFile.list();
                for (int i = 0; i < filelist.length; i++) {
                	if ( filelist[i].startsWith(searchdate) ) {
                		File file = new File(logPath + filelist[i]);
						fileList.add(file);
					}
                }
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
		return fileList;
	}
	/**
	 * 读文件夹下所有的文件
	 */
	public static List<File> getAllFiles(String dirPath) {
		List<File> fileList = new ArrayList<File>();
		try {
			File f = new File(dirPath);
			if (!f.exists()) {//判断路径是否存在
				return null;
			}
			File[] files = f.listFiles();
			if(files==null){//判断权限
				return null;
			}
			for (File _file : files) {//遍历目录
				if(_file.isFile()){
//					String _name=_file.getName();
//					String filePath = _file.getAbsolutePath();//获取文件路径
//					String fileName = _file.getName().substring(0,_name.length()-4);//获取文件名
					fileList.add(_file);
				} else if(_file.isDirectory()){//查询子目录
					getAllFiles(_file.getAbsolutePath());
				}
			}
			return fileList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileList;
	}
	/**
	 * 读文件夹下所有的文件的绝对路径
	 */
	public static List<String> getAdvFiles(String path) {
		List<String> fileList = new ArrayList<String>();
		try {
			File dirFile = new File(path);
			if (!dirFile.exists()) {//判断路径是否存在
				dirFile.createNewFile();
				return null;
			}
			File[] files = dirFile.listFiles();
			if(files==null){//判断权限
				return null;
			}
			for (File _file : files) {//遍历目录
				if(_file.isFile()){
					String _name=_file.getName();
					String filePath = _file.getAbsolutePath();//获取文件路径
					String fileName = _file.getName().substring(0,_name.length()-4);//获取文件名
					fileList.add(_file.getAbsolutePath());
				} else if(_file.isDirectory()){//查询子目录
					getAdvFiles(_file.getAbsolutePath());
				}
			}
			return fileList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileList;
	}
	/**
	 * 删除文件夹下所有的文件
	 */
	public static void deletAllAdvFiles(String path) {
		try {
			File dirFile = new File(path);
			if (!dirFile.exists()) {//判断路径是否存在
				dirFile.createNewFile();
			}
			File[] files = dirFile.listFiles();
			if(files!=null){
				for (File _file : files) {//遍历目录
					_file.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
