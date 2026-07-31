import { PaymentService, type CreatePaymentCommand } from './PaymentService';

declare global {
  namespace JSX {
    interface Element {}
    interface IntrinsicElements {
      section: { children?: unknown };
      strong: { children?: unknown };
      time: { children?: unknown };
    }
  }
}

type PaymentSummaryProps = {
  paymentService: PaymentService;
  command: CreatePaymentCommand;
};

export function PaymentSummary({
  paymentService,
  command,
}: PaymentSummaryProps) {
  const payment = paymentService.create(command);

  return (
    <section>
      <strong>{payment.orderId}</strong>
      <time>{payment.expiresAt.toISOString()}</time>
    </section>
  );
}
