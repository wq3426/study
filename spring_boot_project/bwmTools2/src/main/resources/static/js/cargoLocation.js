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
var updateFlag = true,deleteFlag = true;
$(function () {
	// 更新权限
    updateFlag = ('true' == $("#updateFlag").val());
    // 删除权限
    deleteFlag = ('true' == $("#deleteFlag").val());
	
    // 加载数据
    loadData(currentPage, currentSize);
    
    // 加载货位类型数据
    loadCargoType($("#typeId"));

    // 绑定搜索事件
    $('#queryBut').click(function(){
    	loadData(1, currentSize);
    });
    
    // 计算货位分值
    $('#scoreCalculationBut').click(function () {
    	$.LoadingOverlay("show",{custom  : getCustomElement('正在进行分值计算，请耐心等待...')});
    	$.get($('#baseUrl').attr('href') + "cargo-location/calculationCargoLocationScore",function(data){
    		 $.LoadingOverlay("hide");
    		if(data){
    			if(data.success){
        			alert('分值计算成功');
        			// 重新加载数据
                    loadData(1, currentSize);
        		}else{
        			alert(data.msg);
        		}
    		}else{
    			alert('分值计算失败');
    		}
    	});
    });
    
    // 绑定添加货位表单提交
    $('#addCargoLocationModal').on('click', '.btn-primary', function () {
    	// 提交表单
        $(this).parent().prev().find('form').submit();
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
            	console.log('添加失败');
            	$.LoadingOverlay("hide");
            	$modal.find('.alert').show();
            }
        });
    }).on('hidden.bs.modal', function () {
        resetForm($(this));
    }).on('show.bs.modal', function () {
    	loadCargoType($(this).find('[name=typeId]'));
    }).find('form').formValidation({
        framework: 'bootstrap',
        excluded: ':disabled',
        icon: {
            valid: 'fa fa-check',
            invalid: 'fa fa-times',
            validating: 'fa fa-refresh'
        },
        fields: {
        	code: {
                validators: {
                    notEmpty: {
                        message: '货位编码不能为空'
                    },
                    remote: {
                    	url: $('#baseUrl').attr('href') + "cargo-location/findCodeIsExist",
                    	data : {id : 0},
                        type: 'GET',
                        delay: 1000,
                        message:'货位编码已经存在'
                    }
                }
            },
            typeId: {
            	validators: {
                    notEmpty: {
                        message: '货位类型编码不能为空'
                    }
                }
            }
        }
    });
    
    // 编辑模态框
    $('#editCargoLocationModal').on('click', '.btn-primary', function () {
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
        $.getJSON($('#baseUrl').attr('href') + 'cargo-location/' + id, function (result) {
            if (result) {
                modal.find('[name=code]').val(result.code);
                var target = modal.find('[name=typeId]');
                var value = result.typeId;
                $.ajaxSettings.async = false;
                $.getJSON($('#baseUrl').attr('href') + 'cargo-location-type/list', function (result) {
                    if (result) {
                    	target.empty();
                    	target.append("<option value=''>请选择</option>");
                    	$(result).each(function(index,item){
                    		if(value && item.id == value){
                    			target.append("<option value='"+ item.id +"' selected>" + item.code + "</option>");
                    		}else{
                    			target.append("<option value='"+ item.id +"'>" + item.code + "</option>");
                    		}
                    	});
                    }
                });
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
        fields: {
        	code: {
                validators: {
                    notEmpty: {
                        message: '货位编码不能为空'
                    },
                    remote: {
                    	url: $('#baseUrl').attr('href') + "cargo-location/findCodeIsExist",
                    	data : function(validator, $field, value){
                    		return {id : $('#editCargoLocationModal').find("[name=id]").val()};
                    	},
                        type: 'GET',
                        delay: 1000,
                        message:'货位编码已经存在'
                    }
                }
            },
            typeId: {
            	validators: {
                    notEmpty: {
                        message: '货位类型编码不能为空'
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
    $('#deleteCargoLocationModal').on('click', '.btn-danger', function () {
        var $form = $(this).parent().prev().find('form');
        var id = $form.find('[name=id]').val();
        var $modal = $(this).parent().parent().parent().parent();
        // Use Ajax to submit form data
        $.ajax({
            url: $form.attr('action') + '/' + id,
            type: 'DELETE',
            success: function (result) {
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
    $('#importCargoLocationModal').on('click', '.btn-primary', function () {
        // 提交表单
        $(this).parent().prev().find('form').submit();
    }).on('hidden.bs.modal', function () {
    	resetForm($(this));
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
        
        if(window.confirm("当前仓库的原货位将被清空，是否继续导入？")){
        	
        	 var formdata = new FormData($form[0]);
        	 
             $.LoadingOverlay("show",{custom  : getCustomElement('正在导入货位，请耐心等待...')});
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
                        loadData(1, currentSize);
                    } else {
                        console.log('导入失败');
                        if(result.msg){
                     	   $("#importError").html(result.msg);
                        }else{
                     	   $("#importError").html('导入货位失败，请重试!');
                        }
                        $modal.find('.alert').show();
                    }
        	   	},
                error : function(){
                   $.LoadingOverlay("hide");
             	   console.log('导入失败');
             	   $("#importError").html('导入货位失败，请重试!');
                   $modal.find('.alert').show();
        	   }
            });
        }
    });
});

/**
 * 重置表单
 */
function resetForm($modal){
	var $form = $modal.find('form');
	$form.find(":input").val('');
	$form.find('select').each(function(index,item){
		$(this).empty();
		$(this).append("<option value=''>请选择</option>");
	});
	$modal.find('.alert').hide();
	$form.formValidation('resetForm', true);
}

/**
 * 加载货位类型
 */
function loadCargoType($select){
	$.get($('#baseUrl').attr('href') + 'cargo-location-type/list',function (result) {
		if(result){
			$(result).each(function(index,item){
				$select.append("<option value='" + item.id + "'>" + item.code + "</option>");
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

    var data = {
    	code: $('.navbar-form').find('[name=code]').val() || '',
    	typeId: $('.navbar-form').find('[name=typeId]').val() || '',
        page: page || 1,
        size: size || 10
    };

    // 获取数据
    $.get($('#baseUrl').attr('href') + 'cargo-location', data, function (result) {
        if (result) {
            // 清空表格
            $('#cargoLocationList').empty();
            
            // 添加数据到表格
            $.each(result.list, function (index, item) {
                $('#cargoLocationList').append('<tr>' +
                    '<td>' + (result.startRow + index) + '</td>' +
                    '<td>' + (item.code || '') + '</td>' +
                    '<td>' + (item.typeCode || '') + '</td>' +
                    '<td>' + (0 == item.score ? 0 : (item.score || '')) + '</td>' +
                    '<td>' +
                    (updateFlag ? ('<button class="btn btn-primary" data-toggle="modal" data-target="#editCargoLocationModal" data-id="' + item.id + '"><i class="fa fa-edit"></i></button>'): '')	 +
                	(deleteFlag ? ('<button class="btn btn-danger" data-toggle="modal" data-target="#deleteCargoLocationModal" data-id="' + item.id + '" data-code="' + item.code + '"><i class="fa fa-trash"></i></button>') : '')	 +
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
            console.log('获取货位列表失败，请重试');
        }
    });
}