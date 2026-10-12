package snippet;

public class Snippet {
	status = WARN
	name = EZPAY_LOGGING
	
	# Console Appender
	appender.console.type = Console
	appender.console.name = ConsoleAppender
	
	appender.console.layout.type = PatternLayout
	appender.console.layout.pattern = %d{yyyy-MM-dd HH:mm:ss} %-5level [%t] %c{1} - %msg%n
	
	# File Appender
	appender.file.type = RollingFile
	appender.file.name = FileAppender
	
	appender.file.fileName = logs/ezpay.log
	appender.file.filePattern = logs/ezpay-%d{yyyy-MM-dd}-%i.log.gz
	
	appender.file.layout.type = PatternLayout
	appender.file.layout.pattern = %d{yyyy-MM-dd HH:mm:ss} %-5level [%t] %c{1} - %msg%n
	
	appender.file.policies.type = Policies
	
	appender.file.policies.time.type = TimeBasedTriggeringPolicy
	appender.file.policies.time.interval = 1
	appender.file.policies.time.modulate = true
	
	appender.file.policies.size.type = SizeBasedTriggeringPolicy
	appender.file.policies.size.size = 10MB
	
	appender.file.strategy.type = DefaultRolloverStrategy
	appender.file.strategy.max = 10
	
	# EZPAY Logger
	logger.ezpay.name = com.ezpay
	logger.ezpay.level = DEBUG
	logger.ezpay.additivity = false
	
	logger.ezpay.appenderRef.console.ref = ConsoleAppender
	logger.ezpay.appenderRef.file.ref = FileAppender
	
	# Root Logger
	rootLogger.level = INFO
	
	rootLogger.appenderRef.console.ref = ConsoleAppender
	rootLogger.appenderRef.file.ref = FileAppender
}

