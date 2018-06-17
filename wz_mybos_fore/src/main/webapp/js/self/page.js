bosfore_app.controller("ctrlRead", ['$scope', '$http', function($scope, $http) {
	$scope.currentPage = 1;//当前页（请求数据）
	$scope.pageSize = 4;//每页显示的数据（请求数据）
	$scope.totalCount = 0;//总记录数（响应数据）
	$scope.totalPages = 0;//总页数（根据总记录数和每页显示的数据计算）
	//加载上一页
	$scope.prev = function() {
		$scope.selectPage($scope.currentPage-1);
	}
	//加载下一页
	$scope.next = function() {
		$scope.selectPage($scope.currentPage+1);
	}
	
	$scope.selectPage = function(page) {
		// 如果页码超出范围
		if($scope.totalPages!=0&&(page<1||page>$scope.totalPages)){
			return;
		}

		$http({
			method: 'GET',
			url: './pagePrommotion_aaa.action', 
			params: {
				//请求参数
				"page": page,
				"pageSize": $scope.pageSize
			}
		}).success(function(data, status, headers, config) {
			// 显示表格数据 
			$scope.pageItems=data.pageData;
			// 计算总页数
			$scope.totalCount=data.totalCount;//总记录数
			$scope.totalPages=Math.ceil($scope.totalCount/$scope.pageSize);
			// 当前显示页，设为当前页
			page=$scope.currentPage;
			// 固定显示10页 (前5后4)
			var begin;
			var end;
			//前五
			begin=$scope.currentPage-5;
			if(begin<0){
				begin=1;
			}
			
			end=begin+9;
			if(end>$scope.totalPages){
				end=$scope.totalPages
			}
			begin=end-9;
			if(begin<1){
				begin=1;
			}
			
			$scope.pageList=new Array();
			$scope.pageList=[];
			//把页码存入集合
			for(var i=begin;begin<=end;begin++){
				$scope.pageList.push(i);
			}
			
		}).error(function(data, status, headers, config) {
			// 当响应以错误状态返回时调用
			alert("出错，请联系管理员 ");
		});
	}
	//判断是否为当前页
	$scope.isActivePage = function(page) {
		return page == $scope.currentPage;
	}

	// 发起请求 显示第一页数据 
	$scope.selectPage($scope.currentPage);
}]);