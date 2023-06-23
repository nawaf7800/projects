import * as SQLite from 'expo-sqlite';
import * as FileSystem from 'expo-file-system';
import { useState } from 'react';

import databaseService from './databaseService';

var result = [];

function executeSql(db, sql, args) {
  const [results, setrResult] = useState(0);

  db.transaction(function(txn) {
    txn.executeSql(sql, args, function(tx, res) {
      set(res.rows._array);
    })}
  );
}

export class Database {
  db;

  constructor(name, fileURI) {

    if(fileURI!==undefined) {
      var createDB = async (name, fileURI)=> {
        if (!(await FileSystem.getInfoAsync(FileSystem.documentDirectory + 'SQLite')).exists) {
          await FileSystem.makeDirectoryAsync(FileSystem.documentDirectory + 'SQLite');
        }
  
        await FileSystem.copyAsync({
          from: fileURI, 
          to: FileSystem.documentDirectory + 'SQLite/'+name+'.db'
        }).catch(function(error) {
          console.log('There has been a problem with your fetch operation: ' + error.message);
          throw error;
        });
      };
      createDB(name, fileURI);  
    }
    this.db = SQLite.openDatabase(name+'.db', '1.0', '', 1);
  }

  executeSql(sql, args, set) {
    this.db.transaction(function(txn) {
      txn.executeSql(sql, args, function(tx, res) {
        set(res.rows._array);
      })}
    );
  }
  
  getTables() {
    this.executeSql(`SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%';`, [], (tables)=>{
      var temp = [];
      for (var table of tables) {
        temp.push(table.name);
      }
      result[0] = temp;
    });
    return result[0];
  }

  getColumns(table) {
    this.executeSql(`PRAGMA table_info(?);`, [table], (columns)=>{
      /*var temp = [];
      for(var r of res) {
        temp.push(r[1]);
      }
      return temp;*/
      result = columns;
    });
    return result;
  }

  getData(table) {
    return databaseService(this.db, `SELECT * FROM ${table}`);
  }

  getMetadata(table, set) {
    var meta = [];
    this.executeSql(`PRAGMA table_info(${table});`, (val)=>{
      
      for(var col of val) {
        if(col[5]==1) continue;

        var type;
        switch (col[2]) {
        case 'INTEGER':
          type = 'numeric'
          break;
        case 'FLOAT':
          type = 'decimal'
          break;
        case 'TEXT':
          type = 'text'
          break;
        case 'DATETIME':
          type = 'datetime'
          break;
        case 'DATE':
          type = 'date'
          break;
        case 'TIME':
          type = 'time'
          break;
        default:
          type = col[2]
          break;
        }
        var constrains = {null: !col[3]}

        meta.push({label: col[1], type: type, constrains: constrains});
      }
    })
    this.executeSql(`PRAGMA foreign_key_list(${table});`, (val)=>{
      for(var col of meta) {
        for(var forein of val) {
          if(col.label == forein[3]) {
            col.type = 'select';
            this.getData(forein[2], (res)=>{
              for(var i=0;i<res.length;i++) {
                res[i] = res[i].toString().replaceAll(',', ' ').trim();
              }
              col.constrains['selection'] = res;
            });
          }
        }
      }
      set(meta);
    })
  }

  getDataByCol(table, col) {
    var res = databaseService(this.db, `SELECT ${col} FROM ${table}`);
    var temp = [];
    for(var r1 of res) {
      for(var r2 of r1) {
        temp.push(r2);
      }
    }
    return temp;
  }

  getDataByRow(table, row) {
    var res = databaseService(this.db, `SELECT * FROM ${table} WHERE ${row[0]}=${row[1]}`);
    var temp = [];
    for(var r1 of res) {
      for(var r2 of r1) {
        temp.push(r2);
      }
    }
    return temp;
  }

  remove(table, id) {
    databaseService(this.db, `DELETE FROM ${table} WHERE rowid=${id};`);
  }

  replace(table, values) {
    databaseService(this.db, `INSERT OR REPLACE INTO ${table}(${Object.keys(values).toString()})
    VALUES(
      ${Object.values(values).map((e=>{
        if(e instanceof Number) return e
        else return `"${e}"`
      })).toString()}
    );`);
  }
}