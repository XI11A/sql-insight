package io.sqlinsight.core.collector;

import io.sqlinsight.core.model.QueryInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QueryCollector {
    private final ConcurrentLinkedQueue<QueryInfo> queries = new ConcurrentLinkedQueue<>();
    private final AtomicInteger count = new AtomicInteger(0);
    private final java.util.concurrent.atomic.AtomicLong totalSessionQueries = new java.util.concurrent.atomic.AtomicLong(0);
    private int maxQueries = 1000;

    public QueryCollector() {
    }

    public QueryCollector(int maxQueries) {
        this.maxQueries = maxQueries;
    }

    public void addQuery(QueryInfo queryInfo) {
        queries.add(queryInfo);
        totalSessionQueries.incrementAndGet();
        int currentSize = count.incrementAndGet();
        
        while (currentSize > maxQueries) {
            if (queries.poll() != null) {
                currentSize = count.decrementAndGet();
            } else {
                break;
            }
        }
    }

    public long getTotalSessionQueries() {
        return totalSessionQueries.get();
    }

    public List<QueryInfo> getQueries() {
        return new ArrayList<>(queries);
    }

    public List<QueryInfo> getSlowQueries() {
        return queries.stream()
            .filter(QueryInfo::isSlowQuery)
            .collect(Collectors.toList());
    }

    public void clear() {
        queries.clear();
        count.set(0);
    }

    public int size() {
        return count.get();
    }

    public void setMaxQueries(int maxQueries) {
        this.maxQueries = maxQueries;
    }
}
