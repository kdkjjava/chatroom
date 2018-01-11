package com.kdkj.intelligent.entity;

public class KeyWord {
    private Integer id;

    private String masterId;

    private String groupSpace;

    private String groupFinal;

    private String typeSpace;

    private String typeFinal;

    private String innerSpace;

    private String innerFinal;

    private String upperKey;

    private String lowerKey;

    private String modifyAttack;

    private String deleteAttack;

    private String queryAttack;

    private String countKey;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getGroupSpace() {
        return groupSpace;
    }

    public void setGroupSpace(String groupSpace) {
        this.groupSpace = groupSpace == null ? null : groupSpace.trim();
    }

    public String getGroupFinal() {
        return groupFinal;
    }

    public void setGroupFinal(String groupFinal) {
        this.groupFinal = groupFinal == null ? null : groupFinal.trim();
    }

    public String getTypeSpace() {
        return typeSpace;
    }

    public void setTypeSpace(String typeSpace) {
        this.typeSpace = typeSpace == null ? null : typeSpace.trim();
    }

    public String getTypeFinal() {
        return typeFinal;
    }

    public void setTypeFinal(String typeFinal) {
        this.typeFinal = typeFinal == null ? null : typeFinal.trim();
    }

    public String getInnerSpace() {
        return innerSpace;
    }

    public void setInnerSpace(String innerSpace) {
        this.innerSpace = innerSpace == null ? null : innerSpace.trim();
    }

    public String getInnerFinal() {
        return innerFinal;
    }

    public void setInnerFinal(String innerFinal) {
        this.innerFinal = innerFinal == null ? null : innerFinal.trim();
    }

    public String getUpperKey() {
        return upperKey;
    }

    public void setUpperKey(String upperKey) {
        this.upperKey = upperKey == null ? null : upperKey.trim();
    }

    public String getLowerKey() {
        return lowerKey;
    }

    public void setLowerKey(String lowerKey) {
        this.lowerKey = lowerKey == null ? null : lowerKey.trim();
    }

    public String getModifyAttack() {
        return modifyAttack;
    }

    public void setModifyAttack(String modifyAttack) {
        this.modifyAttack = modifyAttack == null ? null : modifyAttack.trim();
    }

    public String getDeleteAttack() {
        return deleteAttack;
    }

    public void setDeleteAttack(String deleteAttack) {
        this.deleteAttack = deleteAttack == null ? null : deleteAttack.trim();
    }

    public String getQueryAttack() {
        return queryAttack;
    }

    public void setQueryAttack(String queryAttack) {
        this.queryAttack = queryAttack == null ? null : queryAttack.trim();
    }

    public String getCountKey() {
        return countKey;
    }

    public void setCountKey(String countKey) {
        this.countKey = countKey == null ? null : countKey.trim();
    }
}