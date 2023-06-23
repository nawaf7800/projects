import { useState, useEffect } from 'react';
import { View, StyleSheet } from 'react-native';
import * as DocumentPicker from 'expo-document-picker';

import Header from './Header';
import FormScreen from './FormScreen';
import ButtonsFooter from './ButtonsFooter';
import Table from './Table';
import { Database } from './Database';

var MetaDB = new Database('thisnamecannttake.db');
MetaDB.db.transaction(function(txn) {
  txn.executeSql(
    'CREATE TABLE IF NOT EXISTS DB(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(30))',
    []
  )
});

function DatabaseApp() {

  console.log("app");
  
  const [dbIndex, setDbIndex] = useState(0);
  const [tableIndex, setTableIndex] = useState(0);
    
  const DBs = MetaDB.getDataByCol('DB', 'name');
  const selectedDB = new Database(DBs[dbIndex]);
  const selectedTable = tables[tableIndex][0];
  const tables = selectedDB.getTables();

  var tableColumns = selectedDB.getColumns(selectedTable);
  var tableRecords = selectedDB.getData(selectedTable);

  const [components, setComponents] = useState([false, false]);
  const [formVisible, setFormVisible] = useState(false);

  const [rowId, setRowId] = useState();

  function databaseChange(index) {
    setDbIndex(index);
    setComponents([true, false]);
  }

  function tableChange(index) {
    setTableIndex(index);
    setComponents([true, true]);
  }

  async function load() {
    const res = await DocumentPicker.getDocumentAsync({copyToCacheDirectory: true});
    
    if(res.type == 'success') {
      new Database(res.name.split('.')[0], res.uri);
      MetaDB.db.transaction((tx)=>{
        tx.executeSql("INSERT INTO DB (name) VALUES (?);", [res.name.split('.')[0]])
      });
      MetaDB.getDataByCol('DB', 'name', setDBs);
    }
  }

  function save() {
    /********************** save **********************/
  }

  return(
    <View style={styles.container}>
      <FormScreen 
        database={selectedDB} 
        table={selectedTable} 
        row={rowId}
        visible={formVisible}
        onConfirm={(texts, valids)=>{
          var db = selectedDB;
          db.replace(selectedTable, texts);
          db.getColumns(selectedTable, setTableColumns);
          db.getData(selectedTable, setTableRecords);
          setFormVisible(false);
        }}
        onCancel={()=>{setFormVisible(false)}}
      />
      <View style={{flex: 1}}>
        <Header text="select database" data={DBs} onChange={databaseChange}/>
        {components.map((val, i)=>{
          if(i==0 && val) return <Header text="select table" data={tables} onChange={tableChange} key={i}/>
          else if(i==1 && val) return (
            <View style={styles.table} key={i}>
              <Table header={tableColumns} data={tableRecords} 
                add={()=>{
                  setRowId(undefined);
                  setFormVisible(true);
                }}
                edit={(id)=>{
                  setRowId(id);
                  setFormVisible(true);
                }}
                remove={(id)=>{
                  var db = selectedDB;
                  db.remove(selectedTable, id);
                }}
              />
            </View>
          )
        })}
      </View>
      <ButtonsFooter buttons = {[
        { title: "Load Database", press: load, disabled: false },
        { title: "Save Database", press: save, disabled: false },
      ]}/>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    width: '100%',
    backgroundColor: '#6b7b8c',
    paddingTop: 50,
    flexDirection: "column",
    flex: 1,
  },
  table: {
    borderTopWidth: 2,
    borderTopColor: '#ffc13b',
    flex: 1,
  }
});