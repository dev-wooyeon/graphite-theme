export class PaymentService {
  static DEFAULT_PAYMENT_WINDOW_MINUTES = 30;

  constructor(paymentStore, paymentProviderGateway, clock) {
    this.paymentStore = paymentStore;
    this.paymentProviderGateway = paymentProviderGateway;
    this.clock = clock;
  }

  create(command) {
    const requestedAt = this.clock.now();
    const expiresAt = new Date(
      requestedAt.getTime() + PaymentService.DEFAULT_PAYMENT_WINDOW_MINUTES * 60_000,
    );
    const payment = this.paymentStore.create(command, requestedAt, expiresAt);

    this.paymentProviderGateway.checkout(payment);
    return { paymentId: payment.id, orderId: payment.orderId, expiresAt };
  }
}