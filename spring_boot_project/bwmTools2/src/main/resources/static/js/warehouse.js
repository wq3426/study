/**
 * Created by liuso on 2017/4/11.
 */

// 当前页
var currentPage = 1;
// 数量
var currentSize = 10;

/**
 * 页面初始化
 */
var updateFlag,deleteFlag;
$(function () {
	
	// 更新权限
    updateFlag = ('true' == $("#updateFlag").val());
    // 删除权限
    deleteFlag = ('true' == $("#deleteFlag").val());

    // 加载数据
    loadData(currentPage, currentSize);
    
    // 绑定搜索事件
    $('#queryBut').click(function(){
    	loadData(1, currentSize);
    });
    
    // 字符长度校验规则
    var lengthsValidation = {
    	validators: {
    		notEmpty: {
                message: '字符长度不能为空'
            },
            integer: {
                thousandsSeparator: '',
                decimalSeparator: '',
                message: '字符长度必须为正整数'
            },
            callback:{
            	message : '字符长度必须大于零',
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
    // 别名校验规则
    var aliasValidation = {
    	validators: {
    		notEmpty: {
                message: '别名不能为空'
            }
        }
    };
    
    // 绑定等级数量改变事件
    $("[name=gradeCount]").change(function(){
    	
    	var $form = $(this).parent().parent().parent().parent().find('form');
    	var typeDiv = $form.find('.paramClass');
    	
    	var length = typeDiv.find("[name=lengths]").length;
    	if(length != 0){
    		$form.formValidation('removeField',typeDiv.find("[name=lengths]"));
    		$form.formValidation('removeField',typeDiv.find("[name=alias]"));
    	}
    	
    	var v = $(this).val();
    	if('' != v){
    		typeDiv.empty();
    		var template = $("#gradeTemplateId").val();
    		var html = '';
    		for(var i = 0; i < parseInt(v);i++){
    			html += template.replace('{index}',(i + 1));
    		}
    		typeDiv.html(html);
    		$form.formValidation('addField',typeDiv.find("[name=lengths]"),lengthsValidation);
    		$form.formValidation('addField',typeDiv.find("[name=alias]"),aliasValidation);
    		
    		typeDiv.find('[name=typeIds]').change(function(){
    			$(this).parent().parent().find('[name=alias]').val($(this).find('option:selected').text());
    		});
    	}else{
    		typeDiv.empty();
    	}
    });
    
    function changeEvent($form,typeDiv,v){
    	var length = typeDiv.find("[name=lengths]").length;
    	if(length != 0){
    		$form.formValidation('removeField',typeDiv.find("[name=lengths]"));
    		$form.formValidation('removeField',typeDiv.find("[name=alias]"));
    	}
    	
    	if('' != v){
    		typeDiv.empty();
    		var template = $("#gradeTemplateId").val();
    		var html = '';
    		for(var i = 0; i < parseInt(v);i++){
    			html += template.replace('{index}',(i + 1));
    		}
    		typeDiv.html(html);
    		$form.formValidation('addField',typeDiv.find("[name=lengths]"),lengthsValidation);
    		$form.formValidation('addField',typeDiv.find("[name=alias]"),aliasValidation);
    	}else{
    		typeDiv.empty();
    	}
    }
    
    var fileds = {
		stallType: {
            validators: {
                notEmpty: {
                    message: '地摊货位类型不能为空'
                }
            }
        },
        personDrivingSpeed:{
        	validators: {
                notEmpty: {
                    message: '人行驶速度不能为空'
                },
                numeric: {
                    message: '人行驶速度必须为数字'
                },
                callback:{
                	message : '人行驶速度必须大于零',
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
        },
        personPickUpSpeed:{
        	validators: {
                notEmpty: {
                    message: '人抬胳膊速度不能为空'
                },
                numeric: {
                    message: '人抬胳膊速度必须为数字'
                },
                callback:{
                	message : '人抬胳膊速度必须大于零',
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
        },
        forkliftDrivingSpeed:{
        	validators: {
                notEmpty: {
                    message: '叉车行驶速度不能为空'
                },
                numeric: {
                    message: '叉车行驶速度必须为数字'
                },
                callback:{
                	message : '叉车行驶速度必须大于零',
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
        },
        forkliftPickUpSpeed:{
        	validators: {
                notEmpty: {
                    message: '叉车抬举速度不能为空'
                },
                numeric: {
                    message: '叉车抬举速度必须为数字'
                },
                callback:{
                	message : '叉车抬举速度必须大于零',
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
        },
        pickTruckDrivingSpeed:{
        	validators: {
                notEmpty: {
                    message: '拣货车行驶速度不能为空'
                },
                numeric: {
                    message: '拣货车行驶速度必须为数字'
                },
                callback:{
                	message : '拣货车行驶速度必须大于零',
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
        },
        pickTuckPickUpSpeed:{
        	validators: {
                notEmpty: {
                    message: '拣货车抬举驶速度不能为空'
                },
                numeric: {
                    message: '拣货车抬举驶速度必须为数字'
                },
                callback:{
                	message : '拣货车抬举速度必须大于零',
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
        },
        gradeCount:{
        	validators: {
                notEmpty: {
                    message: '等级数量不能为空'
                }
            }
        }
    };
    
    // 添加模态框
    $('#addWareHouseModal').on('click', '.btn-primary', function () {
        $(this).parent().prev().find('form').submit();
    }).on('hidden.bs.modal', function () {
        resetForm($(this));
    }).find('form').formValidation({
        framework: 'bootstrap',
        excluded: ':disabled',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        fields: $.extend(fileds,{
            code: {
                validators: {
                    notEmpty: {
                        message: '编号不能为空'
                    },
                    remote: {
                    	url: $('#baseUrl').attr('href') + "ware-house/findCodeIsExist",
                    	data : {id : 0},
                        type: 'GET',
                        delay: 1000,
                        message:'编号已经存在'
                    }
                }
            }
        })
    }).on('success.form.fv', function (e) {
        // Prevent form submission
        e.preventDefault();

        var $form = $(e.target),
            fv = $form.data('formValidation'),
            $modal = $form.parent().parent().parent().parent();

        $.LoadingOverlay("show");
        $.ajax({
            url: $form.attr('action'),
            type: 'PUT',
            data: $form.serialize(),
            success: function (result) {
            	$.LoadingOverlay("hide");
                if (result) {
                    console.log('添加成功');
                    // 隐藏模态框
                    $modal.modal('hide');
                    // 重新加载数据
                    loadData(1, currentSize);
                } else {
                    console.log('添加失败');
                    $modal.find('.alert').show();
                }
            },error: function(result){
            	$.LoadingOverlay("hide");
            	console.log('添加失败');
                $modal.find('.alert').show();
            }
        });
    });

    // 编辑模态框
    $('#editWareHouseModal').on('click', '.btn-primary', function () {
        // 提交表单
        $(this).parent().prev().find('form').submit();
    }).on('hidden.bs.modal', function () {
        // 隐藏模态框时，清理校验状态
        resetForm($(this));
    }).on('show.bs.modal', function (event) {
    	
        var button = $(event.relatedTarget); // Button that triggered the modal
        var id = button.data('id'); // Extract info from data-* attributes

        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this);
        modal.find('[name=id]').val(id);

        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        $.getJSON($('#baseUrl').attr('href') + 'ware-house/' + id, function (result) {
            if (result) {
            	
                modal.find('[name=code]').val(result.code);
                modal.find('[name=stallType]').val(result.stallType);
                modal.find('[name=personDrivingSpeed]').val(result.personDrivingSpeed);
                modal.find('[name=personPickUpSpeed]').val(result.personPickUpSpeed);
                modal.find('[name=forkliftDrivingSpeed]').val(result.forkliftDrivingSpeed);
                modal.find('[name=forkliftPickUpSpeed]').val(result.forkliftPickUpSpeed);
                modal.find('[name=pickTruckDrivingSpeed]').val(result.pickTruckDrivingSpeed);
                modal.find('[name=pickTuckPickUpSpeed]').val(result.pickTuckPickUpSpeed);
                
                var lengths = result.config.length;
                
                if(lengths > 0){
                	
                	modal.find('[name=gradeCount]').find("option[value='"+ lengths +"']").prop("selected",true);
                	
                	var template = $('#gradeTemplateId').val();
                	var paramHtml = '';
                	for(var i =0;i<lengths;i++){
                		paramHtml += template.replace('{index}',i + 1);
                	}
                	modal.find(".paramClass").html(paramHtml);
                	
                	//添加校验事件
                	modal.find('form').formValidation('addField',modal.find("[name=lengths]"),lengthsValidation);
                	modal.find('form').formValidation('addField',modal.find("[name=alias]"),aliasValidation);
                	
                	//初始化仓库配置信息
                	$.each(modal.find('[name=typeIds]'),function(index,item){
                		$(item).find("option[value='"+ result.config[index].wareHouseTypeId +"']").prop("selected",true);
                	});
                	$.each(modal.find('[name=lengths]'),function(index,item){
                		$(item).val(result.config[index].length);
                	});
                	$.each(modal.find('[name=alias]'),function(index,item){
                		$(item).val(result.config[index].wareHouseTypeAlias);
                	});
                	
                	// 添加类型改变事件
                	modal.find('[name=typeIds]').change(function(){
            			$(this).parent().parent().find('[name=alias]').val($(this).find('option:selected').text());
            		});
                }
            }
        });
    }).find('form').formValidation({
        framework: 'bootstrap',
        excluded: ':disabled',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        fields: $.extend(fileds,{
            code: {
                validators: {
                    notEmpty: {
                        message: '编号不能为空'
                    },
                    remote: {
                    	url: $('#baseUrl').attr('href') + "ware-house/findCodeIsExist",
                    	data : function(validator, $field, value){
                    		return {id : $('#editWareHouseModal').find("[name=id]").val()};
                    	},
                        type: 'GET',
                        delay: 1000,
                        message:'编号已经存在'
                    }
                }
            }
        })
    }).on('success.form.fv', function (e) {
        // Prevent form submission
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
                if (result) {
                    console.log('编辑成功');
                    // 隐藏模态框
                    $modal.modal('hide');
                    // 重新加载数据
                    loadData(currentPage, currentSize);
                } else {
                    console.log('编辑失败');
                    $modal.find('.alert').show();
                }
            },error: function(result){
            	$.LoadingOverlay("hide");
            	console.log('编辑失败');
                $modal.find('.alert').show();
            }
        });
    });

    // 删除模态框
    $('#deleteWareHouseModal').on('click', '.btn-danger', function () {
        var $form = $(this).parent().prev().find('form');
        var id = $form.find('[name=id]').val();
        var $modal = $(this).parent().parent().parent().parent();
        // Use Ajax to submit form data
        $.LoadingOverlay("show");
        $.ajax({
            url: $form.attr('action') + '/' + id,
            type: 'DELETE',
            success: function (result) {
            	$.LoadingOverlay("hide");
                if (result) {
                    console.log('删除成功');
                    // 隐藏模态框
                    $modal.modal('hide');
                    // 重新加载数据
                    loadData(currentPage, currentSize);
                }else{
                	alert('删除失败');
                }
            },error : function(result){
            	$.LoadingOverlay("hide");
            	alert('删除失败');
            }
        });
    }).on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        var id = button.data('id'); // Extract info from data-* attributes
        var code = button.data('code'); // Extract info from data-* attributes

        var modal = $(this);
        modal.find('[name=id]').val(id);
        modal.find('.label-info').text(code);
    });
});

/**
 * 重置modal
 * 
 * @param $modal
 */
function resetForm($modal){
	var $form = $modal.find('form');
	var options = $modal.find('.paramClass').find('[name=lengths]');
	if(options.length > 0){
		$form.formValidation('removeField',options);
		$form.formValidation('removeField',$modal.find('.paramClass').find("[name=alias]"));
	}
	$form.find(':input').val('');
	$form.find('select').each(function(index,item){
		if($(item).val() == ''){
			$(item).attr("selected",true);
		}else{
			$(item).attr("selected",false);
		}
	});
	$modal.find('.paramClass').empty();
	$modal.find('.alert').hide();
	$form.formValidation('resetForm', true);
}

/**
 * 加载数据
 * @param page 页码
 * @param size 数量
 */
function loadData(page, size) {

    currentSize = size;
    currentPage = page;

    var data = {
        code: $('.navbar-form').find('[name=code]').val() || '',
        page: page || 1,
        size: size || 10
    };

    // 获取数据
    $.get($('#baseUrl').attr('href') + 'ware-house', data, function (result) {
        if (result) {
            // 清空表格
            $('#warehouseList').empty();
            
            // 添加数据到表格
            $.each(result.list, function (index, item) {
                $('#warehouseList').append('<tr>' +
                    '<td>' + (result.startRow + index) + '</td>' +
                    '<td>' + (item.code || '') + '</td>' +
                    '<td>' + (item.wareHousetype || '') + '</td>' +
                    '<td>' + (item.stallType || '') + '</td>' +
                    '<td>' +
                    '<div class="btn-group">' +
                    (updateFlag ? ('<button class="btn btn-primary" data-toggle="modal" data-target="#editWareHouseModal" data-id="' + item.id + '"><i class="fa fa-edit"></i></button>') : '')+
                    (deleteFlag ? ('<button class="btn btn-danger" data-toggle="modal" data-target="#deleteWareHouseModal" data-id="' + item.id + '" data-code="' + item.code + '"><i class="fa fa-trash"></i></button>') : '') +
                    '</div>' +
                    '</td>' +
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
            console.log('获取仓库列表失败，请重试');
        }
    });
}