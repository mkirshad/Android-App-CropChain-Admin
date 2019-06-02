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

	$query = "INSERT INTO Requests(Request) VALUES ('$p_string')";
	$mysqli->query($query) or die('Errant query:  '.$query);
	$logId = $mysqli->insert_id;
	$isLocked = 0;
	
	$json_obj = json_decode($p_string, true);
	$results = [];
	$user = $json_obj;
	$msg = "";
				$query = "SELECT Id, count(*) as rowCount, Password, Email, Name, IsActive FROM Users WHERE Email = '".$user['Email']."'";
				$result = $mysqli->query($query) or die('Errant query:  '.$query);
				$row = $result->fetch_array(MYSQLI_ASSOC);
				if($row['rowCount'] == 0){
					$msg = "Eamail Address does not exist. Please correct it or SignUp.";
				}ELSE{
					if($row['IsActive'] == 0){
						$msg = "User is inactive, please contact Administrator";
					}
					elseif($row['Password'] != $user['Password']){
						$msg = "Invalid Password! If you have forgotten please reset";
					}else{
							$msg = "Success";
						}
					}

	$results['Id'] = $row['Id'];
	$results['Password'] = $row['Password'];
	$results['Email'] = $row['Email'];
	$results['Name'] = $row['Name'];
	$results['msg']=$msg;
	$response = json_encode(array('results'=>$results));

	header('Content-type: application/json');
	echo $response;
	$query = "UPDATE Requests SET Response = '$response' WHERE Id = $logId";
	$mysqli->query($query) or die('Errant query:  '.$query);

	/* disconnect from the db */
	@mysqli_close($link);
?>