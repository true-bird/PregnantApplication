<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

$train_num=isset($_POST['train_num']) ? $_POST['train_num'] : '';
$reader_id=isset($_POST['reader_id']) ? $_POST['reader_id'] : '';
$traffic_num=isset($_POST['traffic_num']) ? $_POST['traffic_num'] : '';
$first_traffic_num=isset($_POST['first_traffic_num']) ? $_POST['first_traffic_num'] : '';
$located=isset($_POST['located']) ? $_POST['located'] : '';
$station_name=isset($_POST['station_name']) ? $_POST['station_name'] : '';
$next_station=isset($_POST['next_station']) ? $_POST['next_station'] : '';
$drawable_id=isset($_POST['drawable_id']) ? $_POST['drawable_id'] : '';
$arrival_time=isset($_POST['arrival_time']) ? $_POST['arrival_time'] : '';
$user_id=isset($_POST['user_id']) ? $_POST['user_id'] : '';


if ($user_id != "" && $train_num !="" && $reader_id !="" && $traffic_num !="" && $located !=""
    && $station_name !="" && $next_station !="" && $drawable_id !="" && $arrival_time !="") {
    

    $sql="replace into reserve_list 
(user_id, train_num, reader_id, res_date, traffic_num, first_traffic_num, 
located, station_name, next_station, drawable_id, arrival_time)
values ('$user_id', $train_num, '$reader_id', 
now(), $traffic_num, $first_traffic_num, $located, '$station_name', '$next_station', $drawable_id, $arrival_time)";
    
    $sql2="CREATE EVENT IF NOT EXISTS auto_delete ON SCHEDULE AT CURRENT_TIMESTAMP + INTERVAL ".$arrival_time." SECOND DO DELETE from reserve_list where user_id='$user_id'";
    $stmt = $con->prepare($sql);
    $stmt->execute();
    $stmt = $con->prepare($sql2);
    $stmt->execute();
}
?>