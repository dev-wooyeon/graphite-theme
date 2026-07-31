from dataclasses import dataclass
from datetime import datetime, timedelta


@dataclass(frozen=True)
class CreatePaymentCommand:
    order_id: str


@dataclass(frozen=True)
class Payment:
    id: str
    order_id: str


@dataclass(frozen=True)
class RequestedPayment:
    payment_id: str
    order_id: str
    expires_at: datetime


class Clock:
    def now(self) -> datetime:
        return datetime.now()


class PaymentStore:
    def create(
        self,
        command: CreatePaymentCommand,
        requested_at: datetime,
        expires_at: datetime,
    ) -> Payment:
        return Payment(id="pay_001", order_id=command.order_id)


class PaymentProviderGateway:
    def checkout(self, payment: Payment) -> None:
        print(f"Checkout: {payment.id}")


class PaymentService:
    DEFAULT_PAYMENT_WINDOW_MINUTES = 30

    def __init__(
        self,
        payment_store: PaymentStore,
        payment_provider_gateway: PaymentProviderGateway,
        clock: Clock,
    ) -> None:
        self.payment_store = payment_store
        self.payment_provider_gateway = payment_provider_gateway
        self.clock = clock

    def create(self, command: CreatePaymentCommand) -> RequestedPayment:
        requested_at = self.clock.now()
        expires_at = requested_at + timedelta(
            minutes=self.DEFAULT_PAYMENT_WINDOW_MINUTES
        )
        payment = self.payment_store.create(command, requested_at, expires_at)

        self.payment_provider_gateway.checkout(payment)
        return RequestedPayment(payment.id, payment.order_id, expires_at)
