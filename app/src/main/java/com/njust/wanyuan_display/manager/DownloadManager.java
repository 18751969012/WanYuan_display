package com.njust.wanyuan_display.manager;

import android.content.Context;
import android.os.Environment;

import com.njust.wanyuan_display.config.Constant;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.util.StringUtil;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载广告管理器
 */
public class DownloadManager {
	
	private final Logger log = Logger.getLogger(DownloadManager.class);
	private static DownloadManager downloadManager;
	private final OkHttpClient okHttpClient;

	public static DownloadManager getInstance() {
		if (downloadManager == null) {
			downloadManager = new DownloadManager();
		}
		return downloadManager;
	}

	private DownloadManager() {
		okHttpClient = new OkHttpClient();
	}

	public void download(final Context context, final int adId, final String url, final String saveDir, final int adType, final String playTime, final OnDownloadListener listener) {
		Request request = new Request.Builder().url(url).build();
		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				listener.onDownloadFailed();
			}
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				InputStream is = null;
				FileOutputStream fos = null;
				byte[] buf = new byte[2048];
				int len = 0;
				File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
				if (!downloadFile.mkdirs()) {
					downloadFile.createNewFile();
				}
				String fileName = StringUtil.getAdvName(adId, url);
				File file = new File(downloadFile.getAbsolutePath(), fileName);
				try {
					is = response.body().byteStream();
					long total = response.body().contentLength();
					fos = new FileOutputStream(file);
					long sum = 0;
					while ((len = is.read(buf)) != -1) {
						fos.write(buf, 0, len);
						sum += len;
						int progress = (int) (sum * 1.0f / total * 100);

						listener.onDownloading(progress);
					}
					fos.flush();
					listener.onDownloadSuccess();
					DBManager.saveAdv(context, String.valueOf(adId), url,file.getAbsolutePath(), adType,playTime,String.valueOf(Constant.download_success));
				} catch (Exception e) {
					log.error(e.getMessage());
					listener.onDownloadFailed();
					/** 如果下载失败，重新下载 */
					download(context,adId, url, saveDir, adType,playTime, listener);
				} finally {
					try {
						if (is != null) {
							is.close();
						}
					} catch (IOException e) {
						log.error(e.getMessage());
					}
					try {
						if (fos != null) {
							fos.close();
						}
					} catch (IOException e) {
						log.error(e.getMessage());
					}
				}
			}
		});
	}

	public interface OnDownloadListener {
		/**
		 * 下载成功
		 */
		void onDownloadSuccess();
		/**
		 * 下载进度
		 */
		void onDownloading(int progress);
		/**
		 * 下载失败
		 */
		void onDownloadFailed();
	}

}
