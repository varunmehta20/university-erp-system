package erp.domain;

public class Setting {
    private String key;
    private String value;

    public Setting() {}

    public Setting(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isMaintenance() {
        return "maintenance".equals(key) && "true".equalsIgnoreCase(value);
    }
    public boolean isDropPeriodOpen() {
        return "dropPeriod".equals(key) && !"CLOSED".equalsIgnoreCase(value);
    }

    @Override
    public String toString() {
        return "Setting{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
