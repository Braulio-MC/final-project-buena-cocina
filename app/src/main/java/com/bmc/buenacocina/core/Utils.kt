package com.bmc.buenacocina.core

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntSize
import com.bmc.buenacocina.R
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.Calendar

fun isPointInPolygon(point: LatLng, polygon: List<LatLng>): Boolean {
    var intersects = false
    for (i in polygon.indices) {
        val a = polygon[i]
        val j = (i + 1) % polygon.size
        val b = polygon[j]
        if ((a.longitude > point.longitude) != (b.longitude > point.longitude)) {
            val slope = (b.latitude - a.latitude) / (b.longitude - a.longitude)
            val intersectLat = slope * (point.longitude - a.longitude) + a.latitude
            if (point.latitude < intersectLat) {
                intersects = !intersects
            }
        }
    }
    return intersects
}

fun getOrderStatusColor(status: String): Int {
    return when (status) {
        OrderStatus.CREATED.status -> R.color.order_status_created
        OrderStatus.ACTIVE.status -> R.color.order_status_active
        OrderStatus.PROGRESS.status -> R.color.order_status_progress
        OrderStatus.ON_WAY.status -> R.color.order_status_way
        OrderStatus.DELIVERED.status -> R.color.order_status_arrived
        OrderStatus.ERROR.status -> R.color.order_status_error
        else -> R.color.order_status_unassigned
    }
}

fun getReviewSentimentStatusColor(status: String): Int {
    val s = status.lowercase().replaceFirstChar { it.titlecase() }
    return when (s) {
        ReviewSentimentStatus.VERY_NEGATIVE.status -> R.color.review_sentiment_very_negative
        ReviewSentimentStatus.NEGATIVE.status -> R.color.review_sentiment_negative
        ReviewSentimentStatus.NEUTRAL.status -> R.color.review_sentiment_neutral
        ReviewSentimentStatus.POSITIVE.status -> R.color.review_sentiment_positive
        ReviewSentimentStatus.VERY_POSITIVE.status -> R.color.review_sentiment_very_positive
        else -> R.color.review_sentiment_unassigned
    }
}

// TODO: create color values in res/value/colors

fun Modifier.shimmerEffect(shape: Shape = RectangleShape): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ), label = ""
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8B5B5),
                Color(0xFF8F8B8B),
                Color(0xFFB8B5B5),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        ),
        shape = shape
    )
        .onGloballyPositioned {
            size = it.size
        }
}

@Composable
fun makeBulletedList(items: List<String>): AnnotatedString {
    val bulletString = "\u2022\t\t"
    val textStyle = LocalTextStyle.current
    val textMeasurer = rememberTextMeasurer()
    val bulletStringWidth = remember(textStyle, textMeasurer) {
        textMeasurer.measure(text = bulletString, style = textStyle).size.width
    }
    val restLine = with(LocalDensity.current) { bulletStringWidth.toSp() }
    val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = restLine))

    return buildAnnotatedString {
        items.forEach { text ->
            withStyle(style = paragraphStyle) {
                append(bulletString)
                append(text)
            }
        }
    }
}

class DateUtils {
    companion object {
        fun convertMillisToLocalDateTime(millis: Long): LocalDateTime {
            return Instant
                .ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }

        private fun convertMillisToLocalDateTimeWithFormatter(
            date: LocalDateTime,
            dateTimeFormatter: DateTimeFormatter
        ): LocalDateTime {
            val dateTimeInMillis =
                LocalDate.parse(date.format(dateTimeFormatter), dateTimeFormatter)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            return Instant
                .ofEpochMilli(dateTimeInMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }

        fun localDateTimeToStringWithoutTime(date: LocalDateTime): String {
            val dateTimeFormatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
            return date.format(dateTimeFormatter)
        }

        fun localDateTimeToString(date: LocalDateTime): String {
            val dateTimeFormatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm a", Locale.getDefault())
            return date.format(dateTimeFormatter)
        }

        fun localDateTimeToStringISO8601(date: LocalDateTime): String {
            val dateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            return date.format(dateTimeFormatter)
        }

        fun stringISO8601ToDateTime(date: String): LocalDateTime {
            val dateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            return LocalDateTime.parse(date, dateTimeFormatter)
        }

        fun instantToLocalDateTime(instant: Instant): LocalDateTime {
            return instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        }

        fun localDateTimeWithLocalTime(date: LocalDateTime, time: LocalTime): LocalDateTime {
            return date.with(time)
        }

        fun localDateTimeToMillis(date: LocalDateTime): Long {
            return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        fun localDateTimeToFirebaseTimestamp(datetime: LocalDateTime): Timestamp {
            return Timestamp(datetime.atZone(ZoneId.systemDefault()).toInstant().epochSecond, 0)
        }

        fun firebaseTimestampToLocalDateTime(timestamp: Timestamp?): LocalDateTime? {
            return timestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())
                ?.toLocalDateTime()
        }

        fun getRemainingPeriod(startDate: LocalDateTime, endDate: LocalDateTime): Period {
            val diff = Period.between(startDate.toLocalDate(), endDate.toLocalDate())
            return diff
        }

        fun getRemainingPeriodString(startDate: LocalDateTime, endDate: LocalDateTime): String {
            val diff = Period.between(startDate.toLocalDate(), endDate.toLocalDate())
            var str = ""
            if (diff.years > 0) {
                str += "${diff.years} años, "
            }
            if (diff.months > 0) {
                str += "${diff.months} meses y "
            }
            if (diff.days > 0) {
                str += "${diff.days} dias"
            }
            return str
        }

        fun getElapsedTime(startDate: LocalDateTime, endDate: LocalDateTime): Long {
            return ChronoUnit.MINUTES.between(startDate, endDate)
        }

        fun isInRange(
            date: LocalDateTime,
            startDate: LocalDateTime,
            endDate: LocalDateTime
        ): Boolean {
            return date.isAfter(startDate) && date.isBefore(endDate)
        }
    }
}

class TimeUtils {
    companion object {
        fun rawValuesToLocalTime(hours: Int, minutes: Int): LocalTime {
            return LocalTime.of(hours, minutes)
        }

        fun localTimeToString(time: LocalTime): String {
            return time.format(DateTimeFormatter.ofPattern("HH:mm a"))
        }
    }
}

class FormatUtils {
    companion object {
        fun formatDiscountPercentage(percentage: BigDecimal): BigDecimal {
            return if (percentage.stripTrailingZeros().scale() <= 0) {
                percentage.setScale(0, RoundingMode.UNNECESSARY)
            } else {
                percentage.setScale(2, RoundingMode.HALF_DOWN)
            }
        }
    }
}

fun getGreetingPerHour(): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    return when (hour) {
        in 5..11 -> "🌞 ¡Buenos días! Mi querido cuceimita, ¿En qué te puedo ayudar?"
        in 12..18 -> "🌤️ ¡Buenas tardes! ¿Listo para comer algo sabroso?"
        in 19..21 -> "🌙 ¡Buenas noches!... Nunca es tarde para un antojito"
        in 22..23, in 0..4 -> "😴 No creo que haya alguien disponible ahora... pero si tienes hambre, ¡siempre hay algo que preparar!"
        else -> "👋 ¡Hola! ¿En qué puedo ayudarte, my friend?"
    }
}

