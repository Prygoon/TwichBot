import ch.qos.logback.classic.Level
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.*

appender("consoleAppender", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "[%d{HH:mm:ss.SSS}] [%thread] [%-5level] [%logger{36}] - %msg%n"
    }
}

/**
 * This would allow us to see parameters passed into prepared statements
 */
logger("org.hibernate.type.descriptor.sql.BasicBinder", TRACE)
/**
 * We don't want to include the whole org.hibernate.type package to get rid of noise,
 * thus we need to include necessary classes explicitly.
 */
logger("org.hibernate.type.EnumType", TRACE)
/**
 * Shows executed SQL statements. This one is better than show_sql because the latter can log only to console. Note,
 * capitalized SQL letters, it's important.
 */
logger("org.hibernate.SQL", TRACE)
root(INFO, ["consoleAppender"])