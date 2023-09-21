public enum CellContentEnum {

    UPDATETIME("更新時間"),
    JOBTITLE("職務名稱"),
    AREA("地區"),
    COMPANY("徵才公司"),
    SALARY("薪資待遇"),
    INFO("職務內容"),
    JOBLINK("104連結");

    private final String label;
    CellContentEnum(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
}
