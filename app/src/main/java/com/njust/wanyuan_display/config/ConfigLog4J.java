package com.njust.wanyuan_display.config;

import com.njust.wanyuan_display.util.DateUtil;

import org.apache.log4j.Level;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * 日志配置
 * Copyright: Changzhou JinTeng Software Co., Ltd.
 */
public class ConfigLog4J {
	
	// 日志级别优先度从高到低:OFF(关闭),FATAL(致命),ERROR(错误),WARN(警告),INFO(信息),DEBUG(调试),ALL(打开所有的日志)
	// Log4j建议只使用FATAL ,ERROR ,WARN ,INFO ,DEBUG这五个级别
	public static void configure() {
		final LogConfigurator logConfigurator = new LogConfigurator();
		// 日志文件路径地址:SD卡下log文件夹的test文件
		String fileName = Resources.logURL() + DateUtil.getCurrentDate() + ".log";
		// 设置文件名
		logConfigurator.setFileName(fileName);
		// 设置root日志输出级别 默认为DEBUG
		logConfigurator.setRootLevel(Level.DEBUG);
		// 设置日志输出级别
		logConfigurator.setLevel("org.apache", Level.INFO);
		// 设置 输出到日志文件的文字格式 默认 %d %-5p [%c{2}]-[%L] %m%n
		logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
		// 设置输出到控制台的文字格式 默认%m%n
		logConfigurator.setLogCatPattern("%m%n");
		// 设置总文件大小
		logConfigurator.setMaxFileSize(1024 * 1024 * 5);
		// 设置最大产生的文件个数
		logConfigurator.setMaxBackupSize(100);
		// 设置所有消息是否被立刻输出 默认为true,false 不输出
		logConfigurator.setImmediateFlush(true);
		// 是否本地控制台打印输出 默认为true ，false不输出
		logConfigurator.setUseLogCatAppender(true);
		// 设置是否启用文件附加,默认为true。false为覆盖文件
		logConfigurator.setUseFileAppender(true);
		// 设置是否重置配置文件，默认为true
		logConfigurator.setResetConfiguration(true);
		// 是否显示内部初始化日志,默认为false
		logConfigurator.setInternalDebugging(false);
		
		logConfigurator.configure();
	}
	
}