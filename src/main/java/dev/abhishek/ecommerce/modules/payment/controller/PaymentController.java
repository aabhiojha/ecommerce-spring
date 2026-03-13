package dev.abhishek.ecommerce.modules.payment.controller;

import dev.abhishek.ecommerce.modules.payment.dto.PaymentRequest;
import dev.abhishek.ecommerce.modules.payment.dto.PaymentResponse;
import dev.abhishek.ecommerce.modules.payment.service.StripeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final StripeService stripeService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/checkout")
    public ResponseEntity<PaymentResponse> createCheckoutSession(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(stripeService.createCheckoutSession(request));
    }

    @GetMapping("/checkout/success")
    public ResponseEntity<PaymentResponse> confirmCheckoutSession(@RequestParam("session_id") String sessionId) {
        return ResponseEntity.ok(stripeService.confirmCheckoutSession(sessionId));
    }

    @GetMapping("/checkout/cancel")
    public ResponseEntity<String> cancelCheckoutSession() {
        return ResponseEntity.ok("Checkout was cancelled");
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/checkout/{checkoutSessionId}")
    public ResponseEntity<PaymentResponse> getPaymentStatus(@PathVariable String checkoutSessionId) {
        return ResponseEntity.ok(stripeService.getCheckoutStatus(checkoutSessionId));
    }
}
