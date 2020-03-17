<?php
    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');
    $first_traffic_num=isset($_POST['first_traffic_num']) ? $_POST['first_traffic_num'] : '';
    $traffic_num=isset($_POST['traffic_num']) ? $_POST['traffic_num'] : '';
    $user_id=isset($_POST['user_id']) ? $_POST['user_id'] : '';
    $located=isset($_POST['located']) ? $_POST['located'] : '';
    
    if ($first_traffic_num !="" && $traffic_num !="") {
        $sql="update ".$first_traffic_num."_traffic_state set state = 1, res_date = now(), user_id = '$user_id' where traffic_num='$traffic_num' and located='$located'";
        $stmt = $con->prepare($sql);
        $stmt->execute();
    }

?>