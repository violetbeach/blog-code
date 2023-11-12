package com.violetbeach.sharding.hibernate;

import com.violetbeach.sharding.database.DBContextHolder;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

@Component
public class PartitionInspector implements StatementInspector {

    @Override
    public String inspect(String sql) {
        String partition = DBContextHolder.getPartition();
        return sql.replaceAll("#partition#", partition);
    }

}
