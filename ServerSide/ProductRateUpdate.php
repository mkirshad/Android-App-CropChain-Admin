<?php
	error_reporting(E_ERROR | E_PARSE);
	$dateTime = date('Y-m-d H:i:s', time());

	$mysqli = new mysqli("localhost", "kashifir_faraz", "2!106F@AJPMd","kashifir_faraz");
	
	if (mysqli_connect_errno()) {
		printf("Connect failed: %s\n", mysqli_connect_error());
    exit();
	}

	/* grab the posts from the db */
	
	$p_string = $json_str = file_get_contents('php://input');
//  $p_string = '{"Email":"admin@users.com","Id":1,"Name":"KINNOW","OldRate":"35","Rate":"36","RateUpdatedAt":"2018-08-18 02:26:15","Password":"7e770dc7aa7353c6fb2dbc563e394e564b3f482ccb21af4d47690a2b3b6856089ab680307908bcc374c1ffcd499237f6f635ce18c3ef4abbdf477d1530876feb"}';

	$query = "INSERT INTO Requests(Request) VALUES ('$p_string')";
	$mysqli->query($query) or die('Errant query:  '.$query);
	$logId = $mysqli->insert_id;
	$isLocked = 0;
	
	$json_obj = json_decode($p_string, true);
	$results = [];
	$user = $json_obj;
	$msg = "";
				$query = "SELECT Id, count(*) as rowCount, Password, Email, Name FROM Users WHERE Email = '".$user['Email']."'";
				$result = $mysqli->query($query) or die('Errant query:  '.$query);
				$row = $result->fetch_array(MYSQLI_ASSOC);
				if($row['rowCount'] == 0){
					$msg = "Eamail Address does not exist. Please correct it or SignUp.";
				}ELSE{
					if($row['Password'] != $user['Password']){
						$msg = "Invalid Password! If you have forgotton please reset";
					}else{
							$msg = "Success";
						}
					}

	
	if($msg == "Success"){
		$query = "UPDATE Products SET Rate = ".$user['Rate'].", RateUpdatedAt = '".$dateTime."'
		 WHERE IsActive = 1 AND Id = ".$user['Id']." AND Name  = '".$user['Name']."' AND Rate = ". $user['OldRate']." AND RateUpdatedAt = '".$user['RateUpdatedAt']."'";
		$result = $mysqli->query($query) or die('Errant query:  '.$query);
		if($mysqli->affected_rows == 0){
			$msg = "Rate is not updated. Please retry after updating product list";
		}
	}
	
	$results['msg']=$msg;
	$results['RateUpdatedAt']=$dateTime;
	
	$response = json_encode(array('results'=>$results));

	header('Content-type: application/json');
	echo $response;
	$query = "UPDATE Requests SET Response = '$response' WHERE Id = $logId";
	$mysqli->query($query) or die('Errant query:  '.$query);

	/* disconnect from the db */
	@mysqli_close($link);
?>