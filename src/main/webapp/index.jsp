<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta content="en-us" http-equiv="Content-Language" />
<meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
<title>Tethys Web Interface</title>

	<!-- <link rel="stylesheet" href="mystyle.css"> -->
	<style>
	.query {
	  margin-left: auto;
	  margin-right: auto;
	  width: 800px;
	}
	
	.auto-style1 {
		text-align: right;
	}
	
	.auto-style2 {
		text-align: center;
	}
	
	</style>
	
	<script>
	
	function validate(){
		regex = new RegExp('^([0-9]{4})(A|B)$');
		var alpha = document.getElementById('alpha').value
		var beta = document.getElementById('beta').value
		if(!regex.test(alpha)){
			alert('Invalid Alpha Value, MMYY[A|B]')
			return false;
		}
		if(!regex.test(beta)){
			alert('Invalid Beta Value, MMYY[A|B]')
			return false;
		}
		
		return true;
	}
	
	</script>

</head>

<body>


<form action="/tethysui/query" id="query" method="post" onsubmit="return validate()">

<table class="query">
	<tr>
		<td class="auto-style2">Tethys Web Interface</td>
	</tr>
	<tr>
		<td>
		<table style="width: 100%">
			<tr>
				<td class="auto-style1">Alpha</td>
				<td><input name="alpha" id="alpha" type="text" size="6" />&nbsp;</td>
				<td class="auto-style1">Beta</td>
				<td><input name="beta" id="beta" type="text" size="6" /></td>
				<td class="auto-style1">
				<input name="qb" type="submit" value="Query" />&nbsp;</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
</table>


</form>


</body>

</html>
