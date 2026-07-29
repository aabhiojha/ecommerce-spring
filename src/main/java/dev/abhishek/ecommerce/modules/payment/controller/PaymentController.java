package dev.abhishek.ecommerce.modules.payment.controller;

import dev.abhishek.ecommerce.modules.payment.dto.PaymentRequest;
import dev.abhishek.ecommerce.modules.payment.dto.PaymentResponse;
import dev.abhishek.ecommerce.modules.payment.service.StripeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Payments", description = "Endpoints for handling Stripe payments")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final StripeService stripeService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create checkout session", description = "Creates a new Stripe checkout session for payment")
    @PostMapping("/checkout")
    public ResponseEntity<PaymentResponse> createCheckoutSession(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(stripeService.createCheckoutSession(request));
    }

    @Operation(summary = "Confirm checkout session", description = "Confirms a successful payment session")
    @GetMapping("/checkout/success")
    public ResponseEntity<PaymentResponse> confirmCheckoutSession(@RequestParam("session_id") String sessionId) {
        return ResponseEntity.ok(stripeService.confirmCheckoutSession(sessionId));
    }

    @Operation(summary = "Cancel checkout session", description = "Handles cancelled payment sessions")
    @GetMapping("/checkout/cancel")
    public ResponseEntity<Map<String, String>> cancelCheckoutSession() {
        return ResponseEntity.ok(Map.of("message", "Checkout was cancelled"));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get payment status", description = "Retrieves the status of a specific checkout session")
    @GetMapping("/checkout/{checkoutSessionId}")
    public ResponseEntity<PaymentResponse> getPaymentStatus(@PathVariable String checkoutSessionId) {
        return ResponseEntity.ok(stripeService.getCheckoutStatus(checkoutSessionId));
    }
}
