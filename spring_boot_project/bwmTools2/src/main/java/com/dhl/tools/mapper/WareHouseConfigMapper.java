package com.dhl.tools.mapper;

import com.dhl.tools.domain.WareHouseConfig;
import com.dhl.tools.domain.WareHouseConfigExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WareHouseConfigMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table WareHouseConfig
     *
     * @mbg.generated
     */
    long countByExample(WareHouseConfigExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table WareHouseConfig
     *
     * @mbg.generated
     */
    int deleteByExample(WareHouseConfigExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table WareHouseConfig
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table WareHouseConfig
     *
     * @mbg.generated
     */
    int insert(WareHouseConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table WareHouseConfig
     *
     * @mbg.generated
     */
    int insertSelective(WareHouseConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table WareHouseConfig
     *
     * @mbg.generated
     */
    List<WareHouseConfig> selectByExample(WareHouseConfigExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table WareHouseConfig
     *
     * @mbg.generated
     */
    WareHouseConfig selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table WareHouseConfig
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") WareHouseConfig record, @Param("example") WareHouseConfigExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table WareHouseConfig
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") WareHouseConfig record, @Param("example") WareHouseConfigExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table WareHouseConfig
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(WareHouseConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table WareHouseConfig
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(WareHouseConfig record);
}