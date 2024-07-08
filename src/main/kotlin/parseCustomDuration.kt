import java.time.Duration
import java.util.concurrent.TimeUnit

fun customDurationToUnix(durationStr: String?): Long? {
    if (durationStr == null) { return null }

    val pattern = "(\\d+)([dhms])".toRegex()
    var duration = Duration.ZERO

    pattern.findAll(durationStr).forEach { matchResult ->
        val value = matchResult.groupValues[1].toLong()
        val unit = matchResult.groupValues[2]

        duration = when (unit) {
            "d" -> duration.plusDays(value)
            "h" -> duration.plusHours(value)
            "m" -> duration.plusMinutes(value)
            "s" -> duration.plusSeconds(value)
            else -> throw IllegalArgumentException("Unknown time unit: $unit")
        }
    }

    return duration.toMillis() / 1000L
}

fun parseUnixToReadable(timestamp: Long?): String? {
    if (timestamp == null) { return null }

    val days = TimeUnit.SECONDS.toDays(timestamp)
    val hours = TimeUnit.SECONDS.toHours(timestamp) % 24
    val minutes = TimeUnit.SECONDS.toMinutes(timestamp) % 60
    val seconds = timestamp % 60

    val parts = mutableListOf<String>()
    if (days > 0) { parts.add("$days days") }
    if (hours > 0) { parts.add("$hours hours") }
    if (minutes > 0) { parts.add("$minutes minutes") }
    if (seconds > 0) { parts.add("$seconds seconds") }

    return parts.joinToString(", ")
}
