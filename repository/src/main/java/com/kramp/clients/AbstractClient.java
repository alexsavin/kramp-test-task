package com.kramp.clients;

import com.kramp.datasource.DataSource;
import com.kramp.mock.ClientBehaviourSimulator;

import java.util.Optional;

public abstract class AbstractClient<T, P> extends ClientBehaviourSimulator implements DataSource<T, P> {

    protected AbstractClient(int latency, int reliability) {
        super(latency, reliability);
    }

    @Override
    public Optional<T> fetch(P params) {
        if (serviceAvailable()) {
            simulateLatency();
            return fetchInternal(params);
        }

        throw new ServiceUnavailableException();
    }

    protected abstract Optional<T> fetchInternal(P params);
}
