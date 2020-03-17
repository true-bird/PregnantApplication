<?php
    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');
    $pregnum=isset($_POST['pregnum']) ? $_POST['pregnum'] : '';
    
    if ($pregnum !="") {
        $sql="select * from user_list where pregnum='$pregnum'";
        $stmt = $con->prepare($sql);
        $stmt->execute();
        if ($stmt->rowCount() > 0){
            
        
            $data = array();
    
            while($row=$stmt->fetch(PDO::FETCH_ASSOC))
            {
                extract($row);
                array_push($data,
                    array('pregnum'=>$pregnum,
                        'name'=>$name,
                        'pregdate'=>$pregdate
                    ));
            }
    
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("root"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
            echo $json;
        }
    }

?>