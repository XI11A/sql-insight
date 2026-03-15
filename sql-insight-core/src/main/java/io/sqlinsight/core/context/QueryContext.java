package io.sqlinsight.core.context;

public class QueryContext {

    private static final ThreadLocal<QueryContextState> CONTEXT = ThreadLocal.withInitial(QueryContextState::new);

    public static void start(String sql) {
        // Hook for query start event, currently managed by Inspector
    }

    public static void startMethod(String sourceClass, String sourceMethod) {
        QueryContextState state = CONTEXT.get();
        state.setActiveMethodClass(sourceClass);
        state.setActiveMethodName(sourceMethod);
    }

    public static void endMethod() {
        QueryContextState state = CONTEXT.get();
        state.setActiveMethodClass(null);
        state.setActiveMethodName(null);
    }

    public static String getCurrentLabel() {
        return CONTEXT.get().getCurrentLabel();
    }

    public static void setLabel(String label) {
        CONTEXT.get().setCurrentLabel(label);
    }

    public static boolean isTrackingDisabled() {
        return CONTEXT.get().isTrackingDisabled();
    }

    public static void setTrackingDisabled(boolean trackingDisabled) {
        CONTEXT.get().setTrackingDisabled(trackingDisabled);
    }

    public static Integer getSlowQueryThresholdOverride() {
        return CONTEXT.get().getSlowQueryThresholdOverride();
    }

    public static void setSlowQueryThresholdOverride(Integer override) {
        CONTEXT.get().setSlowQueryThresholdOverride(override);
    }

    public static String getActiveMethodClass() {
        return CONTEXT.get().getActiveMethodClass();
    }

    public static String getActiveMethodName() {
        return CONTEXT.get().getActiveMethodName();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    public static class QueryContextState {
        private String currentLabel;
        private boolean trackingDisabled;
        private Integer slowQueryThresholdOverride;
        private String activeMethodClass;
        private String activeMethodName;

        public String getCurrentLabel() { return currentLabel; }
        public void setCurrentLabel(String currentLabel) { this.currentLabel = currentLabel; }

        public boolean isTrackingDisabled() { return trackingDisabled; }
        public void setTrackingDisabled(boolean trackingDisabled) { this.trackingDisabled = trackingDisabled; }

        public Integer getSlowQueryThresholdOverride() { return slowQueryThresholdOverride; }
        public void setSlowQueryThresholdOverride(Integer slowQueryThresholdOverride) { this.slowQueryThresholdOverride = slowQueryThresholdOverride; }

        public String getActiveMethodClass() { return activeMethodClass; }
        public void setActiveMethodClass(String activeMethodClass) { this.activeMethodClass = activeMethodClass; }

        public String getActiveMethodName() { return activeMethodName; }
        public void setActiveMethodName(String activeMethodName) { this.activeMethodName = activeMethodName; }
    }
}
