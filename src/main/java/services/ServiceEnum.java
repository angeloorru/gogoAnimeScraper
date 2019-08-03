package services;

public enum ServiceEnum {

    NUMBER_OF_SERVICES(4);


    private Integer value;

    public Integer getValue() {
        return value;
    }

    ServiceEnum(Integer value) {
        this.value = value;
    }
}
