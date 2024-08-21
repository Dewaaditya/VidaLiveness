import React, {useState} from 'react';
import {NativeModules, Button, StyleSheet, View, Image, Text} from 'react-native';

const {VidaLivenessModule} = NativeModules;

const NewModuleButton = () => {
  const [resultImage, setResultImage] = useState(null);

  const onPress = () => {
    console.log('test click')
    VidaLivenessModule.startLiveness()
      .then(base64Image => {
        // console.log("Liveness successful, image base64: ", base64Image);
        const result = "data:image/png;base64,"+base64Image;
        setResultImage(result)
      })
      .catch(error => {
        console.log("Liveness detection failed: ", error);

        setResultImage('failed')
      });
  };

  return (
    <>
      <Button
        title="Start to Liveness Detection"
        color="#841584"
        onPress={onPress}
      />

      <View style={styles.container}>
        {resultImage == 'failed' ? (
          <>
            <Image
              source={require('./src/images/failed.png')}
              style={styles.image}
            />
            <Text style={{ marginTop: 20 }}>Identity verification failed</Text>
          </>
        ) : resultImage != null ? (
          <>
            <Image
              style={styles.image}
              source={{ uri: resultImage }}
            />
            <Text style={{ marginTop: 20 }}>Identity verification successful. Liveness check completed, and the image has been captured</Text>
          </>
        ) : <></>}
      </View>
    </>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  image: {
    width: 200,
    height: 200,
    resizeMode: 'contain',
  },
});

export default NewModuleButton;