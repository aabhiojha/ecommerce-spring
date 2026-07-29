package dev.abhishek.ecommerce.common.helpers;

import java.security.SecureRandom;

public final class RandomNumbers {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int RESET_TOKEN_MIN = 100_000_000;
    private static final int RESET_TOKEN_BOUND = 900_000_000;

    private RandomNumbers() {
    }

    /**
     * Nine digit password reset code. Always the same length so a code is never guessable
     * by being short, and wide enough that brute forcing the endpoint is impractical.
     */
    public static Integer generateResetToken() {
        return RESET_TOKEN_MIN + RANDOM.nextInt(RESET_TOKEN_BOUND);
    }
}
