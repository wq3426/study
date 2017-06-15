package com.dhl.tools.domain;

import java.util.ArrayList;
import java.util.List;

public class RecommendedLocationExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    public RecommendedLocationExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andMaterialIdIsNull() {
            addCriterion("materialId is null");
            return (Criteria) this;
        }

        public Criteria andMaterialIdIsNotNull() {
            addCriterion("materialId is not null");
            return (Criteria) this;
        }

        public Criteria andMaterialIdEqualTo(Integer value) {
            addCriterion("materialId =", value, "materialId");
            return (Criteria) this;
        }

        public Criteria andMaterialIdNotEqualTo(Integer value) {
            addCriterion("materialId <>", value, "materialId");
            return (Criteria) this;
        }

        public Criteria andMaterialIdGreaterThan(Integer value) {
            addCriterion("materialId >", value, "materialId");
            return (Criteria) this;
        }

        public Criteria andMaterialIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("materialId >=", value, "materialId");
            return (Criteria) this;
        }

        public Criteria andMaterialIdLessThan(Integer value) {
            addCriterion("materialId <", value, "materialId");
            return (Criteria) this;
        }

        public Criteria andMaterialIdLessThanOrEqualTo(Integer value) {
            addCriterion("materialId <=", value, "materialId");
            return (Criteria) this;
        }

        public Criteria andMaterialIdIn(List<Integer> values) {
            addCriterion("materialId in", values, "materialId");
            return (Criteria) this;
        }

        public Criteria andMaterialIdNotIn(List<Integer> values) {
            addCriterion("materialId not in", values, "materialId");
            return (Criteria) this;
        }

        public Criteria andMaterialIdBetween(Integer value1, Integer value2) {
            addCriterion("materialId between", value1, value2, "materialId");
            return (Criteria) this;
        }

        public Criteria andMaterialIdNotBetween(Integer value1, Integer value2) {
            addCriterion("materialId not between", value1, value2, "materialId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationIdIsNull() {
            addCriterion("cargoLocationId is null");
            return (Criteria) this;
        }

        public Criteria andCargoLocationIdIsNotNull() {
            addCriterion("cargoLocationId is not null");
            return (Criteria) this;
        }

        public Criteria andCargoLocationIdEqualTo(Integer value) {
            addCriterion("cargoLocationId =", value, "cargoLocationId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationIdNotEqualTo(Integer value) {
            addCriterion("cargoLocationId <>", value, "cargoLocationId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationIdGreaterThan(Integer value) {
            addCriterion("cargoLocationId >", value, "cargoLocationId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("cargoLocationId >=", value, "cargoLocationId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationIdLessThan(Integer value) {
            addCriterion("cargoLocationId <", value, "cargoLocationId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationIdLessThanOrEqualTo(Integer value) {
            addCriterion("cargoLocationId <=", value, "cargoLocationId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationIdIn(List<Integer> values) {
            addCriterion("cargoLocationId in", values, "cargoLocationId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationIdNotIn(List<Integer> values) {
            addCriterion("cargoLocationId not in", values, "cargoLocationId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationIdBetween(Integer value1, Integer value2) {
            addCriterion("cargoLocationId between", value1, value2, "cargoLocationId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationIdNotBetween(Integer value1, Integer value2) {
            addCriterion("cargoLocationId not between", value1, value2, "cargoLocationId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationTypeIdIsNull() {
            addCriterion("cargoLocationTypeId is null");
            return (Criteria) this;
        }

        public Criteria andCargoLocationTypeIdIsNotNull() {
            addCriterion("cargoLocationTypeId is not null");
            return (Criteria) this;
        }

        public Criteria andCargoLocationTypeIdEqualTo(Integer value) {
            addCriterion("cargoLocationTypeId =", value, "cargoLocationTypeId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationTypeIdNotEqualTo(Integer value) {
            addCriterion("cargoLocationTypeId <>", value, "cargoLocationTypeId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationTypeIdGreaterThan(Integer value) {
            addCriterion("cargoLocationTypeId >", value, "cargoLocationTypeId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationTypeIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("cargoLocationTypeId >=", value, "cargoLocationTypeId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationTypeIdLessThan(Integer value) {
            addCriterion("cargoLocationTypeId <", value, "cargoLocationTypeId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationTypeIdLessThanOrEqualTo(Integer value) {
            addCriterion("cargoLocationTypeId <=", value, "cargoLocationTypeId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationTypeIdIn(List<Integer> values) {
            addCriterion("cargoLocationTypeId in", values, "cargoLocationTypeId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationTypeIdNotIn(List<Integer> values) {
            addCriterion("cargoLocationTypeId not in", values, "cargoLocationTypeId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationTypeIdBetween(Integer value1, Integer value2) {
            addCriterion("cargoLocationTypeId between", value1, value2, "cargoLocationTypeId");
            return (Criteria) this;
        }

        public Criteria andCargoLocationTypeIdNotBetween(Integer value1, Integer value2) {
            addCriterion("cargoLocationTypeId not between", value1, value2, "cargoLocationTypeId");
            return (Criteria) this;
        }

        public Criteria andPickingFrequencyIsNull() {
            addCriterion("pickingFrequency is null");
            return (Criteria) this;
        }

        public Criteria andPickingFrequencyIsNotNull() {
            addCriterion("pickingFrequency is not null");
            return (Criteria) this;
        }

        public Criteria andPickingFrequencyEqualTo(Integer value) {
            addCriterion("pickingFrequency =", value, "pickingFrequency");
            return (Criteria) this;
        }

        public Criteria andPickingFrequencyNotEqualTo(Integer value) {
            addCriterion("pickingFrequency <>", value, "pickingFrequency");
            return (Criteria) this;
        }

        public Criteria andPickingFrequencyGreaterThan(Integer value) {
            addCriterion("pickingFrequency >", value, "pickingFrequency");
            return (Criteria) this;
        }

        public Criteria andPickingFrequencyGreaterThanOrEqualTo(Integer value) {
            addCriterion("pickingFrequency >=", value, "pickingFrequency");
            return (Criteria) this;
        }

        public Criteria andPickingFrequencyLessThan(Integer value) {
            addCriterion("pickingFrequency <", value, "pickingFrequency");
            return (Criteria) this;
        }

        public Criteria andPickingFrequencyLessThanOrEqualTo(Integer value) {
            addCriterion("pickingFrequency <=", value, "pickingFrequency");
            return (Criteria) this;
        }

        public Criteria andPickingFrequencyIn(List<Integer> values) {
            addCriterion("pickingFrequency in", values, "pickingFrequency");
            return (Criteria) this;
        }

        public Criteria andPickingFrequencyNotIn(List<Integer> values) {
            addCriterion("pickingFrequency not in", values, "pickingFrequency");
            return (Criteria) this;
        }

        public Criteria andPickingFrequencyBetween(Integer value1, Integer value2) {
            addCriterion("pickingFrequency between", value1, value2, "pickingFrequency");
            return (Criteria) this;
        }

        public Criteria andPickingFrequencyNotBetween(Integer value1, Integer value2) {
            addCriterion("pickingFrequency not between", value1, value2, "pickingFrequency");
            return (Criteria) this;
        }

        public Criteria andCargoLocationScoreIsNull() {
            addCriterion("cargoLocationScore is null");
            return (Criteria) this;
        }

        public Criteria andCargoLocationScoreIsNotNull() {
            addCriterion("cargoLocationScore is not null");
            return (Criteria) this;
        }

        public Criteria andCargoLocationScoreEqualTo(Integer value) {
            addCriterion("cargoLocationScore =", value, "cargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andCargoLocationScoreNotEqualTo(Integer value) {
            addCriterion("cargoLocationScore <>", value, "cargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andCargoLocationScoreGreaterThan(Integer value) {
            addCriterion("cargoLocationScore >", value, "cargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andCargoLocationScoreGreaterThanOrEqualTo(Integer value) {
            addCriterion("cargoLocationScore >=", value, "cargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andCargoLocationScoreLessThan(Integer value) {
            addCriterion("cargoLocationScore <", value, "cargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andCargoLocationScoreLessThanOrEqualTo(Integer value) {
            addCriterion("cargoLocationScore <=", value, "cargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andCargoLocationScoreIn(List<Integer> values) {
            addCriterion("cargoLocationScore in", values, "cargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andCargoLocationScoreNotIn(List<Integer> values) {
            addCriterion("cargoLocationScore not in", values, "cargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andCargoLocationScoreBetween(Integer value1, Integer value2) {
            addCriterion("cargoLocationScore between", value1, value2, "cargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andCargoLocationScoreNotBetween(Integer value1, Integer value2) {
            addCriterion("cargoLocationScore not between", value1, value2, "cargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationIdIsNull() {
            addCriterion("recommendedCargoLocationId is null");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationIdIsNotNull() {
            addCriterion("recommendedCargoLocationId is not null");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationIdEqualTo(Integer value) {
            addCriterion("recommendedCargoLocationId =", value, "recommendedCargoLocationId");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationIdNotEqualTo(Integer value) {
            addCriterion("recommendedCargoLocationId <>", value, "recommendedCargoLocationId");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationIdGreaterThan(Integer value) {
            addCriterion("recommendedCargoLocationId >", value, "recommendedCargoLocationId");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("recommendedCargoLocationId >=", value, "recommendedCargoLocationId");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationIdLessThan(Integer value) {
            addCriterion("recommendedCargoLocationId <", value, "recommendedCargoLocationId");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationIdLessThanOrEqualTo(Integer value) {
            addCriterion("recommendedCargoLocationId <=", value, "recommendedCargoLocationId");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationIdIn(List<Integer> values) {
            addCriterion("recommendedCargoLocationId in", values, "recommendedCargoLocationId");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationIdNotIn(List<Integer> values) {
            addCriterion("recommendedCargoLocationId not in", values, "recommendedCargoLocationId");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationIdBetween(Integer value1, Integer value2) {
            addCriterion("recommendedCargoLocationId between", value1, value2, "recommendedCargoLocationId");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationIdNotBetween(Integer value1, Integer value2) {
            addCriterion("recommendedCargoLocationId not between", value1, value2, "recommendedCargoLocationId");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationScoreIsNull() {
            addCriterion("recommendedCargoLocationScore is null");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationScoreIsNotNull() {
            addCriterion("recommendedCargoLocationScore is not null");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationScoreEqualTo(Integer value) {
            addCriterion("recommendedCargoLocationScore =", value, "recommendedCargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationScoreNotEqualTo(Integer value) {
            addCriterion("recommendedCargoLocationScore <>", value, "recommendedCargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationScoreGreaterThan(Integer value) {
            addCriterion("recommendedCargoLocationScore >", value, "recommendedCargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationScoreGreaterThanOrEqualTo(Integer value) {
            addCriterion("recommendedCargoLocationScore >=", value, "recommendedCargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationScoreLessThan(Integer value) {
            addCriterion("recommendedCargoLocationScore <", value, "recommendedCargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationScoreLessThanOrEqualTo(Integer value) {
            addCriterion("recommendedCargoLocationScore <=", value, "recommendedCargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationScoreIn(List<Integer> values) {
            addCriterion("recommendedCargoLocationScore in", values, "recommendedCargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationScoreNotIn(List<Integer> values) {
            addCriterion("recommendedCargoLocationScore not in", values, "recommendedCargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationScoreBetween(Integer value1, Integer value2) {
            addCriterion("recommendedCargoLocationScore between", value1, value2, "recommendedCargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andRecommendedCargoLocationScoreNotBetween(Integer value1, Integer value2) {
            addCriterion("recommendedCargoLocationScore not between", value1, value2, "recommendedCargoLocationScore");
            return (Criteria) this;
        }

        public Criteria andOptimizationRecommendIsNull() {
            addCriterion("optimizationRecommend is null");
            return (Criteria) this;
        }

        public Criteria andOptimizationRecommendIsNotNull() {
            addCriterion("optimizationRecommend is not null");
            return (Criteria) this;
        }

        public Criteria andOptimizationRecommendEqualTo(Integer value) {
            addCriterion("optimizationRecommend =", value, "optimizationRecommend");
            return (Criteria) this;
        }

        public Criteria andOptimizationRecommendNotEqualTo(Integer value) {
            addCriterion("optimizationRecommend <>", value, "optimizationRecommend");
            return (Criteria) this;
        }

        public Criteria andOptimizationRecommendGreaterThan(Integer value) {
            addCriterion("optimizationRecommend >", value, "optimizationRecommend");
            return (Criteria) this;
        }

        public Criteria andOptimizationRecommendGreaterThanOrEqualTo(Integer value) {
            addCriterion("optimizationRecommend >=", value, "optimizationRecommend");
            return (Criteria) this;
        }

        public Criteria andOptimizationRecommendLessThan(Integer value) {
            addCriterion("optimizationRecommend <", value, "optimizationRecommend");
            return (Criteria) this;
        }

        public Criteria andOptimizationRecommendLessThanOrEqualTo(Integer value) {
            addCriterion("optimizationRecommend <=", value, "optimizationRecommend");
            return (Criteria) this;
        }

        public Criteria andOptimizationRecommendIn(List<Integer> values) {
            addCriterion("optimizationRecommend in", values, "optimizationRecommend");
            return (Criteria) this;
        }

        public Criteria andOptimizationRecommendNotIn(List<Integer> values) {
            addCriterion("optimizationRecommend not in", values, "optimizationRecommend");
            return (Criteria) this;
        }

        public Criteria andOptimizationRecommendBetween(Integer value1, Integer value2) {
            addCriterion("optimizationRecommend between", value1, value2, "optimizationRecommend");
            return (Criteria) this;
        }

        public Criteria andOptimizationRecommendNotBetween(Integer value1, Integer value2) {
            addCriterion("optimizationRecommend not between", value1, value2, "optimizationRecommend");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table RecommendedLocation
     *
     * @mbg.generated do_not_delete_during_merge Sat Apr 15 16:39:20 CST 2017
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table RecommendedLocation
     *
     * @mbg.generated Sat Apr 15 16:39:20 CST 2017
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }
    }
}