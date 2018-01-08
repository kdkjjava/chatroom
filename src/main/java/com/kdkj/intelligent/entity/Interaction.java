package com.kdkj.intelligent.entity;

public class Interaction {
    private Integer id;
    /*
	 * 群主账号
	 */
    private Integer masterId;
    /*
	 * 添加用户后@对方
	 */
    private String afterAdd;
    /*
	 * 删除用户后@对方
	 */
    private String afterDelete;
    /*
	 * 修改用户后@对方
	 */
    private String modifyProvision;

    private String modifyPlaying;

    private String truncatePlaying;

    private String modifyNickname;

    private String usingImage;

    private String cancleMessage;

    private String receiveRequest;

    private String ensureRequest;

    private String ensurePlaying;

    private String commitFalse;

    private String lackProvision;

    private String limitProvision;

    private String gameStoped;

    private String undoMessage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    public String getAfterAdd() {
        return afterAdd;
    }

    public void setAfterAdd(String afterAdd) {
        this.afterAdd = afterAdd == null ? null : afterAdd.trim();
    }

    public String getAfterDelete() {
        return afterDelete;
    }

    public void setAfterDelete(String afterDelete) {
        this.afterDelete = afterDelete == null ? null : afterDelete.trim();
    }

    public String getModifyProvision() {
        return modifyProvision;
    }

    public void setModifyProvision(String modifyProvision) {
        this.modifyProvision = modifyProvision == null ? null : modifyProvision.trim();
    }

    public String getModifyPlaying() {
        return modifyPlaying;
    }

    public void setModifyPlaying(String modifyPlaying) {
        this.modifyPlaying = modifyPlaying == null ? null : modifyPlaying.trim();
    }

    public String getTruncatePlaying() {
        return truncatePlaying;
    }

    public void setTruncatePlaying(String truncatePlaying) {
        this.truncatePlaying = truncatePlaying == null ? null : truncatePlaying.trim();
    }

    public String getModifyNickname() {
        return modifyNickname;
    }

    public void setModifyNickname(String modifyNickname) {
        this.modifyNickname = modifyNickname == null ? null : modifyNickname.trim();
    }

    public String getUsingImage() {
        return usingImage;
    }

    public void setUsingImage(String usingImage) {
        this.usingImage = usingImage == null ? null : usingImage.trim();
    }

    public String getCancleMessage() {
        return cancleMessage;
    }

    public void setCancleMessage(String cancleMessage) {
        this.cancleMessage = cancleMessage == null ? null : cancleMessage.trim();
    }

    public String getReceiveRequest() {
        return receiveRequest;
    }

    public void setReceiveRequest(String receiveRequest) {
        this.receiveRequest = receiveRequest == null ? null : receiveRequest.trim();
    }

    public String getEnsureRequest() {
        return ensureRequest;
    }

    public void setEnsureRequest(String ensureRequest) {
        this.ensureRequest = ensureRequest == null ? null : ensureRequest.trim();
    }

    public String getEnsurePlaying() {
        return ensurePlaying;
    }

    public void setEnsurePlaying(String ensurePlaying) {
        this.ensurePlaying = ensurePlaying == null ? null : ensurePlaying.trim();
    }

    public String getCommitFalse() {
        return commitFalse;
    }

    public void setCommitFalse(String commitFalse) {
        this.commitFalse = commitFalse == null ? null : commitFalse.trim();
    }

    public String getLackProvision() {
        return lackProvision;
    }

    public void setLackProvision(String lackProvision) {
        this.lackProvision = lackProvision == null ? null : lackProvision.trim();
    }

    public String getLimitProvision() {
        return limitProvision;
    }

    public void setLimitProvision(String limitProvision) {
        this.limitProvision = limitProvision == null ? null : limitProvision.trim();
    }

    public String getGameStoped() {
        return gameStoped;
    }

    public void setGameStoped(String gameStoped) {
        this.gameStoped = gameStoped == null ? null : gameStoped.trim();
    }

    public String getUndoMessage() {
        return undoMessage;
    }

    public void setUndoMessage(String undoMessage) {
        this.undoMessage = undoMessage == null ? null : undoMessage.trim();
    }
}