<script setup lang="ts">
import { PaymentService, type CreatePaymentCommand } from './PaymentService';

const props = defineProps<{
  paymentService: PaymentService;
  command: CreatePaymentCommand;
}>();

const payment = props.paymentService.create(props.command);
</script>

<template>
  <section>
    <strong>{{ payment.orderId }}</strong>
    <time>{{ payment.expiresAt.toISOString() }}</time>
  </section>
</template>