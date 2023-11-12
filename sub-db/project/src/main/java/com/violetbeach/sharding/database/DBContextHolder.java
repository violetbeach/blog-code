package com.violetbeach.sharding.database;

public class DBContextHolder {
    private static final ThreadLocal<DbInfo> threadLocal = new ThreadLocal();

    public static void setDbInfo(DbInfo dbInfo) {
        threadLocal.set(dbInfo);
    }

    public static DbInfo getDbInfo() {
        DbInfo dbInfo = threadLocal.get();
        if(dbInfo == null) {
            throw new IllegalStateException("DbInfo가 존재하지 않습니다.");
        }
        return dbInfo;
    }
    
    public static String getIp() {
        DbInfo dbInfo = getDbInfo();
        return dbInfo.ip();
    }
    
    public static String getPartition() {
        DbInfo dbInfo = getDbInfo();
        return dbInfo.partition();
    }

    public static void clear() {
        threadLocal.remove();
    }
}