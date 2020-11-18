package com.equiron.acc.profiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Striped;

import net.logstash.logback.marker.Markers;

public class LogstashMetricsPublisher {
    private static final Logger LOGSTASH_JSON_LOGGER = LoggerFactory.getLogger("LOGSTASH_JSON_LOGGER");
    
    private final ConcurrentHashMap<String, OperationProfile> byTypeAndInfoAndStackTraceMap = new ConcurrentHashMap<>();
    
    private final ConcurrentHashMap<String, OperationProfile> byTypeAndInfoMap = new ConcurrentHashMap<>();
    
    private final String dbName;
    
    private final String instance;
    
    private volatile String logstashHost;
    
    private final Striped<Lock> striped = Striped.lock(4096);

    public LogstashMetricsPublisher(String dbName) {
        this.dbName = dbName;
        
        logstashHost = System.getenv("LOGSTASH_HOST");

        if (logstashHost == null || logstashHost.isBlank()) {
            logstashHost = System.getProperty("LOGSTASH_HOST");
        }

        instance = System.getenv("SERVICE_NAME") == null ? "default_instance" : System.getenv("SERVICE_NAME");
        
        if (logstashHost != null && !logstashHost.isBlank()) {        
            Thread t = new Thread() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        for (Entry<String, OperationProfile> e : byTypeAndInfoAndStackTraceMap.entrySet()) {
                            OperationProfile profile = e.getValue();
                            
                            Map<String, Object> metrics = new HashMap<>();
                            
                            //Поля для общей фильтрации
                            metrics.put("acc.instance", instance);
                            metrics.put("acc.database", profile.getDatabase());
                            metrics.put("acc.operationType", profile.getOperationType());
                            metrics.put("acc.operationInfo", profile.getOperationInfo());
                            metrics.put("acc.instance_database_operation", extrackKey(profile.getOperationType().toString(), profile.getOperationInfo()));
                            
                            //Поле для агрегации в кибане
                            metrics.put("acc.byStack.instance_database_operation_stack", e.getKey());
                            
                            //Поле для отображения stacktrace
                            metrics.put("acc.byStack.stacktrace", profile.getStackTrace());

                            //Не используются в кибане
                            metrics.put("acc.byStack.totalTime", profile.getTotalTime());
                            metrics.put("acc.byStack.count", profile.getCount());
                            metrics.put("acc.byStack.size", profile.getSize());
                            
                            metrics.put("acc.byStack.success", profile.getSuccessCount());
                            metrics.put("acc.byStack.notFound", profile.getNotFoundCount());
                            metrics.put("acc.byStack.conflicts", profile.getConflictCount());
                            metrics.put("acc.byStack.errors", profile.getErrorsCount());
                            
                            info(metrics);
                        }
                        
                        for (Entry<String, OperationProfile> e : byTypeAndInfoMap.entrySet()) {
                            OperationProfile profile = e.getValue();
                            
                            Map<String, Object> metrics = new HashMap<>();
                            
                            //Поля для общей фильтрации
                            metrics.put("acc.instance", instance);
                            metrics.put("acc.database", profile.getDatabase());
                            metrics.put("acc.operationType", profile.getOperationType());
                            metrics.put("acc.operationInfo", profile.getOperationInfo());
                            metrics.put("acc.instance_database_operation", extrackKey(profile.getOperationType().toString(), profile.getOperationInfo()));

                            //Поля для таблицы
                            metrics.put("acc.byOp.totalTime", profile.getTotalTime());
                            metrics.put("acc.byOp.count", profile.getCount());
                            metrics.put("acc.byOp.size", profile.getSize());
                            
                            metrics.put("acc.byOp.success", profile.getSuccessCount());
                            metrics.put("acc.byOp.notFound", profile.getNotFoundCount());
                            metrics.put("acc.byOp.conflicts", profile.getConflictCount());
                            metrics.put("acc.byOp.errors", profile.getErrorsCount());

                            info(metrics);
                        }
                        
                        try {
                            Thread.sleep(30_000);
                        } catch (@SuppressWarnings("unused") InterruptedException e) {
                            break;
                        }
                    }
                }
            };
            
            t.setDaemon(true);
            t.start();
        }
    }
    
    public void addOperation(OperationInfo opInfo) {
        if ((logstashHost != null && !logstashHost.isBlank())) {      
            fill(byTypeAndInfoAndStackTraceMap, opInfo, extrackKey(opInfo.getOperationType().toString(), opInfo.getOperationInfo()) + "/" + opInfo.getStackTrace().hashCode());
            fill(byTypeAndInfoMap,              opInfo, extrackKey(opInfo.getOperationType().toString(), opInfo.getOperationInfo()));
        }
    }
    
    private void info(@Nonnull Map<String, Object> fields) {
        fields.put("acc.metrics", true);//чтобы отличать метрики и логи
            
        LOGSTASH_JSON_LOGGER.info(Markers.appendEntries(fields), "");
    }
    
    private String extrackKey(String type, String info) {
        return instance + "/" + dbName + "/" + type + (info.isBlank() ? "" : ("/" + info));
    }
    
    private void fill(ConcurrentHashMap<String, OperationProfile> map, OperationInfo opInfo, String key) {
        striped.get(key).lock();
        
        try {
            OperationProfile prevProfile = map.get(key);
    
            long opTime = System.currentTimeMillis() - opInfo.getStartTime();
            
            OperationProfile profile;
            
            if (prevProfile == null) {
                profile = new OperationProfile(dbName, opInfo.getOperationType(), opInfo.getOperationInfo(), opInfo.getStackTrace(), opTime, opInfo.getSize());
                
                if (opInfo.getStatus() == 200) {
                    profile.setSuccessCount(1);
                } else if (opInfo.getStatus() == 404) {
                    profile.setNotFoundCount(1);
                } else if (opInfo.getStatus() == 409) {
                    profile.setConflictCount(1);
                } else {
                    profile.setErrorsCount(1);
                }
            } else {
                profile = new OperationProfile(dbName, opInfo.getOperationType(), opInfo.getOperationInfo(), opInfo.getStackTrace());
                
                profile.setTotalTime(prevProfile.getTotalTime() + opTime);
                profile.setCount(prevProfile.getCount() + 1);
                profile.setSize(prevProfile.getSize() + opInfo.getSize());

                if (opInfo.getStatus() == 200) {
                    profile.setSuccessCount(prevProfile.getSuccessCount() + 1);
                } else if (opInfo.getStatus() == 404) {
                    profile.setNotFoundCount(prevProfile.getNotFoundCount() + 1);
                } else if (opInfo.getStatus() == 409) {
                    profile.setConflictCount(prevProfile.getConflictCount() + 1);
                } else {
                    profile.setErrorsCount(prevProfile.getErrorsCount() + 1);
                }
            }
            
            map.put(key, profile);
        } finally {
            striped.get(key).unlock();
        }
    }
}