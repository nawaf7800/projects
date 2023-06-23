import { StyleSheet, View, Pressable, Text } from 'react-native';

function ButtonsFooter(prop) {
    var footerHeight = (prop.buttons.length > 10) ? 10 * 6.1 :
    prop.buttons.length * 6.1;
    footerHeight = footerHeight + 1;        //new
    footerHeight = footerHeight.toString() + '%';
    return (
        <View style={[styles.controlCont, {height: footerHeight}]}>
            {Object.entries(prop.buttons).map(([key, button]) => (
                <Pressable 
                    key={key}
                    onPress={button.press} 
                    disabled={button.disabled}
                    style={({pressed}) => [
                        {
                          backgroundColor: pressed ? '#466581' : '#1e3d59',
                        },
                        styles.button,
                      ]}
                    >
                    <Text style={[styles.text, {color: button.disabled ? 'gray': '#ffc13b'}]}>{button.title}</Text>
                </Pressable>
            ))}
        </View>
    )
}

export default ButtonsFooter;

const styles = StyleSheet.create({
    controlCont: {
        borderTopWidth: 1,
        borderTopColor: '#ffc13b',
        backgroundColor: '#1e3d59'
    },
    button: {
        alignItems: 'center',
        justifyContent: 'center',
        flex: 1,
        borderTopWidth: 1,
        borderTopColor: '#ffc13b',   
    },
    text: {
        fontSize: 22,
        fontWeight: 'bold',
        fontFamily: 'Courier New',
    },
});