<?php
/**
 * Game delete query script
 */
require_once "db.inc.php";
require_once "get-user.php";
require_once "ensure.php";
echo '<?xml version="1.0" encoding="UTF-8" ?>';

//if (!ensure("get")) exit;

delete($_GET['id']);
/**
 * Delete query handler
 * @param $id the name of the game being deleted
 * @return void
 */
function delete($id) {
    $pdo = pdo_connect();
    $nameQ = $pdo->quote($id);
    $query = "delete from game where name=$nameQ";
    $result = $pdo->query($query);
    if (!$result) {
        echo '<game status="no" msg="delete failed" />';
    } else {
        echo '<game status="yes" msg="delete successful" />';
    }

}
