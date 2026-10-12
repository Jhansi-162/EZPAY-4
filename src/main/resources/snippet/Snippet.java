status = WARN
name = EzPayLogConfig

appenders = console

appender.console.type = Console
appender.console.name = ConsoleAppender
appender.console.layout.type = PatternLayout
appender.console.layout.pattern = %d{yyyy-MM-dd HH:mm:ss.SSS} %-5p [%t] %c{1} - %m%n

rootLogger.level = info
rootLogger.appenderRefs = console
rootLogger.appenderRef.console.ref = ConsoleAppender

logger.ezpay.name = com.ezpay
logger.ezpay.level = debug