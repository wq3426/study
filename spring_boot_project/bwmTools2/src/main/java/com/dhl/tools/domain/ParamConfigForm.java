package com.dhl.tools.domain;

/**
 * 设置参数子节点表单
 * 
 * @author sunjitao
 *
 */
public class ParamConfigForm {

	private String[] id;

	private Integer[] childChecked;

	private Integer[] configTypeId;

	private Integer[] childPrimaryPriority;

	private Integer[] childStandbyPriority;

	private Double[] childDistance;

	private Integer[] childPickTool;

	public String[] getId() {
		return id;
	}

	public void setId(String[] id) {
		this.id = id;
	}

	public Integer[] getChildChecked() {
		return childChecked;
	}

	public void setChildChecked(Integer[] childChecked) {
		this.childChecked = childChecked;
	}

	public Integer[] getConfigTypeId() {
		return configTypeId;
	}

	public void setConfigTypeId(Integer[] configTypeId) {
		this.configTypeId = configTypeId;
	}

	public Integer[] getChildPrimaryPriority() {
		return childPrimaryPriority;
	}

	public void setChildPrimaryPriority(Integer[] childPrimaryPriority) {
		this.childPrimaryPriority = childPrimaryPriority;
	}

	public Integer[] getChildStandbyPriority() {
		return childStandbyPriority;
	}

	public void setChildStandbyPriority(Integer[] childStandbyPriority) {
		this.childStandbyPriority = childStandbyPriority;
	}

	public Double[] getChildDistance() {
		return childDistance;
	}

	public void setChildDistance(Double[] childDistance) {
		this.childDistance = childDistance;
	}

	public Integer[] getChildPickTool() {
		return childPickTool;
	}

	public void setChildPickTool(Integer[] childPickTool) {
		this.childPickTool = childPickTool;
	}
}
