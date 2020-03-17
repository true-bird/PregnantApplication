<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');
$user_id = isset($_POST['user_id']) ? $_POST['user_id'] : '';

$sql="delete from reserve_list where user_id='$user_id'";
$stmt = $con->prepare($sql);
$stmt->execute();

?>