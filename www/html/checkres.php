<?php
    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');
    $user_id=isset($_POST['user_id']) ? $_POST['user_id'] : '';
    
    if ($user_id !="") {
        $sql="select * from reserve_list where user_id='$user_id'";
        $stmt = $con->prepare($sql);
        $stmt->execute();
        if ($stmt->rowCount() > 0){
            
        
            $data = array();
    
            while($row=$stmt->fetch(PDO::FETCH_ASSOC))
            {
                extract($row);
        
                array_push($data,
                    array('train_num'=>$train_num,
                        'reader_id'=>$reader_id,
                        'located'=>$located,
                        'station_name'=>$station_name,
                        'next_station'=>$next_station,
                        'drawable_id'=>$drawable_id,
                        'traffic_num'=>$traffic_num,
                        'res_date'=>$res_date,
                ));
            }
    
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("root"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
            echo $json;
        }
    }

?>