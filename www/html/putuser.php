<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

$pregnum=isset($_POST['pregnum']) ? $_POST['pregnum'] : '';
$name=isset($_POST['name']) ? $_POST['name'] : '';
$pregdate=isset($_POST['pregdate']) ? $_POST['pregdate'] : '';


if ($pregnum != "" && $name !="" && $pregdate != "") {
    $sql="replace into user_list (pregnum, name, pregdate) values ('$pregnum', '$name', '$pregdate')";
    $stmt = $con->prepare($sql);
    $stmt->execute();
}
?>