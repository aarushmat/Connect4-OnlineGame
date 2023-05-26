<?php
/**
 * Gets a fcm token to the database
 */
require_once "db.inc.php";
require_once "get-user.php";
require_once "ensure.php";

get_token($_GET["user"]);
function get_token($name) {
    $pdo = pdo_connect();
    $nameQ = $pdo->quote($name);
    $query = "SELECT id FROM fcm WHERE user=$nameQ";
    $result = $pdo->query($query);
    if($row = $result->fetch()) {
        //User exists in table
        $id = $row["id"];
        echo "<game status=\"yes\" id=\"$id\" />";
        exit;
    }
    echo "<game status=\"no\" msg=\"User does not exist\" />";
    exit;
}
