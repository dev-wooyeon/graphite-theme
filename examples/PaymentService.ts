export type CreatePaymentCommand = { orderId: string };
export type Payment = { id: string; orderId: string };
export type RequestedPayment = Payment & { expiresAt: Date };

export interface Clock {
  now(): Date;
}

export interface PaymentStore {
  create(command: CreatePaymentCommand, requestedAt: Date, expiresAt: Date): Payment;
}

export interface PaymentProviderGateway {
  checkout(payment: Payment): void;
}

export class PaymentService {
  private static readonly DEFAULT_PAYMENT_WINDOW_MINUTES = 30;

  constructor(
    private readonly paymentStore: PaymentStore,
    private readonly paymentProviderGateway: PaymentProviderGateway,
    private readonly clock: Clock,
  ) {}

  create(command: CreatePaymentCommand): RequestedPayment {
    const requestedAt = this.clock.now();
    const expiresAt = new Date(
      requestedAt.getTime() + PaymentService.DEFAULT_PAYMENT_WINDOW_MINUTES * 60_000,
    );
    const payment = this.paymentStore.create(command, requestedAt, expiresAt);

    this.paymentProviderGateway.checkout(payment);
    return { ...payment, expiresAt };
  }
}
