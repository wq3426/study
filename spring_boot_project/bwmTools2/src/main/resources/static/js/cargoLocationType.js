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
    
    // 绑定导出事件
    $('#exportBut').click(function () {
    	window.location.href = $('#baseUrl').attr('href') + 'cargo-location-type/export';
    });
    
    // 添加模态框
    var $addCargoLocationTypeModal = $('#addCargoLocationTypeModal');

    // 绑定添加货位类型表单提交
    $addCargoLocationTypeModal.find('.btn-primary').on('click', function () {
    	$addCargoLocationTypeModal.find('form').submit();
    });
    
    // 绑定是否托盘类型
    $(document).find("select[name='pallet']").on('change', function () {
    	var v = $(this).val();
    	$(this).parent().parent().parent().find('div').each(function(index,item){
    		if($(item).hasClass("extendId") || $(item).hasClass("extendLengthId")){
    			if('false' == v){
    				$(item).show();
    				$(item).find(':input').removeAttr("disabled");
    				$(item).find('[name=extend]').removeAttr("disabled");
    				$(item).find(':input').val('0');
    				$(item).find("[name=extend]").find("option[text='']").attr("selected", "selected");
    	    	}else{
    	    		$(item).hide();
    	    		$(item).find(':input').attr("disabled","disabled");
    	    		$(item).find('[name=extend]').attr("disabled","disabled");
    	    	}
    		}
    	});
    });
    
    // 绑定是否扩展
    $(document).find("select[name='extend']").on('change', function () {
    	var $extend = $(this).parent().parent().next();
    	if('false' == $(this).val()){
    		$extend.hide();
    	}else{
    		$extend.show();
    	}
    });
    
    var fileds = {
            length: {
                validators: {
                    notEmpty: {
                        message: '长不能为空'
                    },
                    numeric: {
                        message: '长必须为数字'
                    },
                    callback:{
                    	message : '长必须大于零',
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
            width: {
                validators: {
                    notEmpty: {
                        message: '宽不能为空'
                    },
                    numeric: {
                        message: '宽必须为数字'
                    },
                    callback:{
                    	message : '宽必须大于零',
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
            height: {
                validators: {
                    notEmpty: {
                        message: '高不能为空'
                    },
                    numeric: {
                        message: '高必须为数字'
                    },
                    callback:{
                    	message : '高必须大于零',
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
            pallet: {
                validators: {
                    notEmpty: {
                        message: '是否托盘类型不能为空'
                    }
                }
            },
            extend: {
                validators: {
                    notEmpty: {
                        message: '是否扩展不能为空'
                    }
                }
            },
            leftIncrease: {
                validators: {
                    notEmpty: {
                        message: '向左扩展的长度不能为空'
                    },
                    numeric: {
                        message: '向左扩展的长度必须为数字'
                    }
                } 
            },
            rightIncrease: {
            	validators: {
                    notEmpty: {
                        message: '向右扩展的长度不能为空'
                    },
                    numeric: {
                        message: '向右扩展的长度必须为数字'
                    }
                }
            },
            frontIncrease: {
            	validators: {
                    notEmpty: {
                        message: '向前扩展的长度不能为空'
                    },
                    numeric: {
                        message: '向前扩展的长度必须为数字'
                    }
                }
            },
            backIncrease: {
            	validators: {
                    notEmpty: {
                        message: '向后扩展的长度不能为空'
                    },
                    numeric: {
                        message: '向后扩展的长度必须为数字'
                    }
                }
            }
        };

    // 绑定添加表单检验事件
    $addCargoLocationTypeModal.find('form').formValidation({
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
                        message: '货位类型编码不能为空'
                    },
                    remote: {
                    	url: $('#baseUrl').attr('href') + "cargo-location-type/findCodeIsExist",
                    	data : {id : 0},
                        type: 'GET',
                        delay: 1000,
                        message:'货位类型编码已经存在'
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
                    loadData(currentPage, currentSize);
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
    // 隐藏模态框时，清理校验状态
    $addCargoLocationTypeModal.on('hidden.bs.modal', function () {
        resetForm($(this));
    });
    
    // 编辑模态框
    $('#editCargoLocationTypeModal').on('click', '.btn-primary', function () {
        // 提交表单
        $(this).parent().prev().find('form').submit();
    }).on('hidden.bs.modal', function () {
        // 隐藏模态框时，清理校验状态
    	resetForm($(this));
    }).on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        var id = button.data('id'); // Extract info from data-* attributes

        var modal = $(this);
        modal.find('[name=id]').val(id);
        $.ajaxSettings.async = false;
        $.getJSON($('#baseUrl').attr('href') + 'cargo-location-type/' + id, function (result) {
            if (result) {
                modal.find('[name=code]').val(result.code);
                modal.find('[name=length]').val(result.length);
                modal.find('[name=width]').val(result.width);
                modal.find('[name=height]').val(result.height);
                modal.find('[name=pallet]').find("option[value='"+ result.pallet +"']").prop("selected",true);
                modal.find('[name=pallet]').change();
                modal.find('[name=extend]').find("option[value='"+ result.extend +"']").prop("selected",true);
                modal.find('[name=extend]').change();
                
                modal.find('[name=leftIncrease]').val(result.leftIncrease);
                modal.find('[name=rightIncrease]').val(result.rightIncrease);
                modal.find('[name=frontIncrease]').val(result.frontIncrease);
                modal.find('[name=backIncrease]').val(result.backIncrease);
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
                        message: '货位类型编码不能为空'
                    },
                    remote: {
                    	url: $('#baseUrl').attr('href') + "cargo-location-type/findCodeIsExist",
                    	data : function(validator, $field, value){
                    		return {id : $('#editCargoLocationTypeModal').find("[name=id]").val()};
                    	},
                        type: 'GET',
                        delay: 1000,
                        message:'货位类型编码已经存在'
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
            },error : function(result){
            	$.LoadingOverlay("hide");
            	console.log('编辑失败');
                $modal.find('.alert').show();
            }
        });
    });
    
    // 删除模态框
    $('#deleteCargoLocationTypeModal').on('click', '.btn-danger', function () {
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
                	alert("删除失败");
                }
            },error: function (result){
            	$.LoadingOverlay("hide");
            	alert("删除失败");
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
    
    //导入货位类型数据
    $('#importCargoLocationTypeModal').on('click', '.btn-primary', function () {
        // 提交表单
        $(this).parent().prev().find('form').submit();
    }).on('hidden.bs.modal', function () {
        var $form = $(this).find('form');
    	$form.formValidation('resetForm', true);
    	$form.find(':input').val('');
    	$(this).find('.alert').hide();
    }).find("form").formValidation({
        framework: 'bootstrap',
        excluded: ':disabled',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        fields: {
            file: {
                validators: {
                    notEmpty: {
                        message: '导入文件不能为空'
                    },
                    file: {
                    	extension : 'xlsx',
                    	message: '文件格式有误,应该是xlsx'
                    }
                }
            }
        }
    }).on('success.form.fv', function (e) {
        // Prevent form submission
        e.preventDefault();
        
        var $form = $(e.target),
        	fv = $form.data('formValidation'),
        	$modal = $form.parent().parent().parent().parent();
        
        if(window.confirm("当前仓库的原货位类型将被清空，是否继续导入？")){
        	
        	var formdata = new FormData($form[0]);
        	
        	$.LoadingOverlay("show",{custom  : getCustomElement('正在导入货位类型，请耐心等待...')});
        	
            $.ajax({
               type : 'post',
               url : $form.attr('action'),
               data : formdata,
               cache : false,
               processData : false, // 不处理发送的数据，因为data值是Formdata对象，不需要对数据做处理
               contentType : false, // 不设置Content-type请求头
               success : function(result){
            	   $.LoadingOverlay("hide");
            	   if (result.success) {
                       console.log('导入成功');
                       // 隐藏模态框
                       $modal.modal('hide');
                       // 重新加载数据
                       loadData(currentPage, currentSize);
                   } else {
                       console.log('导入失败');
                       if(result.msg){
                    	   $("#importError").html(result.msg);
                       }else{
                    	   $("#importError").html('导入货位类型失败，请重试!');
                       }
                       $modal.find('.alert').show();
                   }
       	   	   },
               error : function(result){
            	   $.LoadingOverlay("hide");
            	   console.log('导入失败');
            	   $("#importError").html('导入货位类型失败，请重试!');
                   $modal.find('.alert').show();
       	       }
           });
        }
    });
});

// 重置modal
function resetForm($modal){
	var $form = $modal.find('form');
	$form.find(":input").val('');
	$form.find("select[name='pallet']").find("option").each(function(index,item){
		$(this).prop("selected",false);
	});
	$form.find("select[name='extend']").find("option").each(function(index,item){
		$(this).prop("selected",false);
	});
	$form.find("div").each(function(index,item){
		if($(item).hasClass("extendId") || $(item).hasClass("extendLengthId")){
			$(item).hide();
		}
	});
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
    $.get($('#baseUrl').attr('href') + 'cargo-location-type', data, function (result) {
        if (result) {
            // 清空表格
            $('#cargoLocationTypeList').empty();
            
            // 添加数据到表格
            $.each(result.list, function (index, item) {
                $('#cargoLocationTypeList').append('<tr>' +
                    '<td>' + (result.startRow + index) + '</td>' +
                    '<td>' + (item.code || '') + '</td>' +
                    '<td>' + (item.length || '') + '*' + (item.width || '') + '*' + (item.height || '') + '</td>' +
                    '<td>' + (item.pallet ? '是' : '否') + '</td>' +
                    '<td>' + (item.extend ? '是' : '否') + '</td>' +
                    '<td>' + (item.total ? item.total : 0) + '</td>' +
                    '<td>' + ((item.total ? item.total : 0) - (item.useCount ? item.useCount : 0)) + '</td>' +
                    '<td>' +
                    	'<div class="btn-group">' +
                    	(updateFlag ? ('<button class="btn btn-primary" data-toggle="modal" data-target="#editCargoLocationTypeModal" data-id="' + item.id + '"><i class="fa fa-edit"></i></button>'): '')	 +
                    	(deleteFlag ? ('<button class="btn btn-danger" data-toggle="modal" data-target="#deleteCargoLocationTypeModal" data-id="' + item.id + '" data-code="' + item.code + '"><i class="fa fa-trash"></i></button>') : '')	 +
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
            console.log('获取货位类型列表失败，请重试');
        }
    });
}