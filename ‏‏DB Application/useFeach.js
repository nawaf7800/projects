import { useState, useEffect } from 'react';

const useFeach = (mainPorpValue, prop1Callback, prop2Callback) => {
    const [main, setMain] = useState(mainPorpValue);
    const [prop1, setProp1] = useState([]);
    const [prop2, setProp2] = useState([]);

    useEffect(()=>{
        setProp1(prop1Callback(main));
        setProp2(prop2Callback(main));
    }, [main]);

    return [main, setMain, prop1, prop2];
}

export default useFeach;


const [state, dispatch] = useReducer((state, action)=>{
    switch(action.state) {
      case 0:
        return {
          selectedDB: undefined,
          tables: undefined,
          selectedTable: undefined,
          tableRecords: undefined,    
        }
      case 1: 
        return {
          selectedDB: action.dbIndex,
          tables: new Database(DBs[action.dbIndex]).getTables(),
          selectedTable: undefined,
          tableRecords: undefined,
        }
      case 2: 
        return {
          ...state,
          selectedTable: state.tables[action.tableIndex][0],
          tableRecords: state.selectedDB.getData(state.selectedTable).unshift(state.selectedDB.getColumns(state.selectedTable)),
        }
      default:
        return state;
    }
  },
  {
    selectedDB: undefined,
    tables: undefined,
    selectedTable: undefined,
    tableRecords: undefined,
  })

