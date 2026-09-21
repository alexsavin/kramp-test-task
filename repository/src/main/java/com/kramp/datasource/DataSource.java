package com.kramp.datasource;

import java.util.Optional;

public interface DataSource<T, P> {

    Optional<T> fetch(P params);
}
