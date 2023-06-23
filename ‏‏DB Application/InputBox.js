import { useState } from 'react';
import { StyleSheet, Text, TextInput, Pressable } from 'react-native';

import SelectDropdown from 'react-native-select-dropdown'
import DateTimePickerModal from "react-native-modal-datetime-picker";


function InputBox(prop) {

  const record = prop.record;
  const [value, setValue] = useState(record.text != undefined ? String(record.text):undefined);
  const [valid, setValid] = useState(true);

  const [isDatePickerVisible, setDatePickerVisibility] = useState(false);
  const showDatePicker = () => {
    setDatePickerVisibility(true);
  };
  const hideDatePicker = () => {
    setDatePickerVisibility(false);
  };
  const handleConfirm = (date) => {
    setValue(date);
    prop.onChange(date, true);
    hideDatePicker();
  };

  if(record.type == 'numeric' || record.type == 'decimal' || record.type == 'text') {
    return (
      <TextInput
        style={[styles.input, {borderColor: (valid) ? '#ffc13b' : 'red'}]}
        placeholder="text"
        placeholderTextColor="gray"
        onEndEditing={(e) => {
          /************************ text validation ************************/
          var val = true;
          if(value == '5')
            val = false;
          // var val = check(text, i);

          setValid(val);    
          prop.onChange(value, val);
        }}
        maxLength = {record.constrains.size}
        keyboardAppearance = "dark"
        inputMode = {record.type}
        value = {value}
        onChangeText = {setValue}
      />
  )}
  else if(record.type == 'date' || record.type == 'time' || record.type == 'datetime') return (
    <Pressable onPress={showDatePicker} style={styles.input}>
      <DateTimePickerModal
        isVisible={isDatePickerVisible}
        mode={record.type}
        onConfirm={handleConfirm}
        onCancel={hideDatePicker}
      />
      <Text style={{color: value==undefined ? "gray":"black"}}>{value==undefined ? 'select' : 
      record.type == 'date' ? value.toLocaleString().split(',')[0] : record.type == 'time' ? value.toLocaleString().split(',')[1]:value.toLocaleString()}</Text>
    </Pressable>
  )
  else if(record.type == 'select') return (
    
    <SelectDropdown
      data={record.constrains.selection}
      onSelect={(selectedItem, index) => {
        setValue(index);
        prop.onChange(index, true);
      }}
      defaultButtonText='select'
      buttonTextAfterSelection={(selectedItem, index) => selectedItem}
      rowTextForSelection={(item, index) => item }
      buttonStyle = {styles.input}
      buttonTextStyle = {{color: value==undefined ? "gray":"black", fontSize: 14, textAlign: 'left',}}
      defaultValueByIndex={Number(value)}
    />
  )
  else {
    console.error(record.label + ': ' + record.type + ' is wrong type');
    return(<></>)
  }
}

export default InputBox;

const styles = StyleSheet.create({
  input: {
    flex: 2,
    maxWidth: '100%',
    backgroundColor: '#EBEBEB',
    borderRadius: 15,
    borderWidth: 2,
    height: 40,
    padding: 10,
  },
});