/**
 * Created by liuso on 2017/4/11.
 */

// 当前页
var currentPage = 1;
// 数量
var currentSize = 10;

//查询结果
var queryResult = '';

/**
 * 页面初始化
 */
$(function () {
	
    // 加载查询条件
    loadQueryConditionData();

    // 绑定搜索事件
    $('#queryBut').click(function(){
    	loadData(1, currentSize);
    });
    
    // 绑定全选事件
    $('[name=selectAll]').click(function(){
    	$('[name=dataId]').prop('checked',!!$(this).prop('checked'));
    });
    
    // 修改参数
    $('#editParam').click(function(){
    	var selectedFlag = false;
    	$.each($('[name=dataId]'),function(i,item){
    		if($(item).prop("checked")){
    			selectedFlag = true;
    		}
    	});
    	if(!selectedFlag){
    		alert('请选中要编辑的数据!');
    	}else{
    		$("#batchUpdateParamModal").modal('show');
    	}
    });
    
    // 绑定同步参数事件
    $("#syncParamBut").click(function(){
    	var selectedFlag = false;
    	$.each($('[name=dataId]'),function(i,item){
    		if($(item).prop("checked")){
    			selectedFlag = true;
    		}
    	});
    	if(!selectedFlag){
    		alert('请选中要同步的数据!');
    	}else{
    		$("#syncParamModal").modal('show');
    	}
    });
    
    // 设置参数模态框
    $('#setParamModal').on('click', '.btn-primary', function () {
    	// 校验用户是否勾选
    	var selectedFlag = false;
    	$("#child").find('[name=childChecked]').each(function(){
    		if($(this).prop("checked")){
    			selectedFlag = true;
    		}
    	})
    	if(!selectedFlag){
    		alert('请勾选需要设置的等级');
    		return;
    	}
        $(this).parent().prev().find('form').submit();
    }).on('hidden.bs.modal', function () {
        //resetForm($(this));
    }).on('show.bs.modal', function () {
    	var modal = $(this);
    	
    	var configTypeId = getQueryConfigTypeId();
    	var parentId = getQueryParentId();
    	
    	var currentConfig = getConfigById(configTypeId);
    	
    	// 设置标题
    	//$(".setParamTitle").html(currentConfig.wareHouseTypeAlias);
    	
    	modal.find('[name=parentId]').val(parentId);
    	
    	// 子级别信息
    	var childData = getChildConfig(configTypeId);
    	var templateHtml = $('#childTemplate').val();
    	var htmlBox = [];
    	var index = 0;
    	
    	var setValue = function(item){
    		var configTypeId = item.wareHouseTypeId;
    		return templateHtml.replace('{configTypeId}',configTypeId).replace('{alias}',item.wareHouseTypeAlias)
    				.replace('{distinceSpanClass}',(1 == configTypeId || 2 == configTypeId) ? '' :'none')
    				.replace('{pickToolSpanClass}',4 == configTypeId ? '' :'none')
    				.replace('{index}',index++).replace('{distanceName}', 1 == configTypeId ? '距离' : (2 == configTypeId ? '宽度' : ''));
    	};
    	htmlBox.push(setValue(currentConfig));
    	$.each(childData,function(i,item){
    		htmlBox.push(setValue(item));
    	});
    	
    	$('#child').html(htmlBox.join(''));
    	
    }).find('form').formValidation({
        framework: 'bootstrap',
        excluded: ':disabled',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        fields: {
        }
    }).on('success.form.fv', function (e) {
    	
        // Prevent form submission
        e.preventDefault();
        
        var $form = $(e.target),
            fv = $form.data('formValidation'),
            $modal = $form.parent().parent().parent().parent();
        
        $.LoadingOverlay("show",{custom  : getCustomElement('正在设置参数，请耐心等待...')});
        $.ajax({
            url: $form.attr('action'),
            type: 'PATCH',
            data: $form.serialize(),
            success: function (result) {
            	$.LoadingOverlay("hide");
            	console.log('设置成功');
                // 隐藏模态框
                $modal.modal('hide');
                // 重新加载数据
                loadData(1, currentSize);
            },error: function(result){
            	$.LoadingOverlay("hide");
            	console.log('设置失败');
                $modal.find('.alert').show();
            }
        });
    });
    
    // 优先级校验规则
    var primaryPriorityValidation = {
    	validators: {
    		notEmpty: {
                message: '优先级不能为空'
            },
            integer: {
                thousandsSeparator: '',
                decimalSeparator: '',
                message: '优先级必须为正整数'
            },
            callback:{
            	message : '优先级必须大于零',
            	callback : function (value, validator, $field){
            		if(!value){
            			return true;
            		}
            		if(!FormValidation.Validator.integer.validate(validator,$field,null)){
            			return true;
            		}
            		if(parseInt(value) > 0){
            			return true;
            		}
            		return false;
            	}
            }
        }
    };
    
    // 次优先级校验规则
    var standbyPriorityValidation = {
    	validators: {
    		notEmpty: {
                message: '次优先级不能为空'
            },
            integer: {
                thousandsSeparator: '',
                decimalSeparator: '',
                message: '次优先级必须为正整数'
            },
            callback:{
            	message : '次优先级必须大于零',
            	callback : function (value, validator, $field){
            		if(!value){
            			return true;
            		}
            		if(!FormValidation.Validator.integer.validate(validator,$field,null)){
            			return true;
            		}
            		if(parseInt(value) > 0){
            			return true;
            		}
            		return false;
            	}
            }
        }
    };
    
    // 距离校验规则
    var distanceValidation = {
		validators: {
            notEmpty: {
                message: '距离不能为空'
            },
            numeric: {
                message: '距离必须为数字'
            },
            callback:{
            	message : '距离必须大于零',
            	callback : function (value, validator, $field){
            		if(!value){
            			return true;
            		}
            		if(!$.isNumeric(value)){
            			return true;
            		}
            		if(parseFloat(value) > 0){
            			return true;
            		}
            		return false;
            	}
            }
        }
    };
    
    // 宽度校验规则
    var widthValidation = {
		validators: {
            notEmpty: {
                message: '宽度不能为空'
            },
            numeric: {
                message: '宽度必须为数字'
            },
            callback:{
            	message : '宽度必须大于零',
            	callback : function (value, validator, $field){
            		if(!value){
            			return true;
            		}
            		if(!$.isNumeric(value)){
            			return true;
            		}
            		if(parseFloat(value) > 0){
            			return true;
            		}
            		return false;
            	}
            }
        }
    };
    
    // 批量修改参数模态框
    $('#batchUpdateParamModal').on('click', '.btn-primary', function () {
        $(this).parent().prev().find('form').submit();
    }).on('hidden.bs.modal', function () {
    	
    	var $form = $(this).find('form');
    	// 去掉校验规则
    	var configTypeId = getQueryConfigTypeId();
    	$form.formValidation('removeField',$form.find("[name=childPrimaryPriority]"));
    	$form.formValidation('removeField',$form.find("[name=childStandbyPriority]"));
    	if(1 == configTypeId || 2 == configTypeId){
    		$form.formValidation('removeField',$form.find("[name=childDistance]"));
    	}else if(4 == configTypeId){
    		$form.formValidation('removeField',$form.find("[name=childPickTool]"));
    	}
        $form.eq(0).html('');
        $form.formValidation('resetForm', true);
        
    }).on('show.bs.modal', function () {
    	var modal = $(this);
    	var $form = modal.find('form');
    	
    	var configTypeId = getQueryConfigTypeId();
    	var parentId = getQueryParentId();
    	
    	var warehouseConfig = getConfigById(configTypeId);
    	
    	// 设置标题
    	$("#updateEditParamTitle").html(warehouseConfig.wareHouseTypeAlias);
    	
    	var data = {
    		configType: configTypeId || '',
	    	parentId: parentId || '',
	        page: 1,
	        size: 10000
    	};
    	
    	var editTemplate = $("#editTemplate").val();
    	editTemplate = editTemplate.replace('{alias}',warehouseConfig.wareHouseTypeAlias).replace('{configTypeId}',configTypeId);
    	if(1 == configTypeId || 2 == configTypeId){
    		editTemplate = editTemplate.replace('{distinceSpanClass}','');
    		editTemplate = editTemplate.replace('{distanceName}',1 == configTypeId ? '距离' : '宽度');
    	}else{
    		editTemplate = editTemplate.replace('{distinceSpanClass}','none');
    	}
    	if(4 == configTypeId){
    		editTemplate = editTemplate.replace('{pickToolSpanClass}','');
    	}else{
    		editTemplate = editTemplate.replace('{pickToolSpanClass}','none');
    	}
    	
    	var html= '';
    	
		if(queryResult){
			
			// 获取选中的数据
			var selectedIds = getSelected();
			
			$.each(queryResult,function(index,item){
				if(selectedIds.indexOf(item.id) >= 0){
					html += editTemplate.replace('{id}',item.id).replace('{code}',item.code)
						.replace('{primaryPriority}',formatData(item.primaryPriority)).replace('{standbyPriority}',formatData(item.standbyPriority))
						.replace('{distance}',formatData(item.distance));
				}
			});
			$form.html(html);
			if(4 == configTypeId){
				$.each($form.find('[name=childPickTool]'),function(index,item){
					$(item).find("option[value='"+ (queryResult[index].pickTool || '') +"']").attr("selected",true);
				});
			}
			
			// 添加校验规则
			modal.find('form').formValidation('addField',modal.find("[name=childPrimaryPriority]"),primaryPriorityValidation);
			modal.find('form').formValidation('addField',modal.find("[name=childStandbyPriority]"),standbyPriorityValidation);
			if(1 == configTypeId){
				modal.find('form').formValidation('addField',modal.find("[name=childDistance]"),distanceValidation);
			}else if(2 == configTypeId){
				modal.find('form').formValidation('addField',modal.find("[name=childDistance]"),widthValidation);
			}else if(4 == configTypeId){
				modal.find('form').formValidation('addField',modal.find("[name=childPickTool]"),{
					validators: {
			            notEmpty: {
			                message: '拣货工具不能为空'
			            }
					}
				});
			}
		}
    }).find('form').formValidation({
        framework: 'bootstrap',
        excluded: ':disabled',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        fields: {
        }
    }).on('success.form.fv', function (e) {
    	
        e.preventDefault();
        
        var $form = $(e.target),
            fv = $form.data('formValidation'),
            $modal = $form.parent().parent().parent().parent();

        $.LoadingOverlay("show");
        $.ajax({
            url: $form.attr('action'),
            type: 'PATCH',
            data: $form.serialize(),
            success: function (result) {
            	$.LoadingOverlay("hide");
            	console.log('修改成功');
                // 隐藏模态框
                $modal.modal('hide');
                // 重新加载数据
                loadData(1, currentSize);
            },error: function(result){
            	$.LoadingOverlay("hide");
            	console.log('修改失败');
                $modal.find('.alert').show();
            }
        });
    });
    
    // 同步参数模态框
    $('#syncParamModal').on('click', '.btn-primary', function () {
        $(this).parent().prev().find('form').submit();
    }).on('hidden.bs.modal', function () {
    	$('#syncChildList').html('');
    }).on('show.bs.modal', function () {
    	var modal = $(this);
    	
    	var configTypeId = getQueryConfigTypeId();
    	var parentId = getQueryParentId();
    	
    	var warehouseConfig = getConfigById(configTypeId);
    	
    	// 设置标题
    	$("#syncParamTitle").html(warehouseConfig.wareHouseTypeAlias);
    	
    	$.get($('#baseUrl').attr('href') + 'cargo-location-data/findListByParentId',{parentId:parentId}, function (result){
    		if(result){
    			
    			// 选中的信息
    			var selectedIds = getSelected();
    			
    			modal.find('[name=syncIds]').val(selectedIds);
    			var referId = modal.find('[name=referId]');
    			referId.empty();
    			$.each(result,function(index,item){
    				// 参考对象不应该在选中列表中
    				if(selectedIds.indexOf(item.id) < 0){
    					referId.append("<option value='"+ item.id +"'>" + item.code + "</option>")
    				}
    			})
    			
    			// 初始化同步级别
    			var templateHtml = '<input type="checkbox" name="configTypeIds" value="{id}" checked>{alias}';
    			var htmlArray = [];
    			$.each(getChildConfig(configTypeId),function(index,item){
    				htmlArray.push(templateHtml.replace('{id}',item.wareHouseTypeId).replace('{alias}',item.wareHouseTypeAlias));
    			})
    			$('#syncChildList').html(htmlArray.join(''));
    		}
    	})
    }).find('form').formValidation({
        framework: 'bootstrap',
        excluded: ':disabled',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        fields: {
        	referId : {
        		validators: {
                    notEmpty: {
                        message: '参考对象不能为空'
                    }
                }
        	}
        }
    }).on('success.form.fv', function (e) {
    	
        e.preventDefault();
        
        var $form = $(e.target),
            fv = $form.data('formValidation'),
            $modal = $form.parent().parent().parent().parent();

        $.LoadingOverlay("show",{custom  : getCustomElement('正在同步参数，请耐心等待...')});
        $.ajax({
            url: $form.attr('action'),
            type: 'PATCH',
            data: $form.serialize(),
            success: function (result) {
            	$.LoadingOverlay("hide");
            	console.log('同步成功');
                // 隐藏模态框
                $modal.modal('hide');
                // 重新加载数据
                loadData(1, currentSize);
            },error: function(result){
            	$.LoadingOverlay("hide");
            	console.log('同步失败');
                $modal.find('.alert').show();
            }
        });
    });
});

function formatData(data){
	if('0' == data){
		return  '0';
	}
	return data ? data : '';
}

// 获取选中的信息
function getSelected(){
	// 选中的信息
	var selectedIds = '';
	$.each($('[name=dataId]'),function(index,item){
		if($(item).prop("checked")){
			selectedIds += $(item).val() + ",";
		}
	});
	return selectedIds;
}

//重置modal
function resetForm($modal){
	var $form = $modal.find('form');
	$form.find(':input').val('');
	// 重置所有select下拉框
	$form.find('select').each(function(index,item){
		$.each($(item).find('option'),function(index,item){
			if($(item).val() == ''){
				$(item).attr("selected",true);
			}else{
				$(item).attr("selected",false);
			}
		});
	});
	$("#child").html('');
	$("#distinceSpan,pickToolSpan").hide();
	$modal.find('.alert').hide();
	$form.formValidation('resetForm', true);
}

// 当前仓库配置
var warehouseconfigjson = [];

//加载查询条件
function loadQueryConditionData(){
	$.get($('#baseUrl').attr('href') + 'ware-house-config/list',function (result) {
		if(result){
			warehouseconfigjson = result;
			var template = $("#queryTemplateId").val();
			var html = '';
			$.each(result,function(index,item){
				// 最后一级不显示
				if(index != result.length - 1){
					html += template.replace('{id}',item.id).replace('{alias}',item.wareHouseTypeAlias).replace('{name}',item.wareHouseTypeId);
				}
			});
			$("#queryCondition").html(html);
			
			// 给下拉框绑定change事件
			typeChange();
			
			// 默认初始化第一级数据
			initTypeData('',$("#queryCondition").find('select').first());
			
			// 加载数据
		    loadData(currentPage, currentSize);
		}
	});
}

// 查询条件下拉框change事件
function typeChange(){
	$("#queryCondition").find('select').change(function(e){
		var nextSelects = $(e.target).nextAll("select");
		$.each(nextSelects,function(index,item){
			$(item).empty();
			$(item).append("<option value=''>请选择</option>");
		});
		if($(e.target).val() != ''){
			var next = nextSelects.first();
			if(next.length == 1){
				initTypeData($(e.target).val(),next);
			}
		}
	});
}

function initTypeData(parentId,target){
	$.get($('#baseUrl').attr('href') + 'cargo-location-data/findListByParentId',{parentId : parentId},function (result) {
		if(result){
			$.each(result,function(index,item){
				target.append("<option value='"+item.id+"'>" + item.code + "</option>");
			});
		}
	});
}

/**
 * 加载数据
 * @param page 页码
 * @param size 数量
 */
function loadData(page, size) {

    currentSize = size;
    currentPage = page;
    
    var parentId = getQueryParentId();
    var configTypeId = getQueryConfigTypeId();
    if(!configTypeId){
    	return;
    }
    var data = {
    	configType: configTypeId || '',
    	parentId: parentId || '',
        page: page || 1,
        size: size || 20
    };

    // 获取数据
    $.LoadingOverlay("show");
    $.get($('#baseUrl').attr('href') + 'cargo-location-data', data, function (result) {
    	$.LoadingOverlay("hide");
    	
    	// 设置具体编码标题
    	var config = getConfigById(configTypeId);
    	$("#codeId").html(config ? config.wareHouseTypeAlias : '');
    	
        if (result) {
        	
        	queryResult = result.list;
        	
        	if(4 == configTypeId){
        		$('#pickToolTh').show();
        	}else{
        		$('#pickToolTh').hide();
        	}
        	
        	if(1 == configTypeId || 2 == configTypeId){
        		$('#distanceTh').show();
        	}else{
        		$('#distanceTh').hide();
        	}
        	
        	if(1 == configTypeId){
        		$('#distanceTh').html('距离(m)');
        	}else if(2 == configTypeId){
        		$('#distanceTh').html('宽度(m)');
        	}
        	
            // 清空表格
            $('#dataList').empty();
            $('[name=selectAll]').prop("checked",false);
            
            // 添加数据到表格
            $.each(result.list, function (index, item) {
                $('#dataList').append('<tr>' +
                    '<td><input type="checkbox" name="dataId" value="' + item.id + '"/>' + (result.startRow + index) + '</td>' +
                    '<td>' + (item.code || '') + '</td>' +
                    '<td>' + (item.primaryPriority || '') + '</td>' +
                    '<td>' + (item.standbyPriority || '') + '</td>' +
                    ((1== configTypeId || 2 == configTypeId) ? ('<td>' + (item.distance || '') + '</td>') : '') +
                    (4 == configTypeId ? ('<td>' + (getPickTool(item.pickTool || '')) + '</td>') : '') + 
                    '</tr>');
            });

            // 初始化分页控件
            $("#pagination").bs_pagination({
                currentPage: currentPage,
                totalRows: result.total,
                rowsPerPage: currentSize,
                totalPages: result.pages,
                onChangePage: function (event, data) {
                    console.log(event, data);
                    loadData(data.currentPage, data.rowsPerPage);
                }
            });
        } else {
        	queryResult = '';
            console.log('获取货位失败，请重试');
        }
    });
}

function getPickTool(pickTool){
	if(pickTool){
		if(1 == pickTool){
			return "人";
		}else if(2 == pickTool){
			return "叉车";
		}else{
			return "拣货车";
		}
	}
	return '';
}

function getQueryParentId(){
	var parentId = '';
	$.each($("#queryCondition").find('select'),function(index,item){
		var v = $(item).val();
		if(v){
			parentId = v;
		}
	});
	return parentId;
}
function getQueryConfigTypeId(){
	var index = -1;
	$.each($("#queryCondition").find('select'),function(i,item){
		if($(item).val()){
			index++;
		}
	});
	if(warehouseconfigjson.length != 0){
		return warehouseconfigjson[index + 1].wareHouseTypeId;
	}
	return '';
}

function getChildConfig(configId){
	var data = [];
	if(configId){
		var index = -1;
		$.each(warehouseconfigjson,function(i,item){
			if(index != -1){
				data.push(item);
			}
			if(item.wareHouseTypeId == configId){
				index = i;
			}
		});
	}
	return data;
}

function getConfigById(configId){
	var data = {};
	if(configId){
		$.each(warehouseconfigjson,function(i,item){
			if(item.wareHouseTypeId == configId){
				data = item;
			}
		});
	}
	return data;
}
