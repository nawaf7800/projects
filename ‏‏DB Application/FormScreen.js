import { useState } from 'react';
import { StyleSheet, Text, View, ScrollView, Modal } from 'react-native';

import InputBox from './InputBox';
import ButtonsFooter from './ButtonsFooter';
import { Database } from './Database';


function preprocess(metadata) {
  for(var col of metadata) {
    var size = 18;
    var text = col.label;
    if(text.length > 22) {
      col.label = text.slice(0, 10) + '...';
      size = 8;
    }
    else if(text.length > 9) {
      var t = text.length - 9;
      size = size - t;
      if(t >= 3 && t <= 6) size--;
      else if(t == 10) size = 9;
      else if(t >= 11 && t <= 13) size = 8;
    }
    col.labelSize = size;
  }
}

function FormScreen(prop) {

  const [metadata, setMetadata] = useState([]);
  const [formCompleted, setFormCompleted] = useState([]);

  const [texts, setTexts] = useState({});
  const [valids, setValids] = useState({});
  
  if(prop.database!=undefined) {
    var db = new Database(prop.database);
   
  }

  return (
    <Modal visible={prop.visible} animationType='slide'>
      <View style={styles.header}>
        <View style={styles.headerCont}>
          <Text style={styles.headerText}>Record</Text>
        </View>
      </View>
      <View style={styles.infoCont}>
        <ScrollView>
          {metadata.map((col) => (
            <View style={styles.inputCont} key={col.label}>
              <View style={styles.textCont}>
                <Text style={[styles.text, {fontSize: col.labelSize}]}>{col.label}</Text>
              </View>
              <InputBox record={col} onChange= {(text, valid) => {
                texts[col.label] = text;
                valids[col.label] = valid;
                setTexts(texts);
                setValids(valids);

                if(!valid || Object.keys(valids).length < metadata.length)
                  setFormCompleted(false);
                else if(!formCompleted) {
                  var flag = true;
                  for(var v in valids)
                  flag = flag && valids[v];
                  setFormCompleted(flag);
                }
                //prop.onChange(col.label, texts, valids, formCompleted);
              }}/>
            </View>
          ))}
        </ScrollView>
      </View>
      <ButtonsFooter buttons = {[
        { title: "Confirm", disabled: !formCompleted, press: ()=>{
          texts['id'] = parseInt(prop.row);
          valids['id'] = true;
          prop.onConfirm(texts, valids);
        }},
        { title: "Cancel", press: prop.onCancel, disabled: false },
      ]}/>
    </Modal>
  );
}

export default FormScreen;

const styles = StyleSheet.create({
    container: {
      flex: 1,
      width: '100%',
    },
    infoCont: {
      flex: 1,
      backgroundColor: '#6b7b8c',
    },
  
    inputCont: {
      flexDirection: 'row',
      backgroundColor: '#1e3d59',
      paddingLeft: 20,
      padding: 10,
    },
    input: {
      flex: 2,
      maxWidth: '100%',
      backgroundColor: 'white',
      borderWidth: 2,
      height: 40,
      padding: 10,
    },
    textCont: {
      flex: 1,
      justifyContent: 'center',
    },
    text: {
      fontSize: 18,
      fontWeight: 'bold',
      fontFamily: 'Courier New',
      color: '#ffc13b',
    },


  header: {
    paddingTop: 50,
    height: 105,
    backgroundColor: '#1e3d59',
    flexDirection: 'row',
    justifyContent: 'center',
    borderBottomWidth: 2,
    borderBottomColor: '#ffc13b',
  },
  headerCont: {
    justifyContent: 'center',
  },
  headerText: {
    fontSize: 30,
    fontWeight: 'bold',
    fontFamily: 'Courier New',
    color: '#ffc13b',
  },
});