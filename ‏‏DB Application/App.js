import { useState, useEffect, useCallback } from 'react';
import { View, StyleSheet, SafeAreaView, Text } from 'react-native';
import * as SQLite from 'expo-sqlite';

import DatabaseApp from './DatabaseApp';
import { Database } from './Database';
import databaseService from './databaseService';
import { Button } from 'react-native';

console.log('1');

export default function App() {

  const [Id, setId] = useState(0);
  const [data, setData] = useState(0);

  var db = new Database('MDB');
  var tables = db.getTables();


  console.log(tables);

  return (
    <SafeAreaView>
      <Button title='fskjd' onPress={()=>{setId(Id+1)}}></Button>
    </SafeAreaView>
  )
}

/* 
var db = new Database('MDB');
var tables = db.getTables();
*/