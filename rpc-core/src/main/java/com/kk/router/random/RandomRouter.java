package com.kk.router.random;
import com.kk.util.ServiceWrapper;
import com.kk.register.Register;
import com.kk.router.Router;


import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomRouter implements Router {
    @Override
    public ServiceWrapper select(List<ServiceWrapper> services) {
        return services.get(ThreadLocalRandom.current().nextInt(0, services.size()));
    }

    @Override
    public List<ServiceWrapper> refresh(Register register, String serviceName) {
        return null;
    }
}