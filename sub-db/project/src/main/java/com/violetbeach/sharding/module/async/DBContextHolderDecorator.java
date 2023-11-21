package com.violetbeach.sharding.module.async;

import com.violetbeach.sharding.module.database.DBContextHolder;
import com.violetbeach.sharding.module.database.DbInfo;
import org.springframework.core.task.TaskDecorator;

public class DBContextHolderDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        DbInfo dbInfo = DBContextHolder.getDbInfo();

        return () -> {
            DBContextHolder.setDbInfo(dbInfo);
            try {
                runnable.run();
            } finally {
                DBContextHolder.clear();
            }
        };
    }
}
