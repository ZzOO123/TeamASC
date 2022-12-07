<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>프로젝트 생성</title>
</head>
<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	console.log('ready');
	
	$("#checkProjectName").on("click",function(){ //중복 체크 기능
		let pn = document.getElementById("projectName").value;
		if(pn === ""){
			alert("팀명을 입력해주세요");
			return false;
		}
		
		$.ajax({
			type : "get",
			url : "checkProjectName",
			dataType : "text",
			data : {projectName:pn},
			success : function(data,status){
			
			let jsonBoolean = JSON.parse(data);
				if(!jsonBoolean){
					alert("이미 존재하는 팀명 입니다");
					document.getElementById("projectName").value = "";
					document.getElementById("checkProjectNameYN").value= "N"; //숨겨진 인풋 텍스트에 값 N 입력
				} else{
					alert("사용 가능한 팀명 입니다");
					document.getElementById("checkProjectNameYN").value= "Y"; //숨겨진 인풋 텍스트에 값 Y 입력
				}
			
				
			},
			error : function(data,status){
				console.log("error");
			},
			complete : function(data,status){
				console.log("finish");
			}
		}); // ajax end 
		
	}); // #checkId
	$('#creatProjectForm').submit(function(){ // submit 버튼 클릭시 발동 , 아이디 중복이거나 중복 체크 안했을경우 진행 안됨
		let checkProjectNameYn = document.getElementById("checkProjectNameYN").value; //숨겨진 인풋 텍스트에 Y값이 없으면 진행 안됨
		
		let start = document.getElementById("startDate").value;
  	  	let end = document.getElementById("endDate").value;

  	  	if(start == "" || end == ""){
  		  	alert("일정을 입력 해주세요");
  		  	return false;
  	  	} 
		
        if(checkProjectNameYn != "Y"){
        	alert("팀명 중복 체크를 먼저 해주세요");
        	return false;
        }
	});
	
	$("#startDate").on("change",function(){
        start = document.getElementById("startDate").value;
        end = document.getElementById("endDate").value;
        document.getElementById("startText").value = start;

        if(start != "" && end != "" && start > end){
        	alert("시작일자가 종료일자 보다 느립니다");
        	document.getElementById("endDate").value = document.getElementById("startText").value;
        	document.getElementById("endText").value = document.getElementById("startText").value;
        }
            
     });
     
     $("#endDate").on("change",function(){
    	start = document.getElementById("startDate").value;
        end = document.getElementById("endDate").value;
        document.getElementById("endText").value = end;
        
        if(start != "" && end != "" && start > end){
           alert("시작일자가 종료일자 보다 느립니다");
           document.getElementById("endDate").value = document.getElementById("startText").value;
           document.getElementById("endText").value = document.getElementById("startText").value;
        }
     });
}); // document end	
</script>
<style>
	table {
    	width: 80%;
    	/* border: 1px solid; */
	}
	.tableName{
		text-align: center;
		width: 15%;
		border: 5px;
		font-size: 25px;
		font-weight: bold;
	}
	input[type=button], input[type=submit]{
		color: white;
	    background-color: #384D59;
	    border: 0;
	    padding: 5px;
	    border-radius: 5px;
	    font-weight: bold;
	    float: right;
	    margin-right: 5px;
	    width:140px;
		height:40px;
	}
	input[type=text]{
		width: 100%;
		height: 40px;
		border-radius: 5px;
		font-size: 20px;
		border-color: #e0e0e0;
	}
	input[type=text]:hover{
		background-color: #e0e0e0;
	}
	.project-body {
		margin-top: 200px;
		margin-right: 50px;
	}
	.tableButton{
		border : 1px solid;
		vertical-align: middle;
	}
	.divbutton{
		width:150px;
		margin-left:auto;
		margin-right:auto;
	}
	.divDate{
		width:50%;
		height:100%;
		margin:0px;
		box-sizing: border-box;
	}
	input[type=date]{
		width:100%;
		height:100%;
		margin:0px;
		width: 100%;
		height: 40px;
		border-radius: 5px;
		font-size: 20px;
		border-color: #e0e0e0;
		text-align:center;
	}
</style>
<body>
<div class="project-body">
<form action="createProject" method="post" id ="creatProjectForm">
<input type="hidden" name="startDate" id="startText">
<input type="hidden" name="endDate" id="endText">
<input type="hidden" id="checkProjectNameYN">
<table>
	<tr>
		<td colspan="3" align="center"><h1>프로젝트 생성</h1></td>
	</tr>
	<tr><td height="20px"></td></tr>
	<tr>
		<th class="tableName">프로젝트명</th>
		<td>
			<input type="text" placeholder="프로젝트명" name="projectName" id="projectName">
		</td>
		<td style="width:15%;">
			<input class="inputButton" type="button" value="프로젝트명 중복 확인" id="checkProjectName">
		</td>
	</tr>
	<tr>
		<th class="tableName">프로젝트 일정</th>
		<td style="display: flex;width:100%;height:35px;">
			<div class="divDate"><input type="date" id="startDate"></div>
			<div class="divDate" style="margin-left:10px"><input type="date" id="endDate"></div>
		</td>
	</tr>
	<tr><td height="20px"></td></tr>
	<tr>
		<td colspan="3"><div class="divbutton"><input class="inputButton" type="submit" value="프로젝트 생성"></div></td>
	</tr>
</table>
</form>
</div>
</html>