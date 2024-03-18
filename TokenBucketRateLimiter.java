import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A thread-safe implementation of the token bucket rate limiting algorithm.
 * This implementation allows configuring the capacity of the bucket and the rate at which tokens are refilled.
 */
public class TokenBucketRateLimiter {

    // Static fields for default configuration
    public static final int CAPACITY = 5;
    public static final int FILL_RATE = 1; // tokens per second

    private final AtomicInteger tokens;
    private final int capacity;
    private final int fillRate;
    private Instant start;
    private final ReentrantLock reentrantLock;

    private TokenBucketRateLimiter(Builder builder) {
        this.capacity = builder.capacity;
        this.fillRate = builder.fillRate;
        this.tokens = new AtomicInteger(this.capacity);
        this.start = Instant.now();
        this.reentrantLock = new ReentrantLock();
    }

    /**
     * Adds tokens to the bucket based on the elapsed time since the last addition.
     * This method is thread-safe and ensures that tokens are added accurately without exceeding the bucket's capacity.
     */
    public void addToken() {
        reentrantLock.lock();
        try {
            Instant now = Instant.now();
            long timeElapsed = Duration.between(start, now).getSeconds();
            if (timeElapsed > 0) {
                int tokensToAdd = (int) (timeElapsed * fillRate);
                tokens.getAndUpdate(currentTokens -> Math.min(capacity, currentTokens + tokensToAdd));
                start = now;
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * Attempts to consume a token from the bucket to grant access.
     * @return true if a token was successfully consumed, false otherwise.
     */
    public boolean canAccess() {
        return tokens.getAndUpdate(currentTokens -> currentTokens > 0 ? currentTokens - 1 : currentTokens) > 0;
    }

    /**
     * Builder for {@link TokenBucketRateLimiter}.
     * Allows configuration of the bucket's capacity and fill rate.
     */
    public static class Builder {
        private int capacity = CAPACITY;
        private int fillRate = FILL_RATE;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        /**
         * Sets the capacity of the bucket.
         * @param capacity the maximum number of tokens the bucket can hold.
         * @return the Builder instance for chaining.
         */
        public Builder setCapacity(int capacity) {
            if (capacity <= 0) {
                throw new IllegalArgumentException("Capacity must be greater than 0.");
            }
            this.capacity = capacity;
            return this;
        }

        /**
         * Sets the fill rate of the bucket.
         * @param fillRate the rate at which tokens are added to the bucket per second.
         * @return the Builder instance for chaining.
         */
        public Builder setFillRate(int fillRate) {
            if (fillRate <= 0) {
                throw new IllegalArgumentException("Fill rate must be greater than 0.");
            }
            this.fillRate = fillRate;
            return this;
        }

        /**
         * Builds the {@link TokenBucketRateLimiter} with the configured parameters.
         * @return a new instance of {@link TokenBucketRateLimiter}.
         */
        public TokenBucketRateLimiter build() {
            return new TokenBucketRateLimiter(this);
        }
    }
}
