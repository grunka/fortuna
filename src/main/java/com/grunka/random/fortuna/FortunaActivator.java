package com.grunka.random.fortuna;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Random;

public class FortunaActivator implements BundleActivator {
    private ServiceRegistration<?> serviceRegistration;
    private Fortuna instance;

    @Override
    public void start(BundleContext bundleContext) {
        instance = Fortuna.createInstance();
        serviceRegistration = bundleContext.registerService(Random.class.getName(), instance, null);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        serviceRegistration.unregister();
        instance.shutdown();
    }
}
