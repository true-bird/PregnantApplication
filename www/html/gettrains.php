<?php
    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');
    // $train_num=isset($_POST['train_num']) ? $_POST['train_num'] : '';
    
    $train_nums="'".$_POST['train_num']."','".$_POST['train_num2']."','".$_POST['train_num3']."','".$_POST['train_num4']."'";
    // $train_nums="'".$_POST['train_num1']."','".$_POST['train_num2']."','".$_POST['train_num3']."','".$_POST['train_num4']."'";
    
    if ($train_nums !="") {
        $sql="select * from entire_state where train_num in (".$train_nums.")";
        $stmt = $con->prepare($sql);
        $stmt->execute();
        if ($stmt->rowCount() > 0){
            
        
            $data = array();
    
            while($row=$stmt->fetch(PDO::FETCH_ASSOC))
            {
                extract($row);
        
                array_push($data,
                    array('first_traffic_num'=>$first_traffic_num,
                    'train_num'=>$train_num,
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