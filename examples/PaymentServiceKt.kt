import java.time.Clock
import java.time.Instant

annotation class ServiceKt

@Service
class PaymentServiceKt(
    private val paymentStore: PaymentStore,
    private val paymentProviderGateway: PaymentProviderGateway,
    private val clock: Clock,
) {
    companion object {
        private const val DEFAULT_PAYMENT_WINDOW_MINUTES = 30
    }

    fun create(command: CreatePaymentCommand): RequestedPayment {
        val requestedAt = clock.instant()
        val expiresAt = requestedAt.plusSeconds(DEFAULT_PAYMENT_WINDOW_MINUTES * 60L)
        val payment = paymentStore.create(command, requestedAt, expiresAt)

        paymentProviderGateway.checkout(payment)
        return RequestedPayment(payment.id, payment.orderId, expiresAt)
    }
}

data class CreatePaymentCommand(val orderId: String)

data class Payment(val id: String, val orderId: String)

data class RequestedPayment(
    val paymentId: String,
    val orderId: String,
    val expiresAt: Instant,
)

interface PaymentStore {
    fun create(
        command: CreatePaymentCommand,
        requestedAt: Instant,
        expiresAt: Instant,
    ): Payment
}

interface PaymentProviderGateway {
    fun checkout(payment: Payment)
}
