<?php
    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');
    $first_traffic_num=isset($_POST['first_traffic_num']) ? $_POST['first_traffic_num'] : '';
    
    if ($first_traffic_num !="") {
        $sql="select * from ".$first_traffic_num."_traffic_state order by traffic_num";
        $stmt = $con->prepare($sql);
        $stmt->execute();
        if ($stmt->rowCount() > 0){
            
        
            $data = array();
    
            while($row=$stmt->fetch(PDO::FETCH_ASSOC))
            {
                extract($row);
        
                array_push($data,
                    array('traffic_num'=>$traffic_num,
                    'reader_id'=>$reader_id,
                    'state'=>$state,
                    'located'=>$located
                
                ));
            }
    
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("root"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
            echo $json;
        }
    }

?>