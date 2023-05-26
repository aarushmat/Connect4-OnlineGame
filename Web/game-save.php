<?php
/**
 * Game save
 */
require_once "db.inc.php";
//require_once "get-user.php";
//require_once "ensure.php";
echo '<?xml version="1.0" encoding="UTF-8" ?>';

save($_POST['name'], $_POST['hostid'], $_POST['guestid'], $_POST['state']);

function save($name, $hostid, $guestid, $state) {

//    $xml = new XMLReader();
//    if (!$xml->XML($xmltext)) {
//        echo '<game status="no" msg="invalid XML" />';
//        exit;
//    }

//    while ($xml->read()) {
//        if ($xml->nodeType == XMLReader::ELEMENT && $xml->name == "game") {
//            $name = $xml->getAttribute("name");
//            $hostid = $xml->getAttribute("hostid");
//            $guestid = $xml->getAttribute("guestid");
//            if ($guestid == '') $gidQ = "NULL";
//            $state = $xml->getAttribute("state");

            // sanitization
            $pdo = pdo_connect();
            $nameQ = $pdo->quote($name);
            $hidQ = $pdo->quote($hostid);
            $gidQ = ($guestid != '') ? $pdo->quote($guestid): "NULL";
            $stateQ = $pdo->quote($state);

            $query = "SELECT id, name, hostid, guestid, state FROM game WHERE name = $nameQ";
            $rows = $pdo->query($query);

            if ($row = $rows->fetch()) {
                // game found
                $query = <<<QUERY
UPDATE game SET name = $nameQ, hostid = $hidQ, guestid = $gidQ, state = $stateQ WHERE name = $nameQ;
QUERY;
                if (!$pdo->query($query)) {
                    echo '<game status="no" msg="updatefail">' . $query . '</game>';
                    exit;
                }

                echo '<game status="yes" msg="update succesful" />';
                exit;
            }


            $query = <<<QUERY
REPLACE INTO game(name, hostid, guestid, state)
    VALUES($nameQ, $hidQ, $gidQ, $stateQ)
QUERY;

            if (!$pdo->query($query)) {
                echo '<game status="no" msg="insertfail">' . $query . '</game>';
                exit;
            }

            echo '<game status="yes" msg="save successful" />';
            exit;

//        }
//        echo '<game status="no" msg="invalid XML" />';
//        exit;
//    }
}

//if (!ensure('post')) exit;
//$pdo = pdo_connect();
//$user = getUser($pdo, $_POST['user'], $_POST['pw']);
//save($pdo, $user, stripslashes($_POST['xml']));
