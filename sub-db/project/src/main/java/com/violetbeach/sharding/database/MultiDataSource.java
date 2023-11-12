package com.violetbeach.sharding.database;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

class MultiDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DBContextHolder.getIp();
    }
}
