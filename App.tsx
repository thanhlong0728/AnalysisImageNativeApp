/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import type {PropsWithChildren} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  Text,
  TouchableOpacity,
  NativeModules,
} from 'react-native';

function App(): React.JSX.Element {
  const {ToastService, AnalysisImageActivity} = NativeModules;

  const openScreen = () => {
    AnalysisImageActivity.open();
  };

  return (
    <SafeAreaView style={styles.container}>
      <TouchableOpacity onPress={openScreen}>
        <Text style={styles.btn}>GPT Analysis Image</Text>
      </TouchableOpacity>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  btn: {
    padding: 10,
    backgroundColor: 'purple',
    color: 'white',
    fontWeight: 'bold',
  },
});

export default App;
