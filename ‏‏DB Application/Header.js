import { StyleSheet, Text, View } from 'react-native';
import { useState } from 'react';
import SelectDropdown from 'react-native-select-dropdown'

function Header(prop) { 
  const [value, setValue] = useState(prop.text);
  return (
    <SelectDropdown
      data={prop.data}
      onSelect={(selectedItem, index) => {
        setValue(index);
        prop.onChange(index);
      }}
      defaultButtonText={value}
      buttonTextAfterSelection={(selectedItem, index) => selectedItem}
      rowTextForSelection={(item, index) => item }
      defaultValueByIndex={Number(value)}
      buttonStyle = {styles.button}
      buttonTextStyle = {styles.buttonText}
      rowStyle={styles.row}
      rowTextStyle={styles.rowText}
    />
  )}
export default Header;

const styles = StyleSheet.create({

  button: {
    height: 55,
    width: '100%',
    backgroundColor: '#1e3d59',
    borderTopColor: '#ffc13b',
    borderTopWidth: 1,
    
  },
  buttonText: {
    fontSize: 30,
    fontWeight: 'bold',
    fontFamily: 'Courier New',
    color: '#ffc13b',
  },
  row: {
    backgroundColor: '#f5f0e1',
    borderBottomColor: '#1e3d59'
  },
  rowText: {
    fontSize: 22,
    fontWeight: 'bold',
    fontFamily: 'Courier New',
    color: '#1e3d59',
  },   
});