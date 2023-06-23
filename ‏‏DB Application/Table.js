import { StyleSheet, View, ScrollView, Text, FlatList, Pressable } from 'react-native';
import { useState } from 'react';
import SelectDropdown from 'react-native-select-dropdown'

import { Database } from './Database';

function Table({ header, data, add, edit, remove }) {

    const [widths, setWidths] = useState([]);


    //for (const row of data)  {
        //for (const [i, value] of row.entries())  {
            //var width = 34+String(value).length*12;  //old => 25+
            //widths[i] =  width > widths[i] ? width:widths[i];
        //}
    //}

    const TableHeader = ({data}) => (
        <View style={styles.headerRow}>
            {data.map((val, i) => (
                <View style={[styles.headerCell, {width: widths[i]}]} key={i} 
                    /*onLayout={(event) => {
                        var width = event.nativeEvent.layout.width;
                        if(widths[i] == undefined || width > widths[i]) {
                            widths[i] = width;
                            setWidths(widths);
                        }
                        //console.log(widths);
                    }}*/
                >
                    <Text style={styles.text}>{val}</Text>
                </View>
            ))}
            <View style={[styles.headerCell, {width: 115, backgroundColor: '#284763'}]}>
                <Text style={styles.text}>options</Text>
            </View>
        </View>
    )

    const TableRow = ({data}) => (
        <View style={styles.row}>
            {data.map((val, i) => (
                <View style={[styles.cell, {width: widths[i]}]} key={i}
                    /*onLayout={(event) => {
                        var width = event.nativeEvent.layout.width;
                        if(widths[i] == undefined || width > widths[i]) {
                            widths[i] = width;
                            setWidths(widths);
                        }
                        //console.log(widths);
                    }}*/
                >
                    <Text style={styles.text}>{val}</Text>
                </View>
            ))}
        </View>
    )

    return (
        <View style={{flex: 1}}>
            <ScrollView horizontal={true}>
                <View style={styles.container}>
                    <TableHeader data={header}/>
                    <FlatList
                        data={data}
                        renderItem={(row) => (
                            <View style={styles.row} key={row.item[0]}>
                                <TableRow data={row.item}/>
                                <SelectDropdown
                                    data={['Edit','Delete']}
                                    onSelect={(selectedItem, index) => {
                                        if(index==0) edit(row.item[0]);
                                        else remove(row.item[0]);
                                    }}
                                    defaultButtonText={"•••"}
                                    buttonTextAfterSelection={(selectedItem, index) => "•••"}
                                    rowTextForSelection={(item, index) => item }
                                    buttonStyle = {styles.button}
                                    buttonTextStyle = {styles.text}
                                    rowStyle={styles.selctionRow}
                                    rowTextStyle={styles.selctionRowText}
                                />
                            </View>
                        )}
                    />
                </View>
            </ScrollView>
            <Pressable 
                onPress={add} 
                disabled={false}
                style={({pressed}) => [
                    {
                      backgroundColor: pressed ? '#284763' : '#1e3d59',
                    },
                    styles.addButton,
                  ]}
                >
                <Text style={styles.addText}>Add</Text>
            </Pressable>
        </View>
    )
}

export default Table;


const styles = StyleSheet.create({
    container: { 
        flex: 1,
        marginTop: 15,
        backgroundColor: '#6b7b8c',
    },
    headerRow: { 
        flexDirection: "row",
        height: 50,
        marginBottom: 10,
    },
    headerCell: {
        padding: 10,
        justifyContent: 'center',
        marginRight: 7,
        borderRadius: 15,
        backgroundColor: '#1e3d59',
    },
    row: { 
        flexDirection: "row",
        height: 50,
        backgroundColor: '#1e3d59',
        borderRadius: 15,
        marginBottom: 5,
        justifyContent: 'center'
    },
    cell: {
        padding: 10,
        justifyContent: 'center',
        marginRight: 7,
    },
    text: {
        fontSize: 22,   //old=>18
        fontWeight: 'bold',
        fontFamily: 'Courier New',
        color: '#ffc13b',
    },

    button: {
        backgroundColor: '#284763',
        borderTopRightRadius: 15,
        borderBottomRightRadius: 15,
        width: 115,
    },
    addButton: {
        borderRadius: 15,
        marginTop: 5,
        marginBottom: 5,
        height: 50,
        justifyContent: 'center',
    },
    selctionRow: {
        backgroundColor: '#f5f0e1',
        borderBottomColor: '#1e3d59'
    },
    selctionRowText: {
        fontSize: 22,
        fontWeight: 'bold',
        fontFamily: 'Courier New',
        color: '#1e3d59',    
    },
    addText: {
        fontSize: 35,
        fontWeight: 'bold',
        fontFamily: 'Courier New',
        color: '#ffc13b',
        textAlign: 'center',
    }
})

/*****
 * 
 * 

      <SelectDropdown
                    data={['Add']}
                    onSelect={add}
                    defaultButtonText={"+"}
                    buttonTextAfterSelection={(selectedItem, index) => "+"}
                    rowTextForSelection={(item, index) => item }
                    buttonStyle = {styles.addButton}
                    buttonTextStyle = {styles.plustext}
                    rowStyle={styles.selctionRow}
                    rowTextStyle={styles.selctionRowText}
                />
 */