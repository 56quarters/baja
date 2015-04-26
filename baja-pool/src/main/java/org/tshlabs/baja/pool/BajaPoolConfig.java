package org.tshlabs.baja.pool;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 */
public class BajaPoolConfig<T> {

    private Supplier<T> resourceSupplier;

    private Function<T, Boolean> resourceHealthCheck;

    private Function<T, Boolean> resourceCleanup;

    private boolean testOnBorrow;

    public Supplier<T> getResourceSupplier() {
        return resourceSupplier;
    }

    public void setResourceSupplier(Supplier<T> resourceSupplier) {
        this.resourceSupplier = resourceSupplier;
    }

    public BajaPoolConfig withResourceSupplier(Supplier<T> resourceSupplier) {
        setResourceSupplier(resourceSupplier);
        return this;
    }

    public Function<T, Boolean> getResourceHealthCheck() {
        return resourceHealthCheck;
    }

    public void setResourceHealthCheck(Function<T, Boolean> resourceHealthCheck) {
        this.resourceHealthCheck = resourceHealthCheck;
    }

    public BajaPoolConfig withResourceHealthCheck(Function<T, Boolean> resourceHealthCheck) {
        setResourceHealthCheck(resourceHealthCheck);
        return this;
    }

    public Function<T, Boolean> getResourceCleanup() {
        return resourceCleanup;
    }

    public void setResourceCleanup(Function<T, Boolean> resourceCleanup) {
        this.resourceCleanup = resourceCleanup;
    }

    public BajaPoolConfig withResourceCleanup(Function<T, Boolean> resourceCleanup) {
        setResourceCleanup(resourceCleanup);
        return this;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public BajaPoolConfig withTestOnBorrow(boolean testOnBorrow) {
        setTestOnBorrow(testOnBorrow);
        return this;
    }
}
