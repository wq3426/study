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
var updateFlag,deleteFlag,allocateRoleFlag;
$(function () {
	
	// 更新权限
    updateFlag = ('true' == $("#updateFlag").val());
    // 删除权限
    deleteFlag = ('true' == $("#deleteFlag").val());
    // 分配角色权限
    allocateRoleFlag = ('true' == $("#allocateRoleFlag").val());

    // 加载数据
    loadData(currentPage, currentSize);

    // 绑定搜索事件
    $('#queryBut').click(function(){
    	loadData(1, currentSize);
    });
    
    // 添加模态框
    $('#addUserInfoModal').on('click', '.btn-primary', function () {
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
        fields: {
        	username: {
                validators: {
                    notEmpty: {
                        message: '工号不能为空'
                    },
                    remote: {
                    	url: $('#baseUrl').attr('href') + "user/findUserNameIsExist",
                    	data : {id : 0},
                        type: 'GET',
                        delay: 1000,
                        message:'工号已经存在'
                    }
                }
            },
            password: {
                validators: {
                    notEmpty: {
                        message: '用户密码不能为为空'
                    }
                }
            },
            confirmPassword: {
                validators: {
                    notEmpty: {
                        message: '确认密码不能为为空'
                    },
                    identical: {
                    	field: 'password',
                        message: '密码和确认密码不一致'
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
    $('#editUserInfoModal').on('click', '.btn-primary', function () {
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
        $.getJSON($('#baseUrl').attr('href') + 'user/' + id, function (result) {
            if (result) {
                modal.find('[name=username]').val(result.username);
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
            username: {
                validators: {
                    notEmpty: {
                        message: '工号不能为空'
                    },
                    remote: {
                    	url: $('#baseUrl').attr('href') + "user/findUserNameIsExist",
                    	data : function(validator, $field, value){
                    		return {id : $('#editUserInfoModal').find("[name=id]").val()};
                    	},
                        type: 'GET',
                        delay: 1000,
                        message:'工号已经存在'
                    }
                }
            },
            password: {
                validators: {
                    notEmpty: {
                        message: '用户密码不能为为空'
                    }
                }
            },
            confirmPassword: {
                validators: {
                    notEmpty: {
                        message: '确认密码不能为为空'
                    },
                    identical: {
                    	field: 'password',
                        message: '密码和确认密码不一致'
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
            },error: function(result){
            	$.LoadingOverlay("hide");
            	console.log('编辑失败');
                $modal.find('.alert').show();
            }
        });
    });

    // 删除模态框
    $('#deleteUserInfoModal').on('click', '.btn-danger', function () {
        var $form = $(this).parent().prev().find('form');
        var id = $form.find('[name=id]').val();
        var $modal = $(this).parent().parent().parent().parent();
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
            },error : function(result){
            	alert("删除失败");
            }
        });
    }).on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        var id = button.data('id'); // Extract info from data-* attributes
        var username = button.data('username'); // Extract info from data-* attributes

        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this);
        modal.find('[name=id]').val(id);
        modal.find('.label-info').text(username);
    });
    
    // 分配角色模态框
    $('#allocateRoleModal').on('click', '.btn-primary', function () {
    	
    	var selected = '';
    	$('#roleList').find("input[type=checkbox]").each(function(){
    		if($(this).prop("checked")){
    			selected += $(this).val() + ",";
    		}
    	});
    	if(selected == ''){
    		alert('请选择要分配的角色');
    	}else{
    		// 用户id
    		var $allocateRoleModal = $('#allocateRoleModal');
    		var userId = $allocateRoleModal.find("[name=id]").val();
    		
    		$.LoadingOverlay("show");
    		$.ajax({
                url: $('#baseUrl').attr('href') + "user/updateUserRole",
                type: 'Patch',
                data: {userId:userId,roleId:selected},
                success: function (result) {
                	$.LoadingOverlay("hide");
                    if (result) {
                        console.log('分配成功');
                        // 隐藏模态框
                        $allocateRoleModal.modal('hide');
                        // 重新加载数据
                        loadData(currentPage, currentSize);
                    } else {
                        console.log('分配失败');
                        $allocateRoleModal.find('.alert').show();
                    }
                },error: function(result){
                	$.LoadingOverlay("hide");
                	console.log('分配失败');
                    $allocateRoleModal.find('.alert').show();
                }
            });
    	}
    }).on('show.bs.modal', function (event) {
    	
        var button = $(event.relatedTarget); // Button that triggered the modal
        var id = button.data('id'); // Extract info from data-* attributes

        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this);
        modal.find('[name=id]').val(id);
        modal.find('[type=checkbox]').prop("checked",false);

        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        $.getJSON($('#baseUrl').attr('href') + 'user/selectRoleInfoByUserId/' + id, function (result) {
            if (result) {
            	loadRoleData(1,currentSize,result);
            }
        });
    });
});

//重置modal
function resetForm($modal){
	var $form = $modal.find('form');
	$form.find(':input').val('');
	$modal.find('.alert').hide();
	$form.formValidation('resetForm', true);
}

// 加载角色信息
function loadRoleData(page, size, roleInfoList) {

    var data = {
        page: page || 1,
        size: size || 10
    };

    // 获取数据
    $.get($('#baseUrl').attr('href') + 'role', data, function (result) {
    	
        if (result) {
            // 清空表格
            $('#roleList').empty();

            // 添加数据到表格
            $.each(result.list, function (index, item) {
                $('#roleList').append('<tr>' +
                		"<td><input type='checkbox' value='" + item.id + "'" + (getCheckedFlag(roleInfoList,item.id) ? 'checked' : '') + "></td>" +
                		'<td>' + (result.startRow + index) + '</td>' +
                		'<td>' + (item.name || '') + '</td>' +
                '</tr>');
            });

            // 初始化分页控件
            $("#rolePagination").bs_pagination({
                currentPage: page,
                totalRows: result.total,
                rowsPerPage: size,
                totalPages: result.pages,
                onChangePage: function (event, data) {
                    console.log(event, data);
                    $("#allocateRoleModal").find(":checkbox").prop("checked",false);
                    loadRoleData(data.currentPage, data.rowsPerPage);
                }
            });
        } else {
            console.log('获取角色列表失败，请重试');
        }
    });
}

function getCheckedFlag(roleInfoList,id){
	var flag = false;
	if(roleInfoList){
		$.each(roleInfoList,function(index,item){
			if(id == item.id){
				flag = true;
			}
		});
	}
	return flag;
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
    	username: $('.navbar-form').find('[name=username]').val() || '',
        page: page || 1,
        size: size || 10
    };

    // 获取数据
    $.get($('#baseUrl').attr('href') + 'user', data, function (result) {
        if (result) {
            // 清空表格
            $('#userList').empty();

            // 添加数据到表格
            $.each(result.list, function (index, item) {
                $('#userList').append('<tr>' +
                    '<td>' + (result.startRow + index) + '</td>' +
                    '<td>' + (item.username || '') + '</td>' +
                    '<td>' + (getRoleInfoNameStr(item.roleInfoList)) + '</td>' +
                    '<td>' +
                    '<div class="btn-group">' +
                    (updateFlag ? ('<button class="btn btn-primary" data-toggle="modal" data-target="#editUserInfoModal" data-id="' + item.id + '" title="编辑"><i class="fa fa-edit"></i></button>') : '') +
                    (deleteFlag ? ('<button class="btn btn-danger" data-toggle="modal" data-target="#deleteUserInfoModal" data-id="' + item.id + '" data-username="' + item.username + '" title="删除"><i class="fa fa-trash"></i></button>') : '') +
                    (allocateRoleFlag ? ('<button class="btn btn-primary" data-toggle="modal" data-target="#allocateRoleModal" data-id="' + item.id + '" title="分配角色"><i class="fa fa-cog"></i></button>') : '') +
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
            console.log('获取用户列表失败，请重试');
        }
    });
}


function getRoleInfoNameStr(list){
	if(list && list.length > 0){
		var name = '';
		$.each(list,function(index, item){
			name += item.name + ',';
		})
		return name.substring(0,name.length - 1);
	}
	return '';
}