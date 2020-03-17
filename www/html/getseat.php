<?php
    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');
    $first_traffic_num=isset($_POST['first_traffic_num']) ? $_POST['first_traffic_num'] : '';
    $line_num=isset($_POST['line_num']) ? $_POST['line_num'] : '';
    
    if ($first_traffic_num !="" && $line_num !="") {
        // $sql="select * from ".$line_num."_train_state where first_traffic_num='$first_traffic_num'";
        $sql="select * from line2_train_state where first_traffic_num='$first_traffic_num'";
        $stmt = $con->prepare($sql);
        $stmt->execute();
        if ($stmt->rowCount() > 0){
            
        
            $data = array();
    
            while($row=$stmt->fetch(PDO::FETCH_ASSOC))
            {
                extract($row);
        
                array_push($data,
                    array('traffic_num'=>$traffic_num,
                    'empty_seat'=>$empty_seat,
                    'total_seat'=>$total_seat
                
                ));
            }
    
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("root"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
            echo $json;
        }
    }

?>