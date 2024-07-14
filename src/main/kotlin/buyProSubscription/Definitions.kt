package buyProSubscription

import com.github.kotlintelegrambot.entities.payments.LabeledPrice
import java.math.BigInteger

object Definitions {
    const val START_PARAMETER = "buySubscription"
    const val PAYLOAD = "boughtProSubscription"
    val PRICES = listOf(
        LabeledPrice(
            label = "Pro subscription",
            amount = BigInteger.valueOf(119)
        )
    )
}
