import java.time.Clock;
import java.time.Instant;

@interface Service {}

@Service
public final class PaymentService {
  private static final int DEFAULT_PAYMENT_WINDOW_MINUTES = 30;

  private final PaymentStore paymentStore;
  private final PaymentProviderGateway paymentProviderGateway;
  private final Clock clock;

  public PaymentService(
      PaymentStore paymentStore,
      PaymentProviderGateway paymentProviderGateway,
      Clock clock
  ) {
    this.paymentStore = paymentStore;
    this.paymentProviderGateway = paymentProviderGateway;
    this.clock = clock;
  }

  public RequestedPayment create(CreatePaymentCommand command) {
    Instant requestedAt = clock.instant();
    Instant expiresAt = requestedAt.plusSeconds(DEFAULT_PAYMENT_WINDOW_MINUTES * 60L);
    Payment payment = paymentStore.create(command, requestedAt, expiresAt);

    paymentProviderGateway.checkout(payment);
    return new RequestedPayment(payment.id(), payment.orderId(), expiresAt);
  }

  public record CreatePaymentCommand(String orderId) {}

  public record Payment(String id, String orderId) {}

  public record RequestedPayment(String paymentId, String orderId, Instant expiresAt) {}

  public interface PaymentStore {
    Payment create(CreatePaymentCommand command, Instant requestedAt, Instant expiresAt);
  }

  public interface PaymentProviderGateway {
    void checkout(Payment payment);
  }
}
