package io.sqlinsight.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sql-insight")
public class SqlInsightProperties {
    private boolean enabled = true;
    private boolean dashboardEnabled = true;
    private int slowQueryThreshold = 200;
    private int maxQueries = 1000;
    private boolean detectNplus1 = true;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isDashboardEnabled() { return dashboardEnabled; }
    public void setDashboardEnabled(boolean dashboardEnabled) { this.dashboardEnabled = dashboardEnabled; }

    public int getSlowQueryThreshold() { return slowQueryThreshold; }
    public void setSlowQueryThreshold(int slowQueryThreshold) { this.slowQueryThreshold = slowQueryThreshold; }

    public int getMaxQueries() { return maxQueries; }
    public void setMaxQueries(int maxQueries) { this.maxQueries = maxQueries; }

    public boolean isDetectNplus1() { return detectNplus1; }
    public void setDetectNplus1(boolean detectNplus1) { this.detectNplus1 = detectNplus1; }
}
