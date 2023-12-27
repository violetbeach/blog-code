package com.violetbeach.sharding.module.hibernate;

import com.violetbeach.sharding.module.database.DBContextHolder;
import org.hibernate.resource.jdbc.spi.StatementInspector;

public class PartitionInspector implements StatementInspector {

    @Override
    public String inspect(String sql) {
        String partition = DBContextHolder.getPartition();
        return sql.replaceAll("#partition#", partition);
    }

}
