package com.njust.wanyuan_display.manager;

import android.content.Context;
import android.os.Environment;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.FileNotFoundException;


/**
 * 商品图片下载管理器
 */
public class DownloadImageManager implements Runnable{

	private final Logger log = Logger.getLogger(DownloadImageManager.class);

	private String imageName;
	private String url;
	private String saveDir;
	private Context context;
	private ImageDownLoadCallBack callBack;
	private File currentFile;
	public DownloadImageManager(Context context, String imageName, String url, String saveDir, ImageDownLoadCallBack callBack) {
		this.imageName = imageName;
		this.url = url;
		this.saveDir = saveDir;
		this.callBack = callBack;
		this.context = context;
	}

	@Override
	public void run() {
		File file = null;
		Bitmap bitmap = null;
		try {
//            file = Glide.with(context)
//                    .load(url)
//                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                    .get();
			bitmap = Glide.with(context)
					.load(url)
					.asBitmap()
					.into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
					.get();
			if (bitmap != null){
				// 在这里执行图片保存方法
				saveImage(bitmap);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
//            if (file != null) {
//                callBack.onDownLoadSuccess(file);
//            } else {
//                callBack.onDownLoadFailed();
//            }
			if (bitmap != null && currentFile.exists()) {
				callBack.onDownLoadSuccess(bitmap);
			} else {
				callBack.onDownLoadFailed();
			}
		}
	}

	public void saveImage(Bitmap bmp) {
      	File image = new File(Environment.getExternalStorageDirectory() ,saveDir);
		if (!image.exists()) {
			image.mkdirs();
		}
		currentFile = new File(image.getAbsolutePath(), imageName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(currentFile);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);//100表示不压缩
			fos.flush();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
	}

	public interface ImageDownLoadCallBack {
		void onDownLoadSuccess(File file);

		void onDownLoadSuccess(Bitmap bitmap);

		void onDownLoadFailed();
	}


}
