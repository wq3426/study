package com.dhl.tools.domain;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table WareHouseConfig
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class WareHouseConfig implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table WareHouseConfig
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.id
     *
     * @mbg.generated
     */
    private Integer id;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.wareHouseId
     *
     * @mbg.generated
     */
    private Integer wareHouseId;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.wareHouseTypeId
     *
     * @mbg.generated
     */
    private Integer wareHouseTypeId;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.wareHouseTypeAlias
     *
     * @mbg.generated
     */
    private String wareHouseTypeAlias;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.length
     *
     * @mbg.generated
     */
    private Integer length;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.width
     *
     * @mbg.generated
     */
    private Integer width;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.primaryPriority
     *
     * @mbg.generated
     */
    private Integer primaryPriority;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.standbyPriority
     *
     * @mbg.generated
     */
    private Integer standbyPriority;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.mode
     *
     * @mbg.generated
     */
    private Integer mode;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.pickTool
     *
     * @mbg.generated
     */
    private Integer pickTool;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.handsUpDrivingSpeed
     *
     * @mbg.generated
     */
    private Double handsUpDrivingSpeed;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.rank
     *
     * @mbg.generated
     */
    private Integer rank;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.createBy
     *
     * @mbg.generated
     */
    private String createBy;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.createDate
     *
     * @mbg.generated
     */
    private Date createDate;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.lastModifiedBy
     *
     * @mbg.generated
     */
    private String lastModifiedBy;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column WareHouseConfig.lastModifiedDate
     *
     * @mbg.generated
     */
    private Date lastModifiedDate;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.id
     *
     * @return the value of WareHouseConfig.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.id
     *
     * @param id the value for WareHouseConfig.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.wareHouseId
     *
     * @return the value of WareHouseConfig.wareHouseId
     *
     * @mbg.generated
     */
    public Integer getWareHouseId() {
        return wareHouseId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.wareHouseId
     *
     * @param wareHouseId the value for WareHouseConfig.wareHouseId
     *
     * @mbg.generated
     */
    public void setWareHouseId(Integer wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.wareHouseTypeId
     *
     * @return the value of WareHouseConfig.wareHouseTypeId
     *
     * @mbg.generated
     */
    public Integer getWareHouseTypeId() {
        return wareHouseTypeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.wareHouseTypeId
     *
     * @param wareHouseTypeId the value for WareHouseConfig.wareHouseTypeId
     *
     * @mbg.generated
     */
    public void setWareHouseTypeId(Integer wareHouseTypeId) {
        this.wareHouseTypeId = wareHouseTypeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.wareHouseTypeAlias
     *
     * @return the value of WareHouseConfig.wareHouseTypeAlias
     *
     * @mbg.generated
     */
    public String getWareHouseTypeAlias() {
        return wareHouseTypeAlias;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.wareHouseTypeAlias
     *
     * @param wareHouseTypeAlias the value for WareHouseConfig.wareHouseTypeAlias
     *
     * @mbg.generated
     */
    public void setWareHouseTypeAlias(String wareHouseTypeAlias) {
        this.wareHouseTypeAlias = wareHouseTypeAlias == null ? null : wareHouseTypeAlias.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.length
     *
     * @return the value of WareHouseConfig.length
     *
     * @mbg.generated
     */
    public Integer getLength() {
        return length;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.length
     *
     * @param length the value for WareHouseConfig.length
     *
     * @mbg.generated
     */
    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.width
     *
     * @return the value of WareHouseConfig.width
     *
     * @mbg.generated
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.width
     *
     * @param width the value for WareHouseConfig.width
     *
     * @mbg.generated
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.primaryPriority
     *
     * @return the value of WareHouseConfig.primaryPriority
     *
     * @mbg.generated
     */
    public Integer getPrimaryPriority() {
        return primaryPriority;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.primaryPriority
     *
     * @param primaryPriority the value for WareHouseConfig.primaryPriority
     *
     * @mbg.generated
     */
    public void setPrimaryPriority(Integer primaryPriority) {
        this.primaryPriority = primaryPriority;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.standbyPriority
     *
     * @return the value of WareHouseConfig.standbyPriority
     *
     * @mbg.generated
     */
    public Integer getStandbyPriority() {
        return standbyPriority;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.standbyPriority
     *
     * @param standbyPriority the value for WareHouseConfig.standbyPriority
     *
     * @mbg.generated
     */
    public void setStandbyPriority(Integer standbyPriority) {
        this.standbyPriority = standbyPriority;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.mode
     *
     * @return the value of WareHouseConfig.mode
     *
     * @mbg.generated
     */
    public Integer getMode() {
        return mode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.mode
     *
     * @param mode the value for WareHouseConfig.mode
     *
     * @mbg.generated
     */
    public void setMode(Integer mode) {
        this.mode = mode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.pickTool
     *
     * @return the value of WareHouseConfig.pickTool
     *
     * @mbg.generated
     */
    public Integer getPickTool() {
        return pickTool;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.pickTool
     *
     * @param pickTool the value for WareHouseConfig.pickTool
     *
     * @mbg.generated
     */
    public void setPickTool(Integer pickTool) {
        this.pickTool = pickTool;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.handsUpDrivingSpeed
     *
     * @return the value of WareHouseConfig.handsUpDrivingSpeed
     *
     * @mbg.generated
     */
    public Double getHandsUpDrivingSpeed() {
        return handsUpDrivingSpeed;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.handsUpDrivingSpeed
     *
     * @param handsUpDrivingSpeed the value for WareHouseConfig.handsUpDrivingSpeed
     *
     * @mbg.generated
     */
    public void setHandsUpDrivingSpeed(Double handsUpDrivingSpeed) {
        this.handsUpDrivingSpeed = handsUpDrivingSpeed;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.rank
     *
     * @return the value of WareHouseConfig.rank
     *
     * @mbg.generated
     */
    public Integer getRank() {
        return rank;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.rank
     *
     * @param rank the value for WareHouseConfig.rank
     *
     * @mbg.generated
     */
    public void setRank(Integer rank) {
        this.rank = rank;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.createBy
     *
     * @return the value of WareHouseConfig.createBy
     *
     * @mbg.generated
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.createBy
     *
     * @param createBy the value for WareHouseConfig.createBy
     * @mbg.generated
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.createDate
     *
     * @return the value of WareHouseConfig.createDate
     * @mbg.generated
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.createDate
     *
     * @param createDate the value for WareHouseConfig.createDate
     * @mbg.generated
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.lastModifiedBy
     *
     * @return the value of WareHouseConfig.lastModifiedBy
     * @mbg.generated
     */
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.lastModifiedBy
     *
     * @param lastModifiedBy the value for WareHouseConfig.lastModifiedBy
     * @mbg.generated
     */
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy == null ? null : lastModifiedBy.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column WareHouseConfig.lastModifiedDate
     *
     * @return the value of WareHouseConfig.lastModifiedDate
     * @mbg.generated
     */
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column WareHouseConfig.lastModifiedDate
     *
     * @param lastModifiedDate the value for WareHouseConfig.lastModifiedDate
     * @mbg.generated
     */
    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table WareHouseConfig
     *
     * @mbg.generated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", wareHouseId=").append(wareHouseId);
        sb.append(", wareHouseTypeId=").append(wareHouseTypeId);
        sb.append(", wareHouseTypeAlias=").append(wareHouseTypeAlias);
        sb.append(", length=").append(length);
        sb.append(", width=").append(width);
        sb.append(", primaryPriority=").append(primaryPriority);
        sb.append(", standbyPriority=").append(standbyPriority);
        sb.append(", mode=").append(mode);
        sb.append(", pickTool=").append(pickTool);
        sb.append(", handsUpDrivingSpeed=").append(handsUpDrivingSpeed);
        sb.append(", rank=").append(rank);
        sb.append(", createBy=").append(createBy);
        sb.append(", createDate=").append(createDate);
        sb.append(", lastModifiedBy=").append(lastModifiedBy);
        sb.append(", lastModifiedDate=").append(lastModifiedDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}