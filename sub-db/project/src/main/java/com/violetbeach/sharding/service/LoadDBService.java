package com.violetbeach.sharding.service;

import com.violetbeach.sharding.module.aop.LoadDbInfoProcess;
import com.violetbeach.sharding.module.database.DbInfo;

public class LoadDBService implements LoadDbInfoProcess {
    @Override
    public DbInfo loadDbInfo() {
        return new DbInfo("127.0.0.1", "01");
    }
}
