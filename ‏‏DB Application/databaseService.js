import { useState, useEffect } from 'react';
const databaseService = (db, sql, setRes) => {
  var result;

  db.transaction(function(txn) {
    console.log('Tran');
    txn.executeSql(sql, [], function(tx, res) {
      var columns = [];
      for (let i = 0; i < res.rows.length; ++i) {
        var temp = [];
        for(const key in res.rows.item(i)) {
          temp.push(res.rows.item(i)[key]);
        }
        columns.push(temp);
      }
      console.log('col ', columns);
      result = columns;
      setRes(columns);
    })}
  );
  console.log('res', result);
  return result;
}

export default databaseService;

/**
 * var db = new Database('thisnamecannttake.db').db;

var res;

function setRes(par) {
  res = par;
}

db.transaction(function(txn) {
  txn.executeSql(`SELECT name FROM DB`, [], function(tx, res) {
    var columns = [];
    for (let i = 0; i < res.rows.length; ++i) {
      var temp = [];
      for(const key in res.rows.item(i)) {
        temp.push(res.rows.item(i)[key]);
      }
      columns.push(temp);
    }
    console.log(columns);
    setRes(columns);
  })}
);

 */

/***
 * 
var db = new Database('thisnamecannttake.db').db;
var cols;
var get = databaseService(db, `SELECT name FROM DB`, (sf)=>{
  cols = sf;
});

 */

/**
var columns = [];
for (let i = 0; i < res.rows.length; ++i) {
  var temp = [];
  for(const key in res.rows.item(i)) {
    temp.push(res.rows.item(i)[key]);
  }
  columns.push(temp);
}
set(columns);
 */